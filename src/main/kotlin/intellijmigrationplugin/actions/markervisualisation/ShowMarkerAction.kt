package intellijmigrationplugin.actions.markervisualisation

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.ToggleAction
import com.intellij.openapi.actionSystem.ToggleOptionAction
import com.intellij.openapi.actionSystem.ex.CheckboxAction
import intellijmigrationplugin.annotationModel.AnnotationInformation

class ShowMarkerAction: CheckboxAction() {


    override fun isSelected(e: AnActionEvent): Boolean {
        return AnnotationInformation.instance!!.showMarker
    }

    override fun setSelected(e: AnActionEvent, state: Boolean) {
        AnnotationInformation.instance!!.showMarker = state
    }


}