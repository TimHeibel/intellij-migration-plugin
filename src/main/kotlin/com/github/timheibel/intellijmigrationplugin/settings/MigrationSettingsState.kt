package com.github.timheibel.intellijmigrationplugin.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.ui.JBColor
import com.intellij.util.xmlb.XmlSerializerUtil

/**
 * State component for migration settings.
 * Holds persistent (global -> for all Projects) state for the Plugins settings.
 */

@State(name = "org.intellij.sdk.settings.AppSettingsState", storages = [Storage("SettingsMigrationHelper.xml")])
internal class MigrationSettingsState : PersistentStateComponent<MigrationSettingsState?> {
    var keywordColorMapping: MutableMap<String, JBColor> = mutableMapOf(
        "MIGRATED" to JBColor(JBColor.YELLOW, JBColor.YELLOW.darker()),
        "LATER" to JBColor(JBColor.RED, JBColor.RED.darker()),
        "UNUSED" to JBColor(JBColor.GRAY, JBColor.GRAY.darker())
    )
    var fileTypeCommentMapping: MutableMap<String, String> = mutableMapOf()
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