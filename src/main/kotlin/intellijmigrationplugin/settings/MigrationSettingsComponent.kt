package intellijmigrationplugin.settings

import com.intellij.util.ui.FormBuilder
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

    // Components
    internal val legacyFolderComponent = LegacyFolderComponent()
    internal val keywordColorMappingComponent = KeywordColorMappingComponent()
    internal val filetypeCommentMappingComponent = FiletypeCommentMappingComponent()
    internal val excludedFolderComponent = ExcludedFoldersComponent()

    init {
        panel = FormBuilder.createFormBuilder()
            .addComponent(excludedFolderComponent.getComponent())
            .addSeparator(2)
            .addComponent(legacyFolderComponent.getComponent())
            .addSeparator(2)
            .addComponent(keywordColorMappingComponent.getComponent())
            .addSeparator(2)
            .addComponent(filetypeCommentMappingComponent.getComponent()).panel
    }
}