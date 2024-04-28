package intellijmigrationplugin.annotationModel

import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.event.DocumentEvent
import intellijmigrationplugin.actions.annotation.utils.AnnotationActionUtils.Companion.getLine
import intellijmigrationplugin.annotationModel.util.AnnotationDetection
import kotlinx.coroutines.yield

open class AnnotationFile {
    var snippets: MutableList<AnnotationSnippet>

    protected var document: Document

    private var fileName: String
    private var fileType: String

    constructor(fileName: String, document: Document) {
        snippets = mutableListOf()

        this.fileName = fileName
        this.document = document
        val splits = fileName.split(".")
        fileType = "." + splits.last()
    }

    /**
     * Since performance is no problem, there is only a trivial case distinction.
     * We return the old list of annotations because nothing changed, if only one element that is not a newline is added or removed.
     * Furthermore, if the edit happens in a line where an annotation is or was the whole document is scanned, to avoid case nesting.
     * In any other case the whole document is scanned again.
     * For future work there could be a sophisticated case distinction by insertion, deletion, or both.
     * @return Returns a complete list of all [AnnotationSnippet] in the document.
     */
    suspend fun handleEvent(event: DocumentEvent): MutableList<AnnotationSnippet>? {
        //More than one element is changed
        if (!(event.newLength == 1 && event.oldLength == 0 || event.newLength == 0 && event.oldLength == 1)) {
            return computeSnippets()
        }

        //The edit contains a linebreak
        val lineFeed = Char(10).toString()
        if (event.newFragment.toString() == lineFeed || event.oldFragment.toString() == lineFeed) {
            return computeSnippets()
        }

        //The edited line contains an Annotation
        val line = document.getLineNumber(event.offset)
        if (AnnotationDetection.detectAnnotationInString(document.getLine(line), fileType) != null) {
            return computeSnippets()
        }

        //The line did previously contain an annotation
        for (snippet in snippets) {
            if (snippet.lineIsAnnotationLine(line)) {
                return computeSnippets()
            }
        }

        return null
    }

    suspend fun computeSnippets(): MutableList<AnnotationSnippet> {
        snippets = mutableListOf()
        val annotations = AnnotationDetection.detectAnnotationInFile(document, fileType, 0, document.lineCount)

        for (i in 0..annotations.count() - 1) {
            val currentAnnotation = annotations[i]
            val snippet: AnnotationSnippet?

            if (currentAnnotation.second == "end") continue

            if (i == annotations.count() - 1) {
                snippet =
                    AnnotationSnippet(currentAnnotation.first, document.lineCount-1, false, currentAnnotation.second)
            } else {
                val nextAnnotation = annotations[i + 1]
                val hasEnd = nextAnnotation.second == "end";
                val hasEndAddition = if (hasEnd) 0 else 1
                snippet = AnnotationSnippet(currentAnnotation.first,
                    nextAnnotation.first - hasEndAddition, hasEnd, currentAnnotation.second)
            }
            snippets.add(snippet)
            yield()
        }
        return snippets
    }

}