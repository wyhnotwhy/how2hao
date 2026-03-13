package data.model

/**
 * 卡片等级
 */
enum class CreditCardLevel {
    STANDARD,   // 普卡
    GOLD,       // 金卡
    PLATINUM,   // 白金卡
    TITANIUM,   // 钛金卡
    DIAMOND,    // 钻石卡
    INFINITE,   // 无限卡
    WORLD       // 世界卡
}

/**
 * 卡组织
 */
enum class CardOrganization {
    UNIONPAY,           // 银联
    UNIONPAY_VISA,      // 银联+VISA
    UNIONPAY_MASTERCARD,// 银联+MasterCard
    UNIONPAY_JCB,       // 银联+JCB
    UNIONPAY_AMEX,      // 银联+运通
    VISA,               // VISA
    MASTERCARD,         // MasterCard
    AMEX,               // 运通
    JCB                 // JCB
}

/**
 * 信用卡/借记卡数据模型
 */
data class CreditCard(
    val id: String,
    val bankId: String,                 // 银行ID
    val bankName: String,               // 银行名称
    val cardName: String,               // 卡片名称
    val cardLevel: CreditCardLevel,     // 卡片等级
    val cardOrganization: CardOrganization, // 卡组织
    val currency: String,               // 币种
    val imageUrl: String?,              // 卡片图片URL
    val benefits: List<String>,         // 权益列表
    val annualFee: String,              // 年费说明
    val cardType: String                // 卡片类型（信用卡/借记卡）
)

/**
 * 卡片分类
 */
enum class CardCategory {
    STANDARD,   // 标准卡
    CAR,        // 车主卡
    SHOPPING,   // 购物卡
    TRAVEL,     // 旅游卡
    AVIATION,   // 航空卡
    HOTEL,      // 酒店卡
    GAME,       // 游戏卡
    CARTOON,    // 卡通卡
    CHARITY,    // 慈善卡
    BUSINESS,   // 商务卡
    FOOD        // 美食卡
}
