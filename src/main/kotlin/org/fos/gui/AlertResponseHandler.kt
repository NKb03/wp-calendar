/**
 *@author Nikolaus Knop
 */

package org.fos.gui

import com.github.kittinunf.fuel.core.*
import javafx.application.Platform
import javafx.scene.control.Alert
import javafx.scene.control.Alert.AlertType.ERROR
import javafx.scene.control.Alert.AlertType.INFORMATION
import org.fos.Page

object AlertResponseHandler : ResponseHandler<Page> {
    override fun failure(request: Request, response: Response, error: FuelError) {
        error.printStackTrace()
        Platform.runLater {
            Alert(ERROR, "Fehler: ${error.message}").showAndWait()
        }
    }

    override fun success(request: Request, response: Response, value: Page) {
        Platform.runLater {
            Alert(INFORMATION, "Konzerttermine erfolgreich veröffentlicht.").showAndWait()
        }
    }
}