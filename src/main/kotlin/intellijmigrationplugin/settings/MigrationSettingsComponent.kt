package intellijmigrationplugin.settings

import com.intellij.openapi.project.ProjectManager
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.gridLayout.HorizontalAlign
import intellijmigrationplugin.settings.components.ExcludedFoldersComponent
import intellijmigrationplugin.settings.components.FiletypeCommentMappingComponent
import intellijmigrationplugin.settings.components.KeywordColorMappingComponent
import intellijmigrationplugin.settings.components.LegacyFolderComponent
import javax.swing.JPanel

/**
 * Represents the settings component for the migration plugin
 * This component includes sections for specifying the legacy folder, keyword color mapping,
 * and filetype comment mapping.
 */
class MigrationSettingsComponent {
    internal val panel: JPanel
    internal val project = ProjectManager.getInstance().openProjects[0]

    // Components
    internal val legacyFolderComponent = LegacyFolderComponent(project)
    internal val keywordColorMappingComponent = KeywordColorMappingComponent()
    internal val filetypeCommentMappingComponent = FiletypeCommentMappingComponent()
    internal val excludedFolderComponent = ExcludedFoldersComponent(project)

    init {
        panel = panel {
            separator().rowComment("Legacy Folder")
            row {
                scrollCell(legacyFolderComponent.getComponent()).horizontalAlign(HorizontalAlign.FILL)
            }
            separator().rowComment("Excluded Folders")
            row {
                scrollCell(excludedFolderComponent.getComponent()).horizontalAlign(HorizontalAlign.FILL)
            }
            separator().rowComment("Filetype Comment Mapping")
            row {
                scrollCell(filetypeCommentMappingComponent.getComponent()).horizontalAlign(HorizontalAlign.FILL)
            }

        }
    }

}
