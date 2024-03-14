package intellijmigrationplugin.actions.annotationVisualisation

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.ex.CheckboxAction
import intellijmigrationplugin.annotationModel.AnnotationInformation

/**
 * Turns the visualisation of the Annotation On or Off
 *
 * The current status can be seen through a checkbox in the Tools windows in the top bar
 */
class ShowAnnotationVisualisationAction: CheckboxAction() {

    override fun isSelected(e: AnActionEvent): Boolean {
        return AnnotationInformation.instance!!.showMarker
    }

    override fun setSelected(e: AnActionEvent, state: Boolean) {
        AnnotationInformation.instance!!.showMarker = state
    }

}