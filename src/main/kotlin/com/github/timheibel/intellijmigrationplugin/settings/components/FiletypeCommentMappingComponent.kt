package com.github.timheibel.intellijmigrationplugin.settings.components

import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.table.JBTable
import com.intellij.util.ui.FormBuilder
import javax.swing.JPanel
import javax.swing.event.TableModelEvent
import javax.swing.table.DefaultTableModel

class FiletypeCommentMappingComponent {
    var fileTypeCommentMapping = mutableMapOf<String, String>()
        set(value) {
            field = value
            initializeTableModelData()
        }
    private val tableModel = DefaultTableModel(arrayOf(arrayOf("", "")), arrayOf("Filetype", "Comment Type"))
    private val table = JBTable(tableModel)
    private val tableScrollPane = JBScrollPane(table)

    fun getComponent(): JPanel {
        configureTableModelListener()
        return FormBuilder.createFormBuilder()
            .addLabeledComponentFillVertically("Filetype-Comment mapping", tableScrollPane)
            .panel
    }

    private fun configureTableModelListener() {
        tableModel.addTableModelListener { event ->
            if (event.type == TableModelEvent.UPDATE) {
                handleTableModelUpdate()
            }
        }
    }

    private fun handleTableModelUpdate() {
        val lastRow = tableModel.rowCount - 1
        val lastFileType = tableModel.getValueAt(lastRow, 0) as? String
        val lastCommentType = tableModel.getValueAt(lastRow, 1) as? String
        if (!lastFileType.isNullOrBlank() || !lastCommentType.isNullOrBlank()) {
            tableModel.addRow(arrayOf("", ""))
        }

        val rowsToRemove = mutableListOf<Int>()
        for (i in tableModel.rowCount - 2 downTo 0) {
            val fileType = tableModel.getValueAt(i, 0) as String
            val commentType = tableModel.getValueAt(i, 1) as String
            if (fileType.isEmpty() && commentType.isEmpty()) {
                tableModel.removeRow(i)
                rowsToRemove.add(i)
            } else {
                fileTypeCommentMapping[fileType] = commentType
            }
        }

        rowsToRemove.forEach {
            fileTypeCommentMapping.remove(tableModel.getValueAt(it, 0) as String)
        }
    }
    private fun initializeTableModelData() {
        // Clear existing rows
        while (tableModel.rowCount > 0) {
            tableModel.removeRow(0)
        }
        // Add new rows based on the loaded data
        fileTypeCommentMapping.forEach { (fileType, commentType) ->
            tableModel.addRow(arrayOf(fileType, commentType))
        }
        // Add empty row for insertion
        tableModel.addRow(arrayOf("", ""))
    }
}