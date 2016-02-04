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

package com.pyamsoft.powermanager.ui;

import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v4.app.Fragment;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

/**
 * Created by pyamsoft on 1/26/16.
 */
public abstract class PicassoTargetFragment extends Fragment implements Target {
  private Handler handler = new Handler();

  @Override public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
    final Drawable drawable = new BitmapDrawable(getResources(), bitmap);
    drawable.setAlpha(0);
    loadDrawableIntoView(drawable);
    handler.post(new Runnable() {
      @Override public void run() {
        final ObjectAnimator animator = ObjectAnimator.ofInt(drawable, "alpha", 255);
        animator.setTarget(drawable);
        animator.setDuration(600L);
        animator.start();
      }
    });
  }

  protected abstract void loadDrawableIntoView(Drawable drawable);

  @Override public void onBitmapFailed(Drawable errorDrawable) {

  }

  @Override public void onPrepareLoad(Drawable placeHolderDrawable) {

  }
}
