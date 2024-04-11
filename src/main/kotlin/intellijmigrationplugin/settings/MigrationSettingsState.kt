package intellijmigrationplugin.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil
import com.intellij.util.xmlb.annotations.OptionTag
import intellijmigrationplugin.settings.components.FileTypeMapping
import intellijmigrationplugin.settings.converters.FileTypeMappingListConverter
import intellijmigrationplugin.settings.converters.PairListConverter

/**
 * State component for migration settings.
 * Holds persistent (global -> for all Projects) state for the Plugins settings.
 */
@State(name = "org.intellij.sdk.settings.AppSettingsState", storages = [Storage("SettingsMigrationHelper.xml")])
internal class MigrationSettingsState : PersistentStateComponent<MigrationSettingsState?> {
    var legacyFolderPath: String = ""
    var excludedFoldersList: MutableList<String> = mutableListOf()

    @OptionTag(converter = FileTypeMappingListConverter::class)
    var fileTypeCommentMapping: MutableList<FileTypeMapping> = mutableListOf(
        FileTypeMapping(".*",  "//", "/* */","import "),
        FileTypeMapping(".py", "#" , "\"\"\" \"\"\"", "import "),
        FileTypeMapping(".java", "//" , "/* */", "import "),
        FileTypeMapping(".cpp", "//" , "/* */", "#include ")
    )


    @OptionTag(converter = PairListConverter::class)
    var keywordColorMapping: MutableList<Pair<String, String>> = mutableListOf(
        Pair("MIGRATED", "#2032cd32"),
        Pair("LATER", "#22ffa500"),
        Pair("UNUSED", "#22808080")
    )
    @OptionTag(converter = PairListConverter::class)
    var keywordShortcutMapping: MutableList<Pair<String, String>> = keywordColorMapping.map { (keyword, _) ->
        Pair(keyword, "") // TODO(David): hier bei shortcut einen sensible default hinterlegen,
        // der dir nicht das dynamische erstellen zerschiesst
    }.toMutableList()

    override fun getState(): MigrationSettingsState {
        return this
    }

    override fun loadState(state: MigrationSettingsState) {
        XmlSerializerUtil.copyBean(state, this)
    }

    companion object {
        val instance: MigrationSettingsState
            get() = ApplicationManager.getApplication().getService(MigrationSettingsState::class.java)
    }
}