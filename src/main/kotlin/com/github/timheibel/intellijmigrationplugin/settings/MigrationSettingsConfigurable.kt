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
        return settingsComponent?.legacyFolderPath != settings.legacyFolderPath || settingsComponent?.keywordColorMapping?.entries != settings.keywordColorMapping.entries || settingsComponent?.fileTypeCommentMapping?.entries != settings.fileTypeCommentMapping.entries
    }

    override fun apply() {
        val settings: MigrationSettingsState = MigrationSettingsState.instance
        settings.legacyFolderPath = settingsComponent?.legacyFolderPath ?: ""
        settings.keywordColorMapping = settingsComponent?.keywordColorMapping ?: mutableMapOf()
        settings.fileTypeCommentMapping = settingsComponent?.fileTypeCommentMapping ?: mutableMapOf()
    }

    override fun reset() {
        val settings: MigrationSettingsState = MigrationSettingsState.instance
        settingsComponent?.legacyFolderPath = settings.legacyFolderPath
        settingsComponent?.keywordColorMapping = settings.keywordColorMapping
        settingsComponent?.fileTypeCommentMapping = settings.fileTypeCommentMapping
        // Ensure that the color labels are updated based on the loaded state (won't happen automatically)
        settingsComponent?.updateColorLabels()
    }

    override fun disposeUIResources() {
        settingsComponent = null
    }
}
