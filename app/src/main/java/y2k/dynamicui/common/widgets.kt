package y2k.dynamicui.common

import android.view.View
import android.widget.SeekBar
import android.widget.Switch
import com.facebook.litho.*
import com.facebook.litho.SizeSpec.UNSPECIFIED
import com.facebook.litho.annotations.*
import java.util.concurrent.atomic.AtomicReference

@Event
class SwitchIsCheckedChanged(@JvmField var isChecked: Boolean = false)

@MountSpec(events = [SwitchIsCheckedChanged::class])
object SwitchComponentSpec {

    private val cachedMinSize = AtomicReference<Size>()

    @OnPrepare
    fun onPrepare(c: ComponentContext, minSize: Output<Size>) {
        val size = cachedMinSize.get()
        if (size != null) {
            minSize.set(Size(size.width, size.height))
        } else {
            Switch(c).apply {
                measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
                val measuredSize = Size(measuredWidth, measuredHeight)
                minSize.set(measuredSize)
                cachedMinSize.set(measuredSize)
            }
        }
    }

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
    fun onMeasure(c: ComponentContext, layout: ComponentLayout, widthSpec: Int, heightSpec: Int, size: Size, @FromPrepare minSize: Size) {
        size.width = when (SizeSpec.getMode(widthSpec)) {
            UNSPECIFIED -> minSize.width
            else -> SizeSpec.getSize(widthSpec)
        }
        size.height = when (SizeSpec.getMode(heightSpec)) {
            UNSPECIFIED -> minSize.height
            else -> SizeSpec.getSize(heightSpec)
        }
    }
}

@Event
class SeekBarChanged(@JvmField var value: Float = 0f)

@MountSpec(events = [SeekBarChanged::class])
object SeekBarComponentSpec {

    @OnCreateMountContent
    fun onCreateMountContent(c: ComponentContext) =
        SeekBar(c).apply { max = 10_000 }

    @OnMount
    fun onMount(c: ComponentContext, view: SeekBar, @Prop value: Float) {
        view.progress = (10_000 * value).toInt()
    }

    @OnBind
    fun onBind(c: ComponentContext, view: SeekBar) {
        val e = SeekBarComponent.getSeekBarChangedHandler(c) ?: return
        view.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) = Unit
            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                SeekBarComponent.dispatchSeekBarChanged(e, seekBar.progress / 10_000f)
            }
        })
    }

    @OnUnbind
    fun onUnbind(c: ComponentContext, view: SeekBar) =
        view.setOnSeekBarChangeListener(null)

    @OnMeasure
    fun onMeasure(c: ComponentContext, layout: ComponentLayout, widthSpec: Int, heightSpec: Int, size: Size) {
        size.width = when (SizeSpec.getMode(widthSpec)) {
            UNSPECIFIED -> 200
            else -> SizeSpec.getSize(widthSpec)
        }
        size.height = 100
    }
}