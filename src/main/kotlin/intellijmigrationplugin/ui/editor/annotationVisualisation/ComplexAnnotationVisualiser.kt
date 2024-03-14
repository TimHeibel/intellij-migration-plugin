package intellijmigrationplugin.ui.editor.annotationVisualisation

import AnnotationVisualiser
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.markup.HighlighterTargetArea
import com.intellij.openapi.editor.markup.MarkupModel
import com.intellij.openapi.editor.markup.RangeHighlighter
import com.intellij.openapi.editor.markup.TextAttributes
import intellijmigrationplugin.annotationModel.*
import intellijmigrationplugin.annotationModel.markervisualisation.HighlightAnnotationFile
import intellijmigrationplugin.annotationModel.markervisualisation.HighlightAnnotationSnippet
import intellijmigrationplugin.annotationModel.markervisualisation.RangeHighlightUpdate
import kotlinx.coroutines.runBlocking
import java.util.*
import kotlin.collections.HashMap

class ComplexAnnotationVisualiser: AnnotationVisualiser {

    private var sourcePath: String
    private var markup: MarkupModel
    private var annotationFile: HighlightAnnotationFile
    private var rangeHighlighter: HashMap<UUID, RangeHighlighter>

    constructor(sourcePath: String, markup: MarkupModel) {
        this.sourcePath = sourcePath
        this.markup = markup
        this.annotationFile = HighlightAnnotationFile(sourcePath, markup.document)
        this.rangeHighlighter = HashMap()
    }


    override fun updateAnnotationVisualisation(snippets: MutableList<HighlightAnnotationSnippet>) {
        //val lineRange = annotationFile.handleEvent(event)
        //updateEditorHighlighting(lineRange)
    }


    override fun visualiseAnnotation(snippets: MutableList<HighlightAnnotationSnippet>) {
        runBlocking {
            val snippets = annotationFile.computeSnippets()
            highlightEditor(snippets)
        }
    }

    override fun turnVisualisationOn(snippets: MutableList<HighlightAnnotationSnippet>) {
        //visualiseAnnotation()
    }

    override fun turnVisualisationOff() {
        markup.removeAllHighlighters()
    }

    private fun highlightEditor(snippets: MutableList<HighlightAnnotationSnippet>) {
        markup.removeAllHighlighters()
        for (snippet in snippets) {
            val startOffset = markup.document.getLineStartOffset(snippet.start)
            val endOffset = markup.document.getLineStartOffset(snippet.end-1)
            val myAttr = TextAttributes()
            myAttr.backgroundColor = AnnotationInformation.instance?.markerRealColorMapping?.get(snippet.type)
            val rangeHighlight = markup.addRangeHighlighter(startOffset, endOffset, 0, myAttr, HighlighterTargetArea.LINES_IN_RANGE)
            rangeHighlighter[snippet.id] = rangeHighlight
        }
    }

    private fun updateEditorHighlighting(update: RangeHighlightUpdate) {

        for (i in 0..update.removed.count()-1) {
            val currentSnippet = update.removed[i]
            val rangeHighlight = rangeHighlighter[currentSnippet.id]!!
            markup.removeHighlighter(rangeHighlight)
        }

        for (i in 0..update.added.count()-1) {
            val currentSnippet = update.added[i]

            val startOffset = markup.document.getLineStartOffset(currentSnippet.start)
            val endOffset = markup.document.getLineStartOffset(currentSnippet.end)

            val myAttr = TextAttributes()
            myAttr.backgroundColor = AnnotationInformation.instance?.markerRealColorMapping?.get(currentSnippet.type)
            val rangeHighlight = markup.addRangeHighlighter(startOffset, endOffset, 0, myAttr, HighlighterTargetArea.LINES_IN_RANGE)
            rangeHighlighter[currentSnippet.id] = rangeHighlight
        }

        for (i in 0..update.changed.count()-1) {
            val currentSnippet = update.changed[i]
            val oldHighlight = rangeHighlighter[currentSnippet.id]!!
            markup.removeHighlighter(oldHighlight)

            val startOffset = markup.document.getLineStartOffset(currentSnippet.start)
            val endOffset = markup.document.getLineStartOffset(currentSnippet.end)

            val myAttr = TextAttributes()
            myAttr.backgroundColor = AnnotationInformation.instance?.markerRealColorMapping?.get(currentSnippet.type)
            val rangeHighlight = markup.addRangeHighlighter(startOffset, endOffset, 0, myAttr, HighlighterTargetArea.LINES_IN_RANGE)
            rangeHighlighter[currentSnippet.id] = rangeHighlight
        }



    }



}