package intellijmigrationplugin.ui.dialogs

import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.dsl.builder.panel
import javax.swing.JComponent

class CollisionDialog : DialogWrapper(true) {
    init {
        title = "Annotation-Collision Detected"
        init()
    }
    override fun createCenterPanel(): JComponent {
        return panel {
            row("Your selection is overlapping with existing Annotations. \n" +
                    "do you wish to continue and overwrite the selected Area?"){}
        }
    }
}