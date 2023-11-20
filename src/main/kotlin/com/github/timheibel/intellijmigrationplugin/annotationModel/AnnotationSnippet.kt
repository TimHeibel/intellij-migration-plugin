package com.github.timheibel.intellijmigrationplugin.annotationModel

import java.util.Date


class AnnotationSnippet {

    var start: Int
    var end: Int
    var type: AnnotationType
    var author: String
    var date: Date
    var newFunctionName: String

    constructor(start: Int, end: Int, type: AnnotationType, author: String, date: Date, newFunctionName: String) {
        this.start = start
        this.end = end
        this.type = type
        this.author = author
        this.date = date
        this.newFunctionName = newFunctionName
    }

    constructor(): this(0, 0, AnnotationType.MIGRATED,
            "Max Muster", Date(213124124L), "migratedFunction")


    fun getRange(): Int {
        return end - start
    }

    fun inRange(line: Int): Boolean {
        return line in start..end
    }


}