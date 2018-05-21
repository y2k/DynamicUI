package y2k.dynamicui.common

import android.widget.SeekBar
import android.widget.Switch
import com.facebook.litho.ComponentContext
import com.facebook.litho.ComponentLayout
import com.facebook.litho.Size
import com.facebook.litho.SizeSpec
import com.facebook.litho.SizeSpec.UNSPECIFIED
import com.facebook.litho.annotations.*

@Event
class SwitchIsCheckedChanged(@JvmField var isChecked: Boolean = false)

@MountSpec(events = [SwitchIsCheckedChanged::class])
object SwitchComponentSpec {

    @OnBind
    fun onBind(c: ComponentContext, view: Switch) {
        val e = SwitchComponent.getSwitchIsCheckedChangedHandler(c) ?: return
        view.setOnCheckedChangeListener { _, isChecked ->
            SwitchComponent.dispatchSwitchIsCheckedChanged(e, isChecked)
        }
    }

    @OnUnbind
    fun onUnbind(c: ComponentContext, view: Switch) =
        view.setOnCheckedChangeListener(null)

    @OnCreateMountContent
    fun onCreateMountContent(c: ComponentContext) = Switch(c)

    @OnMount
    fun onMount(c: ComponentContext, view: Switch, @Prop isChecked: Boolean) {
        view.isChecked = isChecked
    }

    @OnMeasure
    fun onMeasure(c: ComponentContext, layout: ComponentLayout, widthSpec: Int, heightSpec: Int, size: Size) {
        size.width = when (SizeSpec.getMode(widthSpec)) {
            UNSPECIFIED -> 200
            else -> SizeSpec.getSize(widthSpec)
        }
        size.height = 100
    }
}

@MountSpec
object SeekBarComponentSpec {

    @OnCreateMountContent
    fun onCreateMountContent(c: ComponentContext) =
        SeekBar(c).apply { max = 10_000 }

    @OnMount
    fun onMount(c: ComponentContext, view: SeekBar, @Prop value: Float) {
        view.progress = (10_000 * value).toInt()
    }

    @OnMeasure
    fun onMeasure(c: ComponentContext, layout: ComponentLayout, widthSpec: Int, heightSpec: Int, size: Size) {
        size.width = when (SizeSpec.getMode(widthSpec)) {
            UNSPECIFIED -> 200
            else -> SizeSpec.getSize(widthSpec)
        }
        size.height = 100
    }
}