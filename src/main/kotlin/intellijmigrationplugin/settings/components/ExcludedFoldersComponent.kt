package intellijmigrationplugin.settings.components

import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.project.Project
import com.intellij.ui.CollectionListModel
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.components.JBList
import com.intellij.util.ui.FormBuilder
import java.nio.file.Paths
import javax.swing.JComponent
import javax.swing.JOptionPane

class ExcludedFoldersComponent(private val project: Project) {

    var excludedFoldersListModel = CollectionListModel<String>()
    private val excludedFoldersList = JBList(excludedFoldersListModel)
    private val descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor().apply {
        title = "Choose Subfolder"
        description = "Select a subfolder of the project"
        withTreeRootVisible(false)
        isTreeRootVisible
    }

    fun getComponent(): JComponent {
        configureExcludedFoldersList()

        val decorator = ToolbarDecorator.createDecorator(excludedFoldersList).setAddAction { addFolder() }
            .setRemoveAction { removeSelectedFolders() }.disableUpDownActions()

        return FormBuilder.createFormBuilder().addComponent(decorator.createPanel()).panel
    }

    private fun configureExcludedFoldersList() {
        excludedFoldersList.emptyText.setText("Optional")
        excludedFoldersList.visibleRowCount = 3
    }

    private fun addFolder() {
        val chosenFiles = FileChooser.chooseFiles(descriptor, project, null)
        val projectBasePath = project.basePath ?: return

        for (file in chosenFiles) {
            val path = file.path

            // Convert to Path objects for better comparison
            val projectPath = Paths.get(projectBasePath).toAbsolutePath().normalize()
            val filePath = Paths.get(path).toAbsolutePath().normalize()

            // Check if filePath starts with projectPath
            if (filePath.startsWith(projectPath) && filePath != projectPath) {
                if (!excludedFoldersListModel.items.contains(path)) {
                    excludedFoldersListModel.add(path)
                } else {
                    // Path is already in List, do not add it again
                }
            } else {
                JOptionPane.showMessageDialog(
                    null,
                    "The selected folder is not a subfolder of the project.",
                    "Invalid Folder",
                    JOptionPane.ERROR_MESSAGE
                )
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
