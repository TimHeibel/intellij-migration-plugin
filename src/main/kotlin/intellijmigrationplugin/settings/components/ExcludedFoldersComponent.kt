package intellijmigrationplugin.settings.components

import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.project.Project
import com.intellij.ui.CollectionListModel
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBList
import com.intellij.util.ui.FormBuilder
import javax.swing.JComponent

class ExcludedFoldersComponent(private val project: Project) {

    var excludedFoldersListModel = CollectionListModel<String>()
    private val excludedFoldersList = JBList(excludedFoldersListModel)
    private val descriptor = FileChooserDescriptorFactory.createMultipleFoldersDescriptor()

    fun getComponent(): JComponent {
        configureExcludedFoldersList()

        val decorator = ToolbarDecorator.createDecorator(excludedFoldersList)
            .setAddAction { addFolder() }
            .setRemoveAction { removeSelectedFolders() }
            .disableUpDownActions()

        return FormBuilder.createFormBuilder()
            .addLabeledComponent(JBLabel("Excluded folders: "), decorator.createPanel(), 1, true)
            .panel
    }

    private fun configureExcludedFoldersList() {
        excludedFoldersList.emptyText.setText("Optional")
    }

    private fun addFolder() {
        val chosenFiles = FileChooser.chooseFiles(descriptor, project, null)
        for (file in chosenFiles) {
            val path = file.path
            if (!excludedFoldersListModel.items.contains(path)) {
                excludedFoldersListModel.add(path)
            }
        }
    }

    private fun removeSelectedFolders() {
        val selectedValues = excludedFoldersList.selectedValuesList.toList()
        for (selectedValue in selectedValues) {
            excludedFoldersListModel.remove(selectedValue)
        }
    }
}
