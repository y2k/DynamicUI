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
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import y2k.dynamicui.common.*
import com.facebook.litho.Column.create as column
import com.facebook.litho.LithoView.create as lithoView
import com.facebook.litho.Row.create as row
import com.facebook.litho.widget.EditText.create as edit
import com.facebook.litho.widget.Text.create as text
import com.facebook.litho.widget.VerticalScroll.create as scroll
import y2k.dynamicui.common.SeekBarComponent.create as seekBar
import y2k.dynamicui.common.SwitchComponent.create as switch

object RootObject {

    fun viewContent(c: ComponentContext, @State state: AppState): Component =
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
            is SwipeItem -> viewSwipe(c, item)
            is SeekBarItem -> viewSeekBarItem(c, item)
            is NumberItem -> viewNumber(c, item)
        }

    private fun viewNumber(c: ComponentContext, item: NumberItem) =
        row(c).apply {
            heightDip(50f)

            child(
                text(c, android.R.attr.buttonStyle, 0).apply {
                    text("-")
                    widthDip(100f)
                    clickHandler(Root.onClick(c, item))
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
                    clickHandler(Root.onClick(c, item))
                    text("+")
                    widthDip(100f)
                })
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

    private fun viewSwipe(c: ComponentContext, item: SwipeItem) =
        switch(c).apply {
            isChecked(item.isChecked)
            switchIsCheckedChangedHandler(Root.onSwiped(c, item))
        }

    private fun viewSeekBarItem(c: ComponentContext, item: SeekBarItem) =
        seekBar(c).apply {
            value(item.value)
        }
}

data class AppState(val items: List<Item>)

@LayoutSpec
object RootSpec {

    @OnEvent(SwitchIsCheckedChanged::class)
    fun onSwiped(c: ComponentContext, @FromEvent isChecked: Boolean, @Param item: Item) {
        reloadAsync(c)
    }

    @OnEvent(ClickEvent::class)
    fun onClick(c: ComponentContext, @Param item: Item) {
        reloadAsync(c)
    }

    @OnCreateInitialState
    fun createInitialState(c: ComponentContext, state: StateValue<AppState>) {
        state.set(AppState(emptyList()))
        reloadAsync(c)
    }

    private fun reloadAsync(c: ComponentContext) {
        launch(UI) {
            Root.updateState(c, Effects.loadSettings().let(::AppState))
        }
    }

    @OnCreateLayout
    fun onCreateLayout(c: ComponentContext, @State state: AppState): Component =
        RootObject.viewContent(c, state)

    @OnUpdateState
    fun updateState(state: StateValue<AppState>, @Param param: AppState) =
        state.set(param)
}

class LithoFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View =
        lithoView(activity, Root.create(ComponentContext(activity)).build())
}