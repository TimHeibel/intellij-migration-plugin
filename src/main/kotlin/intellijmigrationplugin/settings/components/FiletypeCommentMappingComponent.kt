package intellijmigrationplugin.settings.components

import com.intellij.openapi.project.Project
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.table.JBTable
import javax.swing.JPanel
import javax.swing.table.DefaultTableModel

class FiletypeCommentMappingComponent(private val project: Project) {

    var tableModel = DefaultTableModel(arrayOf(arrayOf("", "")), arrayOf("Filetype", "Comment Type"))
    var table = JBTable(tableModel)

    fun getComponent(): JPanel {
        configureTableDesign()

        val decorator = ToolbarDecorator.createDecorator(table).setAddAction { addEmptyRow() }
            .setRemoveAction { removeSelectedRows() }

        return decorator.createPanel()
    }

    private fun configureTableDesign() {
        table.emptyText.setText("Optional")
        table.isStriped = true
    }

    private fun addEmptyRow() {
        tableModel.addRow(arrayOf("", ""))
        tableModel.fireTableDataChanged()

        // Focus the first cell of the newly added row
        val newRow = tableModel.rowCount - 1
        val firstColumn = 0
        table.editCellAt(newRow, firstColumn)
        table.requestFocusInWindow()
    }

    private fun removeSelectedRows() {
        tableModel.removeRow(table.selectedRow)
        tableModel.fireTableDataChanged()
    }

    fun initializeTableData(mapping: MutableList<Pair<String, String>>) {
        // Remove all rows
        while (tableModel.getRowCount() > 0) {
            tableModel.removeRow(0);
        }
        for (pair in mapping) {
            tableModel.addRow(arrayOf(pair.first, pair.second))
        }
    }
}
