package intellijmigrationplugin.actions.annotation

import intellijmigrationplugin.annotationModel.AnnotationType
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.fileTypes.FileTypeRegistry


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

        event.presentation.setEnabledAndVisible(project != null
                && editor != null
                && editor.selectionModel.hasSelection(false))
    }

    abstract val annotationType : AnnotationType;
    override fun actionPerformed(event: AnActionEvent) {

        //Get Required information from the event
        val project = event.getRequiredData(CommonDataKeys.PROJECT)
        val editor = event.getRequiredData(CommonDataKeys.EDITOR)
        val document = editor.document
        val primaryCaret = editor.caretModel.primaryCaret

        //Get Start and End of current selection
        val startSelection = primaryCaret.selectionStart
        val endSelection = primaryCaret.selectionEnd
        val startSelectionLine = document.getLineNumber(startSelection)
        val endSelectionLine = document.getLineNumber(endSelection)

        val commentStart : String = getCommentTypeByEvent(event)

        WriteCommandAction.runWriteCommandAction(project) {
            document.insertString(document.getLineEndOffset(endSelectionLine), "\n${commentStart}END\n")
            document.insertString(document.getLineStartOffset(startSelectionLine),
                "$commentStart${annotationType.name} $addInfo\n")
        }

    }

    private fun getCommentTypeByEvent(event: AnActionEvent) : String {

        val default = "//"

        val vFile = event.getData(PlatformCoreDataKeys.VIRTUAL_FILE)
            ?: return default

        val fType = FileTypeRegistry.getInstance().getFileTypeByFileName(vFile.name)

        //TODO: Replace Strings with generic input
        return when (fType.name) {
            "Haskell" -> "--"
            else -> default
        }
    }

    override fun getActionUpdateThread(): ActionUpdateThread {

        return ActionUpdateThread.BGT

    }

}

/**
 * Sets MIGRATED-Annotations
 * @see AnnotationAction
 */
class MIGRATEDAnnotationAction(override val annotationType: AnnotationType = AnnotationType.MIGRATED)
    : AnnotationAction()

/**
 * Sets LATER-Annotations
 * @see AnnotationAction
 */
class LATERAnnotationAction(override val annotationType: AnnotationType = AnnotationType.LATER)
    : AnnotationAction()

/**
 * Sets UNUSED-Annotations
 * @see AnnotationAction
 */
class UNUSEDAnnotationAction(override val annotationType: AnnotationType = AnnotationType.UNUSED)
    : AnnotationAction()

/**
 * Sets Annotation From User-Dialog
 * @see AnnotationAction
 */
class DIALOGAnnotationAction(override val annotationType: AnnotationType, annotationInformation: String)
    : AnnotationAction(annotationInformation)