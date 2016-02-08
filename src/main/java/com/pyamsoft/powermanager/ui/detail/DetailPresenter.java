/*
 * Copyright 2013 - 2016 Peter Kenji Yamanaka
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

package com.pyamsoft.powermanager.ui.detail;

import android.content.Context;
import com.pyamsoft.pydroid.base.PresenterBase;
import com.pyamsoft.pydroid.util.LogUtil;

/**
 * Detail interfaces have two floating action buttons
 */
public final class DetailPresenter extends PresenterBase<DetailInterface> {

  private static final String TAG = DetailPresenter.class.getSimpleName();
  private DetailModel model;

  @Override public void bind(DetailInterface reference) {
    throw new IllegalBindException("Can't use bind without a context");
  }

  public void bind(final Context context, DetailInterface reference) {
    super.bind(reference);
    model = new DetailModel(context);
  }

  @Override public void unbind() {
    super.unbind();
    model = null;
  }

  public boolean isWifiManaged() {
    final DetailInterface reference = getBoundReference();
    if (reference == null) {
      LogUtil.e(TAG, "reference is NULL");
      return false;
    }
    return model.isWifiManaged();
  }

  public void setWifiManaged(final boolean isChecked) {
    final DetailInterface reference = getBoundReference();
    if (reference == null) {
      LogUtil.e(TAG, "reference is NULL");
      return;
    }
    model.setWifiManaged(isChecked);
    if (isChecked) {
      reference.onLargeFABChecked();
    } else {
      reference.onLargeFABUnchecked();
    }
  }

  public boolean isDataManaged() {
    final DetailInterface reference = getBoundReference();
    if (reference == null) {
      LogUtil.e(TAG, "reference is NULL");
      return false;
    }
    return model.isDataManaged();
  }

  public void setDataManaged(final boolean isChecked) {
    final DetailInterface reference = getBoundReference();
    if (reference == null) {
      LogUtil.e(TAG, "reference is NULL");
      return;
    }
    model.setDataManaged(isChecked);
    if (isChecked) {
      reference.onLargeFABChecked();
    } else {
      reference.onLargeFABUnchecked();
    }
  }

  public boolean isBluetoothManaged() {
    final DetailInterface reference = getBoundReference();
    if (reference == null) {
      LogUtil.e(TAG, "reference is NULL");
      return false;
    }
    return model.isBluetoothManaged();
  }

  public void setBluetoothManaged(final boolean isChecked) {
    final DetailInterface reference = getBoundReference();
    if (reference == null) {
      LogUtil.e(TAG, "reference is NULL");
      return;
    }
    model.setBluetoothManaged(isChecked);
    if (isChecked) {
      reference.onLargeFABChecked();
    } else {
      reference.onLargeFABUnchecked();
    }
  }

  public boolean isSyncManaged() {
    final DetailInterface reference = getBoundReference();
    if (reference == null) {
      LogUtil.e(TAG, "reference is NULL");
      return false;
    }
    return model.isSyncManaged();
  }

  public void setSyncManaged(final boolean isChecked) {
    final DetailInterface reference = getBoundReference();
    if (reference == null) {
      LogUtil.e(TAG, "reference is NULL");
      return;
    }
    model.setSyncManaged(isChecked);
    if (isChecked) {
      reference.onLargeFABChecked();
    } else {
      reference.onLargeFABUnchecked();
    }
  }

  public boolean isWifiReOpen() {
    final DetailInterface reference = getBoundReference();
    if (reference == null) {
      LogUtil.e(TAG, "reference is NULL");
      return false;
    }
    return model.isWifiReOpen();
  }

  public void setWifiReOpen(final boolean isChecked) {
    final DetailInterface reference = getBoundReference();
    if (reference == null) {
      LogUtil.e(TAG, "reference is NULL");
      return;
    }
    model.setWifiReOpen(isChecked);
    if (isChecked) {
      reference.onSmallFABChecked();
    } else {
      reference.onSmallFABUnchecked();
    }
  }

  public boolean isDataReOpen() {
    final DetailInterface reference = getBoundReference();
    if (reference == null) {
      LogUtil.e(TAG, "reference is NULL");
      return false;
    }
    return model.isDataReOpen();
  }

  public void setDataReOpen(final boolean isChecked) {
    final DetailInterface reference = getBoundReference();
    if (reference == null) {
      LogUtil.e(TAG, "reference is NULL");
      return;
    }
    model.setDataReOpen(isChecked);
    if (isChecked) {
      reference.onSmallFABChecked();
    } else {
      reference.onSmallFABUnchecked();
    }
  }

  public boolean isBluetoothReOpen() {
    final DetailInterface reference = getBoundReference();
    if (reference == null) {
      LogUtil.e(TAG, "reference is NULL");
      return false;
    }
    return model.isBluetoothReOpen();
  }

  public void setBluetoothReOpen(final boolean isChecked) {
    final DetailInterface reference = getBoundReference();
    if (reference == null) {
      LogUtil.e(TAG, "reference is NULL");
      return;
    }
    model.setBluetoothReOpen(isChecked);
    if (isChecked) {
      reference.onSmallFABChecked();
    } else {
      reference.onSmallFABUnchecked();
    }
  }

  public boolean isSyncReOpen() {
    final DetailInterface reference = getBoundReference();
    if (reference == null) {
      LogUtil.e(TAG, "reference is NULL");
      return false;
    }
    return model.isSyncReOpen();
  }

  public void setSyncReOpen(final boolean isChecked) {
    final DetailInterface reference = getBoundReference();
    if (reference == null) {
      LogUtil.e(TAG, "reference is NULL");
      return;
    }
    model.setSyncReOpen(isChecked);
    if (isChecked) {
      reference.onSmallFABChecked();
    } else {
      reference.onSmallFABUnchecked();
    }
  }
}
