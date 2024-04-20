package intellijmigrationplugin.ui.popup

import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.popup.JBPopup
import com.intellij.openapi.ui.popup.JBPopupFactory
import javax.swing.JComponent

class ConformationPopUp {

    fun createConformationPopup(): JBPopup {
        val title = "Confirmation"

        val dialog = object : DialogWrapper(true) {
            init {
                init()
                setTitle(title)
            }

            override fun createCenterPanel(): JComponent? {
                return null
            }
        }
        val runnable = Runnable {
            println("This is a runnable task")
        }

        val factory = JBPopupFactory.getInstance()
        val popup = factory.createConfirmation(
            title,
            runnable,
            1
        )
        return popup
    }

    fun showPopup() {
        val popUp = createConformationPopup()
        popUp.showInFocusCenter()
    }
}