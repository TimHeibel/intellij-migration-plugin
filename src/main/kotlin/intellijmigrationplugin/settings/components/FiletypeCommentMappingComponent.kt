package intellijmigrationplugin.settings.components

import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.table.JBTable
import javax.swing.JPanel
import javax.swing.table.DefaultTableModel

class FiletypeCommentMappingComponent {

    private val tableModel = DefaultTableModel(arrayOf(arrayOf("", "")), arrayOf("Filetype", "Comment Type"))
    private val table = JBTable(tableModel)

    fun getComponent(): JPanel {
        configureTableDesign()

        val decorator = ToolbarDecorator.createDecorator(table)
            .setAddAction { addEmptyRow() }
            .setRemoveAction { removeSelectedRows() }

        return decorator.createPanel()
    }

    private fun configureTableDesign() {
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
}
