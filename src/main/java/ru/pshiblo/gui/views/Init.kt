package ru.pshiblo.gui.views

import javafx.scene.control.TextField
import ru.pshiblo.Config
import ru.pshiblo.youtube.YouTubeInitializer
import tornadofx.*

class Init:Fragment("Настройки") {

    private val videoId = TextField()
    private val maxTimeAudio = TextField(Config.getInstance().maxTimeTrack.toString())

    override val root = borderpane {
        center {
            form {
                label("Для начала введи в дискорде команду !connect <название канала>")
                fieldset("Настройки YouTube") {
                    field("id трансляции (после v=)") {
                        add(videoId)
                    }

                }
                fieldset("Для настройки дискорд бота") {
                    field("Максимальное время одного трека (в мс)") {
                        add(maxTimeAudio)
                    }
                }
                button("Начать") {
                    action {
                        if (Config.getInstance().messageChannel != null) {
                            Config.getInstance().videoId = videoId.text
                            Config.getInstance().maxTimeTrack = Integer.parseInt(maxTimeAudio.text)
                            println(Config.getInstance().toString())
                            YouTubeInitializer.go();
                            this@borderpane.add(text("Работает!"));
                            this.isDisable = true
                        }
                    }
                }
            }
        }
    }
}