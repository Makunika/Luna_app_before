package ru.pshiblo.gui;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ConsoleOut {

    private static final TextArea textArea = new TextArea();

    private static final PrintStream out = new PrintStream(new TextAreaOutputStream(textArea));

    public static void println(String msg) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        out.println("[" + dateFormat.format(new Date()) + "] - " + msg);
    }

    public static TextArea getTextArea() {
        return textArea;
    }

    public static void alert(String message) {
        println(" [ALERT] " + message);
    }
}
