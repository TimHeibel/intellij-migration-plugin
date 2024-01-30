package intellijmigrationplugin.annotationModel.`markervisualisation+`

import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.event.DocumentEvent
import intellijmigrationplugin.annotationModel.AnnotationDetection
import intellijmigrationplugin.annotationModel.AnnotationFile
import kotlinx.coroutines.yield

class HighlightAnnotationFile: AnnotationFile {

    var snippets: MutableList<HighlightAnnotationSnippet>
    private var annotations: MutableList<Pair<Int, String>>

    constructor(fileName: String, document: Document): super(fileName, document) {
        snippets = mutableListOf()
        annotations = mutableListOf()
    }

    suspend fun computeSnippets(): MutableList<HighlightAnnotationSnippet> {
        val splits = fileName.split(".")
        val fileType = "."+splits.last()
        annotations = AnnotationDetection.detectAnnotationInFile(document, fileType, 0, document.lineCount)

        for (i in 0..annotations.count()-1) {
            val currentAnnotation = annotations[i]
            val snippet: HighlightAnnotationSnippet?

            if (currentAnnotation.second == "end") continue

            if (i == annotations.count()-1) {
                snippet = HighlightAnnotationSnippet(currentAnnotation.first, document.lineCount, currentAnnotation.second)
            } else {
                val nextAnnotation = annotations[i + 1]
                snippet = HighlightAnnotationSnippet(currentAnnotation.first, nextAnnotation.first, currentAnnotation.second)
            }
            snippets.add(snippet)
            yield()
        }
        return snippets
    }

    /**
     * Case distinction:
     * - Insertion
     * - Deletion
     * - Changes
     *
     *  Difference between single and multiline changes
     * @return RangeHighlightUpdate that contains a list of added, removed and changed HighlightAnnotationSnippets
     */
    fun handleEvent(event: DocumentEvent): RangeHighlightUpdate {
        var update = RangeHighlightUpdate(mutableListOf(), mutableListOf(), mutableListOf())

        if (event.oldLength == 0) {
            //Only insertion
            val startOffSet = event.offset
            val endOffSet = startOffSet + event.newLength
            val startLine = document.getLineNumber(startOffSet)
            val endLine = document.getLineNumber(endOffSet)+1
            //val annotationsInRange = AnnotationDetection.detectAnnotationInFile(document, ".java", startLine, endLine)
            val numberOfNewLines = countNewLines(event.newFragment.toString())

           // update = insertAnnotations(annotationsInRange, numberOfNewLines, startLine)

        } else if (event.newLength == 0) {
            //Only deletion

        } else {
            //both insertion and deletion

        }

        return update
    }


    /**
     * idee ist wir holen uns den index an den die neuen annotationen rein sollen
     * alle alten werden gelöscht in einer range und die neuen sollten ja auch alte unveränderte enthalten
     */
    private fun insertAnnotations(insertion: List<Pair<Int, String>>, numberOfNewLines: Int, insertionLine: Int): RangeHighlightUpdate {
        var firstInsertionLine = if (insertion.isEmpty()) -1 else insertion[0].first
        var annotationInsertionIndex = -1
        var endAnnotationInsertionIndex = -1
        for (i in 0..annotations.count()-1) {
            val annotation = annotations[i]

            if (annotation.first >= insertionLine && annotation.first <= insertionLine+numberOfNewLines) {
                //remove annotation
                if (annotationInsertionIndex == -1) annotationInsertionIndex = i
            } else if (annotation.first > insertionLine+numberOfNewLines) {
                //change annotations
                annotations[i] = annotation.copy(first = annotation.first+numberOfNewLines)
                endAnnotationInsertionIndex = i-1
            }
        }

        if (annotationInsertionIndex != -1) {
            for (i in annotationInsertionIndex..endAnnotationInsertionIndex) {
                annotations.removeAt(i)
            }
        }

        if (insertion.isNotEmpty()) {
            for (i in 0..insertion.count()-1) {
                annotations.add(i+annotationInsertionIndex, insertion[i])
            }
        }

        var firstSnippetDeletionIndex = -1
        var lastSnippetDeletionindex = -1
        for (i in 0..snippets.count()-1) {
            val snippet = snippets[i]

            if (snippet.start >= insertionLine && snippet.start <= insertionLine+numberOfNewLines ||
                    snippet.end >= insertionLine && snippet.end <= insertionLine+numberOfNewLines) {
                //anfang oder ende des snippets im bearbeiteten bereich dann sollte es gelöscht werden
                if (firstSnippetDeletionIndex == -1) firstSnippetDeletionIndex = i
            } else if (snippet.start > insertionLine+numberOfNewLines) {
                //lines der snippets ändern
                snippet.start += numberOfNewLines
                snippet.end += numberOfNewLines
                lastSnippetDeletionindex = i-1
            }
        }

        val removeList = mutableListOf<HighlightAnnotationSnippet>()
        for (i in firstSnippetDeletionIndex..lastSnippetDeletionindex) {
             removeList.add(snippets.removeAt(firstSnippetDeletionIndex))
        }

        val update = RangeHighlightUpdate(mutableListOf(), removeList, mutableListOf())
        return update
    }



    private fun countNewLines(str: String): Int {
        return countOccurrences(str, "\n")
    }

    private fun countOccurrences(str: String, searchStr: String): Int {
        var count = 0
        var startIndex = 0

        while (startIndex < str.length) {
            val index = str.indexOf(searchStr, startIndex)
            if (index >= 0) {
                count++
                startIndex = index + searchStr.length
            } else {
                break
            }
        }

        return count
    }

}