package com.github.timheibel.intellijmigrationplugin.settings

import com.intellij.openapi.options.Configurable
import org.jetbrains.annotations.Nls
import javax.swing.JComponent

/**
 * Configurable for SDK Application Settings Example.
 * Allows users to configure migration settings.
 */
internal class MigrationsSettingsConfigurable : Configurable {
    private var settingsComponent: MigrationSettingsComponent? = null


    override fun getDisplayName(): @Nls(capitalization = Nls.Capitalization.Title) String {
        return "Migration Plugin Settings"
    }

    override fun createComponent(): JComponent? {
        settingsComponent = MigrationSettingsComponent()
        return settingsComponent?.panel
    }

    override fun isModified(): Boolean {
        val settings: MigrationSettingsState = MigrationSettingsState.instance
        return settingsComponent?.legacyFolderComponent?.legacyFolderPath != settings.legacyFolderPath || settingsComponent?.keywordColorMappingComponent?.keywordColorMapping?.entries != settings.keywordColorMapping.entries || settingsComponent?.filetypeCommentMappingComponent?.fileTypeCommentMapping?.entries != settings.fileTypeCommentMapping.entries
    }

    override fun apply() {
        val settings: MigrationSettingsState = MigrationSettingsState.instance
        settings.legacyFolderPath = settingsComponent?.legacyFolderComponent?.legacyFolderPath ?: ""
        settings.keywordColorMapping = settingsComponent?.keywordColorMappingComponent?.keywordColorMapping ?: mutableMapOf()
        settings.fileTypeCommentMapping =
            settingsComponent?.filetypeCommentMappingComponent?.fileTypeCommentMapping ?: mutableMapOf()
    }

    override fun reset() {
        val settings: MigrationSettingsState = MigrationSettingsState.instance
        settingsComponent?.legacyFolderComponent?.legacyFolderPath = settings.legacyFolderPath
        settingsComponent?.keywordColorMappingComponent?.keywordColorMapping = settings.keywordColorMapping
        settingsComponent?.filetypeCommentMappingComponent?.fileTypeCommentMapping = settings.fileTypeCommentMapping
    }

    override fun disposeUIResources() {
        settingsComponent = null
    }
}
