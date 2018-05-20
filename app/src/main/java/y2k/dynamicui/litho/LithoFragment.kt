package y2k.dynamicui.litho

import android.app.Fragment
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.facebook.litho.Column
import com.facebook.litho.Component
import com.facebook.litho.ComponentContext
import com.facebook.litho.StateValue
import com.facebook.litho.annotations.*
import com.facebook.yoga.YogaEdge
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import y2k.dynamicui.common.*
import com.facebook.litho.Column.create as column
import com.facebook.litho.LithoView.create as lithoView
import com.facebook.litho.widget.Text.create as text

object RootObject {

    fun viewContent(c: ComponentContext, @State state: AppState): Component =
        column(c).apply {
            paddingDip(YogaEdge.HORIZONTAL, 16f)
            backgroundColor(Color.WHITE)

            state.items
                .map { viewConfig(c, it) }
                .forEach { child(it) }
        }.build()

    private fun viewConfig(c: ComponentContext, item: Item) =
        when (item) {
            is EditItem -> viewEdit(c, item)
            is SpinnerItem -> viewSpinner(c, item)
            is GroupItem -> viewGroup(c, item)
        }

    private fun viewGroup(c: ComponentContext, item: GroupItem): Column.Builder =
        column(c).apply {
            marginDip(YogaEdge.VERTICAL, 8f)

            child(
                text(c).apply {
                    text(item.title)
                    textSizeSp(20f)
                })

            item.children
                .map { viewConfig(c, it) }
                .forEach { child(it) }
        }

    private fun viewEdit(c: ComponentContext, item: EditItem) =
        viewStub(c, item)

    private fun viewSpinner(c: ComponentContext, item: SpinnerItem) =
        viewStub(c, item)

    private fun viewStub(c: ComponentContext, item: Item) =
        text(c).apply {
            text("$item")
            textSizeSp(20f)
        }
}

data class AppState(val items: List<Item>)

@LayoutSpec
object RootSpec {

    @OnCreateInitialState
    fun createInitialState(c: ComponentContext, state: StateValue<AppState>) {
        state.set(AppState(emptyList()))

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