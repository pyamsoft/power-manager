package com.pyamsoft.powermanager.ui.detail;

import android.content.Context;
import com.pyamsoft.pydroid.base.Presenter;
import com.pyamsoft.pydroid.util.LogUtil;

/**
 * Detail interfaces have two floating action buttons
 */
public final class DetailPresenter extends Presenter<DetailInterface> {

  private static final String TAG = DetailPresenter.class.getSimpleName();
  private DetailModel smallModel;
  private DetailModel largeModel;

  @Override public void bind(DetailInterface reference) {
    throw new IllegalBindException("Can't use bind without a context");
  }

  public void bind(final Context context, DetailInterface reference) {
    super.bind(reference);
    smallModel = new DetailModel(context, reference.getTarget(), DetailModel.FAB_TYPE_SMALL);
    largeModel = new DetailModel(context, reference.getTarget(), DetailModel.FAB_TYPE_LARGE);
  }

  @Override public void unbind() {
    super.unbind();
    smallModel = null;
    largeModel = null;
  }

  public boolean isSmallFABChecked() {
    LogUtil.d(TAG, "isSmallFABChecked");
    return smallModel.isFABChecked();
  }

  public boolean isLargeFABChecked() {
    LogUtil.d(TAG, "isLargeFABChecked");
    return largeModel.isFABChecked();
  }

  public void onClickSmallFAB() {
    final DetailInterface detail = getBoundReference();
    if (detail == null) {
      LogUtil.d(TAG, "Null detail reference");
      return;
    }

    LogUtil.d(TAG, "onClickSmallFAB");
    final boolean newChecked = !smallModel.isFABChecked();
    smallModel.setFABChecked(newChecked);
    if (newChecked) {
      detail.onSmallFABChecked();
    } else {
      detail.onSmallFABUnchecked();
    }
  }

  public boolean onLongClickSmallFAB() {
    final DetailInterface detail = getBoundReference();
    if (detail == null) {
      LogUtil.d(TAG, "Null detail reference");
      return false;
    }
    LogUtil.d(TAG, "onLongClickSmallFAB");
    detail.onLongClickSmallFAB();
    return true;
  }

  public boolean onLongClickLargeFAB() {
    final DetailInterface detail = getBoundReference();
    if (detail == null) {
      LogUtil.d(TAG, "Null detail reference");
      return false;
    }
    LogUtil.d(TAG, "onLongClickLargeFAB");
    detail.onLongClickLargeFAB();
    return true;
  }

  public void onClickLargeFAB() {
    final DetailInterface detail = getBoundReference();
    if (detail == null) {
      LogUtil.d(TAG, "Null detail reference");
      return;
    }

    LogUtil.d(TAG, "onClickLargeFAB");
    final boolean newChecked = !largeModel.isFABChecked();
    largeModel.setFABChecked(newChecked);
    if (newChecked) {
      detail.onLargeFABChecked();
    } else {
      detail.onLargeFABUnchecked();
    }
  }
}
