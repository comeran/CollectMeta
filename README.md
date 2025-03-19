# CollectMeta

一个媒体收藏应用，允许用户收集和管理电影、电视剧、书籍和游戏等媒体内容信息。

## 项目目录结构

### 根目录

- `app/` - 主应用程序代码
- `docs/` - 项目文档
- `gradle/` - Gradle包装器文件
- `.gradle/` - Gradle构建缓存文件
- `.idea/` - IntelliJ IDEA/Android Studio项目文件
- `.git/` - Git版本控制文件
- `.vscode/` - VS Code配置文件
- `.cursor/` - Cursor编辑器配置文件
- 构建脚本和配置文件（build.gradle.kts, settings.gradle.kts等）

### app/src目录

- `main/` - 主要源代码
- `test/` - 单元测试代码
- `androidTest/` - Android UI测试代码

### app/src/main目录

- `java/` - Kotlin/Java源代码
- `res/` - 资源文件（布局、字符串、图片等）
- `AndroidManifest.xml` - 应用程序清单文件

### 主要代码包结构(com.homeran.collectmeta)

#### 数据层 (data/)

- `db/` - 数据库相关代码
  - `entities/` - 数据库实体类
  - `dao/` - 数据访问对象
  - `converters/` - 类型转换器
- `mapper/` - 数据映射器，在不同层之间转换数据
- `repository/` - 仓库实现，实现领域层中定义的仓库接口
- `remote/` - 远程数据源，如API调用

#### 领域层 (domain/)

- `model/` - 领域模型类（TvShow, Movie, Book, Game等）
- `repository/` - 仓库接口，定义数据操作方法

#### 表示层 (presentation/)

- 包含activities, fragments, viewmodels等
- 负责UI和用户交互

#### 其他

- `di/` - 依赖注入配置
- `CollectMetaApplication.kt` - 应用程序入口类
- `MainActivity.kt` - 主活动类

## 架构

本项目采用Clean Architecture架构模式，遵循分层架构原则：

1. **表示层（Presentation）**：负责UI和用户交互，使用MVVM模式
2. **领域层（Domain）**：包含业务逻辑和规则，不依赖于其他层
3. **数据层（Data）**：负责数据访问和处理，实现领域层定义的仓库接口

## 技术栈

- Kotlin编程语言
- Android Jetpack组件库
  - Room - 本地数据库
  - ViewModel - UI状态管理
  - LiveData/Flow - 响应式编程
  - Navigation - 导航管理
- Dagger Hilt - 依赖注入
- Material Design - UI设计
- Coroutines - 异步编程
