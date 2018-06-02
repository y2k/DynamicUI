package y2k.dynamicui.common

import kotlinx.coroutines.experimental.delay
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

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

        val idFactory = AtomicInteger(1)
        val r = Random()

        return List(r.nextInt(8) + 4) {
            GroupItem("Group #${it + 1}", r.nextBoolean(), idFactory.getAndIncrement(), foo(r, idFactory))
        }
    }

    private fun foo(r: Random, idFactory: AtomicInteger): List<Item> =
        List(r.nextInt(5) + 1) {
            when (r.nextInt(3)) {
                0 -> NumberItem(r.nextInt(100), idFactory.getAndIncrement())
                1 -> SeekBarItem(r.nextFloat(), idFactory.getAndIncrement())
                else -> SwitchItem(names[r.nextInt(names.size)], r.nextBoolean(), idFactory.getAndIncrement())
            }
        }

    private val names = listOf(
        "Oliver", "Jack", "Harry", "Jacob", "Charlie", "Thomas", "George", "Oscar", "James", "William", "Noah", "Alfie",
        "Joshua", "Muhammad", "Henry", "Leo", "Archie", "Ethan", "Joseph", "Freddie", "Samuel", "Alexander", "Logan",
        "Daniel", "Isaac", "Max", "Mohammed", "Benjamin", "Mason", "Lucas", "Edward", "Harrison", "Jake", "Dylan", "Riley",
        "Finley", "Theo", "Sebastian", "Adam", "Zachary", "Arthur", "Toby", "Jayden", "Luke", "Harley", "Lewis", "Tyler",
        "Harvey", "Matthew", "David", "Reuben", "Michael", "Elijah", "Kian", "Tommy", "Mohammad", "Blake", "Luca", "Theodore",
        "Stanley", "Jenson", "Nathan", "Charles", "Frankie", "Jude", "Teddy", "Louie", "Louis", "Ryan", "Hugo", "Bobby",
        "Elliott", "Dexter", "Ollie", "Alex", "Liam", "Kai", "Gabriel", "Connor", "Aaron", "Frederick", "Callum", "Elliot",
        "Albert", "Leon", "Ronnie", "Rory", "Jamie", "Austin", "Seth", "Ibrahim", "Owen", "Caleb", "Ellis", "Sonny", "Robert",
        "Joey", "Felix", "Finlay", "Jackson", "Top", "Girls", "Amelia", "Olivia", "Isla", "Emily", "Poppy", "Ava", "Isabella",
        "Jessica", "Lily", "Sophie", "Grace", "Sophia", "Mia", "Evie", "Ruby", "Ella", "Scarlett", "Isabelle", "Chloe",
        "Sienna", "Freya", "Phoebe", "Charlotte", "Daisy", "Alice", "Florence", "Eva", "Sofia", "Millie", "Lucy", "Evelyn",
        "Elsie", "Rosie", "Imogen", "Lola", "Matilda", "Elizabeth", "Layla", "Holly", "Lilly", "Molly", "Erin", "Ellie",
        "Maisie", "Maya", "Abigail", "Eliza", "Georgia", "Jasmine", "Esme", "Willow", "Bella", "Annabelle", "Ivy", "Amber",
        "Emilia", "Emma", "Summer", "Hannah", "Eleanor", "Harriet", "Rose", "Amelie", "Lexi", "Megan", "Gracie", "Zara",
        "Lacey", "Martha", "Anna", "Violet", "Darcey", "Maria", "Maryam", "Brooke", "Aisha", "Katie", "Leah", "Thea", "Darcie",
        "Hollie", "Amy", "Mollie", "Heidi", "Lottie", "Bethany", "Francesca", "Faith", "Harper", "Nancy", "Beatrice", "Isabel",
        "Darcy", "Lydia", "Sarah", "Sara", "Julia", "Victoria", "Zoe", "Robyn")
}