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
        return settingsComponent?.legacyFolderPath != settings.legacyFolderPath
    }

    override fun apply() {
        val settings: MigrationSettingsState = MigrationSettingsState.instance
        settings.legacyFolderPath = settingsComponent?.legacyFolderPath ?: ""
    }

    override fun reset() {
        val settings: MigrationSettingsState = MigrationSettingsState.instance
        settingsComponent?.legacyFolderPath = settings.legacyFolderPath
    }

    override fun disposeUIResources() {
        settingsComponent = null
    }
}
