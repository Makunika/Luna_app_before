package ru.pshiblo.gui.views

import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleLongProperty
import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.Alert
import javafx.scene.control.TextField
import ru.pshiblo.Config
import ru.pshiblo.audio.LocalAudio
import ru.pshiblo.discord.YouTubeBot
import ru.pshiblo.global.keypress.GlobalKeyListener
import ru.pshiblo.youtube.YouTubeInitializer
import ru.pshiblo.youtube.listener.UpdatedCommand
import tornadofx.*

class Init:Fragment("Настройки") {

    private val videoId = TextField()
    private val maxTimeAudio = SimpleLongProperty()
    private val timeInsert = SimpleLongProperty()
    private val timeList = SimpleLongProperty()
    private val volume = SimpleDoubleProperty()
    private val globalListener = checkbox {
        isSelected = false
        this.selectedProperty()
    }
    private val configYouTube = Config.getInstance()

    init {
        maxTimeAudio.set(configYouTube.maxTimeTrack / 1000)
        timeInsert.set(configYouTube.timeInsert / (1000 * 60))
        timeList.set(configYouTube.timeList / 1000)

        maxTimeAudio.addListener(ChangeListener { observable, oldValue, newValue ->
            configYouTube.maxTimeTrack = newValue as Long * 1000
        })
        timeInsert.addListener(ChangeListener { observable, oldValue, newValue ->
            configYouTube.timeInsert = newValue as Long * 60 * 1000
        })
        timeList.addListener(ChangeListener { observable, oldValue, newValue ->
            configYouTube.timeList = newValue as Long * 1000
        })

    }

    override val root = borderpane {
        center {
            form {
                fieldset( if (Config.getInstance().isDiscord) "Для начала введи в дискорде команду !connect <название канала>" else "Настройки") {
                    field("id трансляции (после v=)") {
                        add(videoId)
                    }
                    field("Максимальное время одного трека (в cекундах)") {
                        textfield(maxTimeAudio)
                    }
                    field("Задержка между сообщениями о боте в чат (в минутах)") {
                        textfield(timeInsert)
                    }
                    field("Задержка между проверками сообщений на команду ./track (в секундах)") {
                        textfield(timeList)
                    }
                    field("F12 для стоп музыки включать?") {
                        add(globalListener)
                    }
                    field("Громкость музыки локальной") {
                        slider(0,100, 100) {
                            this.valueProperty().addListener(ChangeListener { observable, oldValue, newValue ->
                                LocalAudio.getPlayer().volume = newValue as Int
                            })
                            this.isDisable = Config.getInstance().isDiscord
                        }
                    }
                    button("Начать") {
                        action {
                            if (validate()) {
                                if (globalListener.isSelected) {
                                    GlobalKeyListener.init()
                                }
                                configYouTube.videoId = videoId.text
                                println(Config.getInstance().toString())
                                YouTubeInitializer.go();
                                this@borderpane.add(text("Работает!"));
                                this.isDisable = true
                                globalListener.isDisable = true
                                videoId.isDisable = true
                                alert(Alert.AlertType.INFORMATION, "", "Можно изменять данные, который не стали серыми.")
                            }
                        }
                    }
                    button("Стоп музыка") {
                        action {
                            if (Config.getInstance().isDiscord) {
                                YouTubeBot.getListener().stop()
                            } else {
                                LocalAudio.getPlayer().stopTrack()
                            }
                        }
                    }
                }
                fieldset("Дополнительная команда") {
                    field("Команда (К примеру /command)") {
                        textfield(UpdatedCommand.getInstance().commandProperty())
                    }
                    field("Ответ на эту команду") {
                        textfield(UpdatedCommand.getInstance().answerProperty())
                    }
                }
            }
        }
    }

    private fun validate(): Boolean {
        if (configYouTube.messageChannel == null && Config.getInstance().isDiscord) {
            alert(Alert.AlertType.ERROR, "Discord", "Забыл написать в канале !connect <название канала>")
            return false
        }
        if (videoId.text.isNullOrEmpty()) {
            alert(Alert.AlertType.ERROR, "YouTube", "Забыл написать id канала")
            return false
        }
        return true
    }
}