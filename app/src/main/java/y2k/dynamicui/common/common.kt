package y2k.dynamicui.common

import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
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
                SeekBarItem(r.nextFloat()), SwitchItem("Switch #$it", r.nextBoolean())))
//            GroupItem("Group #$it", r.nextBoolean(), listOf(
//                NumberItem(0), SeekBarItem(r.nextFloat()), SwitchItem("Switch #$it", r.nextBoolean())))
        }
    }
}

class GenericAdapter<T, TH : RecyclerView.ViewHolder> : ListAdapter<T, TH>(Foo()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TH {
        val i = getItem(viewType)
        val F = instance<HolderFactory<T>>(i)
        val l = LayoutInflater.from(parent.context)
        return F.create(i, l, parent) as TH
    }

    override fun onBindViewHolder(holder: TH, position: Int) {
        val i = getItem(position)
        val F = instance<HolderBinder<T, TH>>(i, holder)
        F.bind(i, holder)
    }

    class Foo<T> : DiffUtil.ItemCallback<T>() {
        override fun areItemsTheSame(oldItem: T?, newItem: T?): Boolean = oldItem == oldItem
        override fun areContentsTheSame(oldItem: T?, newItem: T?): Boolean = oldItem == oldItem
    }
}

interface HolderFactory<T> {
    fun create(item: T, inflater: LayoutInflater, parent: ViewGroup?): RecyclerView.ViewHolder
}

interface HolderBinder<T, TVH> {
    fun bind(item: T, holder: TVH)
}