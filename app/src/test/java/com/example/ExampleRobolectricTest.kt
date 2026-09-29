package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
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
    assertEquals("Elementa", appName)
  }

  @Test
  fun `chemical catalog contains starting elements and reactions`() {
    val catalog = com.example.data.model.ChemicalCatalog.ALL_CHEMICALS
    org.junit.Assert.assertTrue(catalog.any { it.id == "H2O" && it.isPreUnlocked })
    org.junit.Assert.assertTrue(catalog.any { it.id == "Fe" && it.isPreUnlocked })

    val reactions = com.example.data.model.ChemicalCatalog.REACTIONS
    org.junit.Assert.assertTrue(reactions.isNotEmpty())
  }

  @Test
  fun `periodic table contains all 118 elements from Hydrogen to Oganesson`() {
    val elements = com.example.data.model.PeriodicTableData.ALL_118_ELEMENTS
    assertEquals(118, elements.size)
    assertEquals(1, elements.first().atomicNumber)
    assertEquals("H", elements.first().symbol)
    assertEquals(118, elements.last().atomicNumber)
    assertEquals("Og", elements.last().symbol)

    for (z in 1..118) {
      org.junit.Assert.assertTrue("Missing element with atomic number $z", elements.any { it.atomicNumber == z })
    }
  }
}
