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
import kotlinx.android.synthetic.main.item_seekbar.*
import kotlinx.android.synthetic.main.item_swipe.*
import y2k.dynamicui.R
import y2k.dynamicui.common.*
import y2k.dynamicui.litho.Model
import y2k.dynamicui.litho.Page

class RecyclerViewFragment : Fragment() {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val adapter = Adapter()
        list.adapter = adapter

        val (model, effect) = Page.init()
        handleEffect(effect, model, adapter)
    }

    private fun handleEffect(effect: EffectHandlers?, model: Model, adapter: Adapter) {
        adapter.submitList(model.items)

        ElmUtils.dispatchEffect(effect) { msg ->
            val (newModel, newEffect) = Page.update(model, msg)
            handleEffect(newEffect, newModel, adapter)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_recyclerview, container, false)
}

private class Adapter : ListAdapter<Item, ViewHolder>(itemCallback) {

    override fun getItemViewType(position: Int): Int = position

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (getItem(viewType)) {
            is GroupItem -> TODO()
            is SwitchItem -> SwitchVH(inflater.inflate(R.layout.item_swipe, parent, false))
            is SeekBarItem -> SeekBarVH(inflater.inflate(R.layout.item_seekbar, parent, false))
            is NumberItem -> TODO()
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        when (item) {
            is GroupItem -> TODO()
            is SwitchItem -> (holder as SwitchVH).bind(item)
            is SeekBarItem -> (holder as SeekBarVH).bind(item)
            is NumberItem -> TODO()
        }
    }

    private class SwitchVH(override val containerView: View)
        : ViewHolder(containerView), LayoutContainer {

        fun bind(item: SwitchItem) {
            switchView.text = "${item.hashCode()}"
        }
    }

    private class SeekBarVH(override val containerView: View)
        : ViewHolder(containerView), LayoutContainer {

        fun bind(item: SeekBarItem) {
            seekbar.progress = (10_000 * item.value).toInt()
            seekbar.max = 10_000
        }
    }

    companion object {

        private val itemCallback = object : DiffUtil.ItemCallback<Item>() {
            override fun areItemsTheSame(oldItem: Item?, newItem: Item?): Boolean = oldItem == oldItem
            override fun areContentsTheSame(oldItem: Item?, newItem: Item?): Boolean = oldItem == oldItem
        }
    }
}