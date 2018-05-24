package y2k.dynamicui.common

import com.facebook.litho.ComponentContext
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import y2k.dynamicui.litho.*

sealed class EffectHandlers {
    object LoadItems : EffectHandlers()
}

object ElmUtils {

    fun dispatchEvents(c: ComponentContext, model: Model, msg: Events, updateState: (Model) -> Unit) {
        val (newModel, effect) = Page.update(model, msg)
        if (newModel != model)
            updateState(newModel)
        dispatchEffect(c, effect)
    }

    fun dispatchEffect(effect: EffectHandlers?, f: (Events) -> Unit) {
        if (effect == null) return
        launch(UI) {
            f(handleEffect(effect))
        }
    }

    @Deprecated("")
    fun dispatchEffect(c: ComponentContext, effect: EffectHandlers?) {
        if (effect == null) return

        launch(UI) {
            val msg = handleEffect(effect)
            Root.onUpdateState(c, msg)
                .dispatchEvent(UpdateStateEvent)
        }
    }

    private suspend fun handleEffect(effect: EffectHandlers): Events =
        when (effect) {
            EffectHandlers.LoadItems ->
                Events.ItemsLoaded(Effects.loadSettings())
        }
}