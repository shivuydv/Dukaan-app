package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.PreloadedData
import com.example.service.VoiceOrderParser
import kotlinx.coroutines.runBlocking
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
  fun read_string_from_context() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("DukaanVoice", appName)
  }

  @Test
  fun voice_order_parser_extracts_items_correctly() = runBlocking {
    val parser = VoiceOrderParser()
    val catalog = PreloadedData.initialShopItems

    val transcript = "Bhaiya, 2 kilo aalu, ek packet Amul butter aur ek Surf Excel bhej do"
    val result = parser.parseVoiceOrder(transcript, catalog)

    assertTrue("Result should identify items", result.items.isNotEmpty())
    val aaluItem = result.items.find { it.name.contains("Aalu", ignoreCase = true) }
    assertTrue("Should detect Aalu", aaluItem != null)
    assertEquals(2.0, aaluItem?.quantity ?: 0.0, 0.01)

    val butterItem = result.items.find { it.name.contains("Butter", ignoreCase = true) }
    assertTrue("Should detect Butter", butterItem != null)

    assertTrue("Total amount should be calculated", result.totalAmount > 0)
  }
}
