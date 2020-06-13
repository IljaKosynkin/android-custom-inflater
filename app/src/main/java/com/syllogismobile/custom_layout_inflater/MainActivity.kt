package com.syllogismobile.custom_layout_inflater

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private val theme = CustomTheme()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(
            CustomLayoutInflater
                .from(this)
                .registerApplier(COLOR_ATTRIBUTE, this::applyColorAttributeValue)
                .inflate(R.layout.activity_main, null)
        )
    }

    private fun applyColorAttributeValue(view: View, colorValue: String) {
        val color = when(colorValue) {
            "darkPurple" -> theme.darkPurple
            "maximumPurple" -> theme.maximumPurple
            "violetBlueCrayola" -> theme.violetBlueCrayola
            "lavenderBlue" -> theme.lavenderBlue
            "aeroBlue" -> theme.aeroBlue
            else -> error("Unexpected color $colorValue")
        }

        when(view) {
            is TextView -> view.setTextColor(color)
            else -> view.setBackgroundColor(color)
        }
    }

    private companion object {
        const val COLOR_ATTRIBUTE = "color"
    }
}
