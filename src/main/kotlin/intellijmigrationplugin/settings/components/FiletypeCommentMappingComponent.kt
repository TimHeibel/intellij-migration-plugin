package intellijmigrationplugin.settings.components

import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.table.JBTable
import com.intellij.util.ui.UIUtil
import java.awt.Color
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.swing.BorderFactory
import javax.swing.DefaultCellEditor
import javax.swing.JPanel
import javax.swing.JTextField
import javax.swing.table.DefaultTableModel

@Suppress("UseJBColor")
class FiletypeCommentMappingComponent {

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
        table.visibleRowCount = 3

        val fileTypeColumn = table.columnModel.getColumn(0)
        fileTypeColumn.cellEditor = createCellEditor()

        // Add KeyListener to the last cell of the last row
        val lastColumn = tableModel.columnCount - 1
        val lastCell = table.getCellEditor(tableModel.rowCount - 1, lastColumn) as DefaultCellEditor
        val lastTextField = lastCell.component as JTextField

        lastTextField.addKeyListener(object : KeyAdapter() {
            override fun keyPressed(e: KeyEvent) {
                if (e.keyCode == KeyEvent.VK_TAB) {
                    val lastRow = tableModel.rowCount - 1
                    if (table.editingRow == lastRow && table.editingColumn == lastColumn) {
                        addEmptyRow()
                        e.consume() // Consume Tab key event to prevent default behavior
                    }
                }
            }
        })
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
                    setWarningBorder(textField, "Filetype already exists in the table", Color(0xF6D89F))
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
        val borderThickness = 4
        val warningBorder = BorderFactory.createLineBorder(color, borderThickness)
        textField.border = warningBorder
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
        if (table.isEditing) {
            table.cellEditor.stopCellEditing()
        }

        tableModel.addRow(arrayOf("", ""))
        tableModel.fireTableDataChanged()

        // Focus the first cell of the newly added row
        val newRow = tableModel.rowCount - 1
        val firstColumn = 0
        table.requestFocusInWindow()
        table.scrollRectToVisible(table.getCellRect(newRow, 0, true))
        table.changeSelection(newRow, firstColumn, false, false)
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
