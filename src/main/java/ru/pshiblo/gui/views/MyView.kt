package ru.pshiblo.gui.views

import com.jagrosh.jmusicbot.JMusicBot
import javafx.scene.Parent
import ru.pshiblo.Config
import ru.pshiblo.audio.LocalAudio
import ru.pshiblo.discord.YouTubeBot
import tornadofx.*

class MyView: View("YouTube Chat") {

    private val tb = tabpane {
        tab<Init>()
        tab<Console>()
        tab<Chat>()
    }

    override var root =  tabpane {
         tab("Настройки") {
             borderpane {
                 center {
                     vbox(20) {
                         label("Использовать дискорд бота для музыки?")
                         button("Да") {
                             action {
                                 Config.getInstance().isDiscord = true
                                 this.isDisable = true
                                 this.text = "Загрузка... ботов"
                                 JMusicBot.init()
                                 YouTubeBot.init()
                                 updateRoot()
                             }
                         }
                         button("Нет") {
                             action {
                                 Config.getInstance().isDiscord = false
                                 this.isDisable = true
                                 this.text = "Загрузка... локальной музыки"
                                 LocalAudio.init()
                                 updateRoot()
                             }
                         }
                     }
                 }
             }
         }
    }

    private fun updateRoot() {
        root.tabs.clear()
        root.tab<Init>()
        root.tab<Console>()
        root.tab<Chat>()
    }
}



