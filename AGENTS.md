## Project Overview
这是一个基于 Kotlin 的 Android 原生习惯打卡 App，项目采用课程基础技术实现，优先保证可以在 Android Studio 中正常运行。App 功能包括：用户可以添加习惯、查看习惯列表、删除习惯、点击习惯卡片进行每日打卡、取消当天打卡，并在统计页面查看总习惯数、今日完成数量、今日完成率、总打卡次数等简单数据。

## Tech Stack
- **构建系统**：Gradle (Kotlin DSL) + Android Gradle Plugin 9.2.0
- **语言**：Kotlin（由 AGP / Gradle toolchain 管理）
- **SDK 级别**：compileSdk 36 / minSdk 30 / targetSdk 36 / Java 11
- **架构**：多 Activity + Fragment + Jetpack Compose 混合；底部导航由 Compose 实现，内容区以 Fragment 承载，统计排行保留 RecyclerView + Adapter
- **核心依赖**：
  - AndroidX AppCompat / Core KTX / Activity KTX
  - ConstraintLayout
  - Material Design Components (1.13.0)
  - TimberKt (1.5.1，日志)
- **测试**：JUnit 4、AndroidX Test、Espresso
- **版本管理**：Gradle Version Catalog (`gradle/libs.versions.toml`)
- **持久化**：本地 JSON 文件 / SharedPreferences（不使用 Room / Firebase）

Do NOT introduce unless explicitly requested:
- Jetpack Compose
- Room / SQLDelight / 任何 ORM
- Navigation Component
- Retrofit / OkHttp / 任何网络库
- Hilt / Koin / 任何 DI 框架
- Coroutines / Flow（除非现有代码已使用）
- 第三方图片加载库（Glide / Coil 等）

### Common Command
- `./gradlew.bat tasks` — 查看所有可用 Gradle 任务
- `./gradlew.bat assembleDebug` — 编译 Debug APK
- `./gradlew.bat installDebug` — 编译并安装到已连接设备
- `./gradlew.bat clean` — 清理构建产物
- `./gradlew.bat test` — 运行单元测试
- `./gradlew.bat connectedDebugAndroidTest` — 运行仪器测试（Espresso）
- `dir` 或 `Get-ChildItem` — 列目录（PowerShell）
- `Get-Content <file>` — 查看文件内容（PowerShell）

**Windows / PowerShell 环境下**：
1. 多条命令链式执行时，使用分号 `;` 而非 `&&`
2. 路径分隔符使用反斜杠 `\` 或统一用正斜杠 `/`
3. 阅读代码前先用 `Get-Location`（或 `pwd`）确认工作路径，不要假设目录位置

## Project Context
- 单 Module 结构：`app/` 为唯一模块，包名 `org.wit.habit`
- 标准 Android 项目目录：`app/src/main/java`、`app/src/main/res`、`app/src/androidTest`、`app/src/test`
- 构建产物输出在 `app/build/outputs/apk/`
- 根项目名：`Habit`

## My Working Rules

### 1. 先思考，后编码

**不要假设。不要掩饰困惑。暴露权衡。**

实现之前：
- 明确陈述你的假设。如果不确定，就提问。
- 如果存在多种解释，把它们列出来——不要默默选一个。
- 如果存在更简单的方法，就说出来。在必要时提出反对意见。
- 如果有不清楚的地方，停下来。说出困惑之处。提问。

### 2. 简单至上

**解决问题所需的最少代码。不写推测性内容。**

- 不添加超出需求范围的功能
- 不为只使用一次的代码做抽象
- 不添加未被要求的“灵活性”或“可配置性”
- 不为不可能发生的场景做错误处理
- 如果你写了 200 行代码而本来可以只写 50 行，那就重写

问自己：“资深工程师会觉得这过于复杂吗？” 如果是，就简化。

### 3. 手术式修改

**只动必须动的地方。只清理自己造成的混乱。**

编辑现有代码时：
- 不要“改进”相邻的代码、注释或格式
- 不要重构没有坏掉的东西
- 匹配现有风格，即使你会用不同的方式做
- 如果你注意到无关的废弃代码，可以提一下——但不要删除它

当你的修改造成孤儿代码时：
- 移除因你的修改而变得未使用的导入/变量/函数
- 除非被要求，否则不要删除原本就存在的废弃代码

检验标准：每个被修改的行都应该能直接追溯到用户的需求。

### 4. 目标驱动执行

**定义成功标准。循环验证直到达成。**

将任务转化为可验证的目标：
- “添加校验” → “为无效输入编写测试，然后让测试通过”
- “修复 bug” → “编写能复现该 bug 的测试，然后让测试通过”
- “重构 X” → “确保重构前后的测试都通过”

对于多步骤任务，陈述一个简要计划：

```
1. [步骤] → 验证: [检查项]
2. [步骤] → 验证: [检查项]
3. [步骤] → 验证: [检查项]
```

强成功标准让你能够独立循环迭代。弱成功标准（“把它搞定”）则需要不断澄清。

---

## Lessons
`LESSON.md` 记录了之前任务中发现的关键洞察、最佳实践和已知陷阱。
每次新任务开始前，先读取 `LESSON.md`
每次任务结束后，如果有新的发现追加进 `LESSON.md`
