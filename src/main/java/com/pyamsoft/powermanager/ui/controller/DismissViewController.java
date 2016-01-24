/*
 * Copyright 2014 - 2016 Peter Kenji Yamanaka
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

package com.pyamsoft.powermanager.ui.controller;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.pydroid.util.AnimUtil;
import java.lang.ref.WeakReference;

public final class DismissViewController {

  private static final int EXPLANATION_ANIM_TIME = 600;
  private WeakReference<Activity> root;

  @Bind(R.id.explain_view) View explainView;
  @Bind(R.id.explain_text) TextView explainText;
  @Bind(R.id.explain_read) Button explainRead;

  public final void bind(final Activity root) {
    this.root = new WeakReference<>(root);
    ButterKnife.bind(this, root);
  }

  public final void unbind() {
    final Activity view = getRoot();
    if (view == null) {
      clearRoot();
      return;
    }
    explainRead.setOnClickListener(null);
    clearRoot();
  }

  private void clearRoot() {
    if (root != null) {
      root.clear();
      root = null;
    }
  }

  private Activity getRoot() {
    return (root == null) ? null : root.get();
  }

  private boolean isViewShown() {
    return explainView.getVisibility() == View.VISIBLE;
  }

  public final void showView(final Spannable text, final int colorResId) {
    final Activity view = getRoot();
    if (view == null) {
      return;
    }
    if (!isViewShown()) {
      if (colorResId > 0) {
        explainView.setBackgroundColor(
            ContextCompat.getColor(explainView.getContext(), colorResId));
      }
      AnimUtil.expand(explainView, EXPLANATION_ANIM_TIME);
      explainText.setText(text);
    }
  }

  public final void hideView() {
    final Activity view = getRoot();
    if (view == null) {
      return;
    }
    if (isViewShown()) {
      explainView.setVisibility(View.GONE);
      explainText.setText(null);
    }
  }

  public final void dismissView() {
    final Activity view = getRoot();
    if (view == null) {
      return;
    }
    if (isViewShown()) {
      AnimUtil.collapse(explainView, EXPLANATION_ANIM_TIME);
      explainText.setText(null);
    }
  }

  @OnClick(R.id.explain_read) public void onClickExplainReadButton(final View view) {
    dismissView();
  }
}
