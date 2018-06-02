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
import y2k.dynamicui.ConfigComponent
import y2k.dynamicui.Model
import y2k.dynamicui.Msg
import y2k.dynamicui.R
import y2k.dynamicui.common.*

class RecyclerViewFragment : Fragment() {

    private val adapter = StateAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        Elm.start(ConfigComponent, adapter::submitState)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        list.adapter = adapter
        reload.setOnClickListener {
            Elm.event(ConfigComponent, Msg.Reload, adapter.state, adapter::submitState)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        list.adapter = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_recyclerview, container, false)
}

private class StateAdapter : ListAdapter<Item, ViewHolder>(ConfigsCallback()) {

    lateinit var state: Model

    fun submitState(state: Model) {
        this.state = state
        submitList(Configs.flatConfigs(state.configs))
    }

    override fun getItemViewType(position: Int): Int =
        when (getItem(position)) {
            is GroupItem -> 0
            is SwitchItem -> 1
            is SeekBarItem -> 2
            is NumberItem -> 3
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            1 -> SwitchVH(inflater.inflate(R.layout.item_switch, parent, false), this)
            2 -> SeekBarVH(inflater.inflate(R.layout.item_seekbar, parent, false), this)
            3 -> NumberVH(inflater.inflate(R.layout.item_number, parent, false), this)
            else -> throw IllegalStateException()
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        when (item) {
            is SwitchItem -> (holder as SwitchVH).bind(item)
            is SeekBarItem -> (holder as SeekBarVH).bind(item)
            is NumberItem -> (holder as NumberVH).bind(item)
            else -> throw IllegalStateException()
        }
    }

    private class SwitchVH(override val containerView: View, private val adapter: StateAdapter)
        : ViewHolder(containerView), LayoutContainer {

        fun bind(item: SwitchItem) {
            switchView.text = item.title
            switchView.isChecked = item.isChecked

            switchView.setOnCheckedChangeListener { _, _ ->
                Elm.event(ConfigComponent, Msg.Switch(item), adapter.state, adapter::submitState)
            }
        }
    }

    private class SeekBarVH(override val containerView: View, private val adapter: StateAdapter)
        : ViewHolder(containerView), LayoutContainer {

        fun bind(item: SeekBarItem) {
            seekbar.max = 10_000
            seekbar.progress = (10_000 * item.value).toInt()

            seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) = Unit
                override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
                override fun onStopTrackingTouch(seekBar: SeekBar) {
                    Elm.event(ConfigComponent,
                        Msg.SeekBar(item, seekBar.progress / 10_000f),
                        adapter.state,
                        adapter::submitState)
                }
            })
        }
    }

    private class NumberVH(override val containerView: View, private val adapter: StateAdapter)
        : ViewHolder(containerView), LayoutContainer {

        fun bind(item: NumberItem) {
            number.text = "${item.value}"

            decrease.setOnClickListener {
                Elm.event(ConfigComponent, Msg.Click(item, false), adapter.state, adapter::submitState)
            }
            increase.setOnClickListener {
                Elm.event(ConfigComponent, Msg.Click(item, true), adapter.state, adapter::submitState)
            }
        }
    }

    private class ConfigsCallback : DiffUtil.ItemCallback<Item>() {
        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean = Configs.compareById(oldItem, newItem)
        override fun areContentsTheSame(oldItem: Item?, newItem: Item?): Boolean = oldItem == newItem
    }
}