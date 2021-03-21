/**
 * @author Nikolaus Knop
 */

package org.fos

import java.time.LocalDate

data class Markdown(val raw: String?)

data class Location(val name: String?, val address: String?)

data class Event(
    val heading: String?,
    val subtitle: String?,
    val date: LocalDate?,
    val time: String?,
    val description: Markdown,
    val location: Location
) : Comparable<Event> {
    constructor() : this(
        "Neuer Termin",
        "",
        null,
        "",
        Markdown(""),
        Location("", "")
    )

    override fun compareTo(other: Event): Int = compareValuesBy(this, other, Event::date)
}

typealias Events = List<Event>