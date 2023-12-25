package intellijmigrationplugin.settings.components

import com.intellij.openapi.project.Project
import com.intellij.ui.ColorPicker
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.table.JBTable
import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.Icon
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTable
import javax.swing.table.DefaultTableCellRenderer
import javax.swing.table.DefaultTableModel

class KeywordColorMappingComponent(private val project: Project) {

    var tableModel = DefaultTableModel(arrayOf(arrayOf("", "#ffffff")), arrayOf("Keyword", "Color"))
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

        // Set custom renderer for the second column
        table.columnModel.getColumn(1).cellRenderer = ColorCellRenderer()

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

    private class ColorCellRenderer : DefaultTableCellRenderer() {
        override fun getTableCellRendererComponent(
            table: JTable?,
            value: Any?,
            isSelected: Boolean,
            hasFocus: Boolean,
            row: Int,
            column: Int
        ): Component {
            val hexColor = value as? String ?: ""
            val color = Color.decode(hexColor)
            val icon = ColorIcon(color)
            val label = JLabel(icon)
            label.toolTipText = hexColor
            return label
        }
    }

    private class ColorIcon(private val color: Color) : Icon {
        override fun paintIcon(c: Component?, g: Graphics?, x: Int, y: Int) {
            val g2d = g as Graphics2D
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

            // White Border
            g2d.color = Color.WHITE
            g2d.fillRoundRect(x, y, iconWidth, iconHeight, 4, 4)

            // Fill Border
            g2d.color = color
            g2d.fillRoundRect(x + 2, y + 2, iconWidth - 4, iconHeight - 4, 4, 4)
        }

        override fun getIconWidth() = 16

        override fun getIconHeight() = 16
    }
}
