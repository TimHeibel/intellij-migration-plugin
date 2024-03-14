package intellijmigrationplugin.actions.annotationVisualisation

import com.intellij.openapi.editor.LogicalPosition
import com.intellij.openapi.editor.ScrollType
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project

class JumpToAnnotationUtil {

    companion object {
        fun gotoLine(lineNumber: Int, project: Project): Boolean {
            val editor = FileEditorManager.getInstance(project).selectedTextEditor ?: return false
            val caretModel = editor.caretModel
            val totalLineCount = editor.document.lineCount
            if (lineNumber > totalLineCount) return false

            //Moving caret to line number
            caretModel.moveToLogicalPosition(LogicalPosition(lineNumber, 0))

            //Scroll to the caret
            val scrollingModel = editor.scrollingModel
            scrollingModel.scrollToCaret(ScrollType.CENTER)
            return true
        }
    }

}