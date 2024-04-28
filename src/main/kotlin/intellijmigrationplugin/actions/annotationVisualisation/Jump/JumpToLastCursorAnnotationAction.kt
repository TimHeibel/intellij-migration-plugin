package intellijmigrationplugin.actions.annotationVisualisation.Jump

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import intellijmigrationplugin.actions.annotationVisualisation.JumpToAnnotationUtil
import intellijmigrationplugin.annotationModel.AnnotationInformation

/**
 * This action lets the user jump to the top of an Annotation
 * if the current cursor is inside an Annotation.
 */
class JumpToLastCursorAnnotationAction: AnAction() {

    override fun actionPerformed(e: AnActionEvent) {

        val information = AnnotationInformation.instance!!

        if (information.lastCursorLine == null) return

        val jumpToLine = information.lastCursorLine!!

        if (jumpToLine != -1) JumpToAnnotationUtil.gotoLine(jumpToLine, e.project!!)
    }

}