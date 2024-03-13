package intellijmigrationplugin.actions.annotation

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import intellijmigrationplugin.actions.annotation.utils.AnnotationActionUtils
import intellijmigrationplugin.actions.annotation.utils.AnnotationActionUtils.Companion.canMerge
import intellijmigrationplugin.actions.annotation.utils.AnnotationActionUtils.Companion.mergeAnnotations
import intellijmigrationplugin.annotationModel.AnnotationDetection.Companion.detectAnnotationInFile

class CleanupAction : AnAction() {

    override fun update(event: AnActionEvent) {

        val project = event.getData(CommonDataKeys.PROJECT)
        val editor = event.getData(CommonDataKeys.EDITOR)

        event.presentation.isEnabledAndVisible = (project != null
                && editor != null)
    }


    override fun actionPerformed(event: AnActionEvent) {

        //Get Required information from the event
        val project = event.getRequiredData(CommonDataKeys.PROJECT)
        val editor = event.getRequiredData(CommonDataKeys.EDITOR)
        val document = editor.document

        val commentStart : String = AnnotationActionUtils.getCommentTypeByEvent(event)

        WriteCommandAction.runWriteCommandAction(project) {

            val annotations = detectAnnotationInFile(document, commentStart).reversed()

            for(i in 0..annotations.size - 2) {
                if(document.canMerge(annotations[i], annotations[i + 1])) {
                    document.mergeAnnotations(annotations[i], annotations[i + 1])
                }
            }
        }
    }
}