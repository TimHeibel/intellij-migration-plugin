package intellijmigrationplugin.settings

import com.intellij.openapi.options.Configurable
import intellijmigrationplugin.actions.annotation.DynamicAction
import intellijmigrationplugin.settings.components.FileTypeMapping
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
        val keyWordColorMappingModified =
            convertTableModelToList(settingsComponent?.keywordColorMappingComponent?.tableModel!!) != settings.keywordColorMapping
        val filetypeCommentMappingModified =
            convertTableModelToFileTypeMappingList(settingsComponent?.filetypeCommentMappingComponent?.tableModel!!) != settings.fileTypeCommentMapping

        return legacyFolderPathModified || excludedFoldersListModified!! || keyWordColorMappingModified || filetypeCommentMappingModified
    }

    override fun apply() {
        val settings: MigrationSettingsState = MigrationSettingsState.instance
        settings.legacyFolderPath = settingsComponent?.legacyFolderComponent?.legacyFolderPath ?: ""
        settings.excludedFoldersList =
            settingsComponent?.excludedFolderComponent?.excludedFoldersListModel?.toList() ?: mutableListOf()
        settings.keywordColorMapping =
            convertTableModelToList(settingsComponent?.keywordColorMappingComponent?.tableModel!!)
        settings.fileTypeCommentMapping =
            convertTableModelToFileTypeMappingList(settingsComponent?.filetypeCommentMappingComponent?.tableModel!!)
        DynamicAction.resetCustomAnnotation()
    }

    override fun reset() {
        val settings: MigrationSettingsState = MigrationSettingsState.instance
        settingsComponent?.legacyFolderComponent?.legacyFolderPath = settings.legacyFolderPath
        settingsComponent?.excludedFolderComponent?.excludedFoldersListModel?.replaceAll(settings.excludedFoldersList)
        settingsComponent?.keywordColorMappingComponent?.initializeTableData(settings.keywordColorMapping)
        settingsComponent?.filetypeCommentMappingComponent?.initializeTableData(settings.fileTypeCommentMapping)
    }

    override fun disposeUIResources() {
        settingsComponent = null
    }

    private fun convertTableModelToList(tableModel: DefaultTableModel): MutableList<Pair<String, String>> {
        val list = mutableListOf<Pair<String, String>>()

        for (row in 0 until tableModel.rowCount) {
            val firstVal = tableModel.getValueAt(row, 0).toString().trim()
            val secondVal = tableModel.getValueAt(row, 1).toString().trim()

            // Skip empty rows
            if (firstVal.isNotEmpty() || secondVal.isNotEmpty()) {
                list.add(firstVal to secondVal)
            }
        }

        return list
    }

    private fun convertTableModelToFileTypeMappingList(tableModel: DefaultTableModel): MutableList<FileTypeMapping> {
        val list = mutableListOf<FileTypeMapping>()

        for (row in 0 until tableModel.rowCount) {
            val filetype = tableModel.getValueAt(row, 0).toString().trim()
            val singleLineComment = tableModel.getValueAt(row, 1).toString().trim()
            val multiLineComment = tableModel.getValueAt(row, 2).toString().trim()
            val importStatement = tableModel.getValueAt(row, 3).toString().trim()

            if (filetype.isNotEmpty()) {
                val fileTypeMapping = FileTypeMapping(filetype, singleLineComment, multiLineComment, importStatement)
                list.add(fileTypeMapping)
            }
        }

        return list
    }
}
