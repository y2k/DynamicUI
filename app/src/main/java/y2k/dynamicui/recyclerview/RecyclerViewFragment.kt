package y2k.dynamicui.recyclerview

import android.app.Fragment
import android.os.Bundle
import android.support.v7.widget.RecyclerView.ViewHolder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.fragment_recyclerview.*
import kotlinx.android.synthetic.main.item_seekbar.*
import kotlinx.android.synthetic.main.item_switch.*
import y2k.dynamicui.R
import y2k.dynamicui.common.*
import y2k.dynamicui.litho.Model
import y2k.dynamicui.litho.Page

class RecyclerViewFragment : Fragment() {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val adapter = GenericAdapter<Item, ViewHolder>()
        list.adapter = adapter

        val (model, effect) = Page.init()
        handleEffect(effect, model, adapter)
    }

    private fun handleEffect(effect: EffectHandlers?, model: Model, adapter: GenericAdapter<Item, ViewHolder>) {
        adapter.submitList(model.items.flatConfigs())

        ElmUtils.dispatchEffect(effect) { msg ->
            val (newModel, newEffect) = Page.update(model, msg)
            handleEffect(newEffect, newModel, adapter)
        }
    }

    private fun List<Item>.flatConfigs(): List<Item> =
        flatMap {
            when (it) {
                is GroupItem -> it.children
                else -> listOf(it)
            }
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_recyclerview, container, false)
}

object SwitchTC : HolderFactory<SwitchItem>, HolderBinder<SwitchItem, SwitchTC.SwitchHolder> {

    override fun create(item: SwitchItem, inflater: LayoutInflater, parent: ViewGroup?): ViewHolder =
        SwitchHolder(inflater.inflate(R.layout.item_switch, parent, false))

    override fun bind(item: SwitchItem, holder: SwitchHolder) {
        holder.switchView.text = "${item.hashCode()}"
        holder.switchView.isChecked = item.isChecked
    }

    class SwitchHolder(override val containerView: View) : LayoutContainer, ViewHolder(containerView)
}

object SeekBarTC : HolderFactory<SeekBarItem>, HolderBinder<SeekBarItem, SeekBarTC.SeekBarHolder> {

    override fun create(item: SeekBarItem, inflater: LayoutInflater, parent: ViewGroup?): ViewHolder =
        SeekBarHolder(inflater.inflate(R.layout.item_seekbar, parent, false))

    override fun bind(item: SeekBarItem, holder: SeekBarHolder) {
        holder.seekbar.progress = (10_000 * item.value).toInt()
        holder.seekbar.max = 10_000
    }

    class SeekBarHolder(override val containerView: View) : ViewHolder(containerView), LayoutContainer
}