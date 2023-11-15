package com.github.timheibel.intellijmigrationplugin.settings.components

import com.github.timheibel.intellijmigrationplugin.settings.MigrationSettingsComponent
import com.intellij.ui.ColorPicker
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBLabel
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JPanel

class KeywordColorMappingComponent(private val keyword: String, private val settingsComponent: MigrationSettingsComponent) {
    private val keywordColorMapping = settingsComponent.keywordColorMapping
    private val colorLabels = settingsComponent.colorLabels

    fun getComponent(): JComponent {
        val colorMappingPanel = JPanel()
        val colorLabel = JBLabel(keyword)

        colorLabel.foreground = keywordColorMapping[keyword]
        colorLabels[keyword] = colorLabel

        val selectColorButton = JButton("Select Color")
        selectColorButton.addActionListener {
            val newColor = ColorPicker.showDialog(
                colorMappingPanel, "Choose Color for $keyword", keywordColorMapping[keyword], false, null, false
            )
            if (newColor != null) {
                keywordColorMapping[keyword] = JBColor(newColor, newColor)
                colorLabel.foreground = keywordColorMapping[keyword]
            }
        }

        colorMappingPanel.add(colorLabel)
        colorMappingPanel.add(selectColorButton)

        return colorMappingPanel
    }


    fun updateColorLabels() {
        for (keyword in keywordColorMapping.keys) {
            val colorLabel = colorLabels[keyword]
            colorLabel?.foreground = keywordColorMapping[keyword]
            colorLabel?.repaint()
        }
    }
}