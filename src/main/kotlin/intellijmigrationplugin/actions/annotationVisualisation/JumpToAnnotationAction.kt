package intellijmigrationplugin.actions.annotationVisualisation

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.LogicalPosition
import com.intellij.openapi.editor.ScrollType
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import intellijmigrationplugin.annotationModel.util.AnnotationDetection
import intellijmigrationplugin.annotationModel.AnnotationInformation

/**
 * This action lets the user jump to the top of an Annotation
 * if the current cursor is inside an Annotation.
 */
class JumpToAnnotationAction: AnAction() {

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
        var jumpToLine = -1

        lineLoop@ for (lineNumber in cursorLine downTo 0) {

            val lineStartOffset = editor.document.getLineStartOffset(lineNumber)
            val lineEndOffset = editor.document.getLineEndOffset(lineNumber)
            val line = editor.document.getText(TextRange(lineStartOffset, lineEndOffset))

            if (line.contains(regexEnd)) break
            for (regex in regexes) {
                if (line.contains(regex)) {
                    jumpToLine = lineNumber
                    break@lineLoop
                }
            }
        }
        println(jumpToLine)
        if (jumpToLine != -1) gotoLine(jumpToLine, e.project!!)
    }


    fun gotoLine(lineNumber: Int, project: Project): Boolean {
        val editor = FileEditorManager.getInstance(project).selectedTextEditor ?: return false
        val caretModel = editor.caretModel
        val totalLineCount = editor.document.lineCount
        if (lineNumber > totalLineCount) return false

        //Moving caret to line number
        caretModel.moveToLogicalPosition(LogicalPosition(lineNumber, 0))

        //Scroll to the caret
        val scrollingModel = editor.scrollingModel
        scrollingModel.scrollToCaret(ScrollType.CENTER)
        return true
    }


}