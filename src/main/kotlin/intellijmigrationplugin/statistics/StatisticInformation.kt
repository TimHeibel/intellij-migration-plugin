package intellijmigrationplugin.statistics

import com.intellij.openapi.application.ApplicationManager
import intellijmigrationplugin.settings.MigrationSettingsState


//TODO: Issue 38
class StatisticInformation {

    private val settings: MigrationSettingsState
        get() {
            return ApplicationManager.getApplication().getService(MigrationSettingsState::class.java)
        }

    val keywordMapping = settings.keywordColorMapping



}