package com.yichiuan.common.viewsample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import com.yichiuan.common.view.BadgeDrawable
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)

        val item = menu?.findItem(R.id.menuitem_star)
        item?.let {
            val icon = item.icon
            it.icon = BadgeDrawable(icon).apply { count = 8 }
        }

        return super.onCreateOptionsMenu(menu)
    }
}
