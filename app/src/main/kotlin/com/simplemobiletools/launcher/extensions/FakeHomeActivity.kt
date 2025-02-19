package com.simplemobiletools.launcher.extensions

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.simplemobiletools.launcher.R


class FakeHomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fake_home)
    }
}
