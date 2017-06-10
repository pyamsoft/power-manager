/*
 * Copyright 2017 Peter Kenji Yamanaka
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pyamsoft.powermanager.base.states

import android.content.Context
import android.support.annotation.CheckResult
import android.support.annotation.WorkerThread
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.Wearable
import com.pyamsoft.powermanager.base.preference.WearablePreferences
import com.pyamsoft.powermanager.model.StateObserver
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

internal class WearStateObserver @Inject internal constructor(context: Context,
    private val preferences: WearablePreferences) : StateObserver {

  private val googleApiClient: GoogleApiClient = GoogleApiClient.Builder(
      context.applicationContext).addApiIfAvailable(Wearable.API).build()
  private val isWearableNodeConnected: Boolean
    @WorkerThread @CheckResult get() {
      val waitTime = preferences.wearableDelay
      Timber.d("Wait for nodes for %d seconds", waitTime)
      val nodesResult = Wearable.NodeApi.getConnectedNodes(googleApiClient).await(waitTime,
          TimeUnit.SECONDS)
      var wearableNode: Node? = null
      val nodes = nodesResult.nodes
      Timber.d("Search node list of size : %d", nodes.size)
      for (node in nodes) {
        if (node.isNearby) {
          Timber.d("Wearable node: %s %s", node.displayName, node.id)
          wearableNode = node
          break
        }
      }
      val result: Boolean
      if (wearableNode == null) {
        Timber.w("No wearable node was found")
        result = false
      } else {
        Timber.d("Found a wearable node")
        result = true
      }

      disconnectGoogleApiClient()
      return result
    }

  /**
   * Return if a wearable is connected
   */
  private val isConnected: Boolean
    @WorkerThread @CheckResult get() {
      Timber.d("Check if wearable is connected")
      val waitTime = preferences.wearableDelay
      Timber.d("Wait for connection for %d seconds", waitTime)
      val connectionResult = googleApiClient.blockingConnect(waitTime, TimeUnit.SECONDS)
      val result: Boolean
      if (connectionResult.isSuccess) {
        Timber.d("Connect Google APIs")
        result = isWearableNodeConnected
      } else {
        Timber.e("Could not connect to Google APIs")
        result = false
      }
      return result
    }

  private fun disconnectGoogleApiClient() {
    if (googleApiClient.isConnected) {
      Timber.d("Disconnect Google Api Client")
      googleApiClient.disconnect()
    }
  }

  override fun enabled(): Boolean {
    return isConnected
  }

  override fun unknown(): Boolean {
    return !googleApiClient.isConnected
  }
}
