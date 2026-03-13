package data.repository

import data.model.Task
import data.model.AddTaskRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 任务数据仓库
 * 支持本地持久化和未来远程API切换
 */
class TaskRepository private constructor() {
    // 内存存储
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()
    
    // 是否使用远程API（后续切换）
    private var useRemoteApi: Boolean = false
    private var apiBaseUrl: String = ""

    companion object {
        @Volatile
        private var instance: TaskRepository? = null

        fun getInstance(): TaskRepository {
            return instance ?: synchronized(this) {
                instance ?: TaskRepository().also { 
                    instance = it
                    it.loadFromLocal()
                }
            }
        }
    }

    /**
     * 添加任务
     */
    fun addTask(request: AddTaskRequest): Task {
        val task = Task(
            id = generateTaskId(),
            title = request.title,
            date = request.date,
            repeatType = request.repeatType,
            reminderTime = request.reminderTime,
            bankTag = request.bankId?.let { bankId ->
                BankRepository.getAllBanks().find { it.id == bankId }
            }
        )
        
        val currentList = _tasks.value.toMutableList()
        currentList.add(task)
        _tasks.value = currentList
        
        // 保存到本地
        saveToLocal()
        
        // 如果启用了远程API，也发送到服务器
        if (useRemoteApi) {
            syncToRemote(task)
        }
        
        return task
    }

    /**
     * 更新任务
     */
    fun updateTask(task: Task): Task {
        val currentList = _tasks.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == task.id }
        if (index != -1) {
            currentList[index] = task
            _tasks.value = currentList
            saveToLocal()
            
            if (useRemoteApi) {
                syncUpdateToRemote(task)
            }
        }
        return task
    }

    /**
     * 删除任务
     */
    fun deleteTask(taskId: String) {
        val currentList = _tasks.value.toMutableList()
        currentList.removeAll { it.id == taskId }
        _tasks.value = currentList
        saveToLocal()
        
        if (useRemoteApi) {
            syncDeleteToRemote(taskId)
        }
    }

    /**
     * 获取所有任务
     */
    fun getAllTasks(): List<Task> = _tasks.value

    /**
     * 根据ID获取任务
     */
    fun getTaskById(taskId: String): Task? {
        return _tasks.value.find { it.id == taskId }
    }

    /**
     * 切换任务完成状态
     */
    fun toggleTaskComplete(taskId: String): Task? {
        val task = getTaskById(taskId) ?: return null
        val updatedTask = task.copy(isCompleted = !task.isCompleted)
        return updateTask(updatedTask)
    }

    /**
     * 启用远程API（一键切换）
     */
    fun enableRemoteApi(baseUrl: String, authToken: String? = null) {
        useRemoteApi = true
        apiBaseUrl = baseUrl
        // TODO: 初始化API客户端
        // 同步本地数据到远程
        syncAllToRemote()
    }

    /**
     * 禁用远程API，仅使用本地
     */
    fun disableRemoteApi() {
        useRemoteApi = false
        apiBaseUrl = ""
    }

    // ========== 本地持久化（平台相关）==========
    
    /**
     * 从本地加载数据
     */
    private fun loadFromLocal() {
        // 平台实现：从SharedPreferences/DataStore加载
        val savedTasks = loadTasksFromStorage()
        _tasks.value = savedTasks
    }

    /**
     * 保存到本地
     */
    private fun saveToLocal() {
        // 平台实现：保存到SharedPreferences/DataStore
        saveTasksToStorage(_tasks.value)
    }

    // ========== 远程同步（预留）==========
    
    private fun syncToRemote(task: Task) {
        // TODO: 实现API调用
        println("[Remote] Creating task: ${task.title}")
    }

    private fun syncUpdateToRemote(task: Task) {
        // TODO: 实现API调用
        println("[Remote] Updating task: ${task.title}")
    }

    private fun syncDeleteToRemote(taskId: String) {
        // TODO: 实现API调用
        println("[Remote] Deleting task: $taskId")
    }

    private fun syncAllToRemote() {
        // TODO: 批量同步本地数据到远程
        println("[Remote] Syncing all tasks to remote")
    }

    private fun generateTaskId(): String {
        return "task_${System.currentTimeMillis()}_${(1000..9999).random()}"
    }
}

// 平台相关函数声明（需要在androidMain/jvmMain中实现）
expect fun loadTasksFromStorage(): List<Task>
expect fun saveTasksToStorage(tasks: List<Task>)
