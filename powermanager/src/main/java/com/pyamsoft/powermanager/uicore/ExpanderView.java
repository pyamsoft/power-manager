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

package com.pyamsoft.powermanager.uicore;

import android.content.Context;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Px;
import android.support.annotation.RequiresApi;
import android.support.annotation.StringRes;
import android.support.annotation.StyleRes;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.databinding.ViewExpanderBinding;
import com.pyamsoft.pydroid.loader.ImageLoader;
import com.pyamsoft.pydroid.loader.LoaderHelper;
import com.pyamsoft.pydroid.loader.loaded.Loaded;

public class ExpanderView extends FrameLayout {

  ViewExpanderBinding binding;
  boolean expanded;
  @NonNull Loaded arrowLoad = LoaderHelper.empty();
  @Nullable ViewPropertyAnimatorCompat arrowAnimation;
  @Nullable ViewPropertyAnimatorCompat containerAnimation;

  public ExpanderView(@NonNull Context context) {
    super(context);
    init();
  }

  public ExpanderView(@NonNull Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public ExpanderView(@NonNull Context context, @Nullable AttributeSet attrs,
      @AttrRes int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  public ExpanderView(@NonNull Context context, @Nullable AttributeSet attrs,
      @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    init();
  }

  private void init() {
    binding = ViewExpanderBinding.inflate(LayoutInflater.from(getContext()), this, false);
    addView(binding.getRoot());

    cancelArrowAnimation();
    cancelContainerAnimation();
    if (expanded) {
      ViewCompat.setRotation(binding.expanderArrow, 0);
      binding.expanderContainer.setAlpha(1);
      binding.expanderContainer.setScaleY(1);
      binding.expanderContainer.setVisibility(View.VISIBLE);
    } else {
      ViewCompat.setRotation(binding.expanderArrow, 180);
      binding.expanderContainer.setVisibility(View.GONE);
      binding.expanderContainer.setAlpha(0);
      binding.expanderContainer.setScaleY(0);
    }

    binding.expanderContainer.setVisibility(expanded ? View.VISIBLE : View.GONE);
    binding.expanderTitleContainer.setOnClickListener(v -> {
      expanded = !expanded;
      cancelArrowAnimation();
      arrowAnimation = ViewCompat.animate(binding.expanderArrow).rotation(expanded ? 0 : 180);
      arrowAnimation.start();

      cancelContainerAnimation();
      if (expanded) {
        // This is expanding now
        // Be visible, but hidden
        binding.expanderContainer.setScaleY(0);
        binding.expanderContainer.setAlpha(0);
        containerAnimation = ViewCompat.animate(binding.expanderContainer)
            .scaleY(1)
            .alpha(1)
            .setListener(new ViewPropertyAnimatorListenerAdapter() {

              @Override public void onAnimationStart(View view) {
                view.setVisibility(View.VISIBLE);
              }

              @Override public void onAnimationEnd(View view) {
                view.setVisibility(View.VISIBLE);
              }
            });
        containerAnimation.start();
      } else {
        // This is collapsing now
        // Be visible
        binding.expanderContainer.setScaleY(1);
        binding.expanderContainer.setAlpha(1);
        containerAnimation = ViewCompat.animate(binding.expanderContainer)
            .scaleY(0)
            .alpha(0)
            .setListener(new ViewPropertyAnimatorListenerAdapter() {

              @Override public void onAnimationStart(View view) {
                view.setVisibility(View.VISIBLE);
              }

              @Override public void onAnimationEnd(View view) {
                view.setVisibility(View.GONE);
              }
            });
        containerAnimation.start();
      }
    });
  }

  void cancelArrowAnimation() {
    if (arrowAnimation != null) {
      arrowAnimation.cancel();
      arrowAnimation = null;
    }
  }

  void cancelContainerAnimation() {
    if (containerAnimation != null) {
      containerAnimation.cancel();
      containerAnimation = null;
    }
  }

  @Override protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    arrowLoad = LoaderHelper.unload(arrowLoad);
    arrowLoad = ImageLoader.fromResource(getContext(), R.drawable.ic_arrow_up_24dp)
        .into(binding.expanderArrow);
  }

  @Override protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    arrowLoad = LoaderHelper.unload(arrowLoad);
  }

  public void setTitle(@StringRes int title) {
    setTitle(new SpannableString(getContext().getString(title)));
  }

  public void setTitle(@Nullable Spannable title) {
    binding.expanderTitle.setText(title);
    binding.expanderTitle.setVisibility(title == null ? View.GONE : View.VISIBLE);
  }

  public void setTitleTextSize(@Px int size) {
    binding.expanderTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
  }

  public void setDescription(@StringRes int description) {
    setDescription(new SpannableString(getContext().getString(description)));
  }

  public void setDescription(@Nullable Spannable description) {
    binding.expanderDescription.setText(description);
    binding.expanderDescription.setVisibility(description == null ? View.GONE : View.VISIBLE);
  }

  public void setExpandingContent(@LayoutRes int layout) {
    setExpandingContent(LayoutInflater.from(getContext()).inflate(layout, this, false));
  }

  public void setExpandingContent(@NonNull View view) {
    binding.expanderContainer.addView(view);
  }
}
