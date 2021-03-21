/**
 * @author Nikolaus Knop
 */

package org.fos

import kotlinx.html.*
import kotlinx.html.stream.appendHTML
import kotlinx.html.stream.createHTML
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import java.time.LocalDate
import java.time.format.TextStyle.FULL
import java.util.Locale.GERMAN

fun Appendable.render(events: Events) {
    appendHTML().apply {
        val upcoming = events.dropWhile { it.date ?: LocalDate.MIN < LocalDate.now() }
        val archive = events.takeWhile { it.date ?: LocalDate.MIN < LocalDate.now() }.reversed()
        for (event in upcoming) {
            render(event, this@render)
            hr()
        }
        h1 { +"Archiv" }
        for (event in archive) {
            render(event, this@render)
            hr()
        }
    }
    createHTML()
}

private fun TagConsumer<*>.render(event: Event, appendable: Appendable) {
    val (heading, subtitle, date, time, description, location) = event
    div {
        style = "font-size: x-large;"
        +heading.orEmpty().ifBlank { "Kein Titel" }
    }
    if (!subtitle.isNullOrBlank()) {
        div {
            style = "font-size: large; font-weight: bold;"
            +subtitle
        }
    }
    if (date != null) {
        div {
            bold()
            +date.dayOfWeek.getDisplayName(FULL, GERMAN)
            +", "
            +date.dayOfMonth.toString()
            +". "
            +date.month.getDisplayName(FULL, GERMAN)
            +". "
            +(date.year.toString())
            if (!time.isNullOrBlank()) {
                +", "
                +time
            }
        }
    }
    div {
        bold()
        if (!location.name.isNullOrBlank()) +location.name
        if (!location.name.isNullOrBlank() && !location.address.isNullOrBlank()) +", "
        if (!location.address.isNullOrBlank()) +location.address
    }
    renderMarkdown(appendable, description)
}

fun renderSingle(event: Event) = buildString {
    appendHTML().apply {
        html {
            head {
                meta(charset = "UTF-8")
            }
            body {
                render(event, this@buildString)
            }
        }
    }
}

private fun CommonAttributeGroupFacade.bold() {
    style = "font-weight: bold"
}

private fun renderMarkdown(appendable: Appendable, markdown: Markdown) {
    val parser = Parser.builder().build()
    val doc = parser.parse(markdown.raw)
    val renderer = HtmlRenderer.builder().build()
    renderer.render(doc, appendable)
}

