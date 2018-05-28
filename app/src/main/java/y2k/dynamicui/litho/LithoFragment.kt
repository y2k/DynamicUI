package y2k.dynamicui.litho

import android.app.Fragment
import android.graphics.Color
import android.os.Bundle
import android.text.Layout.Alignment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.facebook.litho.*
import com.facebook.litho.annotations.*
import com.facebook.yoga.YogaAlign
import com.facebook.yoga.YogaEdge
import y2k.dynamicui.common.*
import com.facebook.litho.Column.create as column
import com.facebook.litho.LithoView.create as lithoView
import com.facebook.litho.Row.create as row
import com.facebook.litho.widget.EditText.create as edit
import com.facebook.litho.widget.Text.create as text
import com.facebook.litho.widget.VerticalScroll.create as scroll
import y2k.dynamicui.common.SeekBarComponent.create as seekBar
import y2k.dynamicui.common.SwitchComponent.create as switch

sealed class Events {
    class ItemsLoaded(val items: List<Item>) : Events()
    class Swiped(val item: Item) : Events()
    class Click(val item: Item, val increase: Boolean) : Events()
}

data class Model(val items: List<Item>)

object Page {

    fun init(): Pair<Model, EffectHandlers?> =
        Model(emptyList()) to EffectHandlers.LoadItems

    fun update(model: Model, msg: Events): Pair<Model, EffectHandlers?> =
        when (msg) {
            is Events.ItemsLoaded -> model.copy(items = msg.items) to null
            is Events.Swiped -> model to EffectHandlers.LoadItems
            is Events.Click -> model to EffectHandlers.LoadItems
        }

    fun view(c: ComponentContext, @State state: Model): Component =
        scroll(c).apply {
            childComponent(
                column(c).apply {
                    paddingDip(YogaEdge.HORIZONTAL, 16f)
                    backgroundColor(Color.WHITE)

                    state.items
                        .map { viewConfig(c, it) }
                        .forEach { child(it) }
                })
        }.build()

    private fun viewConfig(c: ComponentContext, item: Item) =
        when (item) {
            is GroupItem -> viewGroup(c, item)
            is NumberItem -> viewNumber(c, item)
            is SwitchItem -> viewSwipe(c, item)
            is SeekBarItem -> viewSeekBarItem(c, item)
        }

    private fun viewGroup(c: ComponentContext, item: GroupItem): Column.Builder =
        column(c).apply {
            marginDip(YogaEdge.VERTICAL, 8f)
            backgroundColor(Color.LTGRAY)

            child(
                text(c).apply {
                    text(item.title)
                    textSizeSp(20f)
                })

            item.children
                .map { viewConfig(c, it) }
                .forEach { child(it) }
        }

    private fun viewNumber(c: ComponentContext, item: NumberItem) =
        row(c).apply {
            heightDip(50f)

            child(
                text(c, android.R.attr.buttonStyle, 0).apply {
                    text("-")
                    widthDip(100f)
                    clickHandler(Root.onClick(c, Events.Click(item, false)))
                })
            child(
                text(c).apply {
                    text("${item.value}")
                    textSizeSp(25f)
                    flexGrow(1f)
                    alignSelf(YogaAlign.CENTER)
                    textAlignment(Alignment.ALIGN_CENTER)
                })
            child(
                text(c, android.R.attr.buttonStyle, 0).apply {
                    clickHandler(Root.onClick(c, Events.Click(item, true)))
                    text("+")
                    widthDip(100f)
                })
        }

    private fun viewSwipe(c: ComponentContext, item: SwitchItem) =
        switch(c).apply {
            isChecked(item.isChecked)
            switchIsCheckedChangedHandler(Root.onSwiped(c, Events.Swiped(item)))
        }

    private fun viewSeekBarItem(c: ComponentContext, item: SeekBarItem) =
        seekBar(c).apply {
            value(item.value)
        }
}

@Event
object UpdateStateEvent

@LayoutSpec
object RootSpec {

    @OnCreateInitialState
    fun createInitialState(c: ComponentContext, state: StateValue<Model>) {
        val (model, effect) = Page.init()
        state.set(model)
        ElmUtils.dispatchEffect(effect, { Root.onUpdateState(c, it).dispatchEvent(UpdateStateEvent) })
    }

    @OnEvent(UpdateStateEvent::class)
    fun onUpdateState(c: ComponentContext, @Param msg: Events, @State state: Model) =
        ElmUtils.dispatchEvents(c, state, msg, { Root.updateState(c, it) })

    @OnEvent(SwitchIsCheckedChanged::class)
    fun onSwiped(c: ComponentContext, @State state: Model, @Param msg: Events) =
        ElmUtils.dispatchEvents(c, state, msg, { Root.updateState(c, it) })

    @OnEvent(ClickEvent::class)
    fun onClick(c: ComponentContext, @State state: Model, @Param msg: Events) =
        ElmUtils.dispatchEvents(c, state, msg, { Root.updateState(c, it) })

    @OnCreateLayout
    fun onCreateLayout(c: ComponentContext, @State state: Model): Component =
        Page.view(c, state)

    @OnUpdateState
    fun updateState(state: StateValue<Model>, @Param param: Model) =
        state.set(param)
}

class LithoFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View =
        lithoView(activity, Root.create(ComponentContext(activity)).build())
}