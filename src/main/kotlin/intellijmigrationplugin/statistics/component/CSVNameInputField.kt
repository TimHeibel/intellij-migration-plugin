package intellijmigrationplugin.statistics.component

import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import javax.swing.JComponent

class CSVNameInputField {

    var inputField = JBTextField()
    var csvName: String
        get() = inputField.text
        set(value) {
            inputField.text = value
        }
    
    fun getComponent(): JComponent {
        configureTextField()
        return FormBuilder.createFormBuilder()
            .addLabeledComponent(JBLabel("CSV name: "), inputField).panel

    }

    private fun configureTextField() {
        inputField.emptyText.setText("Enter")
        inputField.addActionListener { handleTextChange() }
    }

    private fun handleTextChange() {
        val input = inputField.text
        if (input.isNotEmpty()) csvName = input
    }
}