package intellijmigrationplugin.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.ui.JBColor
import com.intellij.util.xmlb.XmlSerializerUtil
import com.intellij.util.xmlb.annotations.OptionTag
import intellijmigrationplugin.settings.converters.JBColorConverter
import intellijmigrationplugin.settings.converters.PairListConverter

/**
 * State component for migration settings.
 * Holds persistent (global -> for all Projects) state for the Plugins settings.
 */
@State(name = "org.intellij.sdk.settings.AppSettingsState", storages = [Storage("SettingsMigrationHelper.xml")])
internal class MigrationSettingsState : PersistentStateComponent<MigrationSettingsState?> {
    @OptionTag(converter = JBColorConverter::class)
    var keywordColorMapping: MutableList<Pair<String, JBColor>> = mutableListOf(
        "MIGRATED" to JBColor(JBColor.YELLOW, JBColor.YELLOW),
        "LATER" to JBColor(JBColor.RED, JBColor.RED),
        "UNUSED" to JBColor(JBColor.GRAY, JBColor.GRAY)
    )

    @OptionTag(converter = PairListConverter::class)
    var fileTypeCommentMapping: MutableList<Pair<String, String>> = mutableListOf()
    var legacyFolderPath: String = ""
    var excludedFoldersList: MutableList<String> = mutableListOf()

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