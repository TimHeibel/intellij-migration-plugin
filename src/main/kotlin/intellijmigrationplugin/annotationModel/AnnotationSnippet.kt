package intellijmigrationplugin.annotationModel


/**
 * Start line is inclusive
 * End line is inclusive
 */
open class AnnotationSnippet {

    var start: Int
    var end: Int
    var type: String


    constructor(start: Int, end: Int, type: String) {
        this.start = start
        this.end = end
        this.type = type
    }


    constructor(): this(0, 0, "MIGRATED" )


    fun getRange(): Int {
        return end - start
    }

    fun inRange(line: Int): Boolean {
        return line in start..end
    }


    override fun toString(): String {
        return "Startline: $start, Endline: $end, Type: $type"
    }

}