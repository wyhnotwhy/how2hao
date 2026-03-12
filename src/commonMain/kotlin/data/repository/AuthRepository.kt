package data.repository

import data.model.LoginRequest
import data.model.LoginResponse
import data.model.User

/**
 * 认证仓库
 * 模拟登录注册，无需真实服务器
 */
object AuthRepository {

    // 模拟用户数据
    private val mockUsers = mutableMapOf<String, User>(
        "admin" to User("1", "管理员", null),
        "user" to User("2", "普通用户", null)
    )

    // 随机用户名列表
    private val randomUsernames = listOf(
        "快乐小羊", "旅行者", "探索者", "梦想家", "追光者",
        "漫步者", "思考者", "创造者", "发现者", "冒险家",
        "观察者", "记录者", "分享者", "学习者", "实践者"
    )

    // 随机头像颜色
    private val avatarColors = listOf(
        0xFFE57373, 0xFF81C784, 0xFF64B5F6, 0xFFFFB74D,
        0xFF9575CD, 0xFF4DB6AC, 0xFFF06292, 0xFFA1887F
    )

    /**
     * 登录
     * 无论输入什么都能登录成功
     */
    fun login(request: LoginRequest): LoginResponse {
        // 模拟网络延迟
        Thread.sleep(500)

        // 如果用户名为空，生成随机用户名
        val username = request.username.takeIf { it.isNotBlank() } ?: generateRandomUsername()

        val user = User(
            id = System.currentTimeMillis().toString(),
            username = username,
            avatarUrl = null
        )

        return LoginResponse(
            success = true,
            user = user,
            token = "mock_token_${System.currentTimeMillis()}",
            message = "登录成功"
        )
    }

    /**
     * 注册
     * 无论输入什么都能注册成功
     */
    fun register(request: LoginRequest): LoginResponse {
        // 模拟网络延迟
        Thread.sleep(500)

        val username = request.username.takeIf { it.isNotBlank() } ?: generateRandomUsername()

        val user = User(
            id = System.currentTimeMillis().toString(),
            username = username,
            avatarUrl = null
        )

        mockUsers[username] = user

        return LoginResponse(
            success = true,
            user = user,
            token = "mock_token_${System.currentTimeMillis()}",
            message = "注册成功"
        )
    }

    /**
     * 获取随机用户名
     */
    fun generateRandomUsername(): String {
        return randomUsernames.random() + "_${(1000..9999).random()}"
    }

    /**
     * 获取随机头像颜色
     */
    fun getRandomAvatarColor(): Long {
        return avatarColors.random()
    }

    /**
     * 退出登录
     */
    fun logout(): Boolean {
        return true
    }
}
