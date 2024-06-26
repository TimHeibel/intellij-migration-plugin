package intellijmigrationplugin.annotationModel


/**
 * Represents an annotation snippet within a document
 *
 * @property start The start position of the annotation in the document.
 * @property end The end position of the annotation in the document.
 * @property hasEnd Indicates whether the annotation has an explicit end.
 * @property type The type of the annotation.
 * @property addInfo Additional information associated with the annotation.
 * @constructor Creates a new instance of [AnnotationSnippet] with the specified parameters.
 */
open class AnnotationSnippet(var start: Int, var end: Int, var hasEnd: Boolean, var type: String, var addInfo: String = "") {


    companion object {
        /**
         * Creates an [AnnotationSnippet] from a given [line] and under consideration
         * of the specific [commentStart].
         *
         * @param line The line containing the annotation.
         * @param commentStart The string to start the annotation comment with.
         * @param start The start position of the annotation in the document.
         * @param end The end position of the annotation in the document.
         * @param hasEnd Indicates whether the annotation has an explicit end.
         * @return An [AnnotationSnippet] object if successful, otherwise null.
         */
        internal fun fromStartLine(line : String, commentStart: String, start: Int = 0, end: Int = 0, hasEnd: Boolean = false) : AnnotationSnippet? {

            val regex = Regex("^\\h*${Regex.escape(commentStart)}\\h*" +
                    "(?<annotationType>(\\S+))\\h*(?<addInfo>((\\S|\\h+\\S)+)|$)(\$|(\\s+\$))", RegexOption.IGNORE_CASE)

            val match = regex.matchEntire(line) ?: return null

            val type = match.groups["annotationType"]!!.value

            if(!(AnnotationInformation.instance?.markerColorMapping?.keys?.map { key -> key.lowercase() }?.contains(type.lowercase()) ?: return null)) {
                return null
            }

            return AnnotationSnippet(start, end, hasEnd, type, match.groups["addInfo"]!!.value)
        }
    }

    /**
     * Checks for a collision with another [AnnotationSnippet].
     *
     * @param annotation The other annotation to check for a collision.
     * @return A [CollisionCode] indicating the type of collision if there is one, otherwise null.
     */
    internal fun hasCollision(annotation: AnnotationSnippet) : CollisionCode? {
        if (annotation.contains(start) && annotation.contains(end)) {
            return CollisionCode.COMPLETE_INSIDE
        }

        if (annotation.contains(start)) {
            return CollisionCode.START_INSIDE
        }

        if (annotation.contains(end)) {
            return CollisionCode.END_INSIDE
        }

        if (contains(annotation.start)) {
            return CollisionCode.SURROUNDING
        }

        return null
    }

    /**
     * Checks for a collision with an annotation defined by [startLine] and [endLine].
     *
     * @param startLine The start line of the annotation.
     * @param endLine The end line of the annotation.
     * @return A [CollisionCode] indicating the type of collision if there is one, otherwise null.
     */
    internal fun hasCollision(startLine : Int, endLine : Int) : CollisionCode? {
        return hasCollision(AnnotationSnippet(startLine, endLine, true, ""))
    }

    /**
     * Creates an annotation-start for the annotation using the specified [commentStart].
     *
     * @param commentStart The string to start the annotation comment with.
     * @return The start line representation of the annotation.
     */
    internal fun createStartLine(commentStart: String) : String {
        return "$commentStart$type $addInfo"
    }

    fun length(): Int {
        return end - start
    }

    fun contains(line: Int): Boolean {
        return line in start..end
    }

    fun lineIsStartLine(line: Int): Boolean {
        return line == start
    }

    fun lineIsEndLine(line: Int): Boolean {
        return line == end
    }

    fun lineIsAnnotationLine(line: Int): Boolean {
        return lineIsStartLine(line) || (hasEnd && lineIsEndLine(line))
    }

    override fun toString(): String {
        return "Startline: $start, Endline: $end, Type: $type"
    }

    /**
     * Checks if the given [annotation] is similar to the current [AnnotationSnippet].
     * Two annotations are considered similar if they have the same type and additional information.
     *
     * @param annotation The annotation snippet to compare.
     * @return `true` if the annotations are similar, `false` otherwise.
     */
    internal fun isSimilar(annotation: AnnotationSnippet) : Boolean {
        return this.type == annotation.type && this.addInfo == annotation.addInfo
    }

}


/**
 * Enum class representing different collision scenarios between annotations.
 */
internal enum class CollisionCode {
    START_INSIDE,
    END_INSIDE,
    COMPLETE_INSIDE,
    SURROUNDING
}