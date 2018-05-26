package y2k.dynamicui

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.facebook.soloader.SoLoader
import y2k.dynamicui.common.*
import y2k.dynamicui.litho.LithoFragment
import y2k.dynamicui.recyclerview.RecyclerViewFragment
import y2k.dynamicui.recyclerview.SeekBarTC
import y2k.dynamicui.recyclerview.SwitchTC

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            fragmentManager
                .beginTransaction()
                .add(R.id.container, LithoFragment())
                .commit()
        }
    }
}

class RecyclerViewActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            fragmentManager
                .beginTransaction()
                .add(R.id.container, RecyclerViewFragment())
                .commit()
        }
    }
}

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        SoLoader.init(this, false)
        registerTypeClasses()
    }

    private fun registerTypeClasses() {
        registerTypeClass<HolderFactory<*>, SwitchItem>(SwitchTC)
        registerTypeClass<HolderBinder<*, *>, SwitchItem>(SwitchTC)
        registerTypeClass<HolderFactory<*>, SeekBarItem>(SeekBarTC)
        registerTypeClass<HolderBinder<*, *>, SeekBarItem>(SeekBarTC)
    }
}