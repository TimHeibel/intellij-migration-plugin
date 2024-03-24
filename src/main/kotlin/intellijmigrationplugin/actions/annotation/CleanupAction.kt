package intellijmigrationplugin.actions.annotation

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.command.WriteCommandAction
import intellijmigrationplugin.actions.annotation.utils.AnnotationActionUtils
import intellijmigrationplugin.actions.annotation.utils.AnnotationActionUtils.Companion.canMerge
import intellijmigrationplugin.actions.annotation.utils.AnnotationActionUtils.Companion.mergeAnnotations
import intellijmigrationplugin.annotationModel.util.AnnotationDetection.Companion.detectAnnotationInFile

class CleanupAction : AnAction() {

    override fun update(event: AnActionEvent) {

        val project = event.getData(CommonDataKeys.PROJECT)
        val editor = event.getData(CommonDataKeys.EDITOR)
        val virtualFile = event.getData(PlatformDataKeys.VIRTUAL_FILE)

        event.presentation.isEnabledAndVisible = (project != null
                && editor != null
                && virtualFile != null)
    }


    override fun actionPerformed(event: AnActionEvent) {

        //Get Required information from the event
        val project = event.getRequiredData(CommonDataKeys.PROJECT)
        val editor = event.getRequiredData(CommonDataKeys.EDITOR)
        val document = editor.document
        val virtualFile = event.getRequiredData(PlatformDataKeys.VIRTUAL_FILE)

        val commentStart : String = AnnotationActionUtils.getCommentTypeByEvent(event)

        WriteCommandAction.runWriteCommandAction(project) {

            val annotations = detectAnnotationInFile(document, virtualFile.extension).reversed()

            for(i in 0..annotations.size - 2) {
                if(document.canMerge(annotations[i], annotations[i + 1], virtualFile.path)) {
                    document.mergeAnnotations(annotations[i], annotations[i + 1])
                }
            }
        }
    }
}