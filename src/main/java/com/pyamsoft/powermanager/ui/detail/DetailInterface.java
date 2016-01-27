package com.pyamsoft.powermanager.ui.detail;

import android.content.SharedPreferences;

/**
 * The detail interface has two Floating Action Buttons, a Title and an Image
 */
public interface DetailInterface {

  void onSmallFABChecked();

  void onSmallFABUnchecked();

  void onLargeFABChecked();

  void onLargeFABUnchecked();

  void onLongClickSmallFAB();

  void onLongClickLargeFAB();

  void onPreferenceChanged(final SharedPreferences preferences, final String key);

  String getTarget();
}
