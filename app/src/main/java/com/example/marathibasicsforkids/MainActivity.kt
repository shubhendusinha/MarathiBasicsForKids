package com.example.marathibasicsforkids

import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavView = findViewById<BottomNavigationView>(R.id.bottom_nav_view)
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        bottomNavView.setupWithNavController(navController)

        // Create and apply text-based icons
        bottomNavView.menu.findItem(R.id.swarFragment).icon = createTextIcon("अ")
        bottomNavView.menu.findItem(R.id.aksharFragment).icon = createTextIcon("क")
        bottomNavView.menu.findItem(R.id.akadeFragment).icon = createTextIcon("१")
    }

    private fun createTextIcon(text: String): BitmapDrawable {
        val size = 96 // Create a 96x96 pixel icon. This will be scaled down by the view.
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = Color.WHITE
        paint.textAlign = Paint.Align.CENTER

        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // --- Adjust text size to fit into the icon --- 
        var textSize = 80f
        paint.textSize = textSize
        val bounds = Rect()
        paint.getTextBounds(text, 0, text.length, bounds)
        // Decrease text size until it fits within the icon's width (with a little padding)
        while (paint.measureText(text) > size - 10) {
            textSize -= 1f
            paint.textSize = textSize
        }

        // --- Draw text in the center of the bitmap --- 
        val x = canvas.width / 2f
        // This formula centers the text vertically
        val y = (canvas.height / 2f) - ((paint.descent() + paint.ascent()) / 2f)
        canvas.drawText(text, x, y, paint)

        return BitmapDrawable(resources, bitmap)
    }
}