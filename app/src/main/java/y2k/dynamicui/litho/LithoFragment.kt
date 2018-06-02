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
import com.facebook.yoga.YogaJustify
import y2k.dynamicui.ConfigComponent
import y2k.dynamicui.Model
import y2k.dynamicui.Msg
import y2k.dynamicui.R
import y2k.dynamicui.common.*
import java.util.concurrent.atomic.AtomicReference
import com.facebook.litho.Column.create as column
import com.facebook.litho.LithoView.create as lithoView
import com.facebook.litho.Row.create as row
import com.facebook.litho.widget.EditText.create as edit
import com.facebook.litho.widget.Progress.create as progress
import com.facebook.litho.widget.Text.create as text
import com.facebook.litho.widget.VerticalScroll.create as scroll
import y2k.dynamicui.common.SeekBarComponent.create as seekBar
import y2k.dynamicui.common.SwitchComponent.create as switch

object StatelessComponent {

    fun render(c: ComponentContext, state: Model): Component.Builder<*> =
        when (state.loading) {
            true -> viewProgress(c)
            else -> viewConfigs(c, state)
        }

    private fun viewProgress(c: ComponentContext) =
        column(c).apply {
            backgroundColor(Color.WHITE)
            alignItems(YogaAlign.CENTER)
            justifyContent(YogaJustify.CENTER)

            child(
                progress(c).apply {
                    widthDip(70f)
                    heightDip(70f)
                    colorRes(android.R.color.holo_blue_light)
                })
        }

    private fun viewConfigs(c: ComponentContext, state: Model) =
        scroll(c).apply {
            backgroundColor(Color.WHITE)

            childComponent(
                column(c).apply {

                    child(
                        text(c, android.R.attr.buttonStyle, 0).apply {
                            marginDip(YogaEdge.HORIZONTAL, 4f)
                            marginDip(YogaEdge.TOP, 4f)
                            heightDip(48f)
                            textRes(R.string.reload)
                            clickHandler(Root.onClicked(c, Msg.Reload))
                        })

                    state.configs
                        .map { viewConfig(c, it) }
                        .forEach { child(it) }
                })
        }

    private fun viewConfig(c: ComponentContext, item: Item) =
        when (item) {
            is GroupItem -> viewGroup(c, item)
            is NumberItem -> viewNumber(c, item)
            is SwitchItem -> viewSwitch(c, item)
            is SeekBarItem -> viewSeekBarItem(c, item)
        }

    private fun viewGroup(c: ComponentContext, item: GroupItem): Column.Builder =
        column(c).apply {
            marginDip(YogaEdge.ALL, 8f)
            paddingDip(YogaEdge.ALL, 8f)
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
            marginDip(YogaEdge.VERTICAL, 8f)
            heightDip(50f)

            child(
                text(c, android.R.attr.buttonStyle, 0).apply {
                    text("-")
                    widthDip(100f)
                    clickHandler(Root.onClicked(c, Msg.Click(item, false)))
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
                    clickHandler(Root.onClicked(c, Msg.Click(item, true)))
                    text("+")
                    widthDip(100f)
                })
        }

    private fun viewSwitch(c: ComponentContext, item: SwitchItem) =
        switch(c).apply {
            marginDip(YogaEdge.VERTICAL, 8f)

            isChecked(item.isChecked)
            switchIsCheckedChangedHandler(Root.onSwitchChanged(c, Msg.Switch(item)))
        }

    private fun viewSeekBarItem(c: ComponentContext, item: SeekBarItem) =
        seekBar(c).apply {
            marginDip(YogaEdge.VERTICAL, 8f)

            value(item.value)
            seekBarChangedHandler(Root.onSeekBarChanged(c, item))
        }
}

@LayoutSpec
object RootSpec {

    @OnCreateInitialState
    fun createInitialState(c: ComponentContext, state: StateValue<Model>) {
        val x = AtomicReference<(Model) -> Unit>(state::set)
        Elm.start(ConfigComponent, {
            x.get().invoke(it)
            x.set { Root.updateStateAsync(c, it) }
        })
    }

    @OnEvent(SwitchIsCheckedChanged::class)
    fun onSwitchChanged(c: ComponentContext, @State state: Model, @Param msg: Msg) =
        Elm.event(ConfigComponent, msg, state, { Root.updateStateAsync(c, it) })

    @OnEvent(ClickEvent::class)
    fun onClicked(c: ComponentContext, @State state: Model, @Param msg: Msg) =
        Elm.event(ConfigComponent, msg, state, { Root.updateStateAsync(c, it) })

    @OnEvent(SeekBarChanged::class)
    fun onSeekBarChanged(c: ComponentContext, @State state: Model, @Param item: SeekBarItem, @FromEvent value: Float) =
        Elm.event(ConfigComponent, Msg.SeekBar(item, value), state, { Root.updateStateAsync(c, it) })

    @OnCreateLayout
    fun onCreateLayout(c: ComponentContext, @State state: Model): Component =
        StatelessComponent.render(c, state).build()

    @OnUpdateState
    fun updateState(state: StateValue<Model>, @Param param: Model) =
        state.set(param)
}

class LithoFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View =
        lithoView(activity, Root.create(ComponentContext(activity)).build())
}