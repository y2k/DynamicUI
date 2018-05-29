package y2k.dynamicui.common

import kotlinx.coroutines.experimental.launch
import y2k.dynamicui.common.Cmd.SideEffect
import y2k.dynamicui.common.Cmd.Update

sealed class Cmd<out TModel, out TMsg> {
    class Update<TModel, TMsg>(val state: TModel) : Cmd<TModel, TMsg>()
    class SideEffect<TModel, T, TMsg>(
        val state: TModel,
        private val f: suspend () -> T,
        private val toAction: (T) -> TMsg) : Cmd<TModel, TMsg>() {
        suspend fun execute(): TMsg = toAction(f())
    }
}

object Elm {

    fun event(msg: Msg_, component: ConfigComponent, getState: () -> Model_, setState: (Model_) -> Unit) {
        val cmd = component.reduce(getState(), msg)
        loop(cmd, component, setState)
    }

    fun start(component: ConfigComponent, setState: (Model_) -> Unit) {
        loop(component.initState(), component, setState)
    }

    private fun loop(cmd: Cmd<Model_, Msg_>, component: ConfigComponent, setState: (Model_) -> Unit) {
        when (cmd) {
            is Update -> setState(cmd.state)
            is SideEffect<Model_, *, Msg_> -> {
                setState(cmd.state)
                launch {
                    val cmd2 = component.reduce(cmd.state, cmd.execute())
                    loop(cmd2, component, setState)
                }
            }
        }
    }
}

interface Component_<TState, TAction> {
    fun initState(): Cmd<TState, TAction>
    fun reduce(state: TState, action: TAction): Cmd<TState, TAction>
}

data class Model_(val configs: List<Item> = emptyList())
sealed class Msg_ {
    class ItemsLoaded(val configs: List<Item>) : Msg_()
    class Switch(val item: SwitchItem) : Msg_()
    class Click(val item: NumberItem, val increase: Boolean) : Msg_()
    class SeekBar(val item: SeekBarItem, val value: Float) : Msg_()
}

object ConfigComponent : Component_<Model_, Msg_> {

    override fun initState(): Cmd<Model_, Msg_> =
        SideEffect(Model_(), { Effects.loadSettings() }, { Msg_.ItemsLoaded(it) })

    override fun reduce(state: Model_, action: Msg_): Cmd<Model_, Msg_> =
        when (action) {
            is Msg_.ItemsLoaded ->
                Update(state.copy(configs = action.configs))
            is Msg_.Click ->
                Update(state.copy(configs = Items.changeNumber(state.configs, action.item.id, action.increase)))
            is Msg_.Switch ->
                Update(state.copy(configs = Items.changeSwitch(state.configs, action.item.id)))
            is Msg_.SeekBar ->
                Update(state.copy(configs = Items.changeSeekBar(state.configs, action.item.id, action.value)))
        }
}