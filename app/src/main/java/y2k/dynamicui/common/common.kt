package y2k.dynamicui.common

import kotlinx.coroutines.experimental.delay

sealed class Item

data class GroupItem(val title: String, val isEnabled: Boolean, val children: List<Item>) : Item()

object EditItem : Item() {
    override fun toString(): String = "EditItem"
}

object SpinnerItem : Item() {
    override fun toString(): String = "SpinnerItem"
}

object Effects {

    suspend fun loadSettings(): List<Item> {
        delay(1000)
        return listOf(
            GroupItem("Group #1", true, listOf(EditItem, SpinnerItem)),
            GroupItem("Group #2", true, listOf(EditItem, SpinnerItem)),
            GroupItem("Group #3", true, listOf(EditItem, SpinnerItem)),
            GroupItem("Group #4", true, listOf(EditItem, SpinnerItem)),
            GroupItem("Group #5", true, listOf(EditItem, SpinnerItem)))
    }
}