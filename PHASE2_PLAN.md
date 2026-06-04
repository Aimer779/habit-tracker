# Phase 2: Component Migration Plan

## 代码探索总结

### 现有架构
- **3 种视图模式**：Month (月视图), Week (周视图), Day (日视图)
- **数据模型**：`Habit` (id, name, icon, color, targetCount, checkInCounts)
- **适配器模式**：`HabitAdapter` + 3 种 ViewHolder (MonthViewHolder, WeekViewHolder, DayViewHolder)
- **布局文件**：
  - `item_habit.xml` - 月视图（热力图 35 个点，7x5 网格）
  - `item_habit_week.xml` - 周视图（7 个点横向排列）
  - `item_habit_day.xml` - 日视图（进度文本 + 大按钮）
- **颜色系统**：8 种习惯颜色（blue, orange, green, pink, purple, teal, yellow, red）
- **交互**：点击编辑、长按删除、按钮打卡/取消

---

## Phase 2 目标：自底向上迁移 UI 组件

### 迁移顺序（从简单到复杂）

```
1. HabitColorHelper (纯逻辑) ✅ 最简单
2. HeatmapDot (最小 UI 单元) ✅ 
3. HabitCard - Day View ✅ 
4. HabitCard - Week View
5. HabitCard - Month View (最复杂，热力图生成)
6. 集成到 MainActivity (LazyColumn 替代 RecyclerView)
```

---

## 任务分解

### Task 1: 创建 Compose 颜色辅助工具
**文件**: `app/src/main/java/org/wit/habit/ui/compose/HabitColorHelper.kt`

**内容**:
```kotlin
object HabitColorHelper {
    fun getColor(colorKey: String): Color = when (colorKey) {
        "blue" -> HabitBlue
        "orange" -> HabitOrange
        "green" -> HabitGreen
        "pink" -> HabitPink
        "purple" -> HabitPurple
        "teal" -> HabitTeal
        "yellow" -> HabitYellow
        "red" -> HabitRed
        else -> HabitBlue
    }
}
```

**验收**: 单元测试通过，所有颜色键映射正确

---

### Task 2: 创建热力图点组件
**文件**: `app/src/main/java/org/wit/habit/ui/compose/HeatmapDot.kt`

**功能**:
- 圆形 8dp 点
- 接收 `isCompleted` 和 `color` 参数
- 已完成显示主题色，未完成显示灰色 (#BDBDBD)

**代码示例**:
```kotlin
@Composable
fun HeatmapDot(
    isCompleted: Boolean,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(8.dp)
            .background(
                color = if (isCompleted) color else Color(0xFFBDBDBD),
                shape = CircleShape
            )
    )
}
```

**验收**: Preview 显示正常，颜色逻辑正确

---

### Task 3: 创建习惯卡片 - 日视图
**文件**: `app/src/main/java/org/wit/habit/ui/compose/HabitCardDay.kt`

**对应 XML**: `item_habit_day.xml`

**组件结构**:
```
Card
├── Icon (24sp emoji)
├── Name (16sp bold, max 1 line)
├── Progress Text ("Today's Progress: 2/3")
└── Check In Button (full width, 48dp height)
```

**状态逻辑**:
- 已完成 (count >= targetCount): 按钮显示 "Uncheck"，红色背景
- 未完成: 按钮显示 "Check In"，主题色背景

**交互回调**:
```kotlin
data class HabitCardCallbacks(
    val onCheckIn: (Habit) -> Unit,
    val onCancelCheckIn: (Habit) -> Unit,
    val onClick: (Habit) -> Unit,
    val onLongClick: (Habit) -> Unit
)
```

**验收**: 
- Preview 显示正常
- 点击/长按事件正确触发
- 按钮状态根据完成情况切换

---

### Task 4: 创建习惯卡片 - 周视图
**文件**: `app/src/main/java/org/wit/habit/ui/compose/HabitCardWeek.kt`

**对应 XML**: `item_habit_week.xml`

**组件结构**:
```
Card (横向布局)
├── Icon (20sp emoji)
├── Name (14sp bold, flex 1)
├── 7 Dots (Row, 最近 7 天)
└── Check In Button (100dp width, 36dp height)
```

**验收**: 7 个点按日期倒序显示，颜色正确

---

### Task 5: 创建习惯卡片 - 月视图
**文件**: `app/src/main/java/org/wit/habit/ui/compose/HabitCardMonth.kt`

**对应 XML**: `item_habit.xml`

**组件结构**:
```
Card (垂直布局)
├── Icon (20sp emoji, 左上角)
├── Name (14sp bold, 居中)
├── Heatmap Grid (7x5 = 35 dots, 最近 35 天)
└── Check In Button (full width, 40dp height)
```

**难点**: 
- FlowRow 或 LazyVerticalGrid 布局 35 个点
- 日期倒序计算 (34 天前 → 今天)

**验收**: 
- 热力图显示 35 个点，7 列 5 行
- 点的颜色根据历史打卡正确显示

---

### Task 6: 创建统一的 HabitCard 入口
**文件**: `app/src/main/java/org/wit/habit/ui/compose/HabitCard.kt`

**功能**: 根据 ViewMode 动态选择渲染哪种卡片

```kotlin
@Composable
fun HabitCard(
    habit: Habit,
    viewMode: ViewMode,
    callbacks: HabitCardCallbacks,
    modifier: Modifier = Modifier
) {
    when (viewMode) {
        ViewMode.MONTH -> HabitCardMonth(habit, callbacks, modifier)
        ViewMode.WEEK -> HabitCardWeek(habit, callbacks, modifier)
        ViewMode.DAY -> HabitCardDay(habit, callbacks, modifier)
    }
}

enum class ViewMode { MONTH, WEEK, DAY }
```

**验收**: 切换 ViewMode 能正确渲染不同卡片

---

### Task 7: 创建习惯列表组件
**文件**: `app/src/main/java/org/wit/habit/ui/compose/HabitList.kt`

**功能**: LazyColumn 或 LazyVerticalGrid 展示习惯列表

```kotlin
@Composable
fun HabitList(
    habits: List<Habit>,
    viewMode: ViewMode,
    callbacks: HabitCardCallbacks,
    modifier: Modifier = Modifier
) {
    val layoutManager = when (viewMode) {
        ViewMode.MONTH -> GridCells.Fixed(2) // 2 列网格
        ViewMode.WEEK, ViewMode.DAY -> GridCells.Fixed(1) // 单列
    }

    LazyVerticalGrid(
        columns = layoutManager,
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(habits, key = { it.id }) { habit ->
            HabitCard(
                habit = habit,
                viewMode = viewMode,
                callbacks = callbacks
            )
        }
    }
}
```

**验收**: 
- 月视图显示 2 列网格
- 周视图/日视图显示单列列表
- 滚动流畅，无性能问题

---

### Task 8: 在 ComposePreviewActivity 中测试
**文件**: 修改 `ComposePreviewActivity.kt`

**功能**: 添加一个带测试数据的习惯列表预览

```kotlin
@Preview(showBackground = true)
@Composable
fun HabitListPreview() {
    val testHabits = listOf(
        Habit(
            id = 1,
            name = "Read Books",
            icon = "📖",
            color = "blue",
            targetCount = 1,
            checkInCounts = mutableMapOf("2026-06-04" to 1)
        ),
        Habit(
            id = 2,
            name = "Exercise",
            icon = "🏃",
            color = "green",
            targetCount = 3,
            checkInCounts = mutableMapOf()
        )
    )

    HabitTheme {
        HabitList(
            habits = testHabits,
            viewMode = ViewMode.MONTH,
            callbacks = HabitCardCallbacks(
                onCheckIn = {},
                onCancelCheckIn = {},
                onClick = {},
                onLongClick = {}
            )
        )
    }
}
```

**验收**: 
- 在 Android Studio Preview 中可见
- 在真机运行 ComposePreviewActivity 显示正常

---

## 涉及文件清单

### 新增文件 (7 个)
```
app/src/main/java/org/wit/habit/ui/compose/
├── HabitColorHelper.kt       # 颜色映射工具
├── HeatmapDot.kt              # 热力图点组件
├── HabitCardDay.kt            # 日视图卡片
├── HabitCardWeek.kt           # 周视图卡片
├── HabitCardMonth.kt          # 月视图卡片
├── HabitCard.kt               # 统一卡片入口
└── HabitList.kt               # 列表容器
```

### 修改文件 (1 个)
```
app/src/main/java/org/wit/habit/
└── ComposePreviewActivity.kt  # 添加测试界面
```

### 暂不删除 (Phase 3 集成后删除)
```
- HabitAdapter.kt
- item_habit.xml
- item_habit_day.xml
- item_habit_week.xml
```

---

## 验收标准

### 功能验收
- [ ] 所有 3 种视图模式的卡片都能正确渲染
- [ ] 热力图点颜色根据完成状态正确显示
- [ ] 按钮状态（Check In / Uncheck）根据完成情况切换
- [ ] 点击/长按事件正确触发
- [ ] 列表支持 2 列网格（月视图）和单列（周/日视图）

### 视觉验收
- [ ] 卡片圆角、阴影与 XML 版本一致
- [ ] 字体大小、颜色、粗细与 XML 版本一致
- [ ] 按钮高度、圆角与 XML 版本一致
- [ ] 间距（padding, margin）与 XML 版本一致

### 技术验收
- [ ] 所有组件都有 `@Preview` 注解
- [ ] 使用 `remember` 和 `derivedStateOf` 优化重组
- [ ] 颜色、尺寸等硬编码值提取为常量
- [ ] 通过 Android Studio Preview 验证 UI
- [ ] 在真机/模拟器运行 ComposePreviewActivity 验证交互

### 性能验收
- [ ] 滚动 50+ 习惯列表无卡顿
- [ ] 切换视图模式响应迅速（< 100ms）
- [ ] 无过度重组（使用 Layout Inspector 验证）

---

## 时间估算

- Task 1 (颜色工具): 10 分钟
- Task 2 (热力图点): 15 分钟
- Task 3 (日视图卡片): 30 分钟
- Task 4 (周视图卡片): 30 分钟
- Task 5 (月视图卡片): 45 分钟
- Task 6 (统一入口): 15 分钟
- Task 7 (列表组件): 30 分钟
- Task 8 (测试界面): 20 分钟

**总计**: ~3 小时

---

## 下一步

完成这些任务后：
1. 在 `ComposePreviewActivity` 中验证所有组件
2. 与原 XML 版本对比，调整细节差异
3. 准备 Phase 3：集成到 MainActivity（替换 RecyclerView）

---

## 开始执行？

输入 `yes` 开始按顺序执行 Task 1-8，或指定任务编号单独执行。
