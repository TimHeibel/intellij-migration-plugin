package intellijmigrationplugin.actions.annotation

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import intellijmigrationplugin.annotationModel.util.AnnotationDetection
import intellijmigrationplugin.actions.annotation.utils.AnnotationActionUtils.Companion.removeAnnotation


/**
 * An action that removes annotations within the selected text range in the editor.
 */

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
        val endSelection = primaryCaret.selectionEnd
        val startSelectionLine = document.getLineNumber(startSelection)
        val endSelectionLine = document.getLineNumber(endSelection)


        WriteCommandAction.runWriteCommandAction(project) {

            val annotationMapping = AnnotationDetection.detectAnnotationInFile(document, vFile.extension)

            for (annotation in annotationMapping.asReversed()) {
                if(annotation.start in startSelectionLine .. endSelectionLine) {
                    document.removeAnnotation(annotation)
                }
            }
        }

        primaryCaret.removeSelection()

    }
}