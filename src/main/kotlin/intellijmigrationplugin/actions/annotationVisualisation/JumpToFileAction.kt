package intellijmigrationplugin.actions.annotationVisualisation

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import intellijmigrationplugin.actions.annotation.utils.AnnotationActionUtils.Companion.getLine
import intellijmigrationplugin.annotationModel.AnnotationInformation
import intellijmigrationplugin.annotationModel.util.AnnotationDetection
import intellijmigrationplugin.annotationModel.util.JumpToAnnotationUtil
import kotlinx.coroutines.runBlocking
import java.io.File

class JumpToFileAction: AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val fileEditorManager = FileEditorManager.getInstance(e.project!!)
        val path = AnnotationInformation.instance!!.legacyFolderPath + "/"
        val legacyFile = File(path)

        val editor = e.getData(CommonDataKeys.EDITOR)!!
        val cursorOffset = editor.caretModel.offset
        val cursorLine = editor.document.getLineNumber(cursorOffset)

        val lineString = editor.document.getLine(cursorLine)

        val psiFile = e.getData(CommonDataKeys.VIRTUAL_FILE)
        val filePath = psiFile?.canonicalPath!!
        val splits = filePath.split(".")
        val fileType = "." + splits.last()

        val (fileName, fileID) = AnnotationDetection.detectNewProjectThing(lineString, fileType)

        if (fileName == "" && fileID == "") {
           falseSchemeNotification(editor.project)
        }

        var file: File? = null

        legacyFile.walk(FileWalkDirection.BOTTOM_UP).forEach { x ->
            val pathSplit = x.canonicalPath.split("/")
            val currentFileName = pathSplit.last()
            if (fileName == currentFileName) {
                file = File(x.canonicalPath)
            }
        }

        if (file == null) return
        if (!file!!.isFile) return

        val virtualFile = LocalFileSystem.getInstance().findFileByIoFile(file!!)!!
        fileEditorManager.openFile(virtualFile, true)

        val openEditors = fileEditorManager.selectedTextEditorWithRemotes
        val newEditor = openEditors[0]

        runBlocking {
            val line = AnnotationDetection.detectIdInFile(newEditor.document, fileID)
            if (line == -1) {
                return@runBlocking
            }
            JumpToAnnotationUtil.gotoLine(line, newEditor.project!!)
        }
    }

    private fun falseSchemeNotification(project: Project?) {
        NotificationGroupManager.getInstance()
            .getNotificationGroup("Custom Notification Group")
            .createNotification("Your scheme for jumping to other files is not correct. The correct scheme is \"Comment : Filename : Id\"",
                NotificationType.ERROR)
            .notify(project)
    }


}