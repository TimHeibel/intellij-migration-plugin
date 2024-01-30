package intellijmigrationplugin.ui.editor

import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerEvent
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.vfs.VirtualFile
import java.util.concurrent.ConcurrentHashMap

class FileSelectionManager: FileEditorManagerListener {

    private val openDocuments: ConcurrentHashMap<String, AnnotationDocumentHandler> = ConcurrentHashMap()

    override fun selectionChanged(event: FileEditorManagerEvent) {
        if (event.newFile == null) return
        val documentHandler = AnnotationDocumentHandler(event.newFile.canonicalPath!!, event.manager.selectedTextEditor!!)
        tryToRegisterDocumentListener(documentHandler)
    }

    override fun fileOpened(source: FileEditorManager, file: VirtualFile) {
        val documentHandler = AnnotationDocumentHandler(file.canonicalPath!!, source.selectedTextEditor!!)
        tryToRegisterDocumentListener(documentHandler)
    }

    override fun fileClosed(source: FileEditorManager, file: VirtualFile) {
        tryToDeregisterDocument(file.canonicalPath!!)
    }

    fun turnVisualisationOn() {
        openDocuments.values.forEach {x -> x.turnVisualisationOn()}
    }
    fun turnVisualisationOff() {
        openDocuments.values.forEach {x -> x.turnVisualisationOff()}
    }

    fun tryToRegisterDocumentListener(documentHandler: AnnotationDocumentHandler) {
        if (isDocumentRegistered(documentHandler.path)) return
        if (isDocumentExcluded(documentHandler.path)) return
        registerDocument(documentHandler)
    }

    fun tryToDeregisterDocument(path: String) {
        if (!isDocumentRegistered(path)) return
        unregisterDocument(path)
    }

    private fun registerDocument(documentHandler: AnnotationDocumentHandler) {
        openDocuments[documentHandler.path] = documentHandler
        documentHandler.registerDocumentListener()
    }

    private fun unregisterDocument(path: String) {
        val openDocument = openDocuments.remove(path)!!
        openDocument.deregisterDocumentListener()
    }

    private fun isDocumentRegistered(path: String): Boolean {
        return openDocuments.containsKey(path)
    }

    private fun isDocumentExcluded(path: String): Boolean {
        //TODO: Excluded folder and files from the settings should not be marked
        return false
    }

}