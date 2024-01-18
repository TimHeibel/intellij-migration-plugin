package intellijmigrationplugin.actions.annotation

import intellijmigrationplugin.ui.dialogs.AnnotationDialog
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.ui.DialogWrapper

class DialogAction : AnAction() {

    override fun update(event: AnActionEvent) {

        val project = event.getData(CommonDataKeys.PROJECT)
        val editor = event.getData(CommonDataKeys.EDITOR)

        event.presentation.isEnabledAndVisible = (project != null
                && editor != null
                && editor.selectionModel.hasSelection(false))
    }
    override fun actionPerformed(event: AnActionEvent) {
        val dialog = AnnotationDialog()
        dialog.show()

        if (dialog.exitCode == DialogWrapper.OK_EXIT_CODE) {

            val annotationAction = DIALOGAnnotationAction(dialog.annotationType, dialog.annotationComment)
            annotationAction.actionPerformed(event)
        }
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }
}