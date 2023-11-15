package com.github.timheibel.intellijmigrationplugin.settings.components

import com.intellij.ui.ColorPicker
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBLabel
import com.intellij.util.ui.FormBuilder
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JPanel

class KeywordColorMappingComponent {
    var keywordColorMapping = mutableMapOf<String, JBColor>()
        set(value) {
            field = value
            // Ensure that the color labels are updated based on the loaded state (won't happen automatically)
            updateColorLabels()
        }
    private val colorLabels = mutableMapOf<String, JBLabel>()

    private val migratedComponent = createColorMappingSubComponent("MIGRATED")
    private val laterComponent = createColorMappingSubComponent("LATER")
    private val unusedComponent = createColorMappingSubComponent("UNUSED")

    private fun createColorMappingSubComponent(subKeyword: String): JComponent {
        val colorMappingPanel = JPanel()
        val colorLabel = JBLabel(subKeyword)

        colorLabel.foreground = keywordColorMapping[subKeyword]
        colorLabels[subKeyword] = colorLabel

        val selectColorButton = JButton("Select Color")
        selectColorButton.addActionListener {
            val newColor = ColorPicker.showDialog(
                colorMappingPanel, "Choose Color for $subKeyword", keywordColorMapping[subKeyword], false, null, false
            )
            if (newColor != null) {
                keywordColorMapping[subKeyword] = JBColor(newColor, newColor)
                colorLabel.foreground = keywordColorMapping[subKeyword]
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
            colorLabel?.foreground = keywordColorMapping[subKeyword]
            colorLabel?.repaint()
        }
    }
}
