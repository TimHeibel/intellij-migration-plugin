package com.github.timheibel.intellijmigrationplugin.ui.dialog

import com.github.timheibel.intellijmigrationplugin.annotationModel.AnnotationType
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.dsl.builder.*
import javax.swing.JComponent

class AnnotationDialog : DialogWrapper(true) {

    private lateinit var data : DataModel

    val annotationType : AnnotationType
        get() = data.annotationType
    val annotationDialog : String
        get() = data.annotationComment

    init {
        title = "Annotation Menu"
        init()
    }

    override fun createCenterPanel(): JComponent? {

        val data = DataModel()
        this.data = data

        return panel {
            buttonsGroup("Select Annotation-Type:") {
                row {
                    radioButton("MIGRATED", AnnotationType.MIGRATED).selected
                }
                row {
                    radioButton("LATER", AnnotationType.LATER)
                }
                row {
                    radioButton("UNUSED", AnnotationType.UNUSED)
                }
            }.bind({data.annotationType}, {data.annotationType = it})

            row("Enter Annotation-Comment:") {
                textField().bindText(data :: annotationComment)
            }
        }
    }
}

data class DataModel(
    var annotationType : AnnotationType = AnnotationType.MIGRATED,
    var annotationComment: String = "",
)