# CollectMeta 项目调用图谱文档

## 项目概述

CollectMeta 是一个用于收集和管理各类媒体元数据的 Android 应用程序，支持图书、电影、电视节目和游戏等媒体类型。该应用程序与 Notion 集成，使用户能够将收集的媒体元数据同步到其 Notion 数据库。

## 调用图谱

以下是从用户界面功能到每一次函数调用的详细图谱，涵盖了主要功能模块的调用流程。

### 1. Notion配置功能调用链

#### 1.1 加载Notion配置流程

```
用户进入Settings页面
 ↓
SettingsFragment.onViewCreated()
 ↓
SettingsViewModel.init() → loadNotionConfig()
 ↓
SettingsViewModel.loadNotionConfig() → _isLoading.value = true
 ↓
getApiConfigUseCase("notion") → 从Repository获取数据
 ↓
ApiConfigRepository.getConfigById(configId) → 从数据库查询
 ↓
数据返回到ViewModel → _notionToken.value = notionConfig.apiKey
                     → _notionUrl.value = notionConfig.baseUrl
 ↓
SettingsFragment.observeViewModel() → 观察数据变化
 ↓
UI更新 → binding.notionTokenInput.setText(token)
       → binding.notionUrlInput.setText(url)
 ↓
_isLoading.value = false → 隐藏进度条
```

#### 1.2 保存Notion配置流程

```
用户输入完成并且输入框失去焦点
 ↓
SettingsFragment.setupFocusListeners() → 监听失焦事件
 ↓
viewModel.setNotionToken(token) / viewModel.setNotionUrl(url)
 ↓
SettingsViewModel.setNotionToken() → 更新_notionToken.value
                                   → saveApiConfigUseCase.updateApiKey() 或
SettingsViewModel.setNotionUrl()   → 更新_notionUrl.value
                                   → saveNotionConfig()
 ↓
SettingsViewModel.saveNotionConfig() → 创建ApiConfig对象
                                     → saveApiConfigUseCase(apiConfig)
 ↓
SaveApiConfigUseCase.invoke(apiConfig) → 调用Repository保存数据
 ↓
ApiConfigRepository.saveConfig(apiConfig) → 保存到数据库
 ↓
结果返回到ViewModel → _saveStatus.value = SaveStatus.SUCCESS/ERROR
 ↓
SettingsFragment.observeViewModel() → 观察保存状态
 ↓
保存失败时UI更新 → showToast("保存失败，请检查配置")
```

### 2. 图书配置功能调用链

#### 2.1 进入图书配置页面

```
用户点击"Books Configuration"
 ↓
SettingsFragment.setupClickListeners() → Books配置点击事件
 ↓
findNavController().navigate(R.id.action_navigation_settings_to_bookConfigFragment)
 ↓
导航到BookConfigFragment
```

#### 2.2 加载图书配置流程

```
BookConfigFragment.onViewCreated()
 ↓
viewModel.loadConfigValues()
 ↓
BookConfigViewModel.loadConfigValues() → _isLoading.value = true
 ↓
getApiConfigUseCase("notion_book") → 从Repository获取数据
getApiConfigUseCase("open_library") → 从Repository获取数据
getApiConfigUseCase("google_books") → 从Repository获取数据
 ↓
ApiConfigRepository.getConfigById(configId) → 从数据库查询多个配置
 ↓
数据返回到ViewModel → 创建并更新BookConfig对象
                     → _bookConfig.value = savedConfig
 ↓
BookConfigFragment.observeViewModel() → 观察数据变化
 ↓
UI更新 → binding.notionDatabaseId.setText(config.notionDatabaseId)
       → binding.openLibraryApiUrl.setText(config.openLibraryApiUrl)
       → binding.googleBooksApiUrl.setText(config.googleBooksApiUrl)
       → binding.googleBooksApiKey.setText(config.googleBooksApiKey)
 ↓
_isLoading.value = false → 隐藏进度条
```

#### 2.3 保存图书配置流程

```
用户点击保存按钮
 ↓
BookConfigFragment.saveConfiguration()
 ↓
创建BookConfig对象 → 从输入框获取值
 ↓
viewModel.saveConfig(config)
 ↓
BookConfigViewModel.saveConfig() → _isLoading.value = true
                                 → validateConfig() 验证配置
 ↓
分别保存各配置 → saveNotionBookConfig()
                → saveOpenLibraryConfig()
                → saveGoogleBooksConfig()
 ↓
每个保存方法 → 创建ApiConfig对象
              → saveApiConfigUseCase(apiConfig)
 ↓
SaveApiConfigUseCase.invoke(apiConfig) → 调用Repository保存数据
 ↓
ApiConfigRepository.saveConfig(apiConfig) → 保存到数据库
 ↓
结果返回到ViewModel → _saveSuccess.value = true/false
                     → _isLoading.value = false
 ↓
BookConfigFragment.observeViewModel() → 观察保存状态
 ↓
UI更新 → 成功时显示提示并返回上一页
       → 失败时显示错误信息
```

## 文档更新

### 版本信息

- **文档版本**：1.0.0
- **最后更新日期**：2023年11月24日
- **作者**：CollectMeta开发团队

### 附录

- **SVG文件**：包含各个配置界面的设计图
- **Markdown文件**：包含项目开发计划和功能特性

---

以上是项目的调用图谱文档，详细描述了从用户界面到每一次函数调用的完整流程，便于开发和维护。 