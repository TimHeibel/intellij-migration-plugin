package intellijmigrationplugin.actions.annotation

import intellijmigrationplugin.annotationModel.AnnotationInformation
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Document
import com.intellij.openapi.ui.DialogWrapper
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
            val startSelectionLine = document.getLineNumber(startSelection)
            val endSelectionLine = document.getLineNumber(endSelection)

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


            document.insertString(document.getLineEndOffset(endSelectionLine), "\n${commentStart}END\n")
            document.insertString(document.getLineStartOffset(startSelectionLine),
                "$commentStart$annotationType $addInfo\n")
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
            : ArrayList<AnnotationSnippet> {

        val existingAnnotations = AnnotationDetection.detectAnnotationInFile(document, fileType)
        val collisionAnnotations = ArrayList<AnnotationSnippet>()

        for (annotation in existingAnnotations) {
            if(annotation.start in startLine..endLine || annotation.end in startLine..endLine) {
                collisionAnnotations.add(annotation)
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