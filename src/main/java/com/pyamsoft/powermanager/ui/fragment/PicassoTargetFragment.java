package com.pyamsoft.powermanager.ui.fragment;

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
