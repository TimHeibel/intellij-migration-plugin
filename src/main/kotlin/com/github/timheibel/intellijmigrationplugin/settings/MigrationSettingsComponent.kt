package com.github.timheibel.intellijmigrationplugin.settings

import com.github.timheibel.intellijmigrationplugin.settings.components.FiletypeCommentMappingComponent
import com.github.timheibel.intellijmigrationplugin.settings.components.KeywordColorMappingComponent
import com.github.timheibel.intellijmigrationplugin.settings.components.LegacyFolderComponent
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBLabel
import com.intellij.util.ui.FormBuilder
import javax.swing.JPanel

class MigrationSettingsComponent {
    val panel: JPanel

    var keywordColorMapping = mutableMapOf<String, JBColor>()
    val colorLabels = mutableMapOf<String, JBLabel>()
    // new
    internal val legacyFolderComponent = LegacyFolderComponent()
    internal val filetypeCommentMappingComponent = FiletypeCommentMappingComponent()

    init {
        panel = createMainPanel()
    }

    private fun createMainPanel(): JPanel {
        return FormBuilder.createFormBuilder()
            .addComponent(legacyFolderComponent.getComponent())
            .addSeparator(2)
            .addComponent(KeywordColorMappingComponent("MIGRATED", this).getComponent())
            .addComponent(KeywordColorMappingComponent("LATER", this).getComponent())
            .addComponent(KeywordColorMappingComponent("UNUSED", this).getComponent())
            .addComponent(filetypeCommentMappingComponent.getComponent())
            .panel
    }


    fun updateColorLabels() {
        for (keyword in keywordColorMapping.keys) {
            val colorLabel = colorLabels[keyword]
            colorLabel?.foreground = keywordColorMapping[keyword]
            colorLabel?.repaint()
        }
    }

}