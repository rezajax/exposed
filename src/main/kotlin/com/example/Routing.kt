package com.example

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

const val MAX_VARCHAR_LENGTH = 128

object Tasks : Table("tasks") {
    val id = integer("id").autoIncrement()
    val title = varchar("name", MAX_VARCHAR_LENGTH)
    val description = varchar("description", MAX_VARCHAR_LENGTH)
    val isCompleted = bool("completed").default(false)
}



object Users : Table("users") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", MAX_VARCHAR_LENGTH)
    val email = varchar("email", MAX_VARCHAR_LENGTH)
}


fun database() {

    Database.connect("jdbc:h2:./rezDB", "org.h2.Driver")

    transaction {
        SchemaUtils.create(Tasks)
        SchemaUtils.createMissingTablesAndColumns(Users)

        val taskId = Tasks.insert {
            it[title] = "Learn Exposed"
            it[description] = "Go through the Get started with Exposed tutorial"
        } get Tasks.id

        val secondTaskId: Int = Tasks.insert {
            it[title] = "Read The Hobbit"
            it[description] = "Read the first two chapters of The Hobbit"
            it[isCompleted] = true
        } get Tasks.id

        println("Created new tasks with ids $taskId and $secondTaskId.")

        Tasks.select(Tasks.id.count(), Tasks.isCompleted).groupBy(Tasks.isCompleted).forEach {
            println("${it[Tasks.isCompleted]}: ${it[Tasks.id.count()]} ")
        }
    }


}