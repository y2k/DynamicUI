package y2k.dynamicui.common

import kotlinx.coroutines.experimental.delay
import java.util.*

sealed class Item

data class GroupItem(val title: String, val isEnabled: Boolean, val children: List<Item>) : Item()

data class SwitchItem(val title: String, val isChecked: Boolean) : Item()

data class SeekBarItem(val value: Float) : Item()

data class NumberItem(val value: Int) : Item()

object Effects {

    suspend fun loadSettings(): List<Item> {
        delay(1000)
        val r = Random()
        return List(10) {
            GroupItem("Group #$it", r.nextBoolean(), listOf(
                SeekBarItem(r.nextFloat()), SwitchItem("Swipe #$it", r.nextBoolean())))
        }
    }
}