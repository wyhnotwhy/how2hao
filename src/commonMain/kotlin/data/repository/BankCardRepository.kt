package data.repository

import data.model.BankCard
import data.model.BankCardType
import data.model.AddBankCardRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 银行卡数据仓库
 * 使用本地内存存储（实际项目中可使用SharedPreferences/DataStore）
 */
object BankCardRepository {

    // 内存存储的银行卡列表
    private val _bankCards = MutableStateFlow<List<BankCard>>(emptyList())
    val bankCards: StateFlow<List<BankCard>> = _bankCards.asStateFlow()

    // 初始化一些示例数据（可选）
    init {
        // 可以在这里加载本地存储的数据
        // loadFromLocalStorage()
    }

    /**
     * 获取所有银行卡
     */
    fun getAllBankCards(): List<BankCard> = _bankCards.value

    /**
     * 根据ID获取银行卡
     */
    fun getBankCardById(id: String): BankCard? {
        return _bankCards.value.find { it.id == id }
    }

    /**
     * 添加银行卡
     */
    fun addBankCard(request: AddBankCardRequest): BankCard {
        val newCard = BankCard(
            id = generateCardId(),
            bankId = request.bankId,
            bankName = request.bankName,
            cardType = request.cardType,
            provinceCode = request.provinceCode,
            provinceName = request.provinceName,
            cityCode = request.cityCode,
            cityName = request.cityName,
            cardLastFour = request.cardLastFour,
            creditLimit = request.creditLimit,
            annualFee = request.annualFee
        )

        _bankCards.value = _bankCards.value + newCard
        // 保存到本地存储
        // saveToLocalStorage()

        return newCard
    }

    /**
     * 删除银行卡
     */
    fun deleteBankCard(cardId: String) {
        _bankCards.value = _bankCards.value.filter { it.id != cardId }
        // saveToLocalStorage()
    }

    /**
     * 更新银行卡
     */
    fun updateBankCard(card: BankCard) {
        _bankCards.value = _bankCards.value.map {
            if (it.id == card.id) card else it
        }
        // saveToLocalStorage()
    }

    /**
     * 按银行分组获取银行卡
     */
    fun getBankCardsGroupedByBank(): Map<String, List<BankCard>> {
        return _bankCards.value.groupBy { it.bankName }
    }

    /**
     * 获取信用卡列表
     */
    fun getCreditCards(): List<BankCard> {
        return _bankCards.value.filter { it.cardType == BankCardType.CREDIT }
    }

    /**
     * 获取借记卡列表
     */
    fun getDebitCards(): List<BankCard> {
        return _bankCards.value.filter { it.cardType == BankCardType.DEBIT }
    }

    /**
     * 生成唯一ID
     */
    private fun generateCardId(): String {
        return "card_${System.currentTimeMillis()}_${(1000..9999).random()}"
    }

    /**
     * 获取银行卡类型显示名称
     */
    fun getCardTypeDisplayName(cardType: BankCardType): String {
        return when (cardType) {
            BankCardType.DEBIT -> "借记卡"
            BankCardType.CREDIT -> "信用卡"
        }
    }

    /**
     * 获取银行卡类型图标（emoji或字符）
     */
    fun getCardTypeIcon(cardType: BankCardType): String {
        return when (cardType) {
            BankCardType.DEBIT -> "💳"
            BankCardType.CREDIT -> "💎"
        }
    }

    // TODO: 实现本地存储
    // private fun saveToLocalStorage() { }
    // private fun loadFromLocalStorage() { }
}
