package data.model

/**
 * 银行卡类型
 */
enum class BankCardType {
    DEBIT,      // 借记卡
    CREDIT      // 信用卡
}

/**
 * 银行卡数据模型
 */
data class BankCard(
    val id: String,
    val bankId: String,                 // 所属银行ID
    val bankName: String,               // 所属银行名称
    val cardType: BankCardType,         // 卡片类型
    val provinceCode: String,           // 归属地省份代码
    val provinceName: String,           // 归属地省份名称
    val cityCode: String,               // 归属地城市代码
    val cityName: String,               // 归属地城市名称
    val cardLastFour: String,           // 卡号后4位
    val creditLimit: Double?,           // 额度（信用卡才有）
    val annualFee: Double = 0.0,        // 年费（信用卡才有，默认0）
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * 添加银行卡请求
 */
data class AddBankCardRequest(
    val bankId: String,
    val bankName: String,
    val cardType: BankCardType,
    val provinceCode: String,
    val provinceName: String,
    val cityCode: String,
    val cityName: String,
    val cardLastFour: String,
    val creditLimit: Double? = null,
    val annualFee: Double = 0.0
)
