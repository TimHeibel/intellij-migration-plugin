package intellijmigrationplugin.settings.components
import com.intellij.ui.ColorPicker
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBLabel
import com.intellij.util.ui.FormBuilder
import java.awt.Color
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JPanel

class KeywordColorMappingComponent {
    var keywordColorMappingList = mutableListOf<Pair<String, JBColor>>()
        set(value) {
            field = value
            updateColorLabels()
        }
    private val colorLabels = mutableMapOf<String, JBLabel>()

    private val migratedComponent = createColorMappingSubComponent("MIGRATED")
    private val laterComponent = createColorMappingSubComponent("LATER")
    private val unusedComponent = createColorMappingSubComponent("UNUSED")

    private fun createColorMappingSubComponent(subKeyword: String): JComponent {
        val colorMappingPanel = JPanel()
        val colorLabel = JBLabel(subKeyword)

        colorLabel.foreground = findColorForSubKeyword(subKeyword)
        colorLabels[subKeyword] = colorLabel

        val selectColorButton = JButton("Select Color")
        selectColorButton.addActionListener {
            val newColor = ColorPicker.showDialog(
                colorMappingPanel, "Choose Color for $subKeyword", findColorForSubKeyword(subKeyword), false, null, false
            )
            if (newColor != null) {
                updateColorForSubKeyword(subKeyword, JBColor(newColor, newColor))
            }
        }

        colorMappingPanel.add(colorLabel)
        colorMappingPanel.add(selectColorButton)

        return colorMappingPanel
    }

    fun getComponent(): JComponent {
        return FormBuilder.createFormBuilder()
            .addComponent(migratedComponent)
            .addComponent(laterComponent)
            .addComponent(unusedComponent).panel
    }

    private fun updateColorLabels() {
        listOf("MIGRATED", "LATER", "UNUSED").forEach { subKeyword ->
            val colorLabel = colorLabels[subKeyword]
            colorLabel?.foreground = findColorForSubKeyword(subKeyword)
            colorLabel?.repaint()
        }
    }

    private fun findColorForSubKeyword(subKeyword: String): JBColor {
        return keywordColorMappingList.find { it.first == subKeyword }?.second ?: JBColor(Color.BLACK, Color.BLACK)
    }

    private fun updateColorForSubKeyword(subKeyword: String, newColor: JBColor) {
        keywordColorMappingList = keywordColorMappingList.map {
            if (it.first == subKeyword) {
                subKeyword to newColor
            } else {
                it
            }
        }.toMutableList()
        updateColorLabels()
    }
}
