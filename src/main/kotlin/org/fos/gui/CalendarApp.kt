/**
 * @author Nikolaus Knop
 */

package org.fos.gui

import com.google.gson.Gson
import javafx.application.Application
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Alert
import javafx.scene.control.Alert.AlertType.CONFIRMATION
import javafx.scene.control.ButtonType.*
import javafx.scene.layout.VBox
import javafx.scene.web.WebView
import javafx.stage.*
import javafx.stage.FileChooser.ExtensionFilter
import org.fos.*
import java.io.File
import java.util.prefs.Preferences

class CalendarApp : Application() {
    private val gson = Gson()
    private val fc = FileChooser()
    private lateinit var stage: Stage
    private val editor = EventsEditor()
    private val pref = Preferences.userRoot()

    init {
        fc.extensionFilters.add(ExtensionFilter("JSON files", "*.json"))
    }

    override fun start(stage: Stage) {
        this.stage = stage
        val saved = pref.get(FILE_KEY, DEFAULT_VALUE)
        if (saved != DEFAULT_VALUE) {
            val file = File(saved)
            if (file.exists()) open(file)
        }
        stage.isMaximized = true
        stage.scene = Scene(createContent())
        askBeforeClosing()
        stage.show()
    }

    private fun askBeforeClosing() {
        stage.setOnCloseRequest { ev ->
            ev.consume()
            if (editor.saved) stage.close()
            else {
                Alert(
                    CONFIRMATION,
                    "Wollen sie ihre Arbeit abspeichern, bevor das Programm geschlossen wird?",
                    YES,
                    NO,
                    CANCEL
                ).showAndWait().ifPresent { answer ->
                    when (answer) {
                        YES    -> {
                            save()
                            stage.close()
                        }
                        NO     -> stage.close()
                        CANCEL -> {
                        }
                    }
                }
            }
        }
    }

    private fun createContent(): Parent {
        val menu = menuBar {
            menu("Datei") {
                item("Speichern") {
                    save()
                }
                item("Speichern Unter") {
                    saveAs()
                }
                item("Öffnen") {
                    val file = fc.showOpenDialog(stage) ?: return@item
                    pref.put(FILE_KEY, file.toString())
                    open(file)
                }
                item("Veröffentlichen") {
                    updateEventsPage(editor.all, AlertResponseHandler)
                }
                item("Vorschau") {
                    val events = editor.all
                    val html = buildString { render(events) }
                    val view = WebView()
                    view.engine.loadContent(html)
                    Stage().apply {
                        scene = Scene(view)
                        showAndWait()
                    }
                }
            }
        }
        return VBox(menu, editor)
    }

    private fun save() {
        val file = pref.get(FILE_KEY, DEFAULT_VALUE)
        if (file == DEFAULT_VALUE) saveAs()
        val f = File(file)
        if (f.exists()) {
            editor.saveAs(f, gson)
        } else {
            saveAs()
        }
    }

    private fun open(file: File) {
        editor.open(file, gson)
    }

    private fun saveAs() {
        val file = fc.showSaveDialog(stage) ?: return
        editor.saveAs(file, gson)
    }

    companion object {
        private const val FILE_KEY = "wp.calendar.file"
        private const val DEFAULT_VALUE = "<default>"

        @JvmStatic
        fun main(args: Array<String>) {
            launch(CalendarApp::class.java, *args)
        }
    }
}
