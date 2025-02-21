package com.example.drawingapp

import android.graphics.Color
import androidx.test.platform.app.InstrumentationRegistry
import androidx.fragment.app.testing.FragmentScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.espresso.matcher.ViewMatchers.*

import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class DrawingViewTest {

    private lateinit var drawingView: DrawingView

    @Before
    fun setupTest() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        drawingView = DrawingView(context)

        drawingView.layout(40, 300, 300, 500)
    }

    @Test
    fun testPenSizeChangeFn()
    {
        val newSize = 20F
        val expectedSize = 10F
        assertEquals(expectedSize, drawingView.getPenSize())

        drawingView.setPenSize(newSize)

        assertEquals(newSize, drawingView.getPenSize())

    }

    @Test
    fun testPenColorChangeFn()
    {
        val expectedColor: Int = Color.BLACK

        assertEquals(expectedColor, drawingView.getPenColor())

        val newColor:Int = Color.RED

        drawingView.setColor(newColor)

        assertEquals(newColor, drawingView.getPenColor())
    }
}