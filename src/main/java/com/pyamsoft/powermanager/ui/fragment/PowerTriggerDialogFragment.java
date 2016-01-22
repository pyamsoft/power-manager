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
package com.pyamsoft.powermanager.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.backend.trigger.PowerTrigger;
import com.pyamsoft.powermanager.backend.trigger.PowerTriggerDataSource;
import com.pyamsoft.powermanager.ui.adapter.PowerTriggerAdapter;
import com.pyamsoft.powermanager.ui.adapter.PowerTriggerDialogAdapter;
import com.pyamsoft.pydroid.util.LogUtil;
import java.util.Set;

public class PowerTriggerDialogFragment extends DialogFragment {

  private static final String TAG = PowerTriggerDialogFragment.class.getSimpleName();
  private TabLayout tabLayout;
  private ViewPager viewPager;
  private PowerTriggerDialogAdapter adapter;
  private PowerTriggerAdapter parentAdapter;

  private static void createNewTrigger(final PowerTrigger newTrigger,
      final PowerTriggerDialogAdapter adapter) {
    newTrigger.setAvailable(PowerTrigger.AVAILABLE);
    newTrigger.setEnabled(PowerTrigger.ENABLED);

    newTrigger.setManageWifi(adapter.getManageEnabledWifi());
    newTrigger.setManageData(adapter.getManageEnabledData());
    newTrigger.setManageBluetooth(adapter.getManageEnabledBluetooth());
    newTrigger.setManageSync(adapter.getManageEnabledSync());

    newTrigger.setReopenWifi(adapter.getReOpenEnabledWifi());
    newTrigger.setReopenData(adapter.getReOpenEnabledData());
    newTrigger.setReopenBluetooth(adapter.getReOpenEnabledBluetooth());
    newTrigger.setReopenSync(adapter.getReOpenEnabledSync());

    int state = adapter.getStateWifi();
    switch (state) {
      case PowerTrigger.TOGGLE_STATE_OFF:
        newTrigger.setStateOffWifi();
        break;
      case PowerTrigger.TOGGLE_STATE_ON:
        newTrigger.setStateOnWifi();
        break;
      default:
        newTrigger.setStateNoneWifi();
    }

    state = adapter.getStateData();
    switch (state) {
      case PowerTrigger.TOGGLE_STATE_OFF:
        newTrigger.setStateOffData();
        break;
      case PowerTrigger.TOGGLE_STATE_ON:
        newTrigger.setStateOnData();
        break;
      default:
        newTrigger.setStateNoneData();
    }

    state = adapter.getStateBluetooth();
    switch (state) {
      case PowerTrigger.TOGGLE_STATE_OFF:
        newTrigger.setStateOffBluetooth();
        break;
      case PowerTrigger.TOGGLE_STATE_ON:
        newTrigger.setStateOnBluetooth();
        break;
      default:
        newTrigger.setStateNoneBluetooth();
    }

    state = adapter.getStateSync();
    switch (state) {
      case PowerTrigger.TOGGLE_STATE_OFF:
        newTrigger.setStateOffSync();
        break;
      case PowerTrigger.TOGGLE_STATE_ON:
        newTrigger.setStateOnSync();
        break;
      default:
        newTrigger.setStateNoneSync();
    }

    // TODO
    newTrigger.setBrightnessLevel(0);
    newTrigger.setAutoBrightness(0);
    newTrigger.setVolume(0);
  }

  public static void hideKeyboard(final Activity act) {
    if (act != null) {
      LogUtil.d(TAG, "Get current focus");
      final View view = act.getCurrentFocus();
      if (view != null) {
        LogUtil.d(TAG, "Attempt to hide keyboard");
        final InputMethodManager imm =
            ((InputMethodManager) act.getSystemService(Context.INPUT_METHOD_SERVICE));
        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
      }
    }
  }

  @Nullable @Override
  public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container,
      @Nullable final Bundle savedInstanceState) {
    return inflater.inflate(R.layout.layout_new_trigger_dialog, container, false);
  }

  public final void setParentAdapter(final PowerTriggerAdapter parentAdapter) {
    this.parentAdapter = parentAdapter;
  }

  @Override public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    setCancelable(false);
    setupViewPagerAndTabs(view);
    setupButtons(view);
  }

  private void setupButtons(final View view) {
    final Button cancel = (Button) view.findViewById(R.id.new_trigger_dialog_cancel_button);
    final Button create = (Button) view.findViewById(R.id.new_trigger_dialog_create_button);

    cancel.setOnClickListener(new View.OnClickListener() {

      @Override public void onClick(final View v) {
        dismiss();
        hideKeyboard(getActivity());
      }
    });

    create.setOnClickListener(new View.OnClickListener() {

      @Override public void onClick(final View v) {
        // Create the new Trigger by reading all parts of the
        // layout for their current
        // states
        if (adapter == null) {
          dismiss();
          hideKeyboard(getActivity());
          return;
        }

        // fetch all adapter values
        final PowerTriggerDataSource source = PowerTriggerDataSource.get();
        source.open();
        if (!source.isOpened()) {
          return;
        }

        final String name = adapter.getName();
        final int level = adapter.getLevel();
        if (name == null || level == -1) {
          source.close();
          return;
        }

        final PowerTrigger newTrigger = new PowerTrigger(source.getNextId(), name, level);
        createNewTrigger(newTrigger, adapter);

        PowerTrigger trigger = null;
        final Set<PowerTrigger> triggers = PowerTriggerDataSource.TriggerSet.get().asSet();
        for (final PowerTrigger t : triggers) {
          if (t.getId() == newTrigger.getId()) {
            LogUtil.d(TAG, "Found matching trigger by ID");
            trigger = t;
            break;
          }

          if (t.getName().equalsIgnoreCase(newTrigger.getName())) {
            LogUtil.d(TAG, "Found matching trigger by NAME");
            trigger = t;
            break;
          }

          if (t.getLevel() == newTrigger.getLevel()) {
            LogUtil.d(TAG, "Found matching trigger by LEVEL");
            trigger = t;
            break;
          }
        }

        if (trigger != null) {
          LogUtil.d(TAG, "Adopt trigger ", newTrigger.getName());
          trigger.adopt(newTrigger);
        } else {
          LogUtil.d(TAG, "Create trigger ", newTrigger.getName());
          trigger = newTrigger;
        }
        source.createTrigger(trigger);
        source.close();

        if (parentAdapter != null) {
          parentAdapter.refreshDataSet();
        }

        dismiss();
        hideKeyboard(getActivity());
      }
    });
  }

  // Have to do this I think because there is no other way to access the dialog to
  // dismiss it
  // other than creating it first and then setting up its view later
  private void setupViewPagerAndTabs(final View view) {
    tabLayout = (TabLayout) view.findViewById(R.id.new_trigger_dialog_tabs);
    viewPager = (ViewPager) view.findViewById(R.id.new_trigger_dialog_viewpager);

    tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

      @Override public void onTabSelected(final TabLayout.Tab tab) {
        if (viewPager != null) {
          viewPager.setCurrentItem(tab.getPosition());
        }
        hideKeyboard(getActivity());
      }

      @Override public void onTabUnselected(final TabLayout.Tab tab) {

      }

      @Override public void onTabReselected(final TabLayout.Tab tab) {

      }
    });
    adapter = new PowerTriggerDialogAdapter(this);
    viewPager.setAdapter(adapter);
    viewPager.setOffscreenPageLimit(5);
    tabLayout.setupWithViewPager(viewPager);
    final int size = tabLayout.getTabCount();
    for (int i = 0; i < size; ++i) {
      final TabLayout.Tab tab = tabLayout.getTabAt(i);
      if (tab != null) {
        int icon;
        switch (i) {
          case 1:
            icon = R.drawable.ic_network_wifi_white_24dp;
            break;
          case 2:
            icon = R.drawable.ic_network_cell_white_24dp;
            break;
          case 3:
            icon = R.drawable.ic_bluetooth_white_24dp;
            break;
          case 4:
            icon = R.drawable.ic_sync_white_24dp;
            break;
          default:
            icon = R.drawable.ic_settings_white_24dp;
        }
        tab.setIcon(icon);
      }
    }
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    if (tabLayout != null) {
      tabLayout.setOnTabSelectedListener(null);
    }

    if (viewPager != null) {
      viewPager.setAdapter(null);
    }
  }
}
