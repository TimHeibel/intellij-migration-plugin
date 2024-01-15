package intellijmigrationplugin.settings.components

import com.intellij.ui.ColorPicker
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.table.JBTable
import intellijmigrationplugin.settings.utils.ColorUtils
import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.Icon
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTable
import javax.swing.table.DefaultTableCellRenderer
import javax.swing.table.DefaultTableModel

class KeywordColorMappingComponent {

    var tableModel = object : DefaultTableModel(
        arrayOf(
            arrayOf("MIGRATED", "#ffffff"),
        ), arrayOf("Keyword", "Color")
    ) {
        override fun isCellEditable(row: Int, column: Int): Boolean {
            // Make the second column not editable, as this could lead to bugs, when tabbing into the content
            return column != 1
        }
    }
    var table = JBTable(tableModel)

    fun getComponent(): JPanel {
        configureTableDesign()

        val decorator = ToolbarDecorator.createDecorator(table).setAddAction { addEmptyRow() }
            .setRemoveAction { removeSelectedRows() }

        // Add mouse listener to open color chooser on column 1 cell click
        table.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                if (e.clickCount == 1 && table.columnAtPoint(e.point) == 1) {
                    openColorChooser(table.selectedRow)
                }
            }
        })

        // Set custom renderer for the second column
        table.columnModel.getColumn(1).cellRenderer = ColorCellRenderer()

        val secondColumn = table.columnModel.getColumn(1)
        secondColumn.preferredWidth = 100

        return decorator.createPanel()
    }

    private fun configureTableDesign() {
        table.emptyText.setText("Optional")
        table.isStriped = true
    }

    private fun addEmptyRow() {
        // Add new row with white color at 50% opacity (#80FFFFFF)
        tableModel.addRow(arrayOf("", "#80FFFFFF"))
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
        while (tableModel.rowCount > 0) {
            tableModel.removeRow(0)
        }
        for (pair in mapping) {
            tableModel.addRow(arrayOf(pair.first, pair.second))
        }
    }

    private fun openColorChooser(row: Int) {
        val currentColorStr = tableModel.getValueAt(row, 1) as String? ?: "#FFFFFFFF"
        val currentColor = ColorUtils.decodeColor(currentColorStr)

        val selectedColor = ColorPicker.showDialog(table, "Choose Color", currentColor, true, null, true)
        if (selectedColor != null) {
            val hex = String.format(
                "#%02x%02x%02x%02x", selectedColor.alpha, selectedColor.red, selectedColor.green, selectedColor.blue
            )
            tableModel.setValueAt(hex, row, 1)
        }
    }

    @Suppress("UseJBColor")
    private class ColorCellRenderer : DefaultTableCellRenderer() {
        override fun getTableCellRendererComponent(
            table: JTable?, value: Any?, isSelected: Boolean, hasFocus: Boolean, row: Int, column: Int
        ): Component {
            val hexColor = value as? String ?: "#FFFFFFFF"
            val color = ColorUtils.decodeColor(hexColor)
            val icon = ColorIcon(color)
            // Get color name based on RGB, ignoring Alpha
            val colorName = ColorUtils.getColorNameFromRgb(color.red, color.green, color.blue)

            val panel = JPanel(FlowLayout(FlowLayout.LEFT)).apply {
                accessibleContext.accessibleName = "Color Picker"
            }

            panel.add(JLabel(icon))
            panel.add(JLabel(colorName))

            if (isSelected) {
                panel.background = table?.selectionBackground
                panel.foreground = table?.selectionForeground
            } else {
                panel.background = table?.background
                panel.foreground = table?.foreground
            }

            return panel
        }
    }


    @Suppress("UseJBColor")
    private class ColorIcon(private val color: Color) : Icon {

        companion object {
            private const val ICON_WIDTH = 16
            private const val ICON_HEIGHT = 16
            private const val BORDER_SIZE = 2
            private const val CORNER_ROUNDNESS = 4
        }

        override fun paintIcon(c: Component?, g: Graphics?, x: Int, y: Int) {
            val g2d = g as Graphics2D
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

            // White Border
            g2d.color = Color.WHITE
            g2d.fillRoundRect(x, y, ICON_WIDTH, ICON_HEIGHT, CORNER_ROUNDNESS, CORNER_ROUNDNESS)

            // Fill Border
            g2d.color = color
            g2d.fillRoundRect(x + BORDER_SIZE, y + BORDER_SIZE, ICON_WIDTH - 2 * BORDER_SIZE, ICON_HEIGHT - 2 * BORDER_SIZE, CORNER_ROUNDNESS, CORNER_ROUNDNESS)
        }

        override fun getIconWidth() = ICON_WIDTH
        override fun getIconHeight() = ICON_HEIGHT
    }



}
