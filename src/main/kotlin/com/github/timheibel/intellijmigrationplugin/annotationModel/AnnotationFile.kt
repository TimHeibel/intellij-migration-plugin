package com.github.timheibel.intellijmigrationplugin.annotationModel

import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class AnnotationFile {


    var fileName: String
    var filePathRelativeToLegacyFoler: String
    var commentType: String
    var annotationSnippets: ArrayList<AnnotationSnippet>
    var endLine: Int

    constructor(fileName: String, filePathRelativeToLegacyFoler: String, commentType: String,
                annotationSnippets: ArrayList<AnnotationSnippet>, endLine: Int) {
        this.fileName = fileName
        this.filePathRelativeToLegacyFoler = filePathRelativeToLegacyFoler
        this.commentType = commentType
        this.annotationSnippets = annotationSnippets
        this.endLine = endLine
    }

    constructor(): this("TestFile", "/src/main/", "//",
            ArrayList(),83)


    fun computeRangePerAnnotationType(type: AnnotationType): Int {
        return 0;
    }

    fun computeAnnotationRanges(): HashMap<AnnotationType, Int> {
        val annotationRanges = HashMap<AnnotationType, Int>()
        return annotationRanges
    }

}