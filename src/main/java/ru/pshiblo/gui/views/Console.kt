package ru.pshiblo.gui.views

import javafx.scene.Parent
import javafx.scene.control.TextArea
import ru.pshiblo.gui.TextAreaOutputStream
import tornadofx.*
import java.io.PrintStream

class Console: Fragment("Вывод консоли") {

    private val textArea: TextArea = TextArea()

    init {
        textArea.isEditable = false
        val con = PrintStream(TextAreaOutputStream(textArea))
        System.setOut(con)
        System.setErr(con)
    }


    override val root = borderpane {
        style {
            paddingAll(20.0)
        }
        top {
            label("Консолька вывода")
        }
        center {
            add(textArea)
        }
    }
}