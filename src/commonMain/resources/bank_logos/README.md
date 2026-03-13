# 银行Logo资源

本目录包含从 iconfont.cn (cid=23316) 抓取的310个银行Logo SVG文件。

## 文件结构

```
bank_logos/
├── bank_{id}.svg          # 310个银行SVG图标文件
├── metadata.json          # 图标元数据（名称、ID、文件名映射）
├── bank_mapping.json      # 银行名称到文件名映射
└── README.md              # 本文件
```

## 使用方式

### 1. 通过BankLogo组件使用（推荐）

```kotlin
import ui.components.BankLogo

task.bankTag?.let { bank ->
    BankLogo(
        bank = bank,
        modifier = Modifier.size(40.dp)
    )
}
```

### 2. 通过BankLogoRepository查询

```kotlin
import data.repository.BankLogoRepository

// 根据银行名称获取SVG文件名
val svgFile = BankLogoRepository.getLogoByBankName("中国工商银行")
// 返回: "bank_15132946.svg"

// 根据银行ID获取
val svgFile = BankLogoRepository.getLogoByBankId("1")

// 搜索银行
val results = BankLogoRepository.searchBankLogos("工商")
```

## 数据来源

- **来源**: iconfont.cn 银行LOGO合集 (cid=23316)
- **抓取时间**: 2026-03-13
- **图标总数**: 310个
- **格式**: SVG矢量图

## 注意事项

1. 银行名称匹配采用模糊匹配，会移除"银行"、"信用卡"等后缀
2. 如果找不到对应的SVG，会显示银行名称首字母作为占位符
3. Android平台需要添加 AndroidSVG 依赖才能正确渲染
4. JVM/Desktop平台需要 Skia 支持

## 后续优化

- [ ] 添加AndroidSVG依赖到build.gradle
- [ ] 完善JVM平台的SVG渲染
- [ ] 添加SVG缓存机制
- [ ] 支持SVG颜色动态修改
