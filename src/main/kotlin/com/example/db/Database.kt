package com.example.db

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

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
        SchemaUtils.create(Tasks, Users)

        // Check if tasks table is empty
        val isTableEmpty = Tasks.selectAll().empty()

        if (isTableEmpty) {

            // اضافه کردن داده‌ها
            val userId = Users.insert {
                it[name] = "Reza Jax"
                it[email] = "reza@example.com"
            }


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
        } else {
            println("Tasks table already contains data. No new tasks were added.")
        }

        // Group by `isCompleted` status and count
        Tasks.select(Tasks.id.count(), Tasks.isCompleted).groupBy(Tasks.isCompleted).forEach {
            println("${it[Tasks.isCompleted]}: ${it[Tasks.id.count()]} ")
        }
    }
}