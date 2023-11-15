package com.github.timheibel.intellijmigrationplugin.settings

import com.github.timheibel.intellijmigrationplugin.settings.components.FiletypeCommentMappingComponent
import com.github.timheibel.intellijmigrationplugin.settings.components.KeywordColorMappingComponent
import com.github.timheibel.intellijmigrationplugin.settings.components.LegacyFolderComponent
import com.intellij.util.ui.FormBuilder
import javax.swing.JPanel

/**
 * Represents the settings component for the migration plugin
 * This component includes sections for specifying the legacy folder, keyword color mapping,
 * and filetype comment mapping.
 */
class MigrationSettingsComponent {
    internal val panel: JPanel

    // Components
    internal val legacyFolderComponent = LegacyFolderComponent()
    internal val keywordColorMappingComponent = KeywordColorMappingComponent()
    internal val filetypeCommentMappingComponent = FiletypeCommentMappingComponent()

    init {
        panel = FormBuilder.createFormBuilder()
            .addComponent(legacyFolderComponent.getComponent())
            .addSeparator(2)
            .addComponent(keywordColorMappingComponent.getComponent())
            .addSeparator(2)
            .addComponent(filetypeCommentMappingComponent.getComponent()).panel
    }
}