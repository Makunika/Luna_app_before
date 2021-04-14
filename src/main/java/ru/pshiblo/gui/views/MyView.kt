package ru.pshiblo.gui.views

import ru.pshiblo.Config
import ru.pshiblo.base.init.InitType
import ru.pshiblo.base.init.Initializer
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
                                 Initializer.init(InitType.DISCORD_BOT);
                                 updateRoot()
                             }
                         }
                         button("Нет") {
                             action {
                                 Config.getInstance().isDiscord = false
                                 this.isDisable = true
                                 this.text = "Загрузка... локальной музыки"
                                 Initializer.init(InitType.LOCAL_AUDIO);
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



