package data.model

/**
 * 地区数据模型
 */
data class Province(
    val code: String,
    val name: String,
    val cities: List<City>
)

data class City(
    val code: String,
    val name: String
)

/**
 * 用户定位信息
 */
data class UserLocation(
    val province: String,
    val city: String,
    val latitude: Double? = null,
    val longitude: Double? = null
)
