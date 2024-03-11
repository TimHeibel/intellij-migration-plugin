package intellijmigrationplugin.statistics

import com.intellij.ide.BrowserUtil
import com.intellij.ui.components.ActionLink
import java.awt.Cursor
import java.awt.GridLayout
import javax.swing.JComponent
import javax.swing.JPanel


class CSVFileCompoment {

    val cvsList = mutableListOf<String>()
    val contentPanel = JPanel()

    fun getComponent(): JComponent {

        return contentPanel

    }

    fun addLink(csvPath: String): JPanel{

        cvsList.add(csvPath)

        contentPanel.removeAll();
        contentPanel.revalidate();
        contentPanel.repaint();

        contentPanel.layout = GridLayout(cvsList.size, 1) // Set GridLayout to arrange labels vertically

        // Create and add the hyperlink labels to the panel
        for (i in 0..cvsList.size - 1) {
            val externalLink = ActionLink(cvsList[i].substringAfterLast("/")) {
                BrowserUtil.browse(cvsList[i])
            }
            externalLink.setExternalLinkIcon()
            externalLink.cursor = Cursor(Cursor.HAND_CURSOR) // Change cursor to hand when mouse hovers over the link

            contentPanel.add(externalLink)
        }
        return contentPanel

    }
}