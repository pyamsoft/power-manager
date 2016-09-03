/*
 * Copyright 2016 Peter Kenji Yamanaka
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

package com.pyamsoft.powermanager.dagger.observer.state;

import android.content.Context;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import java.util.List;
import java.util.concurrent.TimeUnit;
import timber.log.Timber;

class WearStateObserver extends StateObserver {

  @NonNull private final GoogleApiClient googleApiClient;

  WearStateObserver(@NonNull Context context) {
    super(context);
    googleApiClient =
        new GoogleApiClient.Builder(context.getApplicationContext()).addApiIfAvailable(Wearable.API)
            .build();
  }

  @WorkerThread @CheckResult private boolean isWearableNodeConnected() {
    final NodeApi.GetConnectedNodesResult nodesResult =
        Wearable.NodeApi.getConnectedNodes(googleApiClient).await(4, TimeUnit.SECONDS);
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
      result = true;
    } else {
      Timber.d("Found a wearable node");
      result = false;
    }

    disconnectGoogleApiClient();
    return result;
  }

  /**
   * Return true if stream should continue, false otherwise
   */
  @WorkerThread @Override public boolean is() {
    Timber.d("Check if wearable is connected");
    final ConnectionResult connectionResult = googleApiClient.blockingConnect(4, TimeUnit.SECONDS);
    boolean result;
    if (connectionResult.isSuccess()) {
      Timber.d("Connect Google APIs");
      result = isWearableNodeConnected();
    } else {
      Timber.e("Could not connect to Google APIs");
      result = true;
    }
    return result;
  }

  private void disconnectGoogleApiClient() {
    if (googleApiClient.isConnected()) {
      Timber.d("Disconnect Google Api Client");
      googleApiClient.disconnect();
    }
  }

  @Override public void register(@NonNull String tag, @Nullable SetCallback setCallback,
      @Nullable UnsetCallback unsetCallback) {
    Timber.w("Cannot monitor for state changes on Wearables");
    disconnectGoogleApiClient();
  }

  @Override public void unregister(@NonNull String tag) {
    Timber.w("Cannot monitor for state changes on Wearables");
    disconnectGoogleApiClient();
  }
}
