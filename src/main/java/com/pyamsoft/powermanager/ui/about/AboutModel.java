package com.pyamsoft.powermanager.ui.about;

import android.content.Context;
import android.content.Intent;
import com.pyamsoft.powermanager.PowerManager;
import com.pyamsoft.pydroid.util.AppUtil;
import java.lang.ref.WeakReference;

public class AboutModel {

  private WeakReference<Context> weakContext;
  private final Intent intent;

  public AboutModel(final Context context) {
    this.weakContext = new WeakReference<>(context);
    intent = AppUtil.getApplicationInfoIntent(PowerManager.class);
  }

  public boolean startApplicationDetailActivity() {
    final Context context = weakContext.get();
    if (context != null) {
      context.startActivity(intent);
      return true;
    } else {
      return false;
    }
  }
}
