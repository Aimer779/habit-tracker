# Compose Migration Guide - Phase 1 Complete

## Phase 1 Summary: Infrastructure Setup ✅

### What was done:
1. ✅ Added Compose BOM and dependencies (Material3, UI, Tooling)
2. ✅ Enabled `buildFeatures.compose = true`
3. ✅ Added Kotlin Compose Compiler plugin
4. ✅ Created theme system (Color.kt, Type.kt, Theme.kt)
5. ✅ Created `ComposePreviewActivity` for testing
6. ✅ Verified build succeeds with Compose

### Files Created:
- `app/src/main/java/org/wit/habit/ComposePreviewActivity.kt` - Standalone Compose test activity
- `app/src/main/java/org/wit/habit/ui/theme/Color.kt` - Color definitions from colors.xml
- `app/src/main/java/org/wit/habit/ui/theme/Type.kt` - Typography definitions
- `app/src/main/java/org/wit/habit/ui/theme/Theme.kt` - Material3 theme setup

### Files Modified:
- `gradle/libs.versions.toml` - Added Compose dependencies
- `app/build.gradle.kts` - Enabled Compose, added dependencies
- `build.gradle.kts` - Added Compose plugin
- `AndroidManifest.xml` - Registered ComposePreviewActivity

---

## How to Use Compose in Existing Activities (Hybrid Mode)

### Option 1: Add Compose to XML Layout

In your existing layout XML, add a ComposeView:

```xml
<!-- activity_main.xml -->
<androidx.compose.ui.platform.ComposeView
    android:id="@+id/composeView"
    android:layout_width="match_parent"
    android:layout_height="wrap_content" />
```

Then in your Activity:

```kotlin
class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        val composeView = findViewById<ComposeView>(R.id.composeView)
        composeView.setContent {
            HabitTheme {
                // Your Compose UI here
                MyComposeComponent()
            }
        }
    }
}
```

### Option 2: Programmatically Add ComposeView

```kotlin
class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        val container = findViewById<ViewGroup>(R.id.container)
        val composeView = ComposeView(this).apply {
            setContent {
                HabitTheme {
                    MyComposeComponent()
                }
            }
        }
        container.addView(composeView)
    }
}
```

### Option 3: Pure Compose Activity (Recommended for new screens)

```kotlin
class NewActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HabitTheme {
                NewScreen()
            }
        }
    }
}
```

---

## Testing Compose Setup

### 1. Launch ComposePreviewActivity
Add this to any existing Activity to test:

```kotlin
startActivity(Intent(this, ComposePreviewActivity::class.java))
```

### 2. View Compose Previews in Android Studio
Open `ComposePreviewActivity.kt` and look for the **Preview** pane on the right side of Android Studio.

### 3. Build and Run
```bash
./gradlew.bat assembleDebug
./gradlew.bat installDebug
```

---

## Next Steps: Phase 2

Ready to migrate UI components:
1. Start with `HabitCard` (habit_item.xml → Composable)
2. Create reusable Compose components
3. Gradually replace RecyclerView items with LazyColumn

Would you like to:
- **A**: Start Phase 2 (migrate habit card component)
- **B**: Test ComposePreviewActivity on a device first
- **C**: See a specific component migration example
