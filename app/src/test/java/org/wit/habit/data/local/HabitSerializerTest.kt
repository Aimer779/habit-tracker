package org.wit.habit.data.local

import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Test
import org.wit.habit.models.Habit

class HabitSerializerTest {

    @Test
    fun `toJson writes all habit fields`() {
        val habit = Habit(
            id = 123L,
            name = "Read",
            description = "Read every day",
            createdDate = "2026-06-01",
            checkInCounts = mapOf("2026-06-23" to 2, "2026-06-24" to 1),
            targetCount = 3,
            icon = "📖",
            color = "purple"
        )

        val json = HabitSerializer.toJson(habit)

        assertEquals(123L, json.getLong("id"))
        assertEquals("Read", json.getString("name"))
        assertEquals("Read every day", json.getString("description"))
        assertEquals("2026-06-01", json.getString("createdDate"))
        assertEquals(3, json.getInt("targetCount"))
        assertEquals("📖", json.getString("icon"))
        assertEquals("purple", json.getString("color"))
        val counts = json.getJSONObject("checkInCounts")
        assertEquals(2, counts.getInt("2026-06-23"))
        assertEquals(1, counts.getInt("2026-06-24"))
    }

    @Test
    fun `fromJson reads all habit fields`() {
        val json = JSONObject().apply {
            put("id", 456L)
            put("name", "Exercise")
            put("description", "Run")
            put("createdDate", "2026-05-01")
            put("targetCount", 2)
            put("icon", "🏃")
            put("color", "green")
            put("checkInCounts", JSONObject().apply {
                put("2026-06-23", 1)
                put("2026-06-24", 3)
            })
        }

        val habit = HabitSerializer.fromJson(json)

        assertEquals(456L, habit.id)
        assertEquals("Exercise", habit.name)
        assertEquals("Run", habit.description)
        assertEquals("2026-05-01", habit.createdDate)
        assertEquals(2, habit.targetCount)
        assertEquals("🏃", habit.icon)
        assertEquals("green", habit.color)
        assertEquals(mapOf("2026-06-23" to 1, "2026-06-24" to 3), habit.checkInCounts)
    }

    @Test
    fun `fromJson falls back to checkInDates for old data`() {
        val json = JSONObject().apply {
            put("id", 789L)
            put("name", "Meditation")
            put("checkInDates", org.json.JSONArray().apply {
                put("2026-06-22")
                put("2026-06-23")
            })
        }

        val habit = HabitSerializer.fromJson(json)

        assertEquals(789L, habit.id)
        assertEquals("Meditation", habit.name)
        assertEquals(mapOf("2026-06-22" to 1, "2026-06-23" to 1), habit.checkInCounts)
    }

    @Test
    fun `fromJson uses defaults for missing fields`() {
        val json = JSONObject().apply {
            put("name", "Minimal")
        }

        val habit = HabitSerializer.fromJson(json)

        assertEquals("Minimal", habit.name)
        assertEquals("", habit.description)
        assertEquals(1, habit.targetCount)
        assertEquals("✅", habit.icon)
        assertEquals("blue", habit.color)
        assertEquals(emptyMap<String, Int>(), habit.checkInCounts)
    }

    @Test
    fun `round trip preserves habit data`() {
        val original = Habit(
            id = 999L,
            name = "Drink Water",
            description = "8 glasses",
            createdDate = "2026-01-01",
            checkInCounts = mapOf("2026-06-24" to 5),
            targetCount = 8,
            icon = "💧",
            color = "teal"
        )

        val restored = HabitSerializer.fromJson(HabitSerializer.toJson(original))

        assertEquals(original, restored)
    }
}
