# CollectMeta - 媒体收藏助手

一个用于收集和管理电影、电视剧、游戏、书籍元数据的 Android 应用，支持与 Notion 同步。

## 功能概述

- [x] 基础框架搭建
  - [x] 项目初始化与依赖配置
  - [x] Clean Architecture 架构设置
  - [x] CI/CD 配置
  - [x] 代码规范和静态检查配置

- [x] 用户认证模块
  - [x] Notion API 授权集成
  - [x] 用户授权流程
  - [x] Token 管理

- [ ] 数据模型设计
  - [ ] 本地数据库设计
  - [ ] Notion 数据库模板设计
  - [ ] 数据同步策略

- [ ] 核心功能开发
  - [ ] 媒体搜索
    - [ ] TMDB API 集成（电影/电视剧）
    - [ ] IGDB API 集成（游戏）
    - [ ] Google Books API 集成（书籍）
  - [ ] 元数据管理
    - [ ] 保存到本地数据库
    - [ ] 同步到 Notion
    - [ ] 批量导入/导出
  - [ ] 数据展示
    - [ ] 列表/网格视图
    - [ ] 详情页面
    - [ ] 筛选和排序
  - [ ] 数据编辑
    - [ ] 手动编辑元数据
    - [ ] 批量更新
    - [ ] 状态管理（已看/在看/想看等）

- [ ] UI/UX 设计
  - [ ] Material Design 3 主题
  - [ ] 深色模式支持
  - [ ] 动画效果
  - [ ] 自适应布局

- [ ] 高级功能
  - [ ] 离线支持
  - [ ] 数据备份还原
  - [ ] 自定义分类和标签
  - [ ] 数据统计和图表
  - [ ] 分享功能

- [ ] 测试
  - [ ] 单元测试
  - [ ] UI 测试
  - [ ] 集成测试
  - [ ] 性能测试

- [ ] 发布准备
  - [ ] 应用图标和品牌设计
  - [ ] 应用商店素材准备
  - [ ] 隐私政策和用户协议
  - [ ] Beta 测试
  - [ ] Play Store 发布

## 数据模型设计

### 整体架构
- 采用无后端设计，数据存储依赖：
  - Room 本地数据库（主数据源）
  - Notion 数据库（同步/备份）
  - DataStore（配置存储）

### 核心实体设计

#### 1. 基础媒体实体 (MediaEntity)
```kotlin
data class MediaEntity(
    val id: String,                // 唯一标识符
    val type: MediaType,          // 媒体类型：MOVIE/TV/GAME/BOOK
    val title: String,            // 标题
    val originalTitle: String,    // 原始标题
    val year: Int,               // 发行年份
    val cover: String,           // 封面图片 URL
    val description: String,     // 描述
    val rating: Float,           // 评分
    val genres: List<String>,    // 类型标签
    val lastModified: Long,      // 最后修改时间
    val notionPageId: String?,   // Notion 页面 ID（可选）
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
    val notionDatabaseId: String?,      // Notion 数据库 ID
    val syncInterval: Long,             // 同步间隔
    val lastSyncTime: Long,            // 最后同步时间
    val defaultMediaStatus: MediaStatus // 默认媒体状态
)
```

### 数据同步策略

1. 本地优先策略
   - 所有修改优先保存到本地数据库
   - 定期或手动触发与 Notion 同步
   - 使用最后修改时间戳解决冲突

2. 同步机制
   - 双向同步：本地 ↔ Notion
   - 增量同步：仅同步变更数据
   - 冲突解决：以最新修改为准

3. 离线支持
   - 完整本地数据存储
   - 离线操作队列
   - 网络恢复后自动同步

### Room 数据库设计

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

### Notion 数据库模板

1. 基础属性
   - 标题（Title）: 媒体标题
   - 类型（Select）: 电影/电视剧/游戏/书籍
   - 状态（Select）: 想看/在看/看过
   - 评分（Number）: 1-10
   - 封面（Files）: 封面图片
   - 发行年份（Number）
   - 描述（Text）
   - 最后修改时间（Last edited time）

2. 媒体类型特有属性
   - 使用 Notion 的动态属性功能
   - 根据媒体类型显示不同字段集

3. 自定义属性
   - 支持用户自定义属性
   - 可通过应用界面配置

### 数据迁移策略

1. 版本升级
   - Room 数据库迁移方案
   - Notion 数据库结构更新方案

2. 数据导出
   - JSON 格式导出
   - CSV 格式导出（兼容其他应用）

3. 数据备份
   - 自动备份到 Notion
   - 本地数据库备份

## 技术栈

- 开发语言：Kotlin
- 架构模式：Clean Architecture + MVI
- 主要框架/库：
  - Android Jetpack (ViewModel, Room, Navigation)
  - Kotlin Coroutines + Flow
  - Retrofit + OkHttp
  - Hilt (依赖注入)
  - Material Design 3
  - JUnit + Espresso (测试)

## 开发时间线

### 第一阶段：基础架构（2周）
- 项目初始化和依赖配置
- 基础架构搭建
- Notion API 集成研究

### 第二阶段：核心功能（4周）
- 数据模型设计
- API 集成
- 本地数据库实现
- 基础 UI 实现

### 第三阶段：功能完善（3周）
- UI/UX 优化
- 离线支持
- 数据同步
- 高级功能实现

### 第四阶段：测试和优化（2周）
- 单元测试编写
- UI 测试
- 性能优化
- Bug 修复

### 第五阶段：发布准备（1周）
- 应用商店材料准备
- Beta 测试
- 最终优化和修复
- Play Store 发布

## 开发规范

### 代码规范
- 遵循 Kotlin 官方代码规范
- 使用 ktlint 进行代码格式化
- 遵循 Clean Architecture 分层原则

### Git 工作流
- 主分支：main
- 开发分支：develop
- 功能分支：feature/*
- 修复分支：bugfix/*
- 发布分支：release/*

### 提交规范
- feat: 新功能
- fix: 修复
- docs: 文档更新
- style: 代码格式
- refactor: 重构
- test: 测试
- chore: 构建过程或辅助工具的变动

## 测试策略

### 单元测试
- ViewModel 测试
- Repository 测试
- Use Case 测试
- 工具类测试

### UI 测试
- 页面导航测试
- 用户交互测试
- 界面显示测试

### 集成测试
- API 集成测试
- 数据库操作测试
- 同步功能测试

## 监控和分析

- Firebase Analytics 集成
- Crash Reporting
- 性能监控
- 用户行为分析

## 注意事项

- 需要注册各个 API 的开发者账号
- 确保遵守各个 API 的使用条款和限制
- 注意数据安全和用户隐私保护
- 保持代码质量和测试覆盖率
- 定期进行性能优化和内存泄漏检查

## 后续计划

- [ ] iOS 版本开发
- [ ] 网页版开发
- [ ] 更多媒体类型支持
- [ ] 社交功能
- [ ] 推荐系统
- [ ] 数据分析功能增强
