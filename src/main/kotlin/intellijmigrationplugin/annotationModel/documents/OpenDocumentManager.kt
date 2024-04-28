package intellijmigrationplugin.annotationModel.documents

import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerEvent
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.vfs.VirtualFile
import intellijmigrationplugin.annotationModel.AnnotationInformation
import intellijmigrationplugin.annotationModel.AnnotationSnippet
import java.util.concurrent.ConcurrentHashMap

/**
 * This class is instantiated once in the project startup phase.
 * A list contains all documents that are opened as a tab.
 * Functionalities regarding the state of a specific document are exposed through specific functions.
 */
class OpenDocumentManager: FileEditorManagerListener {

    private val openDocuments: ConcurrentHashMap<String, AnnotationDocumentHandler> = ConcurrentHashMap()

    override fun selectionChanged(event: FileEditorManagerEvent) {
        if (event.newFile == null) return
        if (event.manager.selectedTextEditor == null) return
        val documentHandler = AnnotationDocumentHandler(event.newFile.canonicalPath!!, event.manager.selectedTextEditor!!)
        tryToRegisterDocumentListener(documentHandler)
    }

    override fun fileOpened(source: FileEditorManager, file: VirtualFile) {
        if (source.selectedTextEditor == null) return
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
        if (AnnotationInformation.instance!!.showMarker) documentHandler.turnVisualisationOn()
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

    /**
     * @return Empty list if the specified file is not open, or it does not contain annotations. <br/>
     * List of all [AnnotationSnippet] in the specified file.
     */
    private fun getSnippetsOfOpenFile(path: String): MutableList<AnnotationSnippet> {
        if (!openDocuments.containsKey(path)) return mutableListOf()
        return openDocuments[path]!!.getSnippets()
    }

    /**
     * @return Null if the current line does not contain an [AnnotationSnippet]. <br/>
     * The [AnnotationSnippet] for the specified line.
     */
    fun getSnippetForLine(path: String, line: Int): AnnotationSnippet? {
        val snippets = getSnippetsOfOpenFile(path);
        if (snippets.isEmpty()) return null
        for (snippet in snippets) {
            if (snippet.contains(line)) return snippet
        }
        return null;
    }

    fun updateDocumentVisualisation() {
        for (document in openDocuments.values) {
            document.updateVisualisation()
        }
    }

}