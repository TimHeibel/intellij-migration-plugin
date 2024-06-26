package intellijmigrationplugin.ui.editor.annotationVisualisation

import com.intellij.openapi.editor.markup.HighlighterTargetArea
import com.intellij.openapi.editor.markup.MarkupModel
import com.intellij.openapi.editor.markup.TextAttributes
import intellijmigrationplugin.annotationModel.AnnotationInformation
import intellijmigrationplugin.annotationModel.AnnotationSnippet
import kotlinx.coroutines.*

class SimpleAnnotationVisualiser(private var sourcePath: String, private var markup: MarkupModel) : AnnotationVisualiser {

    override fun updateAnnotationVisualisation(snippets: MutableList<AnnotationSnippet>) {
        runBlocking {
            highlightEditor(snippets)
        }
    }

    override fun visualiseAnnotation(snippets: MutableList<AnnotationSnippet>) {
        runBlocking {
            highlightEditor(snippets)
        }
    }

    private suspend fun highlightEditor(snippets: MutableList<AnnotationSnippet>) {
        markup.removeAllHighlighters()
        for (snippet in snippets) {
            val startOffset = markup.document.getLineStartOffset(snippet.start)
            val endOffset = markup.document.getLineStartOffset(snippet.end)
            val myAttr = TextAttributes()
            myAttr.backgroundColor = AnnotationInformation.instance?.markerRealColorMapping?.get(snippet.type)
            markup.addRangeHighlighter(startOffset, endOffset, 0, myAttr, HighlighterTargetArea.LINES_IN_RANGE)
            yield()
        }
    }

    override fun turnVisualisationOn(snippets: MutableList<AnnotationSnippet>) {
        runBlocking {
            highlightEditor(snippets)
        }
    }

    override fun turnVisualisationOff() {
        markup.removeAllHighlighters()
    }

}