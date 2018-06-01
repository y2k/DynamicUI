package y2k.dynamicui.common

import kotlinx.coroutines.experimental.delay
import java.util.*

sealed class Item
data class GroupItem(val title: String, val isEnabled: Boolean, val id: Int, val children: List<Item>) : Item()
data class SwitchItem(val title: String, val isChecked: Boolean, val id: Int) : Item()
data class SeekBarItem(val value: Float, val id: Int) : Item()
data class NumberItem(val value: Int, val id: Int) : Item()

object Configs {

    fun changeNumber(configs: List<Item>, id: Int, increase: Boolean): List<Item> =
        configs.walk {
            when (it) {
                is NumberItem -> when (it.id == id) {
                    true -> when (increase) {
                        true -> it.copy(value = it.value + 1)
                        false -> it.copy(value = it.value - 1)
                    }
                    false -> it
                }
                else -> it
            }
        }

    fun changeSwitch(configs: List<Item>, id: Int): List<Item> =
        configs.walk {
            when (it) {
                is SwitchItem -> when (id) {
                    it.id -> it.copy(isChecked = !it.isChecked)
                    else -> it
                }
                else -> it
            }
        }

    fun changeSeekBar(configs: List<Item>, id: Int, value: Float): List<Item> =
        configs.walk {
            when (it) {
                is SeekBarItem -> when (id) {
                    it.id -> it.copy(value = value)
                    else -> it
                }
                else -> it
            }
        }

    fun flatConfigs(configs: List<Item>): List<Item> =
        configs.flatMap {
            when (it) {
                is GroupItem -> flatConfigs(it.children)
                else -> listOf(it)
            }
        }

    fun compareById(oldItem: Item, newItem: Item): Boolean = when (oldItem) {
        is GroupItem -> newItem is GroupItem && oldItem.id == newItem.id
        is SwitchItem -> newItem is SwitchItem && oldItem.id == newItem.id
        is SeekBarItem -> newItem is SeekBarItem && oldItem.id == newItem.id
        is NumberItem -> newItem is NumberItem && oldItem.id == newItem.id
    }
}

private fun List<Item>.walk(f: (Item) -> Item): List<Item> =
    map {
        when (it) {
            is GroupItem -> it.copy(children = it.children.walk(f))
            else -> f(it)
        }
    }

object Effects {

    suspend fun loadSettings(): List<Item> {
        delay(500)
        var id = 0
        val r = Random()

        return List(r.nextInt(8) + 4) {
            GroupItem("Group #${it + 1}", r.nextBoolean(), id++, listOf(
                NumberItem(r.nextInt(100), id++),
                SeekBarItem(r.nextFloat(), id++),
                SwitchItem("Swipe #${it + 1}", r.nextBoolean(), id++)))
        }
    }
}