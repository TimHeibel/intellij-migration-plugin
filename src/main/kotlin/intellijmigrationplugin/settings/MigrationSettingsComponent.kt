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
    private val project = ProjectManager.getInstance().openProjects[0]

    // Components
    internal val legacyFolderComponent = LegacyFolderComponent(project)
    internal val excludedFolderComponent = ExcludedFoldersComponent(project)
    internal val keywordColorMappingComponent = KeywordColorMappingComponent(project)
    internal val filetypeCommentMappingComponent = FiletypeCommentMappingComponent(project)


    init {
        panel = panel {
            group("Legacy Folder") {
                row {
                    scrollCell(legacyFolderComponent.getComponent()).horizontalAlign(HorizontalAlign.FILL)
                }
            }

            group("Excluded Folders") {
                row {
                    scrollCell(excludedFolderComponent.getComponent()).horizontalAlign(HorizontalAlign.FILL)
                }
            }

            group("Filetype Comment Mapping") {
                row {
                    scrollCell(filetypeCommentMappingComponent.getComponent()).horizontalAlign(HorizontalAlign.FILL)
                }
            }

            group("Keyword color mapping") {
                row {
                    scrollCell(keywordColorMappingComponent.getComponent()).horizontalAlign(HorizontalAlign.FILL)
                }
            }
        }
    }
}

