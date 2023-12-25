package intellijmigrationplugin.settings.components

import com.intellij.openapi.project.Project
import com.intellij.ui.ColorPicker
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.table.JBTable
import java.awt.Color
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JPanel
import javax.swing.table.DefaultTableModel

class KeywordColorMappingComponent(private val project: Project) {

    var tableModel = DefaultTableModel(arrayOf(arrayOf("", "#ffffff")), arrayOf("Filetype", "Comment Type"))
    var table = JBTable(tableModel)

    fun getComponent(): JPanel {
        configureTableDesign()

        val decorator = ToolbarDecorator.createDecorator(table).setAddAction { addEmptyRow() }
            .setRemoveAction { removeSelectedRows() }

        // Add mouse listener to open color chooser on column 1 cell click
        table.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                if (e.clickCount == 1 && table.columnAtPoint(e.point) == 1) {
                    openColorChooser(table.selectedRow, 1)
                }
            }
        })

        return decorator.createPanel()
    }

    private fun configureTableDesign() {
        table.emptyText.setText("Optional")
        table.isStriped = true
    }

    private fun addEmptyRow() {
        tableModel.addRow(arrayOf("", "#fffff"))
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

    private fun openColorChooser(row: Int, column: Int) {
        val currentColor = tableModel.getValueAt(row, column) as? String ?: ""

        val selectedColor =
            ColorPicker.showDialog(table, "Choose Color", Color.decode(currentColor), false, null, false)
        if (selectedColor != null) {
            tableModel.setValueAt("#" + Integer.toHexString(selectedColor.rgb).substring(2), row, column)
        }
    }

    fun initializeTableData(mapping: MutableList<Pair<String, String>>) {
        tableModel.removeRow(0)
        for (pair in mapping) {
            tableModel.addRow(arrayOf(pair.first, pair.second))
        }
    }
}

