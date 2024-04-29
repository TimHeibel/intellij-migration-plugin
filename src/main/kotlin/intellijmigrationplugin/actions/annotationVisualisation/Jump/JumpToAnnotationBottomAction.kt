package intellijmigrationplugin.actions.annotationVisualisation.Jump

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import intellijmigrationplugin.annotationModel.util.JumpToAnnotationUtil
import intellijmigrationplugin.annotationModel.AnnotationInformation

/**
 * This action lets the user jump to the top of an Annotation
 * if the current cursor is inside an Annotation.
 */
class JumpToAnnotationBottomAction: AnAction() {

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
        val jumpToLine = snippet.end-1

        if (jumpToLine != -1) JumpToAnnotationUtil.gotoLine(jumpToLine, e.project!!)
    }

}