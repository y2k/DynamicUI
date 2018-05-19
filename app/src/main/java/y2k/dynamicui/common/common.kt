package y2k.dynamicui.common

sealed class Item

object EditItem : Item() {
    override fun toString(): String = "EditItem"
}

object SpinnerItem : Item() {
    override fun toString(): String = "SpinnerItem"
}