package intellijmigrationplugin.statistics.component

import javax.swing.JButton

class RunStatisticComponent(private val fileChooserComponent: FileChooserComponent) {

    fun runStatisticButton(): JButton{
        val statisticButton = JButton("run Statistic").apply {
            addActionListener {
                //TODO: exclude&include files after file-ignore

                println("Processing complete.")
            }
        }
        return statisticButton
    }


}