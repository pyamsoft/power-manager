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

package com.pyamsoft.powermanager.dagger.manager.backend;

import android.content.Context;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.pyamsoft.powermanager.PowerManagerPreferences;
import java.util.List;
import java.util.concurrent.TimeUnit;
import rx.Observable;
import timber.log.Timber;

abstract class WearableManagerInteractorImpl extends ManagerInteractorBase
    implements WearableManagerInteractor {

  @NonNull private final Context appContext;
  @NonNull private final PowerManagerPreferences preferences;
  @NonNull private final GoogleApiClient googleApiClient;

  WearableManagerInteractorImpl(@NonNull Context context,
      @NonNull PowerManagerPreferences preferences) {
    this.appContext = context.getApplicationContext();
    this.preferences = preferences;
    googleApiClient =
        new GoogleApiClient.Builder(appContext).addApiIfAvailable(Wearable.API).build();
  }

  @NonNull @Override public Observable<Boolean> isWearableManaged() {
    return Observable.defer(() -> Observable.just(preferences.isWearableManaged()));
  }

  @NonNull @CheckResult @Override public Observable<Boolean> isWearableConnected() {
    return Observable.defer(() -> {
      Timber.d("Check if wearable is connected");
      final ConnectionResult connectionResult =
          googleApiClient.blockingConnect(1, TimeUnit.SECONDS);
      boolean result;
      if (connectionResult.isSuccess()) {
        Timber.d("Connect Google APIs");
        final NodeApi.GetConnectedNodesResult nodesResult =
            Wearable.NodeApi.getConnectedNodes(googleApiClient).await(1, TimeUnit.SECONDS);
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

        if (wearableNode == null) {
          Timber.d("No wearable node was found");
          result = false;
        } else {
          Timber.d("Found a wearable node");
          result = true;
        }
      } else {
        result = false;
      }
      return Observable.just(result);
    });
  }

  @Override public void disconnectGoogleApis() {
    Timber.d("Disconnect Google APIs");
    googleApiClient.disconnect();
  }
}
