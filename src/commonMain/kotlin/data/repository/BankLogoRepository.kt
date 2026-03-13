package data.repository

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

/**
 * 银行Logo资源管理
 * 管理从iconfont抓取的310个银行SVG图标
 */
object BankLogoRepository {
    
    // 银行名称到SVG文件名的映射
    private var bankMapping: Map<String, String> = emptyMap()
    
    // 银行ID到SVG文件名的映射
    private var idMapping: Map<String, String> = emptyMap()
    
    // 元数据列表
    private var metadata: List<BankLogoMetadata> = emptyList()
    
    data class BankLogoMetadata(
        val id: String,
        val name: String,
        val coreName: String,
        val unicode: String,
        val filename: String
    )
    
    /**
     * 初始化（在应用启动时调用）
     */
    fun initialize() {
        // 加载映射数据
        // 注意：实际加载需要在平台层实现（Android使用AssetManager，Desktop使用ClassLoader）
    }
    
    /**
     * 根据银行名称获取SVG文件名
     */
    fun getLogoByBankName(bankName: String): String? {
        // 清理银行名称
        val cleanedName = cleanBankName(bankName)
        
        // 尝试直接匹配
        bankMapping[cleanedName]?.let { return it }
        
        // 尝试模糊匹配
        return bankMapping.entries.find { 
            cleanedName.contains(it.key) || it.key.contains(cleanedName)
        }?.value
    }
    
    /**
     * 根据银行ID获取SVG文件名
     */
    fun getLogoByBankId(bankId: String): String? {
        return idMapping[bankId]
    }
    
    /**
     * 获取所有银行Logo列表
     */
    fun getAllBankLogos(): List<BankLogoMetadata> = metadata
    
    /**
     * 搜索银行Logo
     */
    fun searchBankLogos(query: String): List<BankLogoMetadata> {
        return metadata.filter { 
            it.name.contains(query, ignoreCase = true) || 
            it.coreName.contains(query, ignoreCase = true)
        }
    }
    
    /**
     * 清理银行名称
     */
    private fun cleanBankName(name: String): String {
        return name
            .replace("银行", "")
            .replace("信用卡", "")
            .replace("借记卡", "")
            .replace("金卡", "")
            .replace("普卡", "")
            .replace("白金卡", "")
            .trim()
    }
}
