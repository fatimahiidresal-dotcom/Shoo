package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.InitialCatalogSeeder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("بازار", appName)
    }

    @Test
    fun `initial catalog seeder provides valid products and discounts`() {
        val products = InitialCatalogSeeder.getInitialProducts()
        assertTrue(products.isNotEmpty())
        val firstProduct = products.first()
        assertTrue(firstProduct.discountPercentage > 0)
    }
}
