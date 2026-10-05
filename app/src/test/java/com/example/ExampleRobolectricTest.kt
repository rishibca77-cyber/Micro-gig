package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.seed.SeedData
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
    assertEquals("QuickGig", appName)
  }

  @Test
  fun `seed data provides more than 35 micro jobs`() {
    val totalGigs = SeedData.initialGigs.size
    assertTrue("Seed data must have more than 35 jobs, found: $totalGigs", totalGigs > 35)
    assertEquals(38, totalGigs)
  }
}
