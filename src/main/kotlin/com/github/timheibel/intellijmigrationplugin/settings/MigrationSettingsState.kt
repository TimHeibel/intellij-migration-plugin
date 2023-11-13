package com.github.timheibel.intellijmigrationplugin.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil
import java.awt.Color

/**
 * State component for migration settings.
 * Holds persistent (global -> for all Projects) state for the Plugins settings.
 */

@State(name = "org.intellij.sdk.settings.AppSettingsState", storages = [Storage("SettingsMigrationHelper.xml")])
internal class MigrationSettingsState : PersistentStateComponent<MigrationSettingsState?> {
    var colorKeywordMapping: Map<String, Color> = emptyMap()
    var fileTypeCommentMapping: Map<String, String> = emptyMap()
    var legacyFolderPath: String = ""

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