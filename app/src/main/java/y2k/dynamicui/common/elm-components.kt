package y2k.dynamicui.common

import kotlinx.coroutines.experimental.launch
import y2k.dynamicui.ConfigComponent
import y2k.dynamicui.Model
import y2k.dynamicui.Msg
import y2k.dynamicui.common.Cmd.Effect
import y2k.dynamicui.common.Cmd.Update

sealed class Cmd<out TModel, out TMsg> {
    class Update<TModel, TMsg>(val state: TModel) : Cmd<TModel, TMsg>()
    class Effect<TModel, T, TMsg>(
        val state: TModel,
        private val f: suspend () -> T,
        private val toAction: (T) -> TMsg) : Cmd<TModel, TMsg>() {
        suspend fun execute(): TMsg = toAction(f())
    }
}

object Elm {

    fun event(component: ConfigComponent, msg: Msg, state: Model, setState: (Model) -> Unit) {
        val cmd = component.update(state, msg)
        loop(cmd, component, setState)
    }

    fun start(component: ConfigComponent, setState: (Model) -> Unit) {
        loop(component.init(), component, setState)
    }

    private fun loop(cmd: Cmd<Model, Msg>, component: ConfigComponent, setState: (Model) -> Unit) {
        when (cmd) {
            is Update -> setState(cmd.state)
            is Effect<Model, *, Msg> -> {
                setState(cmd.state)
                launch {
                    val cmd2 = component.update(cmd.state, cmd.execute())
                    loop(cmd2, component, setState)
                }
            }
        }
    }
}

interface Component_<TState, TAction> {
    fun init(): Cmd<TState, TAction>
    fun update(state: TState, action: TAction): Cmd<TState, TAction>
}