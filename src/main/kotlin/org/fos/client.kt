/**
 * @author Nikolaus Knop
 */

package org.fos

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.ResponseHandler
import com.github.kittinunf.fuel.core.extensions.authentication
import com.github.kittinunf.fuel.core.requests.CancellableRequest
import com.github.kittinunf.fuel.gson.jsonBody
import com.github.kittinunf.fuel.gson.responseObject

private const val USERNAME = "Nikolaus"
private const val PASSWORD = "VhgJ I5L1 xZEJ i91n FfBd 2nAD"
private val title = Title("Termine")

data class Title(val raw: String)

data class Content(val raw: String)

data class Page(val title: Title, val content: Content)

private const val ROUTE = "http://carstenwiebusch.de/wp-json/wp/v2/pages/29"

private fun Request.authenticate() = authentication().basic(USERNAME, PASSWORD)

fun updateEventsPage(events: Events, handler: ResponseHandler<Page>): CancellableRequest {
    val raw = buildString { render(events) }
    val page = Page(title, Content(raw))
    return Fuel.post(ROUTE)
        .authenticate()
        .jsonBody(page)
        .responseObject(handler)
}