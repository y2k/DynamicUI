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

private class Adapter : ListAdapter<Item, Adapter.VH>(itemCallback) {

    override fun getItemViewType(position: Int): Int = position

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val i = LayoutInflater.from(parent.context)
        return when (getItem(viewType)) {
            is GroupItem -> SwitchVH(i.inflate(R.layout.item_swipe, parent, false))
            is SwipeItem -> TODO()
            is SeekBarItem -> TODO()
            is NumberItem -> TODO()
        }
    }

    override fun onBindViewHolder(holder: VH, position: Int) {

        val x =
            when (getItem(position)) {
                is GroupItem -> TODO()
                is SwipeItem -> TODO()
                is SeekBarItem -> TODO()
                is NumberItem -> TODO()
            }

        holder.bind(getItem(position))
    }

    private abstract class VH(view: View) : ViewHolder(view) {
        abstract fun bind(item: Item)
    }

    private class SwitchVH(
        override val containerView: View) :
        LayoutContainer,
        VH(containerView) {

        override fun bind(item: Item) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        fun bind(item: SwipeItem) {
            switchView.text = "${item.hashCode()}"
        }
    }

    private class SpinnerVH(override val containerView: View) : ViewHolder(containerView), LayoutContainer {

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