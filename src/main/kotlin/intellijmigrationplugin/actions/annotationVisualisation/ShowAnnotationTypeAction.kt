package intellijmigrationplugin.actions.annotationVisualisation

import com.intellij.codeInsight.hint.HintManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import intellijmigrationplugin.annotationModel.AnnotationInformation

/**
 * This action shows the current AnnotationType if the cursor is inside an Annotation
 */
class ShowAnnotationTypeAction: AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val information = AnnotationInformation.instance!!

        val editor = e.getData(CommonDataKeys.EDITOR)!!
        val cursorOffset = editor.caretModel.offset
        val cursorLine = editor.document.getLineNumber(cursorOffset)
        information.lastCursorLine = cursorLine

        val psiFile = e.getData(CommonDataKeys.VIRTUAL_FILE)
        val path = psiFile?.canonicalPath!!

        val snippet = information.documentManager.getSnippetForLine(path, cursorLine)
        if (snippet == null) return

        HintManager.getInstance().showInformationHint(editor, snippet.type)
    }

}