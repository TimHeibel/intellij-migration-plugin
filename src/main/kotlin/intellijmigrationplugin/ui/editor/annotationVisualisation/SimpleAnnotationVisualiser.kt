package intellijmigrationplugin.ui.editor.markervisualisation


import AnnotationVisualiser
import com.intellij.openapi.editor.markup.HighlighterTargetArea
import com.intellij.openapi.editor.markup.MarkupModel
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.openapi.util.TextRange
import intellijmigrationplugin.annotationModel.AnnotationInformation
import intellijmigrationplugin.annotationModel.markervisualisation.HighlightAnnotationFile
import intellijmigrationplugin.annotationModel.markervisualisation.HighlightAnnotationSnippet
import kotlinx.coroutines.*

class SimpleAnnotationVisualiser : AnnotationVisualiser {

    private var sourcePath: String
    private var markup: MarkupModel

    constructor(sourcePath: String, markup: MarkupModel) {
        this.sourcePath = sourcePath
        this.markup = markup
    }

    override fun updateAnnotationVisualisation(event: com.intellij.openapi.editor.event.DocumentEvent) {
        otherVisulations()
    }

    override fun visualiseAnnotation() {
        otherVisulations()
    }

    private fun otherVisulations() {
        runBlocking {
            val annotationFile = HighlightAnnotationFile(sourcePath, markup.document)
            val snippets = annotationFile.computeSnippets()
            highlightEditor(snippets)
        }
    }

    private suspend fun highlightEditor(snippets: MutableList<HighlightAnnotationSnippet>) {
        markup.removeAllHighlighters()
        for (snippet in snippets) {
            val startOffset = markup.document.getLineStartOffset(snippet.start)
            val endOffset = markup.document.getLineStartOffset(snippet.end - 1)
            val myAttr = TextAttributes()
            myAttr.backgroundColor = AnnotationInformation.instance?.markerRealColorMapping?.get(snippet.type)
            markup.addRangeHighlighter(startOffset, endOffset, 0, myAttr, HighlighterTargetArea.LINES_IN_RANGE)
            yield()
        }
    }

    override fun turnVisualisationOn() {
        otherVisulations()
    }

    override fun turnVisualisationOff() {
        markup.removeAllHighlighters()
    }

    private fun visualizeCoroutine() {
        GlobalScope.launch(Dispatchers.Main) {
            markup.removeAllHighlighters()
            yield()
            var currentAnnotationString = "Unmarked"
            val keywords = AnnotationInformation.instance?.keywords!!
            val doc = markup.document;

            val regexes = AnnotationInformation.instance?.keywords
                    ?.map { x -> Regex("//\\s*$x(\$|\\s)", RegexOption.IGNORE_CASE) }!!

            val regexEnd = Regex("//\\s*end(\$|\\s)", RegexOption.IGNORE_CASE)

            for (i in 0..doc.lineCount - 1) {

                val start = doc.getLineStartOffset(i)
                val end = doc.getLineEndOffset(i)
                val line = doc.getText(TextRange(start, end))

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
                yield()
            }

        }
    }


}