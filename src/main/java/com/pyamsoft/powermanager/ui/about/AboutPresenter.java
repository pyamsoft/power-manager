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

package com.pyamsoft.powermanager.ui.about;

import android.content.Context;
import com.pyamsoft.pydroid.base.PresenterBase;

public class AboutPresenter extends PresenterBase<AboutInterface> {

  private AboutModel model;

  @Override public void bind(AboutInterface reference) {
    throw new IllegalBindException("Can't bind without context");
  }

  public void bind(final Context context, final AboutInterface reference) {
    super.bind(reference);
    model = new AboutModel(context);
  }

  @Override public void unbind() {
    super.unbind();
    model = null;
  }

  public void onClickDetailButton() {
    final AboutInterface reference = getBoundReference();
    if (reference == null) {
      return;
    }

    if (model.startApplicationDetailActivity()) {
      reference.onDetailActivityLaunched();
    }
  }
}
