package intellijmigrationplugin.ui.editor.visualiser


import AnnotationVisualiser
import com.intellij.openapi.editor.markup.MarkupModel
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.openapi.util.TextRange
import intellijmigrationplugin.annotationModel.AnnotationInformation

class SimpleAnnotationVisualiser: AnnotationVisualiser {

    private var sourcePath: String
    private var markup: MarkupModel

    constructor(sourcePath: String, markup: MarkupModel) {
        this.sourcePath = sourcePath
        this.markup = markup
    }

    override fun visualiseAnnotation() {

        markup.removeAllHighlighters()

        var currentAnnotationString = "Unmarked"
        val keywords = AnnotationInformation.instance?.keywords!!
        val doc = markup.document;

        for (i in 0..doc.lineCount-1) {

            val start = doc.getLineStartOffset(i)
            val end = doc.getLineEndOffset(i)
            val line = doc.getText(TextRange(start, end))

            val regexes = AnnotationInformation.instance?.keywords
                    ?.map { x -> Regex("//\\s*$x(\$|\\s)", RegexOption.IGNORE_CASE) }!!

            val regexEnd = Regex("//\\s*end(\$|\\s)", RegexOption.IGNORE_CASE)
            var j = 0;
            for (regex in regexes) {
                if (line.contains(regex)) {
                    currentAnnotationString = keywords[j]
                }
                j++
            }
            if (line.contains(regexEnd)) {
                currentAnnotationString = "Unmarked"
            }

            val myAttr = TextAttributes()
            if (currentAnnotationString != "Unmarked") {
                myAttr.backgroundColor = AnnotationInformation.instance?.markerRealColorMapping?.get(currentAnnotationString)
            }
            markup.addLineHighlighter(i, 0, myAttr)
        }

    }


}