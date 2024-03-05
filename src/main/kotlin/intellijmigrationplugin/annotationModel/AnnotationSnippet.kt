package intellijmigrationplugin.annotationModel

class AnnotationSnippet(var start: Int, var end: Int, var hasEnd: Boolean, var type: String, var addInfo: String = "") {

    companion object {
        internal fun fromStartLine(line : String, commentStart: String, start: Int = 0, end: Int = 0, hasEnd: Boolean = false) : AnnotationSnippet? {

            val regex = Regex("^\\h*${Regex.escape(commentStart)}" +
                    "(?<annotationType>(\\S+))\\h*(?<addInfo>((\\S|\\h+\\S)+)|$)(\$|(\\s+\$))", RegexOption.IGNORE_CASE)

            val match = regex.matchEntire(line) ?: return null

            val type = match.groups["annotationType"]!!.value

            if(!(AnnotationInformation.instance?.markerColorMapping?.keys?.contains(type) ?: return null)) {
                return null
            }

            return AnnotationSnippet(start, end, hasEnd, type, match.groups["addInfo"]!!.value)
        }
    }

    internal fun hasCollision(annotation: AnnotationSnippet) : CollisionCode? {
        if (annotation.inRange(start) && annotation.inRange(end)) {
            return CollisionCode.COMPLETE_INSIDE
        }

        if (annotation.inRange(start)) {
            return CollisionCode.START_INSIDE
        }

        if (annotation.inRange(end)) {
            return CollisionCode.END_INSIDE
        }

        if (inRange(annotation.start)) {
            return CollisionCode.SURROUNDING
        }

        return null
    }

    internal fun hasCollision(startLine : Int, endLine : Int) : CollisionCode? {
        return hasCollision(AnnotationSnippet(startLine, endLine, true, ""))
    }

    internal fun createStartLine(commentStart: String) : String {
        return "$commentStart$type $addInfo"
    }

    var newFunctionName: String? = null

    fun getRange(): Int {
        return end - start
    }

    fun inRange(line: Int): Boolean {
        return line in start..end
    }

}

internal enum class CollisionCode {
    START_INSIDE,
    END_INSIDE,
    COMPLETE_INSIDE,
    SURROUNDING
}