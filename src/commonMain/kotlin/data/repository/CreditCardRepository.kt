package data.repository

import data.model.CreditCard
import data.model.CreditCardLevel
import data.model.CardOrganization
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 信用卡/借记卡数据仓库
 * 数据来源：我爱卡 (51credit.com)
 */
object CreditCardRepository {

    // 信用卡数据（基于我爱卡真实数据）
    private val creditCards = listOf(
        // 中信银行
        CreditCard(
            id = "citic_001",
            bankId = "citic",
            bankName = "中信银行",
            cardName = "颜卡信用卡",
            cardLevel = CreditCardLevel.GOLD,
            cardOrganization = CardOrganization.UNIONPAY,
            currency = "人民币",
            imageUrl = "https://kaku.51credit.com/citic/zxykdz/",
            benefits = listOf("新户达标送JBL蓝牙耳机", "网购有积分", "9元看大片、精彩365"),
            annualFee = "免首年年费，消费5笔免次年年费",
            cardType = "信用卡"
        ),
        CreditCard(
            id = "citic_002",
            bankId = "citic",
            bankName = "中信银行",
            cardName = "标准车主金卡",
            cardLevel = CreditCardLevel.GOLD,
            cardOrganization = CardOrganization.UNIONPAY,
            currency = "人民币",
            imageUrl = "https://kaku.51credit.com/citic/zxbzczk/",
            benefits = listOf("为你加油，乐享返还", "可达80元/月加油返还奖励", "0元洗车"),
            annualFee = "免首年年费",
            cardType = "信用卡"
        ),
        CreditCard(
            id = "citic_003",
            bankId = "citic",
            bankName = "中信银行",
            cardName = "i白金信用卡",
            cardLevel = CreditCardLevel.PLATINUM,
            cardOrganization = CardOrganization.UNIONPAY,
            currency = "人民币",
            imageUrl = "https://kaku.51credit.com/citic/ika/",
            benefits = listOf("不要年费还能赚钱的白金卡", "IMAX五折观影", "2小时1000元延误险"),
            annualFee = "免年费",
            cardType = "信用卡"
        ),
        CreditCard(
            id = "citic_004",
            bankId = "citic",
            bankName = "中信银行",
            cardName = "银联标准IC信用卡",
            cardLevel = CreditCardLevel.GOLD,
            cardOrganization = CardOrganization.UNIONPAY,
            currency = "人民币",
            imageUrl = "https://kaku.51credit.com/citic/zxylbzIC/",
            benefits = listOf("9分享兑大杯手调星巴克", "9元享购双人观影票", "周三、周六消费半价"),
            annualFee = "免首年年费",
            cardType = "信用卡"
        ),

        // 交通银行
        CreditCard(
            id = "bocomm_001",
            bankId = "bocomm",
            bankName = "交通银行",
            cardName = "Y-POWER信用卡白卡",
            cardLevel = CreditCardLevel.GOLD,
            cardOrganization = CardOrganization.UNIONPAY,
            currency = "人民币",
            imageUrl = "https://kaku.51credit.com/bocomm/y-power/",
            benefits = listOf("新户5积分轻松购", "年轻首选，取现百分百", "分期消费门槛低至500元"),
            annualFee = "免首年年费",
            cardType = "信用卡"
        ),
        CreditCard(
            id = "bocomm_002",
            bankId = "bocomm",
            bankName = "交通银行",
            cardName = "标准信用卡",
            cardLevel = CreditCardLevel.GOLD,
            cardOrganization = CardOrganization.UNIONPAY,
            currency = "人民币",
            imageUrl = "https://kaku.51credit.com/bocomm/jhbiaozhun/",
            benefits = listOf("新户5积分轻松购", "超市、加油全年返5%", "免息还款期最长达56天"),
            annualFee = "免首年年费",
            cardType = "信用卡"
        ),
        CreditCard(
            id = "bocomm_003",
            bankId = "bocomm",
            bankName = "交通银行",
            cardName = "沃尔玛卡",
            cardLevel = CreditCardLevel.GOLD,
            cardOrganization = CardOrganization.UNIONPAY,
            currency = "人民币",
            imageUrl = "https://kaku.51credit.com/bocomm/walmart/",
            benefits = listOf("周五乐享5%刷卡金", "沃尔玛超市周六至周四乐享2.5%刷卡金", "积分回馈最高1%"),
            annualFee = "免首年年费",
            cardType = "信用卡"
        ),
        CreditCard(
            id = "bocomm_004",
            bankId = "bocomm",
            bankName = "交通银行",
            cardName = "永达汽车信用卡",
            cardLevel = CreditCardLevel.GOLD,
            cardOrganization = CardOrganization.UNIONPAY,
            currency = "人民币",
            imageUrl = "https://kaku.51credit.com/bocomm/yongda/",
            benefits = listOf("每周五加油乐享5%优惠", "永达专卖店购车，每年免费6次洗车", "生日月88元维修券、购车礼金券588元"),
            annualFee = "免首年年费",
            cardType = "信用卡"
        ),

        // 光大银行
        CreditCard(
            id = "ceb_001",
            bankId = "ceb",
            bankName = "光大银行",
            cardName = "小黄鸭酷黑主题卡",
            cardLevel = CreditCardLevel.GOLD,
            cardOrganization = CardOrganization.UNIONPAY,
            currency = "人民币",
            imageUrl = "https://kaku.51credit.com/ceb/gdxhy/",
            benefits = listOf("消费可享85折VIP特权", "永乐票务有机会满300减100", "可获100万元出行意外保险"),
            annualFee = "免首年年费",
            cardType = "信用卡"
        ),
        CreditCard(
            id = "ceb_002",
            bankId = "ceb",
            bankName = "光大银行",
            cardName = "福IC信用卡",
            cardLevel = CreditCardLevel.GOLD,
            cardOrganization = CardOrganization.UNIONPAY,
            currency = "人民币",
            imageUrl = "https://kaku.51credit.com/ceb/fu/",
            benefits = listOf("微信/QQ支付最高双倍积分", "嗨购京东,满100减30", "卡到福到 金福到家"),
            annualFee = "免首年年费",
            cardType = "信用卡"
        ),

        // 招商银行
        CreditCard(
            id = "cmb_001",
            bankId = "cmb",
            bankName = "招商银行",
            cardName = "YOUNG卡青年版白卡",
            cardLevel = CreditCardLevel.GOLD,
            cardOrganization = CardOrganization.UNIONPAY,
            currency = "人民币",
            imageUrl = "https://kaku.51credit.com/cmb/youngka/",
            benefits = listOf("每月首笔取现免手续费", "100%的取现额度", "每月赠送积分最高2000分"),
            annualFee = "免首年年费",
            cardType = "信用卡"
        ),
        CreditCard(
            id = "cmb_002",
            bankId = "cmb",
            bankName = "招商银行",
            cardName = "Car Card汽车卡",
            cardLevel = CreditCardLevel.GOLD,
            cardOrganization = CardOrganization.UNIONPAY_MASTERCARD,
            currency = "人民币+美元",
            imageUrl = "https://kaku.51credit.com/cmb/ZSCC/",
            benefits = listOf("专享最高5%加油金返现", "分期购车享实惠", "异型卡实用美观"),
            annualFee = "免首年年费",
            cardType = "信用卡"
        ),
        CreditCard(
            id = "cmb_003",
            bankId = "cmb",
            bankName = "招商银行",
            cardName = "银联单币卡",
            cardLevel = CreditCardLevel.GOLD,
            cardOrganization = CardOrganization.UNIONPAY,
            currency = "人民币",
            imageUrl = "https://kaku.51credit.com/cmb/yinlianka/",
            benefits = listOf("小积分享美食", "新户刷卡可赠1000积分", "积分永久有效"),
            annualFee = "免首年年费",
            cardType = "信用卡"
        ),

        // 兴业银行
        CreditCard(
            id = "cib_001",
            bankId = "cib",
            bankName = "兴业银行",
            cardName = "淘宝网联名IC芯片卡",
            cardLevel = CreditCardLevel.GOLD,
            cardOrganization = CardOrganization.UNIONPAY,
            currency = "人民币",
            imageUrl = "https://kaku.51credit.com/cib/taobao/",
            benefits = listOf("网上购物给积分", "每个账单月首笔取现免手续费", "淘宝天猫满118立减18"),
            annualFee = "免首年年费",
            cardType = "信用卡"
        ),
        CreditCard(
            id = "cib_002",
            bankId = "cib",
            bankName = "兴业银行",
            cardName = "银联标准IC信用卡",
            cardLevel = CreditCardLevel.GOLD,
            cardOrganization = CardOrganization.UNIONPAY,
            currency = "人民币",
            imageUrl = "https://kaku.51credit.com/cib/xybzsb/",
            benefits = listOf("最低6积分兑星巴克", "200万航意险", "淘宝天猫满118立减18"),
            annualFee = "免首年年费",
            cardType = "信用卡"
        ),

        // 广发银行
        CreditCard(
            id = "gdb_001",
            bankId = "gdb",
            bankName = "广发银行",
            cardName = "one卡",
            cardLevel = CreditCardLevel.GOLD,
            cardOrganization = CardOrganization.UNIONPAY,
            currency = "人民币",
            imageUrl = "https://kaku.51credit.com/gdb/gfonek/",
            benefits = listOf("开卡送80权益金", "随时兑换享更多权益", "多款卡版任意选择"),
            annualFee = "免首年年费",
            cardType = "信用卡"
        ),
        CreditCard(
            id = "gdb_002",
            bankId = "gdb",
            bankName = "广发银行",
            cardName = "易车联名卡",
            cardLevel = CreditCardLevel.GOLD,
            cardOrganization = CardOrganization.UNIONPAY,
            currency = "人民币",
            imageUrl = "https://kaku.51credit.com/gdb/GFYC/",
            benefits = listOf("刷卡加油3%现金返还", "每季度35元汽车保养代金券", "保险额外5%补贴"),
            annualFee = "免首年年费",
            cardType = "信用卡"
        ),
        CreditCard(
            id = "gdb_003",
            bankId = "gdb",
            bankName = "广发银行",
            cardName = "车主卡",
            cardLevel = CreditCardLevel.GOLD,
            cardOrganization = CardOrganization.UNIONPAY,
            currency = "人民币",
            imageUrl = "https://kaku.51credit.com/gdb/car/",
            benefits = listOf("刷卡加油1%现金返还或5倍积分", "免费赠送高额意外险", "免费赠送道路救援服务"),
            annualFee = "免首年年费",
            cardType = "信用卡"
        ),
        CreditCard(
            id = "gdb_004",
            bankId = "gdb",
            bankName = "广发银行",
            cardName = "新聪明卡",
            cardLevel = CreditCardLevel.GOLD,
            cardOrganization = CardOrganization.UNIONPAY,
            currency = "人民币",
            imageUrl = "https://kaku.51credit.com/gdb/cmk/",
            benefits = listOf("透现/分期5倍积分", "积分折抵消费余额", "周三积分1折兑"),
            annualFee = "免首年年费",
            cardType = "信用卡"
        ),

        // 浦发银行
        CreditCard(
            id = "spd_001",
            bankId = "spd",
            bankName = "浦发银行",
            cardName = "腾讯联名卡",
            cardLevel = CreditCardLevel.PLATINUM,
            cardOrganization = CardOrganization.UNIONPAY,
            currency = "人民币",
            imageUrl = "https://kaku.51credit.com/spd/pftengxunka/",
            benefits = listOf("新户微信支付享3倍积分", "快捷支付有红包", "礼券银行天天美食五折"),
            annualFee = "免首年年费",
            cardType = "信用卡"
        ),
        CreditCard(
            id = "spd_002",
            bankId = "spd",
            bankName = "浦发银行",
            cardName = "青春卡",
            cardLevel = CreditCardLevel.GOLD,
            cardOrganization = CardOrganization.UNIONPAY,
            currency = "人民币",
            imageUrl = "https://kaku.51credit.com/spd/pfqingchun/",
            benefits = listOf("全年免费双人观影", "消费额度+梦想金总额度15,000起", "每月刷三笔，次月赠2次观影"),
            annualFee = "免首年年费",
            cardType = "信用卡"
        ),

        // 民生银行
        CreditCard(
            id = "cmbc_001",
            bankId = "cmbc",
            bankName = "民生银行",
            cardName = "故宫主题卡-如朕亲临版",
            cardLevel = CreditCardLevel.GOLD,
            cardOrganization = CardOrganization.UNIONPAY,
            currency = "人民币",
            imageUrl = "https://kaku.51credit.com/cmbc/msgugongk/",
            benefits = listOf("获赠故宫限量文创", "精美卡面，任你选择", "御用之物，必是好物"),
            annualFee = "免首年年费",
            cardType = "信用卡"
        ),
        CreditCard(
            id = "cmbc_002",
            bankId = "cmbc",
            bankName = "民生银行",
            cardName = "标准卡",
            cardLevel = CreditCardLevel.GOLD,
            cardOrganization = CardOrganization.UNIONPAY,
            currency = "人民币",
            imageUrl = "https://kaku.51credit.com/cmbc/msbiaozhun/",
            benefits = listOf("免首年年费", "三秒核发", "预借现金 民生解您不时之需"),
            annualFee = "免首年年费",
            cardType = "信用卡"
        ),
        CreditCard(
            id = "cmbc_003",
            bankId = "cmbc",
            bankName = "民生银行",
            cardName = "车车信用卡（经典版）",
            cardLevel = CreditCardLevel.GOLD,
            cardOrganization = CardOrganization.UNIONPAY,
            currency = "人民币",
            imageUrl = "https://kaku.51credit.com/cmbc/chechek/",
            benefits = listOf("1元洗车，最高返500元加油金", "1元机场停车", "车险投保最高享商业险15%加油卡回馈"),
            annualFee = "免首年年费",
            cardType = "信用卡"
        ),
        CreditCard(
            id = "cmbc_004",
            bankId = "cmbc",
            bankName = "民生银行",
            cardName = "女人花标准金卡",
            cardLevel = CreditCardLevel.GOLD,
            cardOrganization = CardOrganization.UNIONPAY,
            currency = "人民币",
            imageUrl = "https://kaku.51credit.com/cmbc/nvrenhua/",
            benefits = listOf("消费满额每月最高获120元花颜贴心礼", "周四独享双倍积分"),
            annualFee = "免首年年费",
            cardType = "信用卡"
        ),

        // 华夏银行
        CreditCard(
            id = "hxb_001",
            bankId = "hxb",
            bankName = "华夏银行",
            cardName = "爱奇艺悦看卡优雅蓝",
            cardLevel = CreditCardLevel.GOLD,
            cardOrganization = CardOrganization.UNIONPAY,
            currency = "人民币",
            imageUrl = "https://kaku.51credit.com/hxb/hxaiqiyi/",
            benefits = listOf("首刷悦看送6个月会员", "月悦刷好看再送6个月会员", "折扣悦看独家VIP会员8折无限享"),
            annualFee = "免首年年费",
            cardType = "信用卡"
        ),
        CreditCard(
            id = "hxb_002",
            bankId = "hxb",
            bankName = "华夏银行",
            cardName = "标准卡",
            cardLevel = CreditCardLevel.GOLD,
            cardOrganization = CardOrganization.UNIONPAY,
            currency = "人民币",
            imageUrl = "https://kaku.51credit.com/hxb/hxiabiaozhun/",
            benefits = listOf("首年免年费", "全国指定机场1元停车", "超长120小时失卡保障"),
            annualFee = "首年免年费",
            cardType = "信用卡"
        ),

        // 平安银行
        CreditCard(
            id = "pingan_001",
            bankId = "pingan",
            bankName = "平安银行",
            cardName = "车主卡",
            cardLevel = CreditCardLevel.GOLD,
            cardOrganization = CardOrganization.UNIONPAY,
            currency = "人民币",
            imageUrl = "https://kaku.51credit.com/pingan/chezhu/",
            benefits = listOf("110万元全车人员意外险", "消费积分可全额兑平安车险保费", "天天加油享88折，免费道路救援"),
            annualFee = "首年免年费",
            cardType = "信用卡"
        ),
        CreditCard(
            id = "pingan_002",
            bankId = "pingan",
            bankName = "平安银行",
            cardName = "标准卡",
            cardLevel = CreditCardLevel.GOLD,
            cardOrganization = CardOrganization.UNIONPAY_JCB,
            currency = "人民币+日元",
            imageUrl = "https://kaku.51credit.com/pingan/pinganbiaozhun/",
            benefits = listOf("首刷赠全方位交通意外保险", "金卡首刷赠高额燃气意外保险", "挂失前72小时失卡保障"),
            annualFee = "首年免年费",
            cardType = "信用卡"
        ),

        // 中国银行
        CreditCard(
            id = "boc_001",
            bankId = "boc",
            bankName = "中国银行",
            cardName = "神偷奶爸系列信用卡白金卡",
            cardLevel = CreditCardLevel.PLATINUM,
            cardOrganization = CardOrganization.UNIONPAY,
            currency = "人民币",
            imageUrl = "https://kaku.51credit.com/boc/zdushibinfen/",
            benefits = listOf("萌卡专属，小黄人周边产品优惠购", "免首年年费", "分期手续费优惠最高8.5折"),
            annualFee = "免首年年费",
            cardType = "信用卡"
        ),
        CreditCard(
            id = "boc_002",
            bankId = "boc",
            bankName = "中国银行",
            cardName = "环球通爱驾汽车卡",
            cardLevel = CreditCardLevel.TITANIUM,
            cardOrganization = CardOrganization.MASTERCARD,
            currency = "人民币",
            imageUrl = "https://kaku.51credit.com/boc/zyaijiaqicheka/",
            benefits = listOf("消费/账单分期可享手续费8.5折优惠", "加油类交易可累积0.5倍交易积分", "申请环球通爱驾汽车卡，同时可获得全币种万事达钛金卡"),
            annualFee = "免首年年费",
            cardType = "信用卡"
        ),

        // 上海银行
        CreditCard(
            id = "bos_001",
            bankId = "bos",
            bankName = "上海银行",
            cardName = "淘宝联名信用卡",
            cardLevel = CreditCardLevel.GOLD,
            cardOrganization = CardOrganization.UNIONPAY,
            currency = "人民币",
            imageUrl = "https://kaku.51credit.com/bos/shtblm/",
            benefits = listOf("积分奖励，一举两得", "100％预借现金", "刷卡免年费"),
            annualFee = "免首年年费",
            cardType = "信用卡"
        ),
        CreditCard(
            id = "bos_002",
            bankId = "bos",
            bankName = "上海银行",
            cardName = "信用卡标准卡",
            cardLevel = CreditCardLevel.GOLD,
            cardOrganization = CardOrganization.UNIONPAY,
            currency = "人民币",
            imageUrl = "https://kaku.51credit.com/bos/ylbzk/",
            benefits = listOf("首年免年费，消费三笔免次年年费", "永久积分 惊喜回馈", "分期付款更自由"),
            annualFee = "首年免年费",
            cardType = "信用卡"
        ),

        // 汇丰银行
        CreditCard(
            id = "hsbc_001",
            bankId = "hsbc",
            bankName = "汇丰银行",
            cardName = "生活信用卡",
            cardLevel = CreditCardLevel.GOLD,
            cardOrganization = CardOrganization.UNIONPAY,
            currency = "人民币",
            imageUrl = "https://kaku.51credit.com/hsbcbank/hfsh/",
            benefits = listOf("无限次五星酒店豪华自助买一赠一", "每月2次9元看电影", "网络消费2倍积分"),
            annualFee = "免首年年费",
            cardType = "信用卡"
        )
    )

    /**
     * 获取所有信用卡
     */
    fun getAllCreditCards(): List<CreditCard> = creditCards

    /**
     * 根据银行获取信用卡
     */
    fun getCreditCardsByBank(bankId: String): List<CreditCard> {
        return creditCards.filter { it.bankId == bankId }
    }

    /**
     * 根据卡片等级筛选
     */
    fun getCreditCardsByLevel(level: CreditCardLevel): List<CreditCard> {
        return creditCards.filter { it.cardLevel == level }
    }

    /**
     * 根据卡组织筛选
     */
    fun getCreditCardsByOrganization(organization: CardOrganization): List<CreditCard> {
        return creditCards.filter { it.cardOrganization == organization }
    }

    /**
     * 搜索信用卡
     */
    fun searchCreditCards(query: String): List<CreditCard> {
        return creditCards.filter { 
            it.cardName.contains(query, ignoreCase = true) ||
            it.bankName.contains(query, ignoreCase = true)
        }
    }

    /**
     * 根据ID获取信用卡
     */
    fun getCreditCardById(id: String): CreditCard? {
        return creditCards.find { it.id == id }
    }

    /**
     * 获取支持的银行列表
     */
    fun getSupportedBanks(): List<Pair<String, String>> {
        return creditCards
            .groupBy { it.bankId }
            .map { (bankId, cards) -> 
                Pair(bankId, cards.first().bankName)
            }
            .sortedBy { it.second }
    }

    /**
     * 从网络获取数据（预留接口）
     */
    suspend fun fetchFromNetwork(): List<CreditCard> = withContext(Dispatchers.Default) {
        // 这里可以实现从我爱卡API抓取数据
        creditCards
    }
}
