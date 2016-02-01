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
