package intellijmigrationplugin.actions.annotation

import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.ui.DialogWrapper
import intellijmigrationplugin.actions.annotation.utils.AnnotationActionUtils.Companion.getCommentTypeByEvent
import intellijmigrationplugin.actions.annotation.utils.AnnotationActionUtils.Companion.getFileTypeByEvent
import intellijmigrationplugin.actions.annotation.utils.AnnotationActionUtils.Companion.placeAnnotation
import intellijmigrationplugin.actions.annotation.utils.AnnotationActionUtils.Companion.placeOneLineAnnotation
import intellijmigrationplugin.actions.annotation.utils.AnnotationActionUtils.Companion.removeAnnotation
import intellijmigrationplugin.actions.annotation.utils.AnnotationActionUtils.Companion.removeLine
import intellijmigrationplugin.annotationModel.util.AnnotationDetection
import intellijmigrationplugin.annotationModel.AnnotationSnippet
import intellijmigrationplugin.annotationModel.CollisionCode
import intellijmigrationplugin.ui.dialogs.CollisionDialog


/**
 * class used to create uniform Annotations independent of [annotationType]
 *
 * @property annotationType declares the type of Annotation to be set.
 * @property actionPerformed placement of Annotations
 */
abstract class AnnotationAction(private val annotationType : String, private val addInfo: String = "") : AnAction() {

    override fun update(event: AnActionEvent) {

        val project = event.getData(CommonDataKeys.PROJECT)
        val editor = event.getData(CommonDataKeys.EDITOR)

        event.presentation.isEnabledAndVisible = (project != null
                && editor != null
                && editor.selectionModel.hasSelection(false))
    }

    override fun actionPerformed(event: AnActionEvent) {

        //Get Required information from the event
        val project = event.getRequiredData(CommonDataKeys.PROJECT)
        val editor = event.getRequiredData(CommonDataKeys.EDITOR)
        val document = editor.document
        val primaryCaret = editor.caretModel.primaryCaret



        //Get Start and End of current selection
        var startSelection = primaryCaret.selectionStart
        var endSelection = primaryCaret.selectionEnd

        if(endSelection == startSelection){
            thisLogger().warn("Nothing to Annotate")
            return
        }

        endSelection -= 1

        var startSelectionLine = document.getLineNumber(startSelection)
        var endSelectionLine = document.getLineNumber(endSelection)

        val commentStart : String = getCommentTypeByEvent(event)

        var collidingAnnotations =
                getAnnotationCollisions(document, getFileTypeByEvent(event), startSelectionLine, endSelectionLine)

        if(collidingAnnotations.isNotEmpty()) {
            val dialog = CollisionDialog()
            dialog.show()

            if(dialog.exitCode != DialogWrapper.OK_EXIT_CODE) {
                return
            }
        }

        WriteCommandAction.runWriteCommandAction(project) {

            collidingAnnotations =
                    getAnnotationCollisions(document, getFileTypeByEvent(event), startSelectionLine, endSelectionLine)

            handleCollisions(collidingAnnotations, commentStart, editor)

            startSelection = primaryCaret.selectionStart
            endSelection = primaryCaret.selectionEnd

            startSelectionLine = document.getLineNumber(startSelection)

            if(endSelection == startSelection){
                document.placeOneLineAnnotation(annotationType, addInfo, startSelectionLine, commentStart)
                return@runWriteCommandAction
            }

            endSelection -= 1


            endSelectionLine = document.getLineNumber(endSelection)

            document.placeAnnotation(annotationType, addInfo, startSelectionLine, endSelectionLine, commentStart)
            
        }

    }

    /**
     * Handles collisions between [collidingAnnotations] and the selected text range in the [editor].
     *
     * @param collidingAnnotations The list of annotations that collide with the selected text range.
     * @param commentStart The comment syntax used in the document.
     * @param editor The editor instance.
     */
    private fun handleCollisions(collidingAnnotations: ArrayList<Pair<AnnotationSnippet, CollisionCode>>, commentStart: String, editor: Editor) {

        val document = editor.document
        val primaryCaret = editor.caretModel.primaryCaret

        for (collision in collidingAnnotations.reversed()) {

            val startSelection = primaryCaret.selectionStart
            var endSelection = primaryCaret.selectionEnd

            endSelection -= 1

            val startLine = document.getLineNumber(startSelection)
            val endLine = document.getLineNumber(endSelection)

            when (collision.second) {
                CollisionCode.START_INSIDE -> handleStartInsideCollision(collision, document, endLine, commentStart)

                CollisionCode.END_INSIDE -> handleEndInsideCollision(collision, document, startLine, commentStart)

                CollisionCode.COMPLETE_INSIDE -> handleCompleteInsideCollision(collision, document)

                CollisionCode.SURROUNDING -> handleSurroundingCollision(collision, document, endLine, startLine, commentStart)
            }
        }

    }

    /**
     * Handles a collision where the annotation surrounds the selected text range.
     *
     * @param collision The collision information, defined as a pair of an [AnnotationSnippet] and a [CollisionCode].
     * @param document The document instance.
     * @param endLine The end line of the selected text range.
     * @param startLine The start line of the selected text range.
     * @param commentStart The comment syntax used in the document.
     */
    private fun handleSurroundingCollision(collision: Pair<AnnotationSnippet, CollisionCode>, document: Document, endLine: Int, startLine: Int, commentStart: String) {

        replaceAnnotationStart(collision, commentStart, endLine, document)

        replaceAnnotationEnd(collision, startLine, document, commentStart)
    }

    /**
     * Handles a collision where the annotation is completely inside the selected text range.
     *
     * @param collision The collision information, defined as a pair of an [AnnotationSnippet] and a [CollisionCode].
     * @param document The document instance.
     */
    private fun handleCompleteInsideCollision(collision: Pair<AnnotationSnippet, CollisionCode>, document: Document) {
        document.removeAnnotation(collision.first)
    }

    /**
     * Handles a collision where the end of the annotation is inside the selected text range.
     *
     * @param collision The collision information, defined as a pair of an [AnnotationSnippet] and a [CollisionCode].
     * @param document The document instance.
     * @param startLine The start line of the selected text range.
     * @param commentStart The comment syntax used in the document.
     */
    private fun handleEndInsideCollision(collision: Pair<AnnotationSnippet, CollisionCode>, document: Document, startLine: Int, commentStart: String) {

        if (collision.first.hasEnd) {
            document.removeLine(collision.first.end)
        }

        replaceAnnotationEnd(collision, startLine, document, commentStart)
    }

    /**
     * Handles a collision where the start of the annotation is inside the selected text range.
     *
     * @param collision The collision information, defined as a pair of an [AnnotationSnippet] and a [CollisionCode].
     * @param document The document instance.
     * @param endLine The end line of the selected text range.
     * @param commentStart The comment syntax used in the document.
     */
    private fun handleStartInsideCollision(collision: Pair<AnnotationSnippet, CollisionCode>, document: Document, endLine: Int, commentStart: String) {

        replaceAnnotationStart(collision, commentStart, endLine, document)

        document.removeLine(collision.first.start)
    }

    /**
     * Replaces or removes the end of the annotation in case of a collision.
     *
     * @param collision The collision information.
     * @param startLine The start line of the selected text range.
     * @param document The document instance.
     * @param commentStart The comment syntax used in the document.
     */
    private fun replaceAnnotationEnd(collision: Pair<AnnotationSnippet, CollisionCode>, startLine: Int, document: Document, commentStart: String) {
        if (collision.first.start in startLine - 1..startLine) {
            document.removeLine(collision.first.start)

        } else {
            document.insertString(document.getLineStartOffset(startLine) - 1, "\n${commentStart}END")
        }
    }

    /**
     * Replaces or removes the start of the annotation in case of a collision.
     *
     * @param collision The collision information.
     * @param commentStart The comment syntax used in the document.
     * @param endLine The end line of the selected text range.
     * @param document The document instance.
     */
    private fun replaceAnnotationStart(collision: Pair<AnnotationSnippet, CollisionCode>, commentStart: String, endLine: Int, document: Document) {
        val collisionStartLine = collision.first.createStartLine(commentStart)

        if (collision.first.end == endLine || ((collision.first.end == (endLine + 1)) && collision.first.hasEnd)) {
            if (collision.first.hasEnd) {
                document.removeLine(collision.first.end)
            }
        } else {

            if(document.lineCount <= endLine + 1) {
                return
            }

            document.insertString(document.getLineStartOffset(endLine + 1), "${collisionStartLine}\n")
        }
    }

    /**
     * Retrieves a [AnnotationSnippet] List that collide with the specified text range in the document.
     *
     * @param document The document to search for annotations.
     * @param fileType The type of the file associated with the document.
     * @param startLine The start line of the text range.
     * @param endLine The end line of the text range.
     * @return A list of annotation snippets along with their collision codes.
     */
    private fun getAnnotationCollisions(
            document : Document,
            fileType : String?,
            startLine : Int,
            endLine : Int
    ) : ArrayList<Pair<AnnotationSnippet,CollisionCode>> {

        val existingAnnotations = AnnotationDetection.detectAnnotationInFile(document, fileType)
        val collisionAnnotations = ArrayList<Pair<AnnotationSnippet,CollisionCode>>()

        for (annotation in existingAnnotations) {

            val collisionCode = annotation.hasCollision(startLine, endLine) ?: continue

            collisionAnnotations.add(Pair(annotation, collisionCode))
        }

        return collisionAnnotations
    }

    override fun getActionUpdateThread(): ActionUpdateThread {

        return ActionUpdateThread.BGT

    }

}

/**
 * An annotation action that sets an annotation based on user input from a dialog.
 *
 * @param annotationType The type of annotation to be performed by the action.
 * @param annotationInformation Additional information for the annotation.
 * @see AnnotationAction
 */
class DIALOGAnnotationAction(annotationType: String, annotationInformation: String)
    : AnnotationAction(annotationType, annotationInformation)
