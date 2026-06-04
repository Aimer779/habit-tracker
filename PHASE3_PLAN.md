# Phase 3: MainActivity Compose Integration Plan

## 代码分析总结

### 当前 MainActivity 架构

**状态变量**:
- `habitStore: HabitStore` - 数据持久化
- `currentFilter: Filter` - 过滤模式 (ALL, CHECKED_IN, NOT_CHECKED_IN)
- `isAscending: Boolean` - 排序方向
- `currentViewMode: HabitAdapter.ViewMode` - 视图模式 (MONTH, WEEK, DAY)

**核心逻辑**:
1. `refreshList()` - 过滤、排序、更新 RecyclerView
2. `onCheckInClick()` / `onCancelCheckInClick()` - 打卡操作
3. `onEditClick()` - 跳转编辑页面
4. `onDeleteClick()` - 删除习惯
5. `showFilterPopup()` - 过滤菜单
6. `onResume()` - 页面返回时刷新

**XML 元素**:
- RecyclerView - 需要替换为 ComposeView
- tvEmpty - 空状态文本
- 其他 XML 元素保持不变（标题栏、底部导航、FAB）

---

## Phase 3 目标：集成 Compose 到 MainActivity

### 策略：混合架构（Hybrid）

保留 XML 框架（标题栏、底部导航、FAB），只用 `ComposeView` 替换 RecyclerView。

**优势**:
- 风险低，增量迁移
- 保持现有导航逻辑
- 避免大规模重构

---

## 任务分解

### Task 1: 替换 RecyclerView 为 ComposeView
**文件**: `activity_main.xml`

**操作**:
```xml
<!-- 删除 RecyclerView -->
<androidx.recyclerview.widget.RecyclerView ... />

<!-- 替换为 ComposeView -->
<androidx.compose.ui.platform.ComposeView
    android:id="@+id/composeView"
    android:layout_width="0dp"
    android:layout_height="0dp"
    android:layout_marginTop="8dp"
    app:layout_constraintBottom_toBottomOf="parent"
    app:layout_constraintEnd_toEndOf="parent"
    app:layout_constraintStart_toStartOf="parent"
    app:layout_constraintTop_toBottomOf="@id/btnFilter" />
```

**验收**: XML 布局编译通过，ComposeView 占据原 RecyclerView 位置

---

### Task 2: 创建 MainContent Composable
**文件**: `app/src/main/java/org/wit/habit/ui/compose/MainContent.kt`

**功能**: 封装主界面 Compose 逻辑

```kotlin
@Composable
fun MainContent(
    habits: List<Habit>,
    viewMode: ViewMode,
    isEmpty: Boolean,
    onCheckIn: (Habit) -> Unit,
    onCancelCheckIn: (Habit) -> Unit,
    onEdit: (Habit) -> Unit,
    onDelete: (Habit) -> Unit
) {
    if (isEmpty) {
        EmptyState()
    } else {
        HabitList(
            habits = habits,
            viewMode = viewMode,
            callbacks = HabitCardCallbacks(
                onCheckIn = onCheckIn,
                onCancelCheckIn = onCancelCheckIn,
                onClick = onEdit,
                onLongClick = onDelete
            )
        )
    }
}

@Composable
private fun EmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No habits yet. Tap the button below to add one.",
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
    }
}
```

**验收**: 组件编译通过，有 Preview

---

### Task 3: 在 MainActivity 中使用 Compose
**文件**: `MainActivity.kt`

**修改步骤**:

#### 3.1 删除 RecyclerView 相关代码
```kotlin
// 删除这些变量
- private lateinit var recyclerView: RecyclerView
- private lateinit var habitAdapter: HabitAdapter
- private lateinit var tvEmpty: TextView

// 删除 HabitAdapter.OnHabitClickListener 接口
- class MainActivity : BaseActivity(), HabitAdapter.OnHabitClickListener {
+ class MainActivity : BaseActivity() {
```

#### 3.2 添加 Compose 状态
```kotlin
private lateinit var habitStore: HabitStore
private lateinit var composeView: ComposeView
private var currentFilter = Filter.ALL
private var isAscending = true
private var currentViewMode = ViewMode.MONTH  // 改用 Compose 的 ViewMode
```

#### 3.3 修改 onCreate
```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    habitStore = HabitStore(this)
    composeView = findViewById(R.id.composeView)

    // 设置 Compose 内容
    setupComposeContent()

    // 保持现有按钮逻辑
    findViewById<ImageButton>(R.id.btnCalendar).setOnClickListener {
        currentViewMode = when (currentViewMode) {
            ViewMode.MONTH -> ViewMode.WEEK
            ViewMode.WEEK -> ViewMode.DAY
            ViewMode.DAY -> ViewMode.MONTH
        }
        Timber.i("User switched view mode to: $currentViewMode")
        refreshComposeContent()
    }

    // ... 其他按钮保持不变
}
```

#### 3.4 实现 setupComposeContent
```kotlin
private fun setupComposeContent() {
    composeView.setContent {
        HabitTheme {
            val habits = remember { getFilteredAndSortedHabits() }
            MainContent(
                habits = habits,
                viewMode = currentViewMode,
                isEmpty = habits.isEmpty(),
                onCheckIn = { habit ->
                    val today = DateUtils.today()
                    habitStore.checkIn(habit, today)
                    Timber.i("User checked in habit: ${habit.name} on $today")
                    refreshComposeContent()
                },
                onCancelCheckIn = { habit ->
                    val today = DateUtils.today()
                    habitStore.cancelCheckIn(habit, today)
                    Timber.i("User cancelled check-in for habit: ${habit.name} on $today")
                    refreshComposeContent()
                },
                onEdit = { habit ->
                    val intent = Intent(this, AddHabitActivity::class.java)
                    intent.putExtra("habit_id", habit.id)
                    startActivity(intent)
                },
                onDelete = { habit ->
                    habitStore.delete(habit)
                    Timber.i("User deleted habit: ${habit.name}")
                    refreshComposeContent()
                }
            )
        }
    }
}
```

#### 3.5 实现辅助方法
```kotlin
private fun getFilteredAndSortedHabits(): List<Habit> {
    val habits = habitStore.findAll()
    val today = DateUtils.today()

    val filtered = when (currentFilter) {
        Filter.ALL -> habits
        Filter.CHECKED_IN -> habits.filter { (it.checkInCounts[today] ?: 0) >= it.targetCount }
        Filter.NOT_CHECKED_IN -> habits.filter { (it.checkInCounts[today] ?: 0) < it.targetCount }
    }

    return if (isAscending) {
        filtered.sortedBy { it.name }
    } else {
        filtered.sortedByDescending { it.name }
    }
}

private fun refreshComposeContent() {
    setupComposeContent()
}

override fun onResume() {
    super.onResume()
    refreshComposeContent()
}
```

**验收**: 
- MainActivity 编译通过
- 所有现有功能保持工作
- 切换视图模式正常

---

### Task 4: 处理 ViewMode 枚举冲突
**问题**: `HabitAdapter.ViewMode` vs `org.wit.habit.ui.compose.ViewMode`

**解决方案**: 统一使用 Compose 的 `ViewMode`

1. 删除 `MainActivity` 中对 `HabitAdapter.ViewMode` 的引用
2. 全部改用 `org.wit.habit.ui.compose.ViewMode`

**验收**: 无编译错误，ViewMode 切换正常

---

### Task 5: 移除 "Compose" 测试按钮
**文件**: 
- `activity_main.xml` - 删除 `btnComposePreview`
- `MainActivity.kt` - 删除按钮点击监听器

**验收**: 标题栏恢复原样，只有标题和日历按钮

---

### Task 6: 完整测试
**测试场景**:

1. **基础功能**
   - [ ] 打开 app 显示习惯列表
   - [ ] 空状态正确显示
   - [ ] 点击 FAB 跳转到添加页面

2. **打卡功能**
   - [ ] 点击 Check In 按钮打卡成功
   - [ ] 按钮变成 Uncheck
   - [ ] 热力图更新
   - [ ] 点击 Uncheck 取消打卡

3. **过滤排序**
   - [ ] 点击 Filter 按钮显示菜单
   - [ ] 切换过滤条件（All/Checked In/Not Checked In）
   - [ ] 点击 Sort 按钮切换排序

4. **视图切换**
   - [ ] 点击日历按钮切换 Month → Week → Day → Month
   - [ ] 月视图显示 2 列网格
   - [ ] 周视图/日视图显示单列

5. **编辑删除**
   - [ ] 点击卡片跳转到编辑页面
   - [ ] 长按卡片删除习惯
   - [ ] 返回主页面后列表更新

6. **导航**
   - [ ] 底部导航切换到 Stats 页面
   - [ ] 底部导航切换到 Settings 页面
   - [ ] 返回主页面列表正确刷新

**验收**: 所有场景通过

---

## 涉及文件清单

### 新增文件 (1 个)
```
app/src/main/java/org/wit/habit/ui/compose/
└── MainContent.kt  # 主界面 Compose 内容
```

### 修改文件 (2 个)
```
app/src/main/res/layout/
└── activity_main.xml  # RecyclerView → ComposeView

app/src/main/java/org/wit/habit/
└── MainActivity.kt  # 移除 RecyclerView 逻辑，集成 Compose
```

### 可删除文件 (Phase 4)
```
- HabitAdapter.kt (保留到 Phase 4 确认无问题后删除)
- item_habit.xml
- item_habit_day.xml
- item_habit_week.xml
```

---

## 关键风险点与对策

| 风险 | 对策 |
|------|------|
| Compose 状态不刷新 | 每次操作后调用 `refreshComposeContent()` |
| ViewMode 枚举冲突 | 明确导入 `org.wit.habit.ui.compose.ViewMode` |
| 性能问题 | `remember` 缓存过滤排序结果 |
| 导航返回不刷新 | `onResume()` 中调用 `refreshComposeContent()` |

---

## 预计时间

- Task 1 (XML 修改): 10 分钟
- Task 2 (MainContent 组件): 20 分钟
- Task 3 (MainActivity 集成): 40 分钟
- Task 4 (ViewMode 统一): 10 分钟
- Task 5 (清理测试按钮): 5 分钟
- Task 6 (完整测试): 30 分钟

**总计**: ~2 小时

---

## 验收标准

### 功能完整性
- [ ] 所有原有功能保持正常工作
- [ ] Compose UI 与 XML 框架完美融合
- [ ] 打卡、编辑、删除操作正确
- [ ] 过滤、排序、视图切换正常

### 代码质量
- [ ] 无编译警告
- [ ] 无 RecyclerView 残留代码
- [ ] 状态管理清晰
- [ ] 日志记录完整

### 用户体验
- [ ] 界面流畅无卡顿
- [ ] 视觉效果与原版一致
- [ ] 无明显的布局跳动或闪烁

---

## 开始执行？

准备好开始 Phase 3 了吗？输入 `yes` 逐步执行 Task 1-6。
