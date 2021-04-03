package ru.pshiblo.gui

import com.jagrosh.jmusicbot.JMusicBot
import javafx.stage.Stage
import ru.pshiblo.discord.YouTubeBot
import ru.pshiblo.global.keypress.GlobalKeyListener
import ru.pshiblo.gui.views.MyView
import tornadofx.*
import kotlin.system.exitProcess

class MyApp: App(MyView::class) {
    override fun start(stage: Stage) {
        super.start(stage)
        stage.width = 1000.0
        stage.height = 600.0
        stage.setOnCloseRequest {
            exitProcess(0);
        }
        JMusicBot.init()
        YouTubeBot.init()
    }



}