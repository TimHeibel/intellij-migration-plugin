package intellijmigrationplugin.actions.annotation

import intellijmigrationplugin.annotationModel.AnnotationInformation
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Document
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.util.TextRange
import intellijmigrationplugin.actions.annotation.utils.AnnotationActionUtils
import intellijmigrationplugin.annotationModel.AnnotationDetection
import intellijmigrationplugin.annotationModel.AnnotationSnippet
import intellijmigrationplugin.ui.dialogs.CollisionDialog


/**
 * Abstract class used to create uniform Annotations independent of AnnotationType
 *
 * @property annotationType declares the type of Annotation to be set.
 * @property actionPerformed placement of Annotations
 */
abstract class AnnotationAction(private val addInfo: String = "") : AnAction() {

    override fun update(event: AnActionEvent) {

        val project = event.getData(CommonDataKeys.PROJECT)
        val editor = event.getData(CommonDataKeys.EDITOR)

        event.presentation.isEnabledAndVisible = (project != null
                && editor != null
                && editor.selectionModel.hasSelection(false))
    }

    abstract val annotationType : String

    override fun actionPerformed(event: AnActionEvent) {

        //Get Required information from the event
        val project = event.getRequiredData(CommonDataKeys.PROJECT)
        val editor = event.getRequiredData(CommonDataKeys.EDITOR)
        val document = editor.document
        val primaryCaret = editor.caretModel.primaryCaret

        WriteCommandAction.runWriteCommandAction(project) {

            //Get Start and End of current selection
            val startSelection = primaryCaret.selectionStart
            val endSelection = primaryCaret.selectionEnd
            var startSelectionLine = document.getLineNumber(startSelection)
            var endSelectionLine = document.getLineNumber(endSelection)

            val commentStart : String = getCommentTypeByEvent(event)

            val collidingAnnotations =
                    getAnnotationCollisions(document, getFileTypeByEvent(event), startSelectionLine, endSelectionLine)

            if(collidingAnnotations.isNotEmpty()) {
                val dialog = CollisionDialog()
                dialog.show()

                if(dialog.exitCode != DialogWrapper.OK_EXIT_CODE) {
                    return@runWriteCommandAction
                }
            }

            for (collision in collidingAnnotations.reversed()) {
                when (collision.second) {
                    CollisionCode.START_INSIDE -> {
                        val collisionStartLine = AnnotationActionUtils.getLineFromDocument(collision.first.start, document)

                        AnnotationActionUtils.removeLine(collision.first.start, document)
                        endSelectionLine -= 1

                        if(collision.first.end in endSelectionLine + 1 .. endSelectionLine + 2) {
                            if(collision.first.hasEnd) {
                                AnnotationActionUtils.removeLine(collision.first.end - 1, document)
                            }
                            continue
                        }

                        document.insertString(document.getLineEndOffset(endSelectionLine), "\n${collisionStartLine}")
                    }
                    CollisionCode.END_INSIDE -> {
                        if(collision.first.hasEnd) {
                            AnnotationActionUtils.removeLine(collision.first.end, document)
                            endSelectionLine -= 1
                        }

                        if(collision.first.start in startSelectionLine - 1 .. startSelectionLine) {
                            AnnotationActionUtils.removeLine(collision.first.start, document)
                            startSelectionLine -= 1
                            endSelectionLine -= 1
                            continue
                        }

                        document.insertString(document.getLineEndOffset(startSelectionLine - 1), "\n${commentStart}END")
                        startSelectionLine += 1
                        endSelectionLine += 1
                    }
                    CollisionCode.COMPLETE_INSIDE -> {
                        AnnotationActionUtils.removeAnnotation(collision.first, document)
                        endSelectionLine -= 1

                        if(collision.first.hasEnd) {
                            endSelectionLine -= 1
                        }
                    }
                    CollisionCode.SURROUNDING -> {
                        val collisionStartLine = AnnotationActionUtils.getLineFromDocument(collision.first.start, document)

                        if(collision.first.end in endSelectionLine .. endSelectionLine + 1) {
                            if(collision.first.hasEnd) {
                                AnnotationActionUtils.removeLine(collision.first.end, document)
                            }
                        } else {
                            document.insertString(document.getLineEndOffset(endSelectionLine), "\n${collisionStartLine}")
                        }

                        if(collision.first.start in startSelectionLine - 1 .. startSelectionLine) {
                            AnnotationActionUtils.removeLine(collision.first.start, document)
                            startSelectionLine -= 1
                            endSelectionLine -= 1
                        } else {
                            document.insertString(document.getLineStartOffset(startSelectionLine) - 1, "\n${commentStart}END")
                            startSelectionLine += 1
                            endSelectionLine += 1
                        }
                    }
                }
            }

            AnnotationActionUtils.placeAnnotation(annotationType, addInfo, startSelectionLine, endSelectionLine, commentStart, document)
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
            if (annotation.start in startLine..endLine && annotation.end in startLine..endLine) {
                collisionAnnotations.add(Pair(annotation, CollisionCode.COMPLETE_INSIDE))
                continue
            }

            if (annotation.start in startLine..endLine) {
                collisionAnnotations.add(Pair(annotation, CollisionCode.START_INSIDE))
                continue
            }

            if (annotation.end in startLine..endLine) {
                collisionAnnotations.add(Pair(annotation, CollisionCode.END_INSIDE))
                continue
            }

            if (startLine in annotation.start..annotation.end) {
                collisionAnnotations.add(Pair(annotation, CollisionCode.SURROUNDING))
            }
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
class DIALOGAnnotationAction(override val annotationType: String, annotationInformation: String)
    : AnnotationAction(annotationInformation)

private enum class CollisionCode {
    START_INSIDE,
    END_INSIDE,
    COMPLETE_INSIDE,
    SURROUNDING
}