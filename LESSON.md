============================================================
  习惯打卡APP构建过程 - 问题记录与经验教训
============================================================
记录时间: 2026-05-07
项目: placemark-0 (从模板改造为习惯打卡APP)


------------------------------------------------------------
【问题1】新增依赖下载失败
------------------------------------------------------------
现象:
  编译报错: Could not resolve androidx.recyclerview:recyclerview:1.3.2
  从 google() 和 mavenCentral() 都返回 400 Bad Request

原因:
  当前网络环境下，Gradle无法下载新的外部依赖。
  用户此前已说明"每次新建项目下载依赖速度过慢"，使用了本地缓存的模板项目。

解决方案:
  移除 RecyclerView 依赖，改用 Android SDK 原生组件。
  将 RecyclerView + Adapter 方案替换为 ScrollView + LinearLayout 动态 inflate 子视图。

经验教训:
  - 在依赖下载受限的环境中，优先使用 Android SDK 自带组件
  - 能用原生 ViewGroup (LinearLayout/ScrollView/ListView) 解决的，不引入第三方库
  - 添加新依赖前，先确认当前网络环境能否正常拉取


------------------------------------------------------------
【问题2】主题重命名不彻底
------------------------------------------------------------
现象:
  编译报错: error: resource style/Base.Theme.Placemark (aka org.wit.habit:style/Base.Theme.Placemark) not found.

原因:
  将 Theme.Placemark 改名为 Theme.Habit 时，只改了 style 的 name，
  但漏改了 parent 引用：
  <style name="Theme.Habit" parent="Base.Theme.Placemark" />  ← 这里还是旧名

解决方案:
  全局搜索关键词 "Placemark"，将所有残留引用一次性替换为 "Habit"。

经验教训:
  - 重命名资源（主题、颜色、字符串）后，必须全局搜索旧名称确认无残留
  - Android 资源之间的 parent/引用关系很脆弱，一处漏改就会导致 AAPT 链接失败
  - 使用 grep/搜索工具全局检查，比肉眼逐个文件检查更可靠


------------------------------------------------------------
【问题3】Activity 未在 AndroidManifest.xml 中注册
------------------------------------------------------------
现象:
  APP能正常打开主页，但点击"添加习惯"按钮后立即闪退。

原因:
  AndroidManifest.xml 中只声明了 MainActivity，
  新建的 HabitActivity 没有在 Manifest 中注册。
  调用 startActivity(Intent(this, HabitActivity::class.java)) 时，
  系统抛出 ActivityNotFoundException 导致崩溃。

解决方案:
  在 AndroidManifest.xml 的 <application> 标签内补充：
  <activity android:name=".HabitActivity" android:exported="false" />

经验教训:
  - 【铁律】每新增一个 Activity / Service / BroadcastReceiver，
    第一件事就是在 AndroidManifest.xml 中注册！
  - 闪退类问题首先检查 logcat 异常堆栈，通常前5行就能定位原因
  - 页面跳转闪退 90% 都是 Manifest 漏注册或 exported 属性问题


------------------------------------------------------------
【问题4】包名重命名时测试目录未同步
------------------------------------------------------------
现象:
  修改包名后，测试文件仍在旧目录 org/wit/placemark/ 下，
  需要手动移动到新目录 org/wit/habit/ 并更新 package 声明。

原因:
  Android Studio 的 Rename Package 功能可以自动处理，
  但手动修改时容易遗漏 test/ 和 androidTest/ 目录。

解决方案:
  同步移动以下目录和文件：
  - app/src/main/java/org/wit/placemark/ → org/wit/habit/
  - app/src/test/java/org/wit/placemark/ → org/wit/habit/
  - app/src/androidTest/java/org/wit/placemark/ → org/wit/habit/
  同时更新 build.gradle.kts 中的 namespace 和 applicationId。

经验教训:
  - 手动改包名要改5个地方：源码目录、测试目录、namespace、applicationId、Manifest中相关引用
  - 修改后立即全局搜索旧包名，确保清理干净


------------------------------------------------------------
【通用排查清单】
------------------------------------------------------------
以后遇到类似问题，按以下顺序排查：

□ 编译阶段报错
  1. 检查 AndroidManifest.xml 是否有语法错误/资源找不到
  2. 检查 themes/colors 等资源文件是否有残留旧名称
  3. 检查 build.gradle 中的依赖版本是否可下载

□ 运行时闪退
  1. 先看 logcat 异常类型和堆栈（最重要！）
  2. 检查新组件是否在 Manifest 中注册
  3. 检查 findViewById 的 ID 是否与布局文件一致
  4. 检查 lateinit 变量是否在访问前已初始化

□ 数据不保存/异常
  1. 检查 SharedPreferences 的 key 是否一致
  2. 检查 JSON 序列化/反序列化字段名是否匹配
  3. 检查存储权限（如果需要写外部存储）


------------------------------------------------------------
【版本提交记录】
------------------------------------------------------------
75adff8  Initial commit: Placemark project template
5d645e8  Transform Placemark template into Habit Tracker (minimal version)
c3f3102  Fix: Register HabitActivity in AndroidManifest to prevent crash


============================================================


------------------------------------------------------------
【问题5】PowerShell 语法限制导致 Git 提交失败
------------------------------------------------------------
现象:
  执行 git add -A && git commit -m "..." 时，PowerShell 报错：
  "&& 不是此版本中的有效语句分隔符"

原因:
  Windows PowerShell 不支持 Bash 风格的 && 链式语法。

解决方案:
  分两步执行：先 git add -A，再单独执行 git commit -m "..."；
  或在 PowerShell 中使用分号 ; 分隔多条命令。

经验教训:
  - Windows 环境下，PowerShell 的语法与 Bash/Unix Shell 不同
  - 批量命令应使用分号 ; 或管道 |，而非 &&


------------------------------------------------------------
【问题6】数据模型字段重命名导致旧数据解析异常
------------------------------------------------------------
现象:
  将 Habit 类的字段 title → name、createdTime → createdDate 后，
  旧版本 SharedPreferences 中保存的 JSON 仍使用旧字段名，
  新代码 optString("name") 读取不到旧数据中的 "title"，导致习惯名称显示为空。

原因:
  JSON 序列化/反序列化字段名变更后，没有兼容旧数据的解析逻辑。

解决方案:
  在交付时明确提醒用户：由于数据模型变更，旧版本本地数据可能无法正确解析，
  建议清除应用数据或卸载重装。

经验教训:
  - 修改持久化模型的字段名时，必须考虑旧数据兼容性
  - 最佳实践：保留旧字段的兼容解析，或提供数据迁移方案
  - 若无法兼容，必须在交付文档中明确告知用户清数据风险


------------------------------------------------------------
【问题7】Activity 重命名时文件清理不彻底
------------------------------------------------------------
现象:
  将 HabitActivity 重命名为 AddHabitActivity 时，
  需要同步处理 Kotlin 文件、XML 布局文件、AndroidManifest.xml 多处，
  容易遗漏旧文件的删除或旧引用的更新。

原因:
  Android 项目中一个 Activity 至少涉及 3 个文件（.kt + .xml + Manifest），
  重命名属于"增删改"组合操作，手动处理容易遗漏。

解决方案:
  使用 git status 检查僵尸文件，确保旧文件已删除、新文件已创建、
  Manifest 已更新、所有引用代码（如 Intent 跳转）已同步。

经验教训:
  - 组件重命名不是简单的"改个名"，而是增删改三连操作
  - 提交前用 git status 确认没有无引用的残留文件


------------------------------------------------------------
【问题8】RecyclerView 依赖的实际可用性误判
------------------------------------------------------------
现象:
  历史记录中因网络受限移除了 RecyclerView 依赖，改用 ScrollView + LinearLayout。
  但接手项目后发现 appcompat 和 material 已通过传递依赖引入了 RecyclerView，
  无需额外声明即可直接使用。

原因:
  早期结论未随依赖树变化而更新，导致过度保守。

解决方案:
  重构回 RecyclerView + Adapter 方案，构建一次通过，未新增任何依赖。

经验教训:
  - 不要盲目相信历史结论，接手项目时应先检查当前实际依赖情况
  - appcompat / material 通常已间接包含 recyclerview、constraintlayout 等核心组件


------------------------------------------------------------
【问题9】卡片布局横向空间不足
------------------------------------------------------------
现象:
  在 item_habit.xml 中为习惯卡片新增"编辑"按钮时，
  右侧原有的"打卡"和"删除"两个按钮已占据大量横向空间，
  三个文字按钮横向并排会导致超出屏幕或挤压内容区。

原因:
  ConstraintLayout 中横向链式排列的按钮过多，未考虑屏幕宽度限制。

解决方案:
  将右侧操作按钮改为垂直排列，并适当缩小字体（textSize="12sp"）
  和按钮最小高度（minHeight="40dp"），让卡片高度自适应容纳。

经验教训:
  - 卡片上的操作按钮超过 2 个时，优先采用垂直堆叠或图标按钮，而非横向排列
  - 设计布局时应考虑小屏设备的横向空间限制


------------------------------------------------------------
【补充排查清单 - 本次开发新增】
------------------------------------------------------------
以后遇到类似问题，在原有清单基础上补充检查以下项目：

□ 数据模型变更后
  1. 旧版本保存的 JSON / SharedPreferences 数据能否被新代码正确解析？
  2. 字段名变更时，是否需要保留旧字段的兼容解析逻辑？
  3. 若无法兼容旧数据，是否在交付文档中明确告知用户清数据风险？

□ 组件重命名时
  1. Kotlin/Java 源文件是否已正确增删？
  2. XML 布局文件是否已正确增删？
  3. AndroidManifest.xml 中的声明是否已同步更新？
  4. 所有引用该组件的代码（如 Intent 跳转、findViewById）是否已同步？
  5. git status 中是否还有无引用的僵尸文件残留？

□ 依赖评估时
  1. 当前 build.gradle 中已声明的库是否已通过传递依赖引入了目标组件？
  2. 添加新依赖前，先确认当前网络环境能否正常拉取
  3. 若网络受限，优先使用 Android SDK 自带组件（LinearLayout/ScrollView/ListView）

□ Windows / PowerShell 环境下
  1. 多条命令链式执行时，使用分号 ; 而非 &&
  2. 路径分隔符使用反斜杠 \ 或统一用正斜杠 /（Git Bash 支持）


------------------------------------------------------------
【版本提交记录 - 本次开发追加】
------------------------------------------------------------
c3bf45b  Implement habit tracker core features: RecyclerView list, check-in/cancel, stats page
79923a9  Add edit habit feature: reuse AddHabitActivity with edit mode, add edit button in card


============================================================


------------------------------------------------------------
【问题10】Android 单元测试中使用 org.json 报 RuntimeException
------------------------------------------------------------
现象:
  新增 HabitSerializerTest 后运行 ./gradlew.bat test，
  所有用到 JSONObject/JSONArray 的测试都报 java.lang.RuntimeException。

原因:
  Android 单元测试默认使用 android.jar 的桩实现，
  org.json 包中未实现的方法会抛出 RuntimeException，
  而不是执行真正的 JSON 解析/构造逻辑。

解决方案:
  在 gradle/libs.versions.toml 中添加 org.json 的测试依赖：
    json = "20231013"
    json = { group = "org.json", name = "json", version.ref = "json" }
  在 app/build.gradle.kts 中：
    testImplementation(libs.json)

经验教训:
  - 单元测试里直接使用 Android SDK 的 org.json 时，需要额外引入真实实现
  - 报错信息只有 RuntimeException 时，优先怀疑 android.jar 桩实现问题
  - 网络受限环境下，添加依赖前确认本地 Gradle 缓存是否已有该库


============================================================


------------------------------------------------------------
【问题11】统计逻辑中 ranking count 的语义误判
------------------------------------------------------------
现象:
  Phase 2 抽取 StatsCalculator 后，单元测试多次断言失败：
  - 期望排行项 count 等于总打卡次数，实际却是"达到目标的天数"。
  - 排行同分按名称排序时，因 count 实际都为 1，顺序与预期不符。

原因:
  原 StatsFragment 中 loadMonthStats / loadWeekStats / loadYearStats 的代码：
    if (count >= habit.targetCount) { monthCount++ }
  这里的 monthCount 统计的是"该周期内有多少天完成了目标"，
  而不是该 habit 的总打卡次数或累计完成次数。
  写测试前没有仔细确认业务语义，直接按字面 count 理解。

解决方案:
  重新阅读原实现，确认 RankItem.count 的语义为"周期内达标天数"，
  并据此修正测试断言：
  - 单个日期打卡次数 >= targetCount 时，count 才 +1。
  - 多个 habit 同分（达标天数相同）时，才按名称升序。

经验教训:
  - 抽离业务逻辑前，先用测试覆盖原行为，再对照原实现确认每个字段语义
  - "count" 这类通用命名容易误导，测试命名或断言注释应体现业务含义
  - 新增单元测试遇到断言失败时，先怀疑对业务规则的理解，再怀疑实现


============================================================
