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
package com.pyamsoft.powermanager.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.backend.service.ActiveService;
import com.pyamsoft.powermanager.backend.util.GlobalPreferenceUtil;
import com.pyamsoft.powermanager.backend.util.PowerPlanUtil;
import com.pyamsoft.pydroid.util.StringUtil;

public final class RadioContentAdapter
    extends RecyclerView.Adapter<RadioContentAdapter.ViewHolder> {

  private static final long[] DELAY_VALUES = {
      ActiveService.Constants.DELAY_RADIO_NONE, ActiveService.Constants.DELAY_RADIO_FIVE,
      ActiveService.Constants.DELAY_RADIO_TEN, ActiveService.Constants.DELAY_RADIO_FIFTEEN,
      ActiveService.Constants.DELAY_RADIO_THIRTY, ActiveService.Constants.DELAY_RADIO_FOURTYFIVE,
      ActiveService.Constants.DELAY_RADIO_SIXTY,
  };

  private static final int[] DELAY_RESID = {
      R.string.delay_none, R.string.delay_five, R.string.delay_ten, R.string.delay_fifteen,
      R.string.delay_thirty, R.string.delay_fourtyfive, R.string.delay_sixty,
  };

  private static final long[] INTERVAL_VALUES = {
      ActiveService.Constants.INTERVAL_REOPEN_FIFTEEN,
      ActiveService.Constants.INTERVAL_REOPEN_THIRTY,
      ActiveService.Constants.INTERVAL_REOPEN_FOURTYFIVE,
      ActiveService.Constants.INTERVAL_REOPEN_SIXTY, ActiveService.Constants.INTERVAL_REOPEN_NINTY,
      ActiveService.Constants.INTERVAL_REOPEN_ONETWENTY,
  };

  private static final int[] INTERVAL_RESID = {
      R.string.delay_fifteen, R.string.delay_thirty, R.string.delay_fourtyfive,
      R.string.delay_sixty, R.string.delay_ninty, R.string.delay_twomin,
  };

  private static final Integer[] RADIO_BUTTON_COUNT = {
      DELAY_VALUES.length, INTERVAL_VALUES.length, INTERVAL_VALUES.length,
  };

  private static final int POSITION_DELAY = 0;
  private static final int POSITION_INTERVAL = 1;
  private static final int POSITION_REOPEN = 2;
  private static final String TAG = RadioContentAdapter.class.getSimpleName();

  private final RadioInterface radioInterface;

  public RadioContentAdapter(final RadioInterface i) {
    this.radioInterface = i;
  }

  @Override public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
    final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    final View view = inflater.inflate(R.layout.adapter_item_radiogroup, parent, false);
    return new ViewHolder(view);
  }

  @Override public void onBindViewHolder(final ViewHolder holder, final int position) {
    initializeRadioButtons(holder, position);
    setupRadioButtonsOnClick(holder, position);
    setupRadioButtonsState(holder, position);
  }

  private void initializeRadioButtons(final ViewHolder holder, final int position) {
    if (position >= RADIO_BUTTON_COUNT.length) {
      return;
    }
    if (holder.itemView instanceof ViewGroup) {
      holder.radioGroup.removeAllViews();
      final LayoutInflater inflater = LayoutInflater.from(holder.itemView.getContext());
      final ViewGroup vg = (ViewGroup) holder.itemView;
      for (int i = 0; i < RADIO_BUTTON_COUNT[position]; ++i) {
        final CheckedTextView chtv =
            (CheckedTextView) inflater.inflate(R.layout.layout_checkedtext_single, vg, false);
        final String str = initializeRadioButtonText(chtv.getContext(), i, position);
        chtv.setText(str);
        holder.radioGroup.addView(chtv);
      }
    }
  }

  private String initializeRadioButtonText(final Context context, final int i, final int position) {
    int resId;
    switch (position) {
      case POSITION_DELAY:
        resId = DELAY_RESID[i];
        break;
      case POSITION_INTERVAL:
        resId = INTERVAL_RESID[i];
        break;
      case POSITION_REOPEN:
        resId = INTERVAL_RESID[i];
        break;
      default:
        resId = 0;
    }

    String str = null;
    if (resId != 0) {
      str = context.getString(resId);
    }
    return str;
  }

  private void setupRadioButtonsState(final ViewHolder holder, final int position) {
    setRadioTitle(holder, position);
    setRadioImageState(holder, position);
    setupRadioButtons(holder, position);
  }

  private void setRadioTitle(final ViewHolder holder, final int position) {
    String str;
    String explain;
    final Context context = holder.title.getContext();
    final String name = radioInterface.getRadioNameString();
    String s;
    String ss;
    switch (position) {
      case POSITION_DELAY:
        s = context.getString(R.string.radio_delay);
        str = StringUtil.formatString(s, name) + "\n";
        ss = context.getString(R.string.radio_delay_explain);
        explain = StringUtil.formatString(ss, name);
        break;
      case POSITION_INTERVAL:
        str = context.getString(R.string.reopen_interval_delay) + "\n";
        s = context.getString(R.string.reopen_interval_delay_explain);
        explain = StringUtil.formatString(s, name, name);
        break;
      case POSITION_REOPEN:
        str = context.getString(R.string.reopen_interval_duration) + "\n";
        s = context.getString(R.string.reopen_interval_duration_explain);
        explain = StringUtil.formatString(s, name);
        break;
      default:
        str = null;
        explain = null;
        break;
    }
    if (str != null && explain != null) {
      final Spannable span = StringUtil.createBuilder(str, explain);
      final int size =
          StringUtil.getTextSizeFromAppearance(context, android.R.attr.textAppearanceSmall);
      final int color =
          StringUtil.getTextColorFromAppearance(context, android.R.attr.textAppearanceSmall);
      final int start = str.length();
      final int end = span.length();
      if (size != -1) {
        StringUtil.sizeSpan(span, start, end, size);
      }
      if (color != -1) {
        StringUtil.colorSpan(span, start, end, color);
      }
      holder.title.setText(span);
    }
  }

  private void setupRadioButtonsOnClick(final ViewHolder holder, final int position) {
    final int childCount = holder.radioGroup.getChildCount();
    for (int i = 0; i < childCount; ++i) {
      final View ch = holder.radioGroup.getChildAt(i);
      if (ch != null && ch instanceof CheckedTextView) {
        final CheckedTextView chtv = (CheckedTextView) ch;
        final int ii = i;
        chtv.setOnClickListener(new View.OnClickListener() {

          @Override public void onClick(View v) {
            if (!(v instanceof CheckedTextView)) {
              return;
            }
            final CheckedTextView cv = (CheckedTextView) v;
            final boolean newChecked = !cv.isChecked();
            cv.setChecked(newChecked);
            setPreference(ii, position);
            setRadioImageState(holder, position);
            setupRadioButtons(holder, position);
            PowerPlanUtil.get()
                .setPlan(PowerPlanUtil.toInt(
                    PowerPlanUtil.POWER_PLAN_CUSTOM[PowerPlanUtil.FIELD_INDEX]));
          }
        });
      }
    }
  }

  private void setupRadioButtons(final ViewHolder holder, final int position) {
    final int childCount = holder.radioGroup.getChildCount();
    for (int i = 0; i < childCount; ++i) {
      final View ch = holder.radioGroup.getChildAt(i);
      if (ch != null && ch instanceof CheckedTextView) {
        final CheckedTextView chtv = (CheckedTextView) ch;
        initRadioButtons(chtv, i, position);
      }
    }
  }

  private void initRadioButtons(final CheckedTextView chtv, final int i, final int position) {
    long time;
    long compare;
    switch (position) {
      case POSITION_DELAY:
        time = radioInterface.getRadioDelay();
        compare = DELAY_VALUES[i];
        break;
      case POSITION_INTERVAL:
        time = GlobalPreferenceUtil.get().powerManagerActive().getIntervalTime();
        compare = INTERVAL_VALUES[i];
        break;
      case POSITION_REOPEN:
        time = radioInterface.getReOpenTime();
        compare = INTERVAL_VALUES[i];
        break;
      default:
        time = -1;
        compare = -1;
        break;
    }
    if (time != -1 && compare != -1) {
      chtv.setChecked(time == compare);
    }
  }

  private void setRadioImageState(final ViewHolder holder, final int position) {
    long time;
    switch (position) {
      case POSITION_DELAY:
        time = radioInterface.getRadioDelay() / 1000;
        break;
      case POSITION_INTERVAL:
        time = GlobalPreferenceUtil.get().powerManagerActive().getIntervalTime() / 1000;
        break;
      case POSITION_REOPEN:
        time = radioInterface.getReOpenTime() / 1000;
        break;
      default:
        time = -1;
        break;
    }
    if (time != -1) {
      final String s = Long.toString(time);
      holder.iconText.setText(s);
    }
  }

  private void setPreference(final int i, final int position) {
    switch (position) {
      case POSITION_DELAY:
        radioInterface.setRadioDelay(DELAY_VALUES[i]);
        break;
      case POSITION_INTERVAL:
        GlobalPreferenceUtil.get().powerManagerActive().setIntervalTime(INTERVAL_VALUES[i]);
        break;
      case POSITION_REOPEN:
        radioInterface.setRadioReopen(INTERVAL_VALUES[i]);
        break;
      default:
        // nothing
    }
  }

  @Override public int getItemCount() {
    return RADIO_BUTTON_COUNT.length;
  }

  public interface RadioInterface {

    String getRadioNameString();

    void setRadioReopen(final long reopen);

    long getRadioDelay();

    void setRadioDelay(final long delay);

    long getReOpenTime();
  }

  public static final class ViewHolder extends RecyclerView.ViewHolder {

    private final TextView iconText;
    private final TextView title;
    private final LinearLayout radioGroup;

    public ViewHolder(final View itemView) {
      super(itemView);
      iconText = (TextView) itemView.findViewById(R.id.card_textimage);
      title = (TextView) itemView.findViewById(R.id.card_name);
      radioGroup = (LinearLayout) itemView.findViewById(R.id.card_radio_group);
    }
  }
}
