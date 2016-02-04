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
    intent = AppUtil.getApplicationInfoIntent(PowerManager.class)
        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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
