/**
 *@author Nikolaus Knop
 */

package org.fos.gui

import javafx.collections.ObservableList
import javafx.geometry.Insets
import javafx.geometry.Pos.CENTER_LEFT
import javafx.scene.control.*
import javafx.scene.layout.HBox
import org.fos.Event

class EventListView(private val events: ObservableList<Event>) : ListView<Event>(events) {
    init {
        setCellFactory { Cell() }
    }

    private inner class Cell : ListCell<Event>() {
        override fun updateItem(item: Event?, empty: Boolean) {
            super.updateItem(item, empty)
            if (item == null || empty) {
                graphic = null
                return
            }
            val delete = Button("Löschen")
            delete.alignment = CENTER_LEFT
            delete.border = null
            delete.padding = Insets(0.0)
            val heading = Label(item.heading.orEmpty().ifBlank { "Kein Titel" })
            delete.setOnAction {
                events.remove(item)
            }
            graphic = HBox(10.0, heading, delete)
        }
    }
}