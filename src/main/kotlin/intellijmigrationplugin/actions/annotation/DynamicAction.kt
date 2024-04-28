package intellijmigrationplugin.actions.annotation

import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.diagnostic.thisLogger
import intellijmigrationplugin.settings.MigrationSettingsState

/**
 * This class allows to dynamically registering and unregistering custom annotation actions
 */
class DynamicAction {
    companion object {

        /**
         * Resets custom annotation actions based on the current settings.
         * This method unregisters all existing custom annotation actions and then registers new ones
         * based on the annotation types defined in the settings.
         */
        internal fun resetCustomAnnotation() {

            val keyWords = MigrationSettingsState.instance.keywordColorMapping.map { it.first }

            unregisterAllCustomAnnotationAction()
            registerCustomAnnotationAction(keyWords.toTypedArray())

        }

        /**
         * Registers custom annotation actions for the provided array of annotation types.
         * @param annotationTypes Array of annotation types for which custom actions should be registered.
         */
        private fun registerCustomAnnotationAction(annotationTypes: Array<String>) {
            for (annotationType in annotationTypes) {
                registerCustomAnnotationAction(annotationType)
            }
        }

        /**
         * Registers a custom annotation action for the provided annotation type.
         * @param annotationType The annotation type for which a custom action should be registered.
         * @param addInfo Additional information to be associated with the action (optional).
         */
        private fun registerCustomAnnotationAction(annotationType: String, addInfo: String = "") {

            sanitizeAnnotationType(annotationType)?: return

            val actionManager = ActionManager.getInstance()

            val actionGroup : DefaultActionGroup

            try {
                actionGroup =  actionManager.getAction("intellijmigrationplugin.AnnotationAction.Place") as DefaultActionGroup
            } catch (e : ClassCastException) {
                thisLogger().warn("action group not defined: $e")
                return
            }

            val actionId = "intellijmigrationplugin.AnnotationAction.Place.Custom.${annotationType}"

            val dynamicAction = AnnotationAction(annotationType, addInfo)

            actionGroup.addAction(dynamicAction)

            actionManager.registerAction(actionId, dynamicAction)
        }

        /**
         * Unregisters all custom annotation actions.
         * This method removes all custom annotation actions from the action group and unregisters them
         * based on Plugin-Settings.
         */
        private fun unregisterAllCustomAnnotationAction() {

            val actionManager = ActionManager.getInstance()
            val actionGroup : DefaultActionGroup

            try {
                actionGroup =  actionManager.getAction("intellijmigrationplugin.AnnotationAction.Place") as DefaultActionGroup
            } catch (e : ClassCastException) {
                thisLogger().warn("tried to use action as actionGroup: $e")
                return
            }

            actionGroup.getChildren(null).forEach { child ->
                val actionId = actionManager.getId(child)

                if(actionId != null) {
                    actionManager.unregisterAction(actionId)
                } else {
                    thisLogger().warn("action: $child not registered")
                }
            }

            actionGroup.removeAll()
        }

        /**
         * Sanitizes the provided annotation type by removing invalid characters.
         * @param annotationType The annotation type to be sanitized.
         * @return The sanitized annotation type, or null if it contains invalid characters.
         */
        private fun sanitizeAnnotationType(annotationType: String): String? {
            val invalidCharacters = annotationType.find { !it.isLetterOrDigit() && it != '_' }
            if (invalidCharacters != null) {
                println("Warning: Annotation type contains invalid character '$invalidCharacters'.")
                return null
            }

            return annotationType
        }
    }
}