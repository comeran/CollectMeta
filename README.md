# CollectMeta - 媒体收藏助手

一个用于收集和管理电影、电视剧、游戏、书籍元数据的Android应用，支持与Notion同步。

## 项目概述

- [x] 基础架构搭建
  - [x] 项目初始化与依赖配置
  - [x] Clean Architecture架构实现
  - [x] CI/CD配置
  - [x] 代码规范和静态分析设置

- [x] 用户认证模块
  - [x] Notion API授权集成
  - [x] 用户授权流程
  - [x] Token管理

- [x] 数据模型设计
  - [x] 本地数据库设计（Room）
    - [x] 书籍数据库设计
    - [x] 电影数据库设计
    - [x] 电视剧数据库设计
    - [x] 游戏数据库设计
  - [x] Notion数据库模板设计
  - [x] 数据同步策略

- [ ] 核心功能开发
  - [x] 媒体搜索
    - [x] TMDB API集成（电影/电视剧）
    - [x] IGDB API集成（游戏）
    - [x] 豆瓣 book集成（书籍）
  - [x] 元数据管理
    - [x] 保存到本地数据库
    - [x] 同步到Notion
    - [ ] 批量导入/导出
  - [x] 数据展示
    - [x] 列表/网格视图
    - [x] 详情页面
    - [x] 筛选和排序
  - [x] 数据编辑
    - [x] 手动编辑元数据
    - [ ] 批量更新
    - [x] 状态管理（已看/在看/想看）

- [x] UI/UX设计
  - [x] Material Design 3实现
  - [x] 深色模式支持
  - [x] 动画效果
  - [x] 自适应布局

- [ ] 高级功能
  - [x] 离线支持
  - [ ] 数据备份与还原
  - [ ] 自定义分类和标签
  - [ ] 数据统计和图表
  - [ ] 分享功能

- [ ] 测试
  - [ ] 单元测试
  - [ ] UI测试
  - [ ] 集成测试
  - [ ] 性能测试

- [ ] 发布准备
  - [ ] 应用图标和品牌设计
  - [ ] 应用商店素材准备
  - [ ] 隐私政策和用户协议
  - [ ] Beta测试
  - [ ] Play Store发布

## 实现进度

### 已完成模块

1. 电视剧模块
   - [x] 数据层
     - [x] 实体类设计（TvShowDetailsEntity, TvSeasonEntity, TvEpisodeEntity, TvShowWatchProgressEntity）
     - [x] DAO接口实现（TvShowDao）
     - [x] 数据映射器（TvShowMappers）
   - [x] 领域层
     - [x] 领域模型（TvShow, TvSeason, TvEpisode）
     - [x] 仓库接口（TvShowRepository）
     - [x] 仓库实现（TvShowRepositoryImpl）
   - [x] 表现层
     - [x] ViewModel（TvShowViewModel）
     - [x] UI界面
       - [x] 列表页面（TvShowListScreen）
       - [x] 详情页面（TvShowDetailScreen）
       - [x] 添加页面（AddTvShowScreen）
     - [x] 导航集成

2. 电影模块
   - [x] 数据层
     - [x] 实体类设计
     - [x] DAO接口实现
     - [x] 数据映射器
   - [x] 领域层
     - [x] 领域模型
     - [x] 仓库接口
     - [x] 仓库实现
   - [x] 表现层
     - [x] ViewModel
     - [x] UI界面
       - [x] 列表页面
       - [x] 详情页面
       - [x] 添加页面
     - [x] 导航集成

3. 媒体搜索模块
   - [x] API接口定义
     - [x] TMDB API（电影/电视剧）
     - [x] IGDB API（游戏）
     - [x] 豆瓣 API（书籍）
   - [x] 设置界面
     - [x] API密钥配置
     - [x] 连接测试
   - [ ] 搜索实现
     - [ ] 电影搜索
     - [ ] 电视剧搜索
     - [ ] 游戏搜索
     - [ ] 书籍搜索

4. Notion同步模块
   - [x] 数据层
     - [x] Notion API接口定义
     - [x] 同步仓库接口（SyncRepository）
     - [x] 同步仓库实现（SyncRepositoryImpl）
     - [x] 数据映射（DomainToNotionMappers）
   - [x] 表现层
     - [x] ViewModel（SyncViewModel）
     - [x] 同步页面（SyncScreen）
     - [x] 同步进度展示
     - [x] 同步结果状态管理

5. 设置模块
   - [x] 数据层
     - [x] 设置仓库接口（SettingsRepository）
     - [x] 设置仓库实现（SettingsRepositoryImpl）
     - [x] 数据持久化（DataStore）
   - [x] 表现层
     - [x] ViewModel（SettingsViewModel）
     - [x] 设置页面（SettingsScreen）
     - [x] API密钥配置
     - [x] Notion连接设置

### 进行中模块

1. 游戏模块
   - [x] IGDB API集成
   - [ ] 数据模型实现
   - [ ] UI界面开发

2. 书籍模块
   - [x] 豆瓣API集成
   - [ ] 数据模型实现
   - [ ] UI界面开发

### 待开发模块

1. 高级功能
   - [ ] 数据备份与还原
   - [ ] 自定义分类和标签
   - [ ] 数据统计和图表
   - [ ] 分享功能

2. 测试
   - [ ] 单元测试
   - [ ] UI测试
   - [ ] 集成测试
   - [ ] 性能测试

3. 发布准备
   - [ ] 应用图标和品牌设计
   - [ ] 应用商店素材准备
   - [ ] 隐私政策和用户协议
   - [ ] Beta测试
   - [ ] Play Store发布

## 最近更新

### 2023-07-15：Notion同步功能实现

- 完成了Notion API的集成，支持将电影、电视剧数据同步到Notion数据库
- 实现了双向同步功能，支持从Notion导入数据到应用
- 添加了同步进度展示和错误处理机制
- 支持针对单个媒体项的同步和批量同步

### 2023-07-10：媒体搜索功能实现

- 完成了TMDB、IGDB和豆瓣API的接口定义
- 添加了API密钥配置界面，支持用户自定义API访问凭证
- 实现了基础的搜索功能和搜索结果显示

## 数据模型设计

### 整体架构
- 采用无后端设计，数据存储依赖：
  - Room本地数据库（主数据源）
  - Notion数据库（同步/备份）
  - DataStore（配置存储）

### 核心实体设计

#### 1. 基础媒体实体
```kotlin
data class MediaEntity(
    val id: String,                // 唯一标识符
    val type: MediaType,          // 媒体类型：MOVIE/TV/GAME/BOOK
    val title: String,            // 标题
    val originalTitle: String,    // 原始标题
    val year: Int,               // 发行年份
    val cover: String,           // 封面图片URL
    val description: String,     // 描述
    val rating: Float,           // 评分
    val genres: List<String>,    // 类型标签
    val lastModified: Long,      // 最后修改时间戳
    val notionPageId: String?,   // Notion页面ID（可选）
    val status: MediaStatus,     // 状态（想看/在看/看过）
    val userRating: Float?,      // 用户评分
    val userComment: String?,    // 用户评论
    val userTags: List<String>,  // 用户标签
    val customFields: Map<String, String> // 自定义字段
)
```

#### 2. 媒体类型特有属性
```kotlin
// 电影特有属性
data class MovieDetails(
    val duration: Int,           // 时长（分钟）
    val director: String,        // 导演
    val cast: List<String>,     // 演员
    val tmdbId: String          // TMDB ID
)

// 电视剧特有属性
data class TvShowDetails(
    val episodes: Int,          // 总集数
    val seasons: Int,           // 季数
    val status: String,         // 播出状态
    val tmdbId: String         // TMDB ID
)

// 游戏特有属性
data class GameDetails(
    val platform: List<String>, // 平台
    val developer: String,      // 开发商
    val publisher: String,      // 发行商
    val igdbId: String         // IGDB ID
)

// 书籍特有属性
data class BookDetails(
    val author: String,         // 作者
    val isbn: String,          // ISBN
    val pages: Int,            // 页数
    val publisher: String      // 出版社
)
```

#### 3. 用户配置实体
```kotlin
data class UserPreferences(
    val notionToken: String?,           // Notion API Token
    val notionDatabaseId: String?,      // Notion数据库ID
    val syncInterval: Long,             // 同步间隔
    val lastSyncTime: Long,            // 最后同步时间
    val defaultMediaStatus: MediaStatus // 默认媒体状态
)
```

### 数据同步策略

1. 本地优先策略
   - 所有修改优先保存到本地数据库
   - 定期或手动触发与Notion同步
   - 使用最后修改时间戳解决冲突

2. 同步机制
   - 双向同步：本地 ↔ Notion
   - 增量同步：仅同步变更数据
   - 冲突解决：以最新修改为准

3. 离线支持
   - 完整本地数据存储
   - 离线操作队列
   - 网络恢复后自动同步

### Room数据库设计

1. 主表设计
```kotlin
@Entity(tableName = "media")
data class MediaEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "type") val type: MediaType,
    // ... 基础字段 ...
)
```

2. 关联表设计
```kotlin
@Entity(tableName = "media_details")
data class MediaDetailsEntity(
    @PrimaryKey val mediaId: String,
    @ColumnInfo(name = "details_type") val detailsType: String,
    @ColumnInfo(name = "details_json") val detailsJson: String
)
```

3. 标签表设计
```kotlin
@Entity(tableName = "tags")
data class TagEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "color") val color: String
)
```

### Notion数据库模板

1. 基础属性
   - 标题（Title）：媒体标题
   - 类型（Select）：电影/电视剧/游戏/书籍
   - 状态（Select）：想看/在看/看过
   - 评分（Number）：1-10
   - 封面（Files）：封面图片
   - 发行年份（Number）
   - 描述（Text）
   - 最后修改时间（Last edited time）

2. 媒体类型特有属性
   - 利用Notion的动态属性功能
   - 根据媒体类型显示不同字段集

3. 自定义属性
   - 支持用户自定义属性
   - 可通过应用界面配置

### 数据迁移策略

1. 版本升级
   - Room数据库迁移方案
   - Notion数据库结构更新方案

2. 数据导出
   - JSON格式导出
   - CSV格式导出（兼容其他应用）

3. 数据备份
   - 自动备份到Notion
   - 本地数据库备份

## 技术栈

- 开发语言：Kotlin
- 架构模式：Clean Architecture + MVI
- 主要框架/库：
  - Android Jetpack (ViewModel, Room, Navigation)
  - Kotlin Coroutines + Flow
  - Retrofit + OkHttp
  - Hilt（依赖注入）
  - Material Design 3
  - JUnit + Espresso（测试）

## 开发时间线

### 第一阶段：基础架构（2周）
- 项目初始化和依赖配置
- 基础架构实现
- Notion API集成研究

### 第二阶段：核心功能（4周）
- 数据模型设计
- API集成
- 本地数据库实现
- 基础UI实现

### 第三阶段：功能完善（3周）
- UI/UX优化
- 离线支持
- 数据同步
- 高级功能实现

### 第四阶段：测试和优化（2周）
- 单元测试编写
- UI测试
- 性能优化
- Bug修复

### 第五阶段：发布准备（1周）
- 应用商店材料准备
- Beta测试
- 最终优化和修复
- Play Store发布

## 开发规范

### 代码规范
- 遵循Kotlin官方编码规范
- 使用ktlint进行代码格式化
- 遵循Clean Architecture分层原则
- 应用SOLID原则
- 优先选择组合而非继承
- 编写职责单一的小型类

### Git工作流
- 主分支：main
- 开发分支：develop
- 功能分支：feature/*
- 修复分支：bugfix/*
- 发布分支：release/*

### 提交规范
- feat：新功能
- fix：修复
- docs：文档更新
- style：代码格式
- refactor：重构
- test：测试
- chore：构建过程或辅助工具变动

## 测试策略

### 单元测试
- ViewModel测试
- Repository测试
- Use Case测试
- 工具类测试

### UI测试
- 导航测试
- 用户交互测试
- UI显示测试

### 集成测试
- API集成测试
- 数据库操作测试
- 同步功能测试

## 监控和分析

- Firebase Analytics集成
- 崩溃报告
- 性能监控
- 用户行为分析

## 注意事项

- 注册各API开发者账号
- 确保遵守各API使用条款和限制
- 注重数据安全和用户隐私保护
- 维持代码质量和测试覆盖率
- 定期进行性能优化和内存泄漏检查

## 未来计划

- [ ] iOS版本开发
- [ ] 网页版开发
- [ ] 支持更多媒体类型
- [ ] 社交功能
- [ ] 推荐系统
- [ ] 增强数据分析功能
