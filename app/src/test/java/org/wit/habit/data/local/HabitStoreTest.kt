package org.wit.habit.data.local

import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Test

class HabitStoreTest {

    @Test
    fun `parseHabits skips corrupted entries but keeps valid ones`() {
        val json = """
            [
                {"id": 1, "name": "Valid One", "checkInCounts": {"2026-06-24": 1}},
                null,
                "not an object",
                {"id": 3, "name": "Valid Two", "checkInCounts": {}}
            ]
        """.trimIndent()

        val habits = HabitStore.parseHabits(json)

        assertEquals(2, habits.size)
        assertEquals(1L, habits[0].id)
        assertEquals("Valid One", habits[0].name)
        assertEquals(3L, habits[1].id)
        assertEquals("Valid Two", habits[1].name)
    }

    @Test
    fun `parseHabits returns empty list for invalid top level json`() {
        val habits = HabitStore.parseHabits("not a json array")

        assertEquals(emptyList<JSONObject>(), habits)
    }

    @Test
    fun `parseHabits returns empty list for blank input`() {
        assertEquals(emptyList<JSONObject>(), HabitStore.parseHabits("   "))
    }
}
