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
        val legacyFolderPathModified = settingsComponent?.legacyFolderComponent?.legacyFolderPath != settings.legacyFolderPath
        val keyWordColorMappingModified = settingsComponent?.keywordColorMappingComponent?.keywordColorMapping?.equals(settings.keywordColorMapping)?.not()
        val filetypeCommentMappingModified = settingsComponent?.filetypeCommentMappingComponent?.fileTypeCommentMapping?.equals(settings.fileTypeCommentMapping)?.not()
        println("filetypeCommentMappingModified: ${filetypeCommentMappingModified}")
        return legacyFolderPathModified || filetypeCommentMappingModified!! || keyWordColorMappingModified!!
    }

    override fun apply() {
        val settings: MigrationSettingsState = MigrationSettingsState.instance
        settings.legacyFolderPath = settingsComponent?.legacyFolderComponent?.legacyFolderPath ?: ""
        settings.keywordColorMapping =
            settingsComponent?.keywordColorMappingComponent?.keywordColorMapping?.toMutableMap() ?: mutableMapOf()
        settings.fileTypeCommentMapping =
            settingsComponent?.filetypeCommentMappingComponent?.fileTypeCommentMapping?.toMutableMap() ?: mutableMapOf()
        println("set file type mapping to: ${settingsComponent?.filetypeCommentMappingComponent?.fileTypeCommentMapping?.entries}")
    }

    override fun reset() {
        val settings: MigrationSettingsState = MigrationSettingsState.instance
        settingsComponent?.legacyFolderComponent?.legacyFolderPath = settings.legacyFolderPath
        println("loaded settings")
        settingsComponent?.keywordColorMappingComponent?.keywordColorMapping =
            settings.keywordColorMapping.toMutableMap()
        settingsComponent?.filetypeCommentMappingComponent?.fileTypeCommentMapping =
            settings.fileTypeCommentMapping.toMutableMap()
    }

    override fun disposeUIResources() {
        settingsComponent = null
    }
}
