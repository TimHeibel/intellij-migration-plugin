package intellijmigrationplugin.settings

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
        val excludedFoldersListModified = settingsComponent?.excludedFolderComponent?.excludedFoldersList?.equals(settings.excludedFoldersList)?.not()
        val keyWordColorMappingModified = settingsComponent?.keywordColorMappingComponent?.keywordColorMappingList?.equals(settings.keywordColorMapping)?.not()
        val filetypeCommentMappingModified = settingsComponent?.filetypeCommentMappingComponent?.fileTypeCommentMappingList?.equals(settings.fileTypeCommentMapping)?.not()
        return legacyFolderPathModified || filetypeCommentMappingModified!! || keyWordColorMappingModified!! || excludedFoldersListModified!!
    }

    override fun apply() {
        val settings: MigrationSettingsState = MigrationSettingsState.instance
        settings.legacyFolderPath = settingsComponent?.legacyFolderComponent?.legacyFolderPath ?: ""
        settings.excludedFoldersList = settingsComponent?.excludedFolderComponent?.excludedFoldersList?.toMutableList() ?: mutableListOf()
        settings.keywordColorMapping = settingsComponent?.keywordColorMappingComponent?.keywordColorMappingList?.toMutableList() ?: mutableListOf()
        settings.fileTypeCommentMapping = settingsComponent?.filetypeCommentMappingComponent?.fileTypeCommentMappingList?.toMutableList() ?: mutableListOf()
    }
    
    override fun reset() {
        val settings: MigrationSettingsState = MigrationSettingsState.instance
        settingsComponent?.legacyFolderComponent?.legacyFolderPath = settings.legacyFolderPath
        settingsComponent?.excludedFolderComponent?.excludedFoldersList = settings.excludedFoldersList
        settingsComponent?.keywordColorMappingComponent?.keywordColorMappingList = settings.keywordColorMapping.toMutableList()
        settingsComponent?.filetypeCommentMappingComponent?.fileTypeCommentMappingList = settings.fileTypeCommentMapping.toMutableList()
    }

    override fun disposeUIResources() {
        settingsComponent = null
    }
}
