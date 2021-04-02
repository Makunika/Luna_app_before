package ru.pshiblo.gui.views

import tornadofx.View
import tornadofx.tabpane
import kotlin.system.exitProcess

class MyView: View("YouTube Chat") {
    override val root = tabpane {
        tab<Init>()
        tab<Console>()
        tab<Chat>()
    }
}



