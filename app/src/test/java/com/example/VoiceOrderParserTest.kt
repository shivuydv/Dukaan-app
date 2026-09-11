package com.example

import com.example.data.local.PreloadedData
import com.example.service.VoiceOrderParser
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class VoiceOrderParserTest {

  private val parser = VoiceOrderParser()
  private val catalog = PreloadedData.initialShopItems

  @Test
  fun testHinglishVoiceOrderParsing() = runBlocking {
    val transcript = "Bhaiya, 2 kilo aalu, ek packet Amul butter aur ek Surf Excel bhej do"
    val result = parser.parseVoiceOrder(transcript, catalog)

    assertNotNull(result)
    assertTrue("Items should not be empty", result.items.isNotEmpty())
    assertTrue("Total amount should be greater than 0", result.totalAmount > 0)
    
    // Check if potato/aalu matched
    val aaluItem = result.items.find { it.name.contains("Aalu") || it.name.contains("Potato") }
    assertNotNull("Aalu should be found in catalog", aaluItem)
    assertEquals(2.0, aaluItem?.quantity ?: 0.0, 0.01)
  }

  @Test
  fun testHindiNumeralsAndWordsParsing() = runBlocking {
    val transcript = "२ किलो आलू और १ पैकेट मैगी"
    val result = parser.parseVoiceOrder(transcript, catalog)

    assertNotNull(result)
    assertTrue("Items should be parsed", result.items.isNotEmpty())
    val aaluItem = result.items.find { it.name.contains("Aalu") }
    assertNotNull("Aalu should match Devanagari text", aaluItem)
    assertEquals(2.0, aaluItem?.quantity ?: 0.0, 0.01)
  }

  @Test
  fun testFractionQuantities() = runBlocking {
    val transcript = "Aadha kilo pyaaz aur dedh kilo tamatar"
    val result = parser.parseVoiceOrder(transcript, catalog)

    assertNotNull(result)
    val pyaaz = result.items.find { it.name.contains("Pyaaz") || it.name.contains("Onion") }
    assertNotNull("Pyaaz should match", pyaaz)
    assertEquals(0.5, pyaaz?.quantity ?: 0.0, 0.01)

    val tamatar = result.items.find { it.name.contains("Tamatar") || it.name.contains("Tomato") }
    assertNotNull("Tamatar should match", tamatar)
    assertEquals(1.5, tamatar?.quantity ?: 0.0, 0.01)
  }
}
