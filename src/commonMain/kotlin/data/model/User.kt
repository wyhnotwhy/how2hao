package data.model

/**
 * 用户数据模型
 */
data class User(
    val id: String,
    val username: String,
    val avatarUrl: String? = null
)

/**
 * 登录请求
 */
data class LoginRequest(
    val username: String,
    val password: String
)

/**
 * 登录响应
 */
data class LoginResponse(
    val success: Boolean,
    val user: User? = null,
    val token: String? = null,
    val message: String? = null
)
