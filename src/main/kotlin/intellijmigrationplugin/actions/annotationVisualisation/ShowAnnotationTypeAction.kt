package intellijmigrationplugin.actions.annotationVisualisation

import com.intellij.codeInsight.hint.HintManager
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.util.TextRange
import intellijmigrationplugin.annotationModel.util.AnnotationDetection
import intellijmigrationplugin.annotationModel.AnnotationInformation

/**
 * This action shows the current AnnotationType if the cursor is inside an Annotation
 */
class ShowAnnotationTypeAction: AnAction() {



    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR)!!
        val cursorOffset = editor.caretModel.offset
        val cursorLine = editor.document.getLineNumber(cursorOffset)

        val vFile = e.getData(CommonDataKeys.VIRTUAL_FILE)
                ?: return
        val fileType = vFile.extension

        val commentType = AnnotationInformation.instance!!.singleCommentMapping[fileType]
                ?: "//"
        val keywords = AnnotationInformation.instance!!.keywords
        val regexes = keywords.map { x -> AnnotationDetection.getAnnotationRegex(commentType, x) }
        val regexEnd = AnnotationDetection.getAnnotationRegex(commentType, "end")
        var annotation = "end"

        lineLoop@ for (lineNumber in cursorLine downTo 0) {

            val lineStartOffset = editor.document.getLineStartOffset(lineNumber)
            val lineEndOffset = editor.document.getLineEndOffset(lineNumber)
            val line = editor.document.getText(TextRange(lineStartOffset, lineEndOffset))

            if (line.contains(regexEnd)) break
            var j = 0
            for (regex in regexes) {
                if (line.contains(regex)) {
                    annotation = keywords[j]
                    break@lineLoop
                }
                j++
            }
        }

        if (annotation == "end") return

        /*NotificationGroupManager.getInstance()
                .getNotificationGroup("Custom Notification Group")
                .createNotification(annotation, NotificationType.INFORMATION)
                .notify(editor.project)*/

        HintManager.getInstance().showInformationHint(editor, annotation)

    }
}