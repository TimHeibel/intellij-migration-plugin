package intellijmigrationplugin.ui.popup

import com.intellij.openapi.ui.popup.JBPopup
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.ui.JBColor
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.gridLayout.HorizontalAlign
import javax.swing.JPanel

class ErrorPopUp {

    fun createErrorPopUp(message: String, adText: String): JBPopup {

        val panel2: JPanel = panel {
                row {
                    label(message).focused().horizontalAlign(HorizontalAlign.CENTER)
                }
            }

        return JBPopupFactory.getInstance()
            .createComponentPopupBuilder(panel2, JPanel())
            .setTitle("Error Pop-Up").setFocusable(true)
            .setResizable(true).setMovable(true).setRequestFocus(true)
            .setBorderColor(JBColor.RED)
            .setAdText(adText)
            .setNormalWindowLevel(true)
            .setShowShadow(true)
            .createPopup()
    }

    fun showErrorPopUp(message: String, adText: String){

        val popUp: JBPopup = createErrorPopUp(message, adText)
        popUp.showInFocusCenter()

    }
}