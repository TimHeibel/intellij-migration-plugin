package intellijmigrationplugin.settings.components

import com.intellij.openapi.project.Project
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.table.JBTable
import com.intellij.util.ui.UIUtil
import java.awt.Color
import java.util.*
import javax.swing.BorderFactory
import javax.swing.DefaultCellEditor
import javax.swing.JPanel
import javax.swing.JTextField
import javax.swing.table.DefaultTableModel

class FiletypeCommentMappingComponent(private val project: Project) {

     val tableModel = DefaultTableModel(arrayOf(arrayOf("", "")), arrayOf("Filetype", "Comment Type"))
    private var table = JBTable(tableModel)

    fun getComponent(): JPanel {
        configureTableDesign()

        val decorator = ToolbarDecorator.createDecorator(table).setAddAction { addEmptyRow() }
            .setRemoveAction { removeSelectedRows() }

        return decorator.createPanel()
    }

    private fun configureTableDesign() {
        table.emptyText.setText("Optional")
        table.isStriped = true

        val fileTypeColumn = table.columnModel.getColumn(0)
        fileTypeColumn.cellEditor = createCellEditor()

    }

    private fun createCellEditor(): DefaultCellEditor {
        val textField = JTextField()
        return object : DefaultCellEditor(textField) {
            override fun isCellEditable(event: java.util.EventObject): Boolean {
                // Allow editing only if the current cell is not the last cell of the last row
                val row = table.editingRow
                val column = table.editingColumn
                return row < table.rowCount - 1 || column < table.columnCount - 1
            }
            override fun stopCellEditing(): Boolean {
                val newFiletype = textField.text.trim()

                if (!validateCellContent(newFiletype)) {
                    setWarningBorder(textField, "Filetype must start with '.'", Color(250, 158, 158))
                    return false
                } else if (filetypeExists(newFiletype, table.editingRow)) {
                    setWarningBorder(textField, "Filetype already exists in the table",Color(0xF6D89F))
                    return false
                } else {
                    textField.border = UIUtil.getTableFocusCellHighlightBorder()
                    textField.toolTipText = null
                    return super.stopCellEditing()
                }
            }


            override fun shouldSelectCell(anEvent: java.util.EventObject): Boolean {
                // Allow selecting the cell only if the validation is successful
                return validateCellContent(textField.text) && !filetypeExists(textField.text.trim(), table.editingRow)
            }
        }
    }

    private fun setWarningBorder(textField: JTextField, tooltip: String, color: Color) {
        textField.border = BorderFactory.createLineBorder(color)
        textField.toolTipText = tooltip
    }

    private fun filetypeExists(filetype: String, currentRow: Int): Boolean {
        val trimmedFiletype = filetype.trim()
        for (row in 0 until tableModel.rowCount) {
            if (row != currentRow) { // Skip the current row
                val cellContent = tableModel.getValueAt(row, 0).toString().trim()
                if (cellContent.equals(trimmedFiletype, ignoreCase = true)) {
                    return true
                }
            }
        }
        return false
    }

    private fun validateCellContent(content: String): Boolean {
        return content.startsWith(".")
    }

    private fun addEmptyRow() {
        tableModel.addRow(arrayOf("", ""))
        tableModel.fireTableDataChanged()

        // Focus the first cell of the newly added row
        val newRow = tableModel.rowCount - 1
        val firstColumn = 0
        table.requestFocusInWindow()
        table.scrollRectToVisible(table.getCellRect(newRow, 0, true))
        table.editCellAt(newRow, firstColumn, EventObject(table))
    }

    private fun removeSelectedRows() {
        tableModel.removeRow(table.selectedRow)
        tableModel.fireTableDataChanged()
    }

    fun initializeTableData(mapping: MutableList<Pair<String, String>>) {
        // Remove all rows
        while (tableModel.rowCount > 0) {
            tableModel.removeRow(0)
        }
        for (pair in mapping) {
            tableModel.addRow(arrayOf(pair.first, pair.second))
        }
    }
}
