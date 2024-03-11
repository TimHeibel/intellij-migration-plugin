package intellijmigrationplugin.statistics

import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Condition
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.CollectionListModel
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.components.JBList
import com.intellij.util.ui.FormBuilder
import intellijmigrationplugin.annotationModel.AnnotationInformation
import javax.swing.JComponent

class FileAndFolderChooserComponent(private val project: Project) {

    var annotationInformation = AnnotationInformation.instance
    var legacyFolderPath = annotationInformation?.legacyFolderPath
    val excludedFoldersList = annotationInformation?.excludedFolderList

    val localFileSystem = LocalFileSystem.getInstance()

    // Find the virtual file by the specified path

    val virtualFile = localFileSystem.findFileByPath(legacyFolderPath.toString())
    val root = arrayListOf(virtualFile)
    var excludedFoldersListModel = CollectionListModel<String>()
    private val choosenFilesFoldersList = JBList(excludedFoldersListModel)

    private val descriptor = FileChooserDescriptorFactory.createSingleFileOrFolderDescriptor().apply {
        title = "Choose Subfolder or File"
        description = "Select a subfolder or a file of the project"


        withRoots(root)

        withTreeRootVisible(true)
        isTreeRootVisible
        withFileFilter(customFileFilter)

    }

    private val customFileFilter: Condition<VirtualFile> = object : Condition<VirtualFile> {
        override fun value(t: VirtualFile?): Boolean {
            val filePath = t?.path
            return excludedFoldersList?.let { !it.contains(filePath) } ?: true
        }
    }

    fun getComponent(): JComponent {
        configureExcludedFoldersList()

        val decorator = ToolbarDecorator.createDecorator(choosenFilesFoldersList).setAddAction { addFolder() }
            .setRemoveAction { removeSelectedFolders() }.disableUpDownActions()

        return FormBuilder.createFormBuilder().addComponent(decorator.createPanel()).panel
    }

    private fun configureExcludedFoldersList() {
        choosenFilesFoldersList.emptyText.setText("Optional")
        choosenFilesFoldersList.visibleRowCount = 3
    }

    private fun addFolder() {
        legacyFolderPath = annotationInformation?.legacyFolderPath
        val chosenFiles = FileChooser.chooseFiles(descriptor, project, null)
        for (file in chosenFiles) {
            val path = file.path
            if (!excludedFoldersListModel.items.contains(path)) {
                excludedFoldersListModel.add(path)
            }
        }
    }

    private fun removeSelectedFolders() {
        val selectedValues = choosenFilesFoldersList.selectedValuesList.toList()
        for (selectedValue in selectedValues) {
            excludedFoldersListModel.remove(selectedValue)
        }
    }
}
