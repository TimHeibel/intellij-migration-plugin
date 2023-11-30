package com.github.timheibel.intellijmigrationplugin.actions.annotation

import com.github.timheibel.intellijmigrationplugin.ui.dialog.AnnotationDialog
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.DialogWrapper

class DialogAction : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
        val dialog = AnnotationDialog()
        dialog.show()

        if (dialog.exitCode == DialogWrapper.OK_EXIT_CODE) {

            val annotationAction = DIALOGAnnotationAction(dialog.annotationType, dialog.annotationComment)
            annotationAction.actionPerformed(event)
        }
    }
}