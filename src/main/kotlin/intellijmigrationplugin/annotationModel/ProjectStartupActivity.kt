package intellijmigrationplugin.ui.editor

import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity
import com.intellij.psi.PsiDocumentManager
import intellijmigrationplugin.annotationModel.AnnotationInformation

/**
 * This class is the starting point for the visualisation of the Annotations
 * it is registered in the plugin.xml as postStartupActivity.
 * At first a listener for file selection changes is registered in the message bus for the project
 */
class ProjectStartupActivity: StartupActivity {

    override fun runActivity(project: Project) {

        val bus = project.messageBus
        val fileSelectionListener = FileSelectionManager()
        bus.connect().subscribe<FileEditorManagerListener>(FileEditorManagerListener.FILE_EDITOR_MANAGER, fileSelectionListener)

        val editors = FileEditorManager.getInstance(project).selectedTextEditorWithRemotes

        for (editor in editors) {
            val psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.document)
            val vFile = psiFile!!.originalFile.virtualFile
            val path = vFile.canonicalPath!!
            val documentHandler = AnnotationDocumentHandler(path, editor)
            fileSelectionListener.tryToRegisterDocumentListener(documentHandler)
        }

        AnnotationInformation.instance!!.fileSelectionChangeManager = fileSelectionListener

    }

}