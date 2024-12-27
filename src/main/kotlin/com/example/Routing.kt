package com.example

import com.example.db.Tasks
import com.example.db.database
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.h2.util.Task
import org.jetbrains.exposed.sql.*

import org.jetbrains.exposed.sql.transactions.transaction


fun Application.configureRouting() {

    database()

    routing {
        get("/") {
            // Get tasks from the database
            val tasks = transaction {
                Tasks.selectAll().map {
                    it[Tasks.title]
                    it[Tasks.description]
                }
            }

            println("tasks: $tasks")

            // Create the HTML response
            call.respondText(contentType = ContentType.Text.Html) {
                """
                <!DOCTYPE html>
                <html>
                    <head>
                        <title>Ktor on Heroku</title>
                    </head>
                    <body>
                        <h1>Hello, world!</h1>
                        <h2>Tasks</h2>
                        <ul>
                            ${tasks.joinToString("") { "<li>${it}</li>" }}
                        </ul>
                    </body>
                </html>
                """.trimIndent()
            }
        }
    }
}

