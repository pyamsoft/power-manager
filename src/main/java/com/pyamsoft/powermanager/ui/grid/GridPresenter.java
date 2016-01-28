package com.pyamsoft.powermanager.ui.grid;

import android.content.Context;
import com.pyamsoft.pydroid.base.PresenterBase;
import com.pyamsoft.pydroid.util.LogUtil;

public final class GridPresenter extends PresenterBase<GridInterface> {

  private static final String TAG = GridPresenter.class.getSimpleName();
  private GridModel model;

  @Override public void bind(GridInterface reference) {
    throw new IllegalBindException("Needs context");
  }

  public void bind(final Context context, final GridInterface reference) {
    super.bind(reference);
    model = new GridModel(context);
  }

  @Override public void unbind() {
    super.unbind();
    model = null;
  }

  public void clickItem(final String name, final int image) {
    final GridInterface reference = getBoundReference();
    if (reference == null) {
      LogUtil.e(TAG, "Null reference");
      return;
    }
    LogUtil.d(TAG, "onItemClicked: ", name);
    reference.onItemClicked(model.createFragment(name, image));
  }

  public void moveItem(final int fromPosition, final int toPosition) {
    final GridInterface reference = getBoundReference();
    if (reference == null) {
      LogUtil.e(TAG, "Null reference");
      return;
    }

    LogUtil.d(TAG, "moveItem from: ", fromPosition, " to: ", toPosition);
    model.moveItems(reference.getItems(), fromPosition, toPosition);
    reference.onItemMoved(fromPosition, toPosition);
  }

  public void clickFAB() {
    final GridInterface reference = getBoundReference();
    if (reference == null) {
      LogUtil.e(TAG, "Null reference");
      return;
    }
    LogUtil.d(TAG, "onFABClicked");
    model.clickFAB();
    reference.onFABClicked();
  }
}
