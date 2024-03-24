package intellijmigrationplugin.actions.annotation

import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.diagnostic.thisLogger

class DynamicAction {
    companion object {
        internal fun registerAnnotationAction(annotationType: String, addInfo: String = "") {

            val actionManager = ActionManager.getInstance()

            val actionGroup : DefaultActionGroup

            try {
                actionGroup =  actionManager.getAction("intellijmigrationplugin.AnnotationAction.Place") as DefaultActionGroup
            } catch (e : ClassCastException) {
                thisLogger().warn("tried to use action as actionGroup: $e")
                return
            }

            val dynamicAction = AnnotationAction(annotationType, addInfo)

            actionGroup.addAction(dynamicAction)
        }
    }
}