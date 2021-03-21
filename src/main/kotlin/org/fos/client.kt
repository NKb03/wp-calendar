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

private val title = Title("Termine")

data class Title(val raw: String)

data class Content(val raw: String)

data class Page(val title: Title, val content: Content)

private fun Request.authenticate(userData: UserData) = authentication().basic(userData.username, userData.password)

fun updateEventsPage(userData: UserData, events: Events, handler: ResponseHandler<Page>): CancellableRequest {
    val raw = buildString { render(events) }
    val page = Page(title, Content(raw))
    return Fuel.post(userData.calendarUrl)
        .authenticate(userData)
        .jsonBody(page)
        .responseObject(handler)
}