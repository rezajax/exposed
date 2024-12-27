package com.example

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import javax.swing.text.AbstractDocument.Content

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText(contentType = ContentType.Text.Html) {"""
                <!DOCTYPE html>
                <html>
                    <head>
                        <title>Ktor on Heroku</title>
                    </head>
                    <body>
                        <h1>Hello, world!</h1>
                    </body>
                </html>
            """.trimIndent()}
        }
    }
}
