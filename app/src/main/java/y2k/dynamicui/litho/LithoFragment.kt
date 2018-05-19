package y2k.dynamicui.litho

import android.app.Fragment
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.facebook.litho.StateValue
import com.facebook.litho.annotations.*
import com.facebook.yoga.YogaEdge
import y2k.dynamicui.common.EditItem
import y2k.dynamicui.common.Item
import y2k.dynamicui.common.SpinnerItem
import com.facebook.litho.Column.create as column
import com.facebook.litho.LithoView.create as lithoView
import com.facebook.litho.widget.Text.create as text

@LayoutSpec
object RootSpec {

    @OnCreateLayout
    fun onCreateLayout(c: ComponentContext, @State state: AppState): Component =
        column(c).apply {
            paddingDip(YogaEdge.ALL, 16f)
            backgroundColor(Color.WHITE)

            child(h1(c, "Settings"))

            state.items
                .map { configView(c, it) }
                .forEach { child(it) }
        }.build()

    private fun configView(c: ComponentContext, item: Item) =
        when (item) {
            is EditItem -> viewEditItem(c, item)
            is SpinnerItem -> viewSpinnerItem(c, item)
        }

    private fun viewEditItem(c: ComponentContext, item: Item) =
        text(c).apply {
            text("($item)")
            textSizeSp(20f)
        }

    private fun viewSpinnerItem(c: ComponentContext, item: Item) =
        text(c).apply {
            text("($item)")
            textSizeSp(20f)
        }

    private fun h1(c: ComponentContext, text: String) =
        text(c).apply {
            text(text)
            textSizeSp(40f)
        }

    @OnCreateInitialState
    fun createInitialState(c: ComponentContext, state: StateValue<AppState>) =
        state.set(AppState(listOf(EditItem, EditItem, SpinnerItem, SpinnerItem, EditItem, EditItem)))

    @OnUpdateState
    fun updateState(state: StateValue<AppState>, @Param param: AppState) =
        state.set(param)
}

class LithoFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View =
        lithoView(activity, Root.create(ComponentContext(activity)).build())
}

data class AppState(val items: List<Item>)