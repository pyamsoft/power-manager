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

package com.pyamsoft.powermanager.base.states;

import android.content.Context;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.pyamsoft.powermanager.base.preference.WearablePreferences;
import com.pyamsoft.powermanager.model.StateObserver;
import java.util.List;
import java.util.concurrent.TimeUnit;
import timber.log.Timber;

class WearStateObserver implements StateObserver {

  @NonNull private final GoogleApiClient googleApiClient;
  @NonNull private final WearablePreferences preferences;

  WearStateObserver(@NonNull Context context, @NonNull WearablePreferences preferences) {
    this.preferences = preferences;
    googleApiClient =
        new GoogleApiClient.Builder(context.getApplicationContext()).addApiIfAvailable(Wearable.API)
            .build();
  }

  @WorkerThread @CheckResult private boolean isWearableNodeConnected() {
    final long waitTime = preferences.getWearableDelay();
    Timber.d("Wait for nodes for %d seconds", waitTime);
    final NodeApi.GetConnectedNodesResult nodesResult =
        Wearable.NodeApi.getConnectedNodes(googleApiClient).await(waitTime, TimeUnit.SECONDS);
    Node wearableNode = null;
    final List<Node> nodes = nodesResult.getNodes();
    Timber.d("Search node list of size : %d", nodes.size());
    for (final Node node : nodes) {
      if (node.isNearby()) {
        Timber.d("Wearable node: %s %s", node.getDisplayName(), node.getId());
        wearableNode = node;
        break;
      }
    }

    final boolean result;
    if (wearableNode == null) {
      Timber.w("No wearable node was found");
      result = false;
    } else {
      Timber.d("Found a wearable node");
      result = true;
    }

    disconnectGoogleApiClient();
    return result;
  }

  /**
   * Return if a wearable is connected
   */
  @WorkerThread @CheckResult private boolean isConnected() {
    Timber.d("Check if wearable is connected");
    final long waitTime = preferences.getWearableDelay();
    Timber.d("Wait for connection for %d seconds", waitTime);
    final ConnectionResult connectionResult =
        googleApiClient.blockingConnect(waitTime, TimeUnit.SECONDS);
    boolean result;
    if (connectionResult.isSuccess()) {
      Timber.d("Connect Google APIs");
      result = isWearableNodeConnected();
    } else {
      Timber.e("Could not connect to Google APIs");
      result = false;
    }
    return result;
  }

  private void disconnectGoogleApiClient() {
    if (googleApiClient.isConnected()) {
      Timber.d("Disconnect Google Api Client");
      googleApiClient.disconnect();
    }
  }

  @Override public boolean enabled() {
    return isConnected();
  }

  @Override public boolean unknown() {
    return !googleApiClient.isConnected();
  }
}
