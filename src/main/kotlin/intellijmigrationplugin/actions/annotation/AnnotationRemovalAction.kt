package intellijmigrationplugin.actions.annotation

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.util.TextRange
import intellijmigrationplugin.annotationModel.AnnotationDetection

class AnnotationRemovalAction : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {

        val editor = event.getData(CommonDataKeys.EDITOR)
            ?: return

        val vFile = event.getData(CommonDataKeys.VIRTUAL_FILE)
            ?: return

        val project = event.getData(CommonDataKeys.PROJECT)
            ?: return

        //Get Required information from the event
        val document = editor.document
        val primaryCaret = editor.caretModel.primaryCaret

        //Get Start and End of current selection
        val startSelection = primaryCaret.selectionStart
        val startSelectionLine = document.getLineNumber(startSelection)

        WriteCommandAction.runWriteCommandAction(project) {

            val annotationMapping = AnnotationDetection.detectAnnotationInFile(document, vFile.extension)

            for (annotation in annotationMapping.asReversed()) {

                if(annotation.start >= startSelectionLine) {

                    if(annotation.hasEnd) {
                        var offset = document.getLineEndOffset(annotation.end) + 1

                        if(offset > document.textLength) {
                            offset -= 1
                        }

                        document.replaceString(document.getLineStartOffset(annotation.end),
                            offset, "")
                    }

                    var offset = document.getLineEndOffset(annotation.start) + 1
                    if(offset > document.textLength) {
                        offset -= 1
                    }

                    document.replaceString(document.getLineStartOffset(annotation.start),
                            offset, "")
                }
            }
        }

        primaryCaret.removeSelection()

    }
}