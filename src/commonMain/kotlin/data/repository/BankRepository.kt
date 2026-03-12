package data.repository

import data.model.Bank
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 银行数据仓库
 * 提供中国前100银行的数据
 */
object BankRepository {

    // 中国主要银行列表（前100中的主要银行）
    private val banks = listOf(
        Bank("1", "中国工商银行", "ICBC", "https://www.icbc.com.cn/icbc/html/gonghang/images/logo.png"),
        Bank("2", "中国建设银行", "CCB", "https://www.ccb.com/cn/home/images/logo.png"),
        Bank("3", "中国农业银行", "ABC", "https://www.abchina.com/cn/images/logo.png"),
        Bank("4", "中国银行", "BOC", "https://www.boc.cn/images/logo.png"),
        Bank("5", "交通银行", "BCM", "https://www.bankcomm.com/BankCommSite/uploadFiles/logo.png"),
        Bank("6", "招商银行", "CMB", "https://www.cmbchina.com/images/logo.png"),
        Bank("7", "中国邮政储蓄银行", "PSBC", "https://www.psbc.com/cn/images/logo.png"),
        Bank("8", "兴业银行", "CIB", "https://www.cib.com.cn/cn/images/logo.png"),
        Bank("9", "浦发银行", "SPDB", "https://www.spdb.com.cn/images/logo.png"),
        Bank("10", "中信银行", "CITIC", "https://www.citicbank.com/images/logo.png"),
        Bank("11", "中国民生银行", "CMBC", "https://www.cmbc.com.cn/images/logo.png"),
        Bank("12", "中国光大银行", "CEB", "https://www.cebbank.com/images/logo.png"),
        Bank("13", "平安银行", "PAB", "https://bank.pingan.com/images/logo.png"),
        Bank("14", "华夏银行", "HXB", "https://www.hxb.com.cn/home/images/logo.png"),
        Bank("15", "北京银行", "BOB", "https://www.bankofbeijing.com.cn/images/logo.png"),
        Bank("16", "上海银行", "BOS", "https://www.bosc.cn/images/logo.png"),
        Bank("17", "江苏银行", "JSB", "https://www.jsbchina.cn/images/logo.png"),
        Bank("18", "宁波银行", "NBCB", "https://www.nbcb.com.cn/images/logo.png"),
        Bank("19", "南京银行", "NJCB", "https://www.njcb.com.cn/images/logo.png"),
        Bank("20", "杭州银行", "HZB", "https://www.hzbank.com.cn/images/logo.png"),
        Bank("21", "浙商银行", "CZB", "https://www.czbank.com/cn/images/logo.png"),
        Bank("22", "渤海银行", "CBHB", "https://www.cbhb.com.cn/images/logo.png"),
        Bank("23", "恒丰银行", "HFB", "https://www.hfbank.com.cn/images/logo.png"),
        Bank("24", "广发银行", "CGB", "https://www.cgbchina.com.cn/images/logo.png"),
        Bank("25", "恒丰银行", "HFB", "https://www.hfbank.com.cn/images/logo.png"),
        Bank("26", "北京农商银行", "BRCB", "https://www.bjrcb.com/images/logo.png"),
        Bank("27", "上海农商银行", "SRCB", "https://www.srcb.com/images/logo.png"),
        Bank("28", "广州银行", "GZCB", "https://www.gzcb.com.cn/images/logo.png"),
        Bank("29", "深圳农村商业银行", "SRCB", "https://www.4001961200.com/images/logo.png"),
        Bank("30", "天津银行", "TCCB", "https://www.tccb.com.cn/images/logo.png"),
        Bank("31", "成都银行", "CDC", "https://www.bocd.cn/images/logo.png"),
        Bank("32", "重庆银行", "CQB", "https://www.cqcbank.com/images/logo.png"),
        Bank("33", "长沙银行", "CSCB", "https://www.cscb.cn/images/logo.png"),
        Bank("34", "哈尔滨银行", "HBB", "https://www.hrbb.com.cn/images/logo.png"),
        Bank("35", "吉林银行", "JLB", "https://www.jlbank.com.cn/images/logo.png"),
        Bank("36", "大连银行", "DLB", "https://www.bankofdl.com/images/logo.png"),
        Bank("37", "盛京银行", "SJB", "https://www.shengjingbank.com.cn/images/logo.png"),
        Bank("38", "锦州银行", "JZB", "https://www.jinzhoubank.com/images/logo.png"),
        Bank("39", "齐鲁银行", "QLB", "https://www.qlbchina.com/images/logo.png"),
        Bank("40", "青岛银行", "QDB", "https://www.qdccb.com/images/logo.png"),
        Bank("41", "郑州银行", "ZZB", "https://www.zzbank.cn/images/logo.png"),
        Bank("42", "中原银行", "ZYB", "https://www.zybank.com.cn/images/logo.png"),
        Bank("43", "徽商银行", "HSB", "https://www.hsbank.com.cn/images/logo.png"),
        Bank("44", "苏州银行", "SZB", "https://www.suzhoubank.com/images/logo.png"),
        Bank("45", "无锡银行", "WXB", "https://www.wuxibank.com/images/logo.png"),
        Bank("46", "常熟农商银行", "CSRCB", "https://www.csrcbank.com/images/logo.png"),
        Bank("47", "江阴农商银行", "JRCB", "https://www.jybank.com/images/logo.png"),
        Bank("48", "张家港农商银行", "ZRCB", "https://www.zrcbank.com/images/logo.png"),
        Bank("49", "吴江农商银行", "WJRCB", "https://www.wjrcb.com/images/logo.png"),
        Bank("50", "昆山农商银行", "KSRCB", "https://www.ksrcb.cn/images/logo.png"),
        Bank("51", "江南农商银行", "JNRCB", "https://www.jnbank.com.cn/images/logo.png"),
        Bank("52", "绍兴银行", "SXB", "https://www.sxccb.com/images/logo.png"),
        Bank("53", "台州银行", "TZB", "https://www.tzbank.com/images/logo.png"),
        Bank("54", "温州银行", "WZCB", "https://www.wzbank.cn/images/logo.png"),
        Bank("55", "民泰银行", "MTB", "https://www.mintaibank.com/images/logo.png"),
        Bank("56", "金华银行", "JHC", "https://www.jhccb.cn/images/logo.png"),
        Bank("57", "嘉兴银行", "JXB", "https://www.bojx.com.cn/images/logo.png"),
        Bank("58", "湖州银行", "HZB", "https://www.hzbank.com.cn/images/logo.png"),
        Bank("59", "浙江泰隆商业银行", "TLB", "https://www.zjtlcb.com/images/logo.png"),
        Bank("60", "浙江民泰商业银行", "MTB", "https://www.mintaibank.com/images/logo.png"),
        Bank("61", "浙江稠州商业银行", "CZCB", "https://www.czcb.com.cn/images/logo.png"),
        Bank("62", "福建海峡银行", "HXB", "https://www.fjhxbank.com/images/logo.png"),
        Bank("63", "厦门银行", "XMB", "https://www.xmbankonline.com/images/logo.png"),
        Bank("64", "泉州银行", "QZCCB", "https://www.qzccbank.com/images/logo.png"),
        Bank("65", "江西银行", "JXC", "https://www.jx-bank.com/images/logo.png"),
        Bank("66", "九江银行", "JJB", "https://www.jjccb.com/images/logo.png"),
        Bank("67", "赣州银行", "GZCB", "https://www.gzzcb.com/images/logo.png"),
        Bank("68", "上饶银行", "SRB", "https://www.srbank.cn/images/logo.png"),
        Bank("69", "湖北银行", "HBC", "https://www.hubeibank.com.cn/images/logo.png"),
        Bank("70", "汉口银行", "HKB", "https://www.hkbchina.com/images/logo.png"),
        Bank("71", "长沙银行", "CSCB", "https://www.cscb.cn/images/logo.png"),
        Bank("72", "湖南银行", "HNB", "https://www.hunan-bank.com/images/logo.png"),
        Bank("73", "广东南粤银行", "NYB", "https://www.gdnybank.com/images/logo.png"),
        Bank("74", "东莞银行", "DGB", "https://www.dongguanbank.cn/images/logo.png"),
        Bank("75", "广东华兴银行", "HXB", "https://www.ghbchina.com/images/logo.png"),
        Bank("76", "华润银行", "CRB", "https://www.crbank.com.cn/images/logo.png"),
        Bank("77", "广西北部湾银行", "BBW", "https://www.bankofbbg.com/images/logo.png"),
        Bank("78", "桂林银行", "GLB", "https://www.guilinbank.com.cn/images/logo.png"),
        Bank("79", "柳州银行", "LZB", "https://www.lzccb.com/images/logo.png"),
        Bank("80", "重庆三峡银行", "SXB", "https://www.ccqtgb.com/images/logo.png"),
        Bank("81", "重庆银行", "CQB", "https://www.cqcbank.com/images/logo.png"),
        Bank("82", "成都银行", "CDC", "https://www.bocd.cn/images/logo.png"),
        Bank("83", "成都农商银行", "CDRCB", "https://www.cdrcb.com/images/logo.png"),
        Bank("84", "贵阳银行", "GYB", "https://www.bankgy.cn/images/logo.png"),
        Bank("85", "贵州银行", "GZB", "https://www.gzcb.com.cn/images/logo.png"),
        Bank("86", "富滇银行", "FDB", "https://www.fudian-bank.com/images/logo.png"),
        Bank("87", "云南红塔银行", "HTB", "https://www.ynhtbank.com/images/logo.png"),
        Bank("88", "西安银行", "XAB", "https://www.xacbank.com/images/logo.png"),
        Bank("89", "长安银行", "CAB", "https://www.ccabchina.com/images/logo.png"),
        Bank("90", "兰州银行", "LZB", "https://www.lzbank.com/images/logo.png"),
        Bank("91", "甘肃银行", "GSB", "https://www.gsbankchina.com/images/logo.png"),
        Bank("92", "青海银行", "QHB", "https://www.bankqh.com/images/logo.png"),
        Bank("93", "宁夏银行", "NXB", "https://www.bankofnx.com.cn/images/logo.png"),
        Bank("94", "乌鲁木齐银行", "WLMQ", "https://www.uccb.com.cn/images/logo.png"),
        Bank("95", "新疆银行", "XJB", "https://www.xjbank.com/images/logo.png"),
        Bank("96", "大连银行", "DLB", "https://www.bankofdl.com/images/logo.png"),
        Bank("97", "锦州银行", "JZB", "https://www.jinzhoubank.com/images/logo.png"),
        Bank("98", "葫芦岛银行", "HLD", "https://www.hldccb.com/images/logo.png"),
        Bank("99", "营口银行", "YKB", "https://www.bankofyk.com/images/logo.png"),
        Bank("100", "阜新银行", "FXB", "https://www.fuxinbank.com/images/logo.png")
    )

    /**
     * 获取所有银行列表
     */
    fun getAllBanks(): List<Bank> = banks

    /**
     * 根据ID获取银行
     */
    fun getBankById(id: String): Bank? = banks.find { it.id == id }

    /**
     * 搜索银行
     */
    fun searchBanks(query: String): List<Bank> {
        return banks.filter { it.name.contains(query, ignoreCase = true) }
    }

    /**
     * 从网络获取银行数据（预留接口）
     */
    suspend fun fetchBanksFromNetwork(): List<Bank> = withContext(Dispatchers.Default) {
        // 这里可以实现从网络API获取银行数据
        // 目前返回本地数据
        banks
    }
}
