package com.github.timheibel.intellijmigrationplugin.annotationModel

import java.awt.Color

class AnnotationInformation {


    private var instance: AnnotationInformation? = null
    val annotationFiles: HashMap<String, AnnotationFile> = HashMap()
    var legacyFolerPath: String = ""
    val markerColorMapping: HashMap<AnnotationType, Color> = HashMap()
    val commentTypeMapping: HashMap<String, String> = HashMap()

    private constructor() {

    }

    public fun getInstance(): AnnotationInformation {
        if (instance == null) {
            instance = AnnotationInformation()
        }
        return instance!!
    }


    fun getAnnotationFile(name: String): AnnotationFile {
        return AnnotationFile()
    }

    fun computeAnnotationRanges(): HashMap<AnnotationType, Int> {
        return HashMap()
    }
}