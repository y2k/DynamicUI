package y2k.dynamicui.recyclerview

import android.app.Fragment
import android.os.Bundle
import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView.ViewHolder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.fragment_recyclerview.*
import kotlinx.android.synthetic.main.item_number.*
import kotlinx.android.synthetic.main.item_seekbar.*
import kotlinx.android.synthetic.main.item_switch.*
import y2k.dynamicui.R
import y2k.dynamicui.common.*

class RecyclerViewFragment : Fragment() {

    private val adapter = Adapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        Elm.start(ConfigComponent, { adapter.submitList(Items.flatConfigs(it.configs)) })
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        list.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        list.adapter = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_recyclerview, container, false)
}

private class Adapter : ListAdapter<Item, ViewHolder>(itemCallback) {

    var list = emptyList<Item>()

    override fun submitList(list: List<Item>) {
        super.submitList(list)
        this.list = list
    }

    override fun getItemViewType(position: Int): Int = position

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (getItem(viewType)) {
            is SwitchItem -> SwitchVH(inflater.inflate(R.layout.item_switch, parent, false), this)
            is SeekBarItem -> SeekBarVH(inflater.inflate(R.layout.item_seekbar, parent, false), this)
            is NumberItem -> NumberVH(inflater.inflate(R.layout.item_number, parent, false), this)
            is GroupItem -> TODO()
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        when (item) {
            is SwitchItem -> (holder as SwitchVH).bind(item)
            is SeekBarItem -> (holder as SeekBarVH).bind(item)
            is NumberItem -> (holder as NumberVH).bind(item)
            is GroupItem -> TODO()
        }
    }

    private class SwitchVH(override val containerView: View, private val adapter: Adapter)
        : ViewHolder(containerView), LayoutContainer {

        fun bind(item: SwitchItem) {
            switchView.text = item.title
            switchView.isChecked = item.isChecked

            switchView.setOnCheckedChangeListener { _, _ ->
                Elm.event(ConfigComponent,
                    Msg_.Switch(item),
                    adapter.list.let(::Model_),
                    { adapter.submitList(Items.flatConfigs(it.configs)) })
            }
        }
    }

    private class SeekBarVH(override val containerView: View, private val adapter: Adapter)
        : ViewHolder(containerView), LayoutContainer {

        fun bind(item: SeekBarItem) {
            seekbar.max = 10_000
            seekbar.progress = (10_000 * item.value).toInt()

            seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) = Unit
                override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
                override fun onStopTrackingTouch(seekBar: SeekBar) {
                    Elm.event(ConfigComponent,
                        Msg_.SeekBar(item, seekBar.progress / 10_000f),
                        adapter.list.let(::Model_),
                        { adapter.submitList(Items.flatConfigs(it.configs)) })
                }
            })
        }
    }

    private class NumberVH(override val containerView: View, private val adapter: Adapter)
        : ViewHolder(containerView), LayoutContainer {

        fun bind(item: NumberItem) {
            number.text = "${item.value}"

            decrease.setOnClickListener {
                Elm.event(ConfigComponent,
                    Msg_.Click(item, false),
                    adapter.list.let(::Model_),
                    { adapter.submitList(Items.flatConfigs(it.configs)) })
            }
            increase.setOnClickListener {
                Elm.event(ConfigComponent, Msg_.Click(item, true),
                    adapter.list.let(::Model_),
                    { adapter.submitList(Items.flatConfigs(it.configs)) })
            }
        }
    }

    companion object {

        val itemCallback = object : DiffUtil.ItemCallback<Item>() {
            override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean = Items.compareById(oldItem, newItem)
            override fun areContentsTheSame(oldItem: Item?, newItem: Item?): Boolean = oldItem == newItem
        }
    }
}