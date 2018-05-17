package y2k.dynamicui

import android.app.Activity
import android.os.Bundle
import y2k.dynamicui.recyclerview.RecyclerViewFragment

class MainActivity : Activity() {

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