package y2k.dynamicui

import y2k.dynamicui.common.*

data class Model(val configs: List<Item> = emptyList())

sealed class Msg {
    object Reload : Msg()
    class Loaded(val configs: List<Item>) : Msg()
    class Switch(val item: SwitchItem) : Msg()
    class Click(val item: NumberItem, val increase: Boolean) : Msg()
    class SeekBar(val item: SeekBarItem, val value: Float) : Msg()
}

object ConfigComponent : Component_<Model, Msg> {

    override fun init(): Cmd<Model, Msg> =
        Cmd.Effect(Model(), { Effects.loadSettings() }, Msg::Loaded)

    override fun update(state: Model, action: Msg): Cmd<Model, Msg> = when (action) {
        Msg.Reload -> Cmd.Effect(state, { Effects.loadSettings() }, Msg::Loaded)
        is Msg.Loaded ->
            Cmd.Update(state.copy(configs = action.configs))
        is Msg.Click ->
            Cmd.Update(state.copy(configs = Configs.changeNumber(state.configs, action.item.id, action.increase)))
        is Msg.Switch ->
            Cmd.Update(state.copy(configs = Configs.changeSwitch(state.configs, action.item.id)))
        is Msg.SeekBar ->
            Cmd.Update(state.copy(configs = Configs.changeSeekBar(state.configs, action.item.id, action.value)))
    }
}