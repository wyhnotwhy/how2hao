package data.repository

import data.model.Task
import java.io.File
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

actual fun loadTasksFromStorage(): List<Task> {
    val file = File(System.getProperty("user.home"), ".how2hao/tasks.json")
    if (!file.exists()) return emptyList()
    
    return try {
        val tasksJson = file.readText()
        Json.decodeFromString<List<Task>>(tasksJson)
    } catch (e: Exception) {
        emptyList()
    }
}

actual fun saveTasksToStorage(tasks: List<Task>) {
    val dir = File(System.getProperty("user.home"), ".how2hao")
    if (!dir.exists()) dir.mkdirs()
    
    val file = File(dir, "tasks.json")
    val tasksJson = Json.encodeToString(tasks)
    file.writeText(tasksJson)
}
