/**
 *@author Nikolaus Knop
 */

package org.fos.gui

import javafx.scene.control.*
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import org.fos.*
import java.time.LocalDate

class EventEditor : VBox() {
    private var edited: Event = Event()
    private val heading = TextField()
    private val subtitle = TextField()
    private val date = DatePicker(LocalDate.now())
    private val time = TextField()
    private val description = TextArea()
    private val location = TextField()
    private val address = TextField()
    val ok = Button("Ok")
    val cancel = Button("Cancel")

    init {
        heading.promptText = "Titel"
        subtitle.promptText = "Untertitel"
        time.promptText = "Uhrzeit"
        description.promptText = "Beschreibung (Markdown)"
        location.promptText = "Ort"
        address.promptText = "Adresse"
        cancel.setOnAction {
            display(edited)
        }
        children.addAll(
            heading,
            subtitle,
            date,
            time,
            location,
            address,
            description,
            HBox(10.0, ok, cancel)
        )
    }

    fun display(event: Event) {
        edited = event
        heading.text = event.heading
        subtitle.text = event.subtitle
        date.value = event.date
        time.text = event.time
        description.text = event.description.raw
        location.text = event.location.name
        address.text = event.location.address
    }

    fun submit(): Event = Event(
        heading = heading.text,
        subtitle = subtitle.text,
        date = date.value,
        time = time.text,
        description = Markdown(description.text),
        location = Location(location.text, address.text)
    )
}