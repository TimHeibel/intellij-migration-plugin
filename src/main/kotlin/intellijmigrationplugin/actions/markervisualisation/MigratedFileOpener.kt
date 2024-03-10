package intellijmigrationplugin.actions.markervisualisation

import com.intellij.ide.DataManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.*
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import intellijmigrationplugin.annotationModel.AnnotationInformation
import java.io.File


class MigratedFileOpener: AnAction() {


    override fun actionPerformed(e: AnActionEvent) {

        val fileEditorManager = FileEditorManager.getInstance(e.project!!)
        val path = "/Users/christoph/Documents/MigrationTestProject/src/hauptvers_deprecated.c"

        val keywords = AnnotationInformation.instance!!.keywords

        val regexes = keywords.map { x -> Regex("^\\s*//\\s*$x(\$|\\s)\\w", RegexOption.IGNORE_CASE) }

        val file = File(path)
        if (!file.isFile) return
        val virtualFile = LocalFileSystem.getInstance().findFileByIoFile(file)!!
        print(virtualFile.name)
        fileEditorManager.openFile(virtualFile, true)
        gotoLine(100, e.project!!)
    }

    fun gotoLine(lineNumber: Int, project: Project): Boolean {
        val dataContext: DataContext = DataManager.getInstance().dataContext

        val editor = FileEditorManager.getInstance(project).selectedTextEditor ?: return false
        val caretModel = editor.caretModel
        val totalLineCount = editor.document.lineCount
        if (lineNumber > totalLineCount) return false

        //Moving caret to line number
        caretModel.moveToLogicalPosition(LogicalPosition(lineNumber - 1, 0))

        //Scroll to the caret
        val scrollingModel = editor.scrollingModel
        scrollingModel.scrollToCaret(ScrollType.CENTER)
        return true
    }

}