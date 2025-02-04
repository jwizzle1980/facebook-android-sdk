/*
 * Copyright (c) 2014-present, Facebook, Inc. All rights reserved.
 *
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to use,
 * copy, modify, and distribute this software in source code or binary form for use
 * in connection with the web services and APIs provided by Facebook.
 *
 * As with any software that integrates with the Facebook platform, your use of
 * this software is subject to the Facebook Developer Principles and Policies
 * [http://developers.facebook.com/policy/]. This copyright notice shall be
 * included in all copies or substantial portions of the software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.facebook

import android.content.Context
import android.net.Uri
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Matchers
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest

@PrepareForTest(FacebookSdk::class, LocalBroadcastManager::class)
class ProfileTest : FacebookPowerMockTestCase() {
  @Before
  fun before() {
    PowerMockito.mockStatic(FacebookSdk::class.java)
    PowerMockito.`when`(FacebookSdk.getApplicationId()).thenReturn("123456789")
    PowerMockito.`when`(FacebookSdk.getApplicationContext())
        .thenReturn(ApplicationProvider.getApplicationContext())
    PowerMockito.mockStatic(LocalBroadcastManager::class.java)
    val mockLocalBroadcastManager = PowerMockito.mock(LocalBroadcastManager::class.java)
    PowerMockito.`when`(LocalBroadcastManager.getInstance(Matchers.isA(Context::class.java)))
        .thenReturn(mockLocalBroadcastManager)
  }

  @Test
  fun testProfileCtorAndGetters() {
    var profile = createDefaultProfile()
    assertDefaultObjectGetters(profile)
    profile = createMostlyNullsProfile()
    assertMostlyNullsObjectGetters(profile)
  }

  @Test
  fun testHashCode() {
    val profile1 = createDefaultProfile()
    val profile2 = createDefaultProfile()
    Assert.assertEquals(profile1.hashCode().toLong(), profile2.hashCode().toLong())
    val profile3 = createMostlyNullsProfile()
    Assert.assertNotEquals(profile1.hashCode().toLong(), profile3.hashCode().toLong())
  }

  @Test
  fun testEquals() {
    val profile1 = createDefaultProfile()
    val profile2 = createDefaultProfile()
    Assert.assertEquals(profile1, profile2)
    val profile3 = createMostlyNullsProfile()
    Assert.assertNotEquals(profile1, profile3)
    Assert.assertNotEquals(profile3, profile1)
  }

  @Test
  fun testJsonSerialization() {
    var profile1 = createDefaultProfile()
    var jsonObject = profile1.toJSONObject()
    jsonObject = checkNotNull(jsonObject)
    var profile2 = Profile(jsonObject)
    assertDefaultObjectGetters(profile2)
    Assert.assertEquals(profile1, profile2)

    // Check with nulls
    profile1 = createMostlyNullsProfile()
    jsonObject = profile1.toJSONObject()
    jsonObject = checkNotNull(jsonObject)
    profile2 = Profile(jsonObject)
    assertMostlyNullsObjectGetters(profile2)
    Assert.assertEquals(profile1, profile2)
  }

  @Test
  fun testParcelSerialization() {
    var profile1 = createDefaultProfile()
    var profile2 = TestUtils.parcelAndUnparcel(profile1)
    assertDefaultObjectGetters(profile2)
    Assert.assertEquals(profile1, profile2)

    // Check with nulls
    profile1 = createMostlyNullsProfile()
    profile2 = TestUtils.parcelAndUnparcel(profile1)
    assertMostlyNullsObjectGetters(profile2)
    Assert.assertEquals(profile1, profile2)
  }

  @Test
  fun testGetSetCurrentProfile() {
    val profile1 = createDefaultProfile()
    Profile.setCurrentProfile(profile1)
    Assert.assertEquals(ProfileManager.getInstance().currentProfile, profile1)
    Assert.assertEquals(profile1, Profile.getCurrentProfile())
    Profile.setCurrentProfile(null)
    Assert.assertNull(ProfileManager.getInstance().currentProfile)
    Assert.assertNull(Profile.getCurrentProfile())
  }

  companion object {
    const val ID = "ID"
    const val ANOTHER_ID = "ANOTHER_ID"
    const val FIRST_NAME = "FIRST_NAME"
    const val MIDDLE_NAME = "MIDDLE_NAME"
    const val LAST_NAME = "LAST_NAME"
    const val NAME = "NAME"
    const val LINK_URI = "https://www.facebook.com/name"
    @JvmStatic
    fun createDefaultProfile(): Profile {
      return Profile(ID, FIRST_NAME, MIDDLE_NAME, LAST_NAME, NAME, Uri.parse(LINK_URI))
    }

    @JvmStatic
    fun assertDefaultObjectGetters(profile: Profile) {
      Assert.assertEquals(ID, profile.id)
      Assert.assertEquals(FIRST_NAME, profile.firstName)
      Assert.assertEquals(MIDDLE_NAME, profile.middleName)
      Assert.assertEquals(LAST_NAME, profile.lastName)
      Assert.assertEquals(NAME, profile.name)
      Assert.assertEquals(Uri.parse(LINK_URI), profile.linkUri)
    }

    @JvmStatic
    fun createMostlyNullsProfile(): Profile {
      return Profile(ANOTHER_ID, null, null, null, null, null)
    }

    @JvmStatic
    fun assertMostlyNullsObjectGetters(profile: Profile) {
      Assert.assertEquals(ANOTHER_ID, profile.id)
      Assert.assertNull(profile.firstName)
      Assert.assertNull(profile.middleName)
      Assert.assertNull(profile.lastName)
      Assert.assertNull(profile.name)
      Assert.assertNull(profile.linkUri)
    }
  }
}
