package y2k.dynamicui.recyclerview

import android.app.Fragment
import android.os.Bundle
import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import y2k.dynamicui.R

class RecyclerViewFragment : Fragment() {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_recyclerview, container, false)
}

sealed class Item
class EditItem : Item()

private class Adapter : ListAdapter<Item, Adapter.VH>(itemCallback) {

    private val items = List(5, { EditItem() })

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_1, parent, false))

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(items[position])

    private class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: Item): Unit = TODO()
    }

    companion object {

        private val itemCallback = object : DiffUtil.ItemCallback<Item>() {
            override fun areItemsTheSame(oldItem: Item?, newItem: Item?): Boolean = oldItem == oldItem
            override fun areContentsTheSame(oldItem: Item?, newItem: Item?): Boolean = oldItem == oldItem
        }
    }
}