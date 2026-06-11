# Habit Tracker - Android App

## Project Overview
基于 Kotlin 的 Android 原生习惯打卡 App，使用混合架构（Jetpack Compose + XML）构建。用户可以添加习惯、每日打卡、查看统计数据，支持 3 种视图模式（月/周/日）展示打卡历史。

## Tech Stack

### 构建系统
- Gradle 9.4.1 (Kotlin DSL)
- Android Gradle Plugin 9.2.0
- Kotlin 2.3.0 (由 AGP 内置支持)

### SDK 级别
- compileSdk: 36
- minSdk: 30
- targetSdk: 36
- Java: 11

### 架构：混合架构 (Hybrid)

**UI 层**:
- **主界面（习惯列表）**: Jetpack Compose + Material3
- **其他页面**: 传统 XML + View Binding
  - AddHabitActivity（添加/编辑习惯）
  - StatsFragment（统计页面）
  - SettingsFragment（设置页面）

**导航**: 单 Activity + Fragment 架构
- MainActivity 是唯一的 tab 容器 Activity，底部悬浮导航栏（Compose）+ FAB 常驻
- Home / Stats / Settings 三个 tab 由 Fragment 承载，使用 FragmentManager `show/hide` 手动切换（保留各 tab 状态，无 Navigation Component）
- AddHabitActivity 保持独立 Activity（模态编辑页，Intent 启动）
- 返回键：非 Home tab 时返回 Home，Home tab 时交给系统默认行为
- 主题切换 recreate 后通过 savedInstanceState 恢复当前 tab

**数据持久化**: 本地 JSON 文件 + SharedPreferences

**状态管理**: Activity / Fragment 本地状态（无 ViewModel 层）

### 核心依赖

#### Compose (Material3)
- Compose BOM: 2025.12.00
- Material3: androidx.compose.material3
- Activity Compose: 1.13.0
- Lifecycle Runtime KTX: 2.8.7

#### AndroidX 组件
- AppCompat: 1.7.1
- Core KTX: 1.18.0
- Activity KTX: 1.13.0
- Fragment KTX: 1.8.5（单 Activity + Fragment 导航）
- ConstraintLayout: 2.2.1（用于 XML 布局框架）

#### Material Design (XML)
- Material Components: 1.13.0（用于底部导航、FAB、按钮等 XML 元素）

#### 日志
- TimberKt: 1.5.1

#### 测试
- JUnit 4: 4.13.2
- AndroidX JUnit: 1.3.0
- Espresso Core: 3.7.0
- Compose UI Test: (via BOM)

### 版本管理
使用 Gradle Version Catalog (`gradle/libs.versions.toml`)

---

## Project Structure

### Compose 组件
```
app/src/main/java/org/wit/habit/ui/
├── compose/
│   ├── HabitColorHelper.kt      # 颜色映射工具
│   ├── HeatmapDot.kt             # 8dp 圆形热力图点
│   ├── HabitCardDay.kt           # 日视图卡片
│   ├── HabitCardWeek.kt          # 周视图卡片（7 点横向）
│   ├── HabitCardMonth.kt         # 月视图卡片（35 点热力图）
│   ├── HabitCard.kt              # 统一卡片入口（ViewMode 切换）
│   ├── HabitList.kt              # LazyVerticalGrid 容器
│   └── MainContent.kt            # 主界面内容 + 空状态
└── theme/
    ├── Color.kt                  # Material3 颜色定义
    ├── Type.kt                   # Typography 定义
    └── Theme.kt                  # HabitTheme (Material3)
```

### 传统 Activity / Fragment (XML)
```
app/src/main/java/org/wit/habit/
├── MainActivity.kt               # 唯一 tab 容器（Fragment 切换 + 常驻导航/FAB）
├── HomeFragment.kt               # 主界面（Compose 集成，state 驱动重组）
├── StatsFragment.kt              # 统计页面
├── SettingsFragment.kt           # 设置页面
├── AddHabitActivity.kt           # 添加/编辑习惯（独立 Activity）
├── BaseActivity.kt               # 基类（主题应用）
└── ComposePreviewActivity.kt     # Compose 测试界面
```

### 数据模型
```
app/src/main/java/org/wit/habit/
├── model/
│   └── Habit.kt                  # 习惯数据类
└── helpers/
    ├── HabitStore.kt             # 数据持久化（JSON）
    ├── DateUtils.kt              # 日期工具
    └── HabitColors.kt            # 颜色映射（XML 资源）
```

---

## UI 组件说明

### Compose 组件详情

#### HabitCard 三种视图模式

**1. 月视图 (MONTH)**
- 布局：2 列网格
- 内容：图标 + 名称 + 35 点热力图（7x5）+ Check In 按钮
- 热力图：显示最近 35 天打卡历史
- 交互：点击编辑、长按删除

**2. 周视图 (WEEK)**
- 布局：单列列表，横向排列
- 内容：图标 + 名称 + 7 点横向热力图 + Check In 按钮
- 热力图：显示最近 7 天打卡历史
- 交互：仅支持打卡/取消打卡

**3. 日视图 (DAY)**
- 布局：单列列表
- 内容：图标 + 名称 + 进度文本 + Check In 按钮
- 进度：显示 "Today's Progress: X/Y"
- 交互：点击编辑、长按删除

#### 状态管理
- HomeFragment 使用 `mutableStateOf` 字段（filter / sort / viewMode / refreshTrigger）驱动重组
- 数据变更后 `refreshTrigger++` 触发重新读取 HabitStore（findAll 每次返回新对象，无需手动 copy）
- 常驻导航栏的选中 tab 是 MainActivity 的 `mutableStateOf`，切换时自动重组

---

## 开发规范

### DO NOT introduce unless explicitly requested:
- Jetpack Navigation Component（使用传统 Activity 导航）
- Room / SQLDelight / 任何 ORM
- Retrofit / OkHttp / 任何网络库
- Hilt / Koin / 任何 DI 框架
- Coroutines / Flow（除非现有代码已使用）
- 第三方图片加载库（Glide / Coil）

### 代码风格
- 使用 Timber 记录日志
- 遵循 Kotlin 代码规范
- Compose 组件必须包含 `@Preview` 注解
- 优先使用 Material3 组件

### 构建命令
```bash
# Windows 环境
./gradlew.bat assembleDebug      # 编译 Debug APK
./gradlew.bat installDebug       # 编译并安装到设备
./gradlew.bat clean              # 清理构建产物
./gradlew.bat test               # 运行单元测试
./gradlew.bat connectedDebugAndroidTest  # 运行仪器测试
```

---

## 功能清单

### 主界面 (HomeFragment)
- [x] 显示习惯列表（Compose UI）
- [x] 3 种视图模式切换（月/周/日）
- [x] 打卡/取消打卡
- [x] 过滤（All / Checked In / Not Checked In）
- [x] 排序（升序/降序）
- [x] 点击卡片编辑习惯
- [x] 长按卡片删除习惯
- [x] 空状态显示
- [x] 底部导航（Home / Stats / Settings）

### 添加/编辑习惯 (AddHabitActivity)
- [x] 输入习惯名称、描述
- [x] 选择图标（12 种 emoji）
- [x] 选择颜色（8 种主题色）
- [x] 设置目标次数
- [x] 编辑模式：加载现有习惯数据

### 统计页面 (StatsFragment)
- [x] 显示总习惯数
- [x] 显示今日完成数量
- [x] 显示今日完成率
- [x] 显示总打卡次数
- [x] 按月/周/年聚合统计

### 设置页面 (SettingsFragment)
- [x] 查看 app 版本
- [x] 其他设置项（待扩展）

---

## 已完成的 Compose 迁移

### Phase 1: 基础设施 ✅
- 添加 Compose 依赖
- 创建 Material3 主题系统
- 搭建测试环境

### Phase 2: UI 组件 ✅
- 创建 7 个可复用 Compose 组件
- 实现 3 种视图模式
- 添加交互逻辑和状态管理

### Phase 3: MainActivity 集成 ✅
- 替换 RecyclerView 为 ComposeView
- 移除 HabitAdapter
- 保留 XML 框架（标题栏、底部导航、FAB）
- 所有功能正常工作

### Phase 4: 清理与收尾 ✅
- 删除遗留代码（HabitAdapter.kt + 3 个 XML 布局）
- 创建项目文档

---

## 未来扩展方向（可选）

### 短期
- 迁移 AddHabitActivity 到 Compose
- 迁移 StatsFragment 到 Compose
- 添加单元测试和 UI 测试

### 长期
- 引入 Navigation Compose 统一导航
- 引入 ViewModel 改进状态管理
- 完全移除 XML 布局

### 建议
当前混合架构运行良好，无需强制全量迁移。除非有明确需求，否则保持现状即可。

---

## 性能指标
- 构建时间：3-7 秒（增量编译）
- APK 大小：未明显增加
- 运行流畅度：无卡顿（50+ 习惯测试）
- 内存占用：与 RecyclerView 版本相当

---

## 参考文档
- `COMPOSE_MIGRATION.md` - Compose 迁移指南
- `PHASE2_PLAN.md` - Phase 2 组件开发计划
- `PHASE3_PLAN.md` - Phase 3 集成计划
- `PHASE4_PLAN.md` - Phase 4 清理计划
- `COMPOSE_MIGRATION_SUMMARY.md` - 完整迁移总结

---

## 联系方式
GitHub Issues: https://github.com/anthropics/claude-code/issues

---

*Last Updated: 2026-06-11*
