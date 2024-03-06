package intellijmigrationplugin.actions.annotation

import intellijmigrationplugin.annotationModel.AnnotationInformation
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.ui.DialogWrapper
import intellijmigrationplugin.actions.annotation.utils.AnnotationActionUtils.Companion.placeAnnotation
import intellijmigrationplugin.actions.annotation.utils.AnnotationActionUtils.Companion.placeOneLineAnnotation
import intellijmigrationplugin.actions.annotation.utils.AnnotationActionUtils.Companion.removeAnnotation
import intellijmigrationplugin.actions.annotation.utils.AnnotationActionUtils.Companion.removeLine
import intellijmigrationplugin.annotationModel.AnnotationDetection
import intellijmigrationplugin.annotationModel.AnnotationSnippet
import intellijmigrationplugin.annotationModel.CollisionCode
import intellijmigrationplugin.ui.dialogs.CollisionDialog


/**
 * Abstract class used to create uniform Annotations independent of AnnotationType
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

    private fun handleSurroundingCollision(collision: Pair<AnnotationSnippet, CollisionCode>, document: Document, endLine: Int, startLine: Int, commentStart: String) {

        replaceAnnotationStart(collision, commentStart, endLine, document)

        replaceAnnotationEnd(collision, startLine, document, commentStart)
    }

    private fun handleCompleteInsideCollision(collision: Pair<AnnotationSnippet, CollisionCode>, document: Document) {
        document.removeAnnotation(collision.first)
    }

    private fun handleEndInsideCollision(collision: Pair<AnnotationSnippet, CollisionCode>, document: Document, startLine: Int, commentStart: String) {

        if (collision.first.hasEnd) {
            document.removeLine(collision.first.end)
        }

        replaceAnnotationEnd(collision, startLine, document, commentStart)
    }

    private fun handleStartInsideCollision(collision: Pair<AnnotationSnippet, CollisionCode>, document: Document, endLine: Int, commentStart: String) {

        replaceAnnotationStart(collision, commentStart, endLine, document)

        document.removeLine(collision.first.start)
    }

    private fun replaceAnnotationEnd(collision: Pair<AnnotationSnippet, CollisionCode>, startLine: Int, document: Document, commentStart: String) {
        if (collision.first.start in startLine - 1..startLine) {
            document.removeLine(collision.first.start)

        } else {
            document.insertString(document.getLineStartOffset(startLine) - 1, "\n${commentStart}END")
        }
    }
    private fun replaceAnnotationStart(collision: Pair<AnnotationSnippet, CollisionCode>, commentStart: String, endLine: Int, document: Document) {
        val collisionStartLine = collision.first.createStartLine(commentStart)

        if (collision.first.end == endLine || ((collision.first.end == (endLine + 1)) && collision.first.hasEnd)) {
            if (collision.first.hasEnd) {
                document.removeLine(collision.first.end)
            }
        } else {
            document.insertString(document.getLineEndOffset(endLine), "\n${collisionStartLine}")
        }
    }

    private fun getCommentTypeByEvent(event: AnActionEvent) : String {

        val default = "//"

        val fType = getFileTypeByEvent(event)
                ?: return default

        val annotationInformation = AnnotationInformation.instance
            ?: return default

        annotationInformation.singleCommentMapping[".$fType"]?.let {
            return it
        }

        return default
    }

    private fun getFileTypeByEvent(event: AnActionEvent) : String? {

        val vFile = event.getData(PlatformCoreDataKeys.VIRTUAL_FILE)
                ?: return null

        return vFile.extension

    }

    private fun getAnnotationCollisions(document : Document, fileType : String?, startLine : Int, endLine : Int)
            : ArrayList<Pair<AnnotationSnippet,CollisionCode>> {

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
 * Sets Annotation From User-Dialog
 * @see AnnotationAction
 */
class DIALOGAnnotationAction(annotationType: String, annotationInformation: String)
    : AnnotationAction(annotationType, annotationInformation)
