package y2k.dynamicui.recyclerview

import android.app.Fragment
import android.os.Bundle
import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView.ViewHolder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.fragment_recyclerview.*
import kotlinx.android.synthetic.main.item_1.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import y2k.dynamicui.R
import y2k.dynamicui.common.Effects
import y2k.dynamicui.common.Item

class RecyclerViewFragment : Fragment() {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val adapter = Adapter()
        list.adapter = adapter

        launch(UI) {
            adapter.submitList(Effects.loadSettings())
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_recyclerview, container, false)
}

private class Adapter : ListAdapter<Item, Adapter.VH>(itemCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_1, parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))

    private class VH(override val containerView: View) : ViewHolder(containerView), LayoutContainer {

        fun bind(item: Item) {
            title.text = "${item.hashCode()}"
        }
    }

    companion object {

        private val itemCallback = object : DiffUtil.ItemCallback<Item>() {
            override fun areItemsTheSame(oldItem: Item?, newItem: Item?): Boolean = oldItem == oldItem
            override fun areContentsTheSame(oldItem: Item?, newItem: Item?): Boolean = oldItem == oldItem
        }
    }
}