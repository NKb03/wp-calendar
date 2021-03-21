/**
 * @author Nikolaus Knop
 */

package org.fos.gui

import com.google.gson.Gson
import javafx.application.Application
import javafx.application.Platform
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.control.Alert.AlertType.CONFIRMATION
import javafx.scene.control.Alert.AlertType.INFORMATION
import javafx.scene.control.ButtonType.*
import javafx.scene.layout.VBox
import javafx.scene.web.WebView
import javafx.stage.*
import javafx.stage.FileChooser.ExtensionFilter
import org.fos.*
import java.io.File
import java.util.prefs.Preferences
import kotlin.concurrent.thread

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
        val saved = pref.get(FILE_KEY, DEFAULT_FILE_KEY)
        if (saved != DEFAULT_FILE_KEY) {
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
                ).fixGlitch()
                    .showAndWait().ifPresent { answer ->
                    when (answer) {
                        YES -> {
                            save()
                            stage.close()
                        }
                        NO -> stage.close()
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
                    val userData = getUserData()
                    if (userData == null) {
                        Alert(INFORMATION, "Du bist nicht eingeloggt").fixGlitch().showAndWait()
                    } else {
                        updateEventsPage(userData, editor.all, AlertResponseHandler)
                    }
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
                item("Login") {
                    login()
                }
                item("Logout") {
                    val answer = Alert(CONFIRMATION, "Willst du dich wirklich abmelden?", YES, NO).fixGlitch()
                        .showAndWait().orElse(NO)
                    if (answer == YES) logout()
                }
            }
        }
        return VBox(menu, editor)
    }

    private fun Dialog<*>.fixGlitch() {
        isResizable = true
        setOnShown {
            thread {
                Thread.sleep(10)
                Platform.runLater {
                    isResizable = false
                }
            }
        }
    }

    private fun Alert.fixGlitch(): Alert {
        isResizable = true
        setOnShown {
            thread {
                Thread.sleep(10)
                Platform.runLater {
                    isResizable = false
                }
            }
        }
        return this
    }

    private fun getUserData(): UserData? {
        val url = pref.get(CALENDAR_URL, null) ?: return null
        val username = pref.get(USERNAME, null) ?: return null
        val password = pref.get(PASSWORD, null) ?: return null
        return UserData(url, username, password)
    }

    private fun logout() {
        pref.remove(CALENDAR_URL)
        pref.remove(USERNAME)
        pref.remove(PASSWORD)
    }

    private fun login() {
        with(Dialog<Unit>()) {
            title = "Login"
            val url = TextField()
            val username = TextField()
            val password = PasswordField()
            url.promptText = "URL des Kalenders"
            url.text = DEFAULT_ROUTE
            username.promptText = "Benutzername"
            password.promptText = "Passwort"
            for (tf in listOf(url, username, password)) {
                tf.prefWidth = 500.0
            }
            dialogPane.content = VBox(url, username, password)
            dialogPane.buttonTypes.setAll(OK, CANCEL)
            fixGlitch()
            setResultConverter { btn ->
                if (btn == OK) {
                    pref.put(CALENDAR_URL, url.text)
                    pref.put(USERNAME, username.text)
                    pref.put(PASSWORD, password.text)
                }
            }
            show()
        }
    }


    private fun save() {
        val file = pref.get(FILE_KEY, DEFAULT_FILE_KEY)
        if (file == DEFAULT_FILE_KEY) saveAs()
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
        private const val DEFAULT_FILE_KEY = "<default>"
        private const val CALENDAR_URL = "wp.calendar.url"
        private const val USERNAME = "wp.calendar.user.name"
        private const val PASSWORD = "wp.calendar.user.password"
        private const val DEFAULT_ROUTE = "http://carstenwiebusch.de/wp-json/wp/v2/pages/29"

        @JvmStatic
        fun main(args: Array<String>) {
            launch(CalendarApp::class.java, *args)
        }
    }
}
