package intellijmigrationplugin.ui.editor

import com.intellij.openapi.editor.Document
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerEvent
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.vfs.VirtualFile
import java.util.concurrent.ConcurrentHashMap

class FileSelectionChangeListener: FileEditorManagerListener {

    private val documents: ConcurrentHashMap<String, Document> = ConcurrentHashMap()
    private val listeners: ConcurrentHashMap<String, DocumentChangeListener> = ConcurrentHashMap()

    override fun selectionChanged(event: FileEditorManagerEvent) {
        if (event.newFile == null) return
        if (isDocumentListenerRegistered(event.newFile.canonicalPath!!)) return

        val document =  event.manager.selectedTextEditor!!.document
        val docChangeListener = DocumentChangeListener(event.newFile.canonicalPath!!, event.manager.selectedTextEditor!!.markupModel)
        registerDocumentListener(event.newFile.canonicalPath!!, document, docChangeListener)
    }

    override fun fileOpened(source: FileEditorManager, file: VirtualFile) {
        if (isDocumentListenerRegistered(file.canonicalPath!!)) return

        val document = source.selectedTextEditor!!.document
        val listener = DocumentChangeListener(file.canonicalPath!!, source.selectedTextEditor!!.markupModel)
        registerDocumentListener(file.canonicalPath!!, document, listener)
    }

    override fun fileClosed(source: FileEditorManager, file: VirtualFile) {
        unregisterDocumentListener(file.canonicalPath!!)
    }

    fun tryToRegisterDocumentListener(path: String, document: Document, listener: DocumentChangeListener) {
        if (isDocumentListenerRegistered(path)) return
        registerDocumentListener(path, document, listener)
    }

    private fun registerDocumentListener(path: String, document: Document, listener: DocumentChangeListener) {
        documents[path] = document
        listeners[path] = listener
        document.addDocumentListener(listener)
    }

    private fun unregisterDocumentListener(path: String) {
        if (!documents.containsKey(path)) return
        val document = documents.remove(path)!!
        val listener = listeners.remove(path)!!
        document.removeDocumentListener(listener)
    }

    private fun isDocumentListenerRegistered(path: String): Boolean {
        return documents.containsKey(path)
    }

}