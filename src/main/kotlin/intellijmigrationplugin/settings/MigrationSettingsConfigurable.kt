package intellijmigrationplugin.settings

import com.intellij.openapi.options.Configurable
import org.jetbrains.annotations.Nls
import javax.swing.JComponent
import javax.swing.table.DefaultTableModel

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

        val legacyFolderPathModified =
            settingsComponent?.legacyFolderComponent?.legacyFolderPath != settings.legacyFolderPath
        val excludedFoldersListModified = settingsComponent?.excludedFolderComponent?.excludedFoldersListModel?.toList()
            ?.equals(settings.excludedFoldersList)?.not()
        //val keyWordColorMappingModified = settingsComponent?.keywordColorMappingComponent?.keywordColorMappingList?.equals(settings.keywordColorMapping)?.not()
        val filetypeCommentMappingModified =
            (convertTableModelToList(settingsComponent?.filetypeCommentMappingComponent?.tableModel!!) != settings.fileTypeCommentMapping)

        return legacyFolderPathModified || excludedFoldersListModified!! || filetypeCommentMappingModified
    }

    override fun apply() {
        val settings: MigrationSettingsState = MigrationSettingsState.instance
        settings.legacyFolderPath = settingsComponent?.legacyFolderComponent?.legacyFolderPath ?: ""
        settings.excludedFoldersList =
            settingsComponent?.excludedFolderComponent?.excludedFoldersListModel?.toList() ?: mutableListOf()
        //settings.keywordColorMapping = settingsComponent?.keywordColorMappingComponent?.keywordColorMappingList?.toMutableList() ?: mutableListOf()
        settings.fileTypeCommentMapping =
            convertTableModelToList(settingsComponent?.filetypeCommentMappingComponent?.tableModel!!)
    }

    override fun reset() {
        val settings: MigrationSettingsState = MigrationSettingsState.instance
        settingsComponent?.legacyFolderComponent?.legacyFolderPath = settings.legacyFolderPath
        settingsComponent?.excludedFolderComponent?.excludedFoldersListModel?.replaceAll(settings.excludedFoldersList)
        //settingsComponent?.keywordColorMappingComponent?.keywordColorMappingList = settings.keywordColorMapping.toMutableList()
        settingsComponent?.filetypeCommentMappingComponent?.initializeTableData(settings.fileTypeCommentMapping)
    }

    override fun disposeUIResources() {
        settingsComponent = null
    }

    fun convertTableModelToList(tableModel: DefaultTableModel): MutableList<Pair<String, String>> {
        val list = mutableListOf<Pair<String, String>>()

        for (row in 0 until tableModel.rowCount) {
            val fileType = tableModel.getValueAt(row, 0).toString()
            val commentType = tableModel.getValueAt(row, 1).toString()
            list.add(fileType to commentType)
        }

        return list
    }
}
