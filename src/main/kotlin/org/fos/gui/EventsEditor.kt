/**
 *@author Nikolaus Knop
 */

package org.fos.gui

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javafx.collections.FXCollections
import javafx.scene.control.Button
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.web.WebView
import org.fos.Event
import org.fos.renderSingle
import java.io.File

class EventsEditor : HBox() {
    var saved = true
        private set

    private val addEvent = Button("Neuer Konzerttermin")
    private val events = FXCollections.observableArrayList<Event>()
    private val listView = EventListView(events)
    private val preview = WebView()
    private val editor = EventEditor()

    fun open(file: File, gson: Gson) {
        val r = file.bufferedReader()
        val list = gson.fromJson<List<Event>>(r, EVENTS)
        events.setAll(list)
        saved = true
    }

    fun saveAs(file: File, gson: Gson) {
        val w = file.bufferedWriter()
        gson.toJson(all, EVENTS, w)
        w.close()
        saved = true
    }

    val all: List<Event> get() = events.toList()

    init {
        listView.prefWidth = 400.0
        listView.prefHeightProperty().bind(this.heightProperty())
        editor.isVisible = false
        addEvent.setOnAction {
            val ev = Event()
            val i = insertionIndex(ev)
            events.add(i, ev)
            listView.selectionModel.select(i)
            invalidate()
        }
        listView.selectionModel.selectedItemProperty().addListener { _, _, selected ->
            if (selected != null) display(selected)
            editor.isVisible = selected != null
            preview.isVisible = selected != null
        }
        editor.ok.setOnAction {
            val idx = listView.selectionModel.selectedIndex
            if (idx != -1) submit(idx)
        }
        children.addAll(VBox(addEvent, listView), VBox(preview, editor))
    }

    private fun display(selected: Event) {
        editor.display(selected)
        preview(selected)
    }

    private fun submit(idx: Int) {
        val new = editor.submit()
        replace(idx, new)
        preview(new)
        invalidate()
        listView.selectionModel.select(idx)
    }

    private fun replace(idx: Int, new: Event) {
        events.removeAt(idx)
        val i = insertionIndex(new)
        events.add(i, new)
    }

    private fun insertionIndex(new: Event): Int {
        var i = events.binarySearch(new)
        if (i < 0) i = -(i + 1)
        return i
    }

    private fun preview(evt: Event) {
        val html = renderSingle(evt)
        preview.engine.loadContent(html)
    }

    fun invalidate() {
        saved = false
    }

    companion object {
        private val EVENTS = object : TypeToken<List<Event>>() {}.type
    }
}