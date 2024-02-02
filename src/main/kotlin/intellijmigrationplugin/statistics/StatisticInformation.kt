package intellijmigrationplugin.statistics

import com.intellij.openapi.application.ApplicationManager
import intellijmigrationplugin.settings.MigrationSettingsState


//TODO: Issue 38
class StatisticInformation private constructor(){

    companion object {
        var instance: StatisticInformation? = null
            private set
            get() {
                if (field == null) {
                    field = StatisticInformation()
                }
                return field!!
            }
    }
    private val settings: MigrationSettingsState
        get() {
            return ApplicationManager.getApplication().getService(MigrationSettingsState::class.java)
        }

    val keywordMapping = settings.keywordColorMapping



}