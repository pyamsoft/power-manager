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

package com.pyamsoft.powermanager.ui.radio;

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
import com.pyamsoft.powermanager.ui.BindableRecyclerAdapter;
import com.pyamsoft.pydroid.base.ValueRunnableBase;
import com.pyamsoft.pydroid.util.StringUtil;

public final class RadioContentAdapter
    extends BindableRecyclerAdapter<RadioContentAdapter.ViewHolder> implements RadioInterface {

  public static final long[] DELAY_VALUES = {
      ActiveService.Constants.DELAY_RADIO_NONE, ActiveService.Constants.DELAY_RADIO_FIVE,
      ActiveService.Constants.DELAY_RADIO_TEN, ActiveService.Constants.DELAY_RADIO_FIFTEEN,
      ActiveService.Constants.DELAY_RADIO_THIRTY, ActiveService.Constants.DELAY_RADIO_FOURTYFIVE,
      ActiveService.Constants.DELAY_RADIO_SIXTY,
  };

  public static final int[] DELAY_RESID = {
      R.string.delay_none, R.string.delay_five, R.string.delay_ten, R.string.delay_fifteen,
      R.string.delay_thirty, R.string.delay_fourtyfive, R.string.delay_sixty,
  };

  public static final long[] INTERVAL_VALUES = {
      ActiveService.Constants.INTERVAL_REOPEN_FIFTEEN,
      ActiveService.Constants.INTERVAL_REOPEN_THIRTY,
      ActiveService.Constants.INTERVAL_REOPEN_FOURTYFIVE,
      ActiveService.Constants.INTERVAL_REOPEN_SIXTY, ActiveService.Constants.INTERVAL_REOPEN_NINTY,
      ActiveService.Constants.INTERVAL_REOPEN_ONETWENTY,
  };

  public static final int[] INTERVAL_RESID = {
      R.string.delay_fifteen, R.string.delay_thirty, R.string.delay_fourtyfive,
      R.string.delay_sixty, R.string.delay_ninty, R.string.delay_twomin,
  };
  // TODO REOPEN_RESID REOPEN_VALUES
  public static final int NUMBER_ITEMS = 3;
  private static final String TAG = RadioContentAdapter.class.getSimpleName();
  private static final int POSITION_DELAY = 0;
  private static final int POSITION_INTERVAL = 1;
  private static final int POSITION_REOPEN = 2;
  private final RadioBase radio;
  private final RadioPresenter presenter;
  private final Context context;

  public RadioContentAdapter(final Context context, final RadioBase i) {
    this.radio = i;
    this.context = context;
    presenter = new RadioPresenter();
    bind();
  }

  @Override public void onDelayTimeChanged() {
    notifyItemChanged(POSITION_DELAY);
  }

  @Override public void onIntervalTimeChanged() {
    notifyItemChanged(POSITION_INTERVAL);
  }

  @Override public void onReOpenTimeChanged() {
    notifyItemChanged(POSITION_REOPEN);
  }

  @Override protected void onBind() {
    presenter.bind(context, this);
  }

  @Override protected void onUnbind() {
    presenter.unbind();
  }

  @Override public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
    final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    final View view = inflater.inflate(R.layout.adapter_item_radiogroup, parent, false);
    return new ViewHolder(view);
  }

  private int getButtonCount(final int position) {
    int buttonCount;
    switch (position) {
      case POSITION_DELAY:
        buttonCount = DELAY_VALUES.length;
        break;
      case POSITION_INTERVAL:
        buttonCount = INTERVAL_VALUES.length;
        break;
      case POSITION_REOPEN:
        // TODO REOPEN
        buttonCount = INTERVAL_VALUES.length;
        break;
      default:
        buttonCount = 0;
    }
    return buttonCount;
  }

  private void getTimeAndClickTarget(final ValueHolder values, final int position) {
    long realTime;
    ValueRunnableBase<Long> onClick;
    switch (radio.getName()) {
      case RadioContentInterface.WIFI:
        switch (position) {
          case POSITION_DELAY:
            realTime = presenter.getDelayTimeWifi();
            onClick = new ValueRunnableBase<Long>() {
              @Override public void run() {
                presenter.setDelayTimeWifi(getValue());
              }
            };
            break;
          case POSITION_INTERVAL:
            realTime = presenter.getIntervalTimeWifi();
            onClick = new ValueRunnableBase<Long>() {
              @Override public void run() {
                presenter.setIntervalTimeWifi(getValue());
              }
            };
            break;
          case POSITION_REOPEN:
            realTime = presenter.getReOpenTimeWifi();
            onClick = new ValueRunnableBase<Long>() {
              @Override public void run() {
                presenter.setReOpenTimeWifi(getValue());
              }
            };
            break;
          default:
            realTime = 1;
            onClick = null;
        }
        break;
      case RadioContentInterface.DATA:
        switch (position) {
          case POSITION_DELAY:
            realTime = presenter.getDelayTimeData();
            onClick = new ValueRunnableBase<Long>() {
              @Override public void run() {
                presenter.setDelayTimeData(getValue());
              }
            };
            break;
          case POSITION_INTERVAL:
            realTime = presenter.getIntervalTimeData();
            onClick = new ValueRunnableBase<Long>() {
              @Override public void run() {
                presenter.setIntervalTimeData(getValue());
              }
            };
            break;
          case POSITION_REOPEN:
            realTime = presenter.getReOpenTimeData();
            onClick = new ValueRunnableBase<Long>() {
              @Override public void run() {
                presenter.setReOpenTimeData(getValue());
              }
            };
            break;
          default:
            realTime = 1;
            onClick = null;
        }
        break;
      case RadioContentInterface.BLUETOOTH:
        switch (position) {
          case POSITION_DELAY:
            realTime = presenter.getDelayTimeBluetooth();
            onClick = new ValueRunnableBase<Long>() {
              @Override public void run() {
                presenter.setDelayTimeBluetooth(getValue());
              }
            };
            break;
          case POSITION_INTERVAL:
            realTime = presenter.getIntervalTimeBluetooth();
            onClick = new ValueRunnableBase<Long>() {
              @Override public void run() {
                presenter.setIntervalTimeBluetooth(getValue());
              }
            };
            break;
          case POSITION_REOPEN:
            realTime = presenter.getReOpenTimeBluetooth();
            onClick = new ValueRunnableBase<Long>() {
              @Override public void run() {
                presenter.setReOpenTimeBluetooth(getValue());
              }
            };
            break;
          default:
            realTime = 1;
            onClick = null;
        }
        break;
      case RadioContentInterface.SYNC:
        switch (position) {
          case POSITION_DELAY:
            realTime = presenter.getDelayTimeSync();
            onClick = new ValueRunnableBase<Long>() {
              @Override public void run() {
                presenter.setDelayTimeSync(getValue());
              }
            };
            break;
          case POSITION_INTERVAL:
            realTime = presenter.getIntervalTimeSync();
            onClick = new ValueRunnableBase<Long>() {
              @Override public void run() {
                presenter.setIntervalTimeSync(getValue());
              }
            };
            break;
          case POSITION_REOPEN:
            realTime = presenter.getReOpenTimeSync();
            onClick = new ValueRunnableBase<Long>() {
              @Override public void run() {
                presenter.setReOpenTimeSync(getValue());
              }
            };
            break;
          default:
            realTime = 1;
            onClick = null;
        }
        break;
      default:
        realTime = 1;
        onClick = null;
    }
    values.realTime = realTime;
    values.onClick = onClick;
  }

  private void fillViewHolderWithRadioButtons(final ValueHolder values, final ViewHolder holder,
      final int position) {

    final Context context = holder.itemView.getContext();
    final LayoutInflater inflater = LayoutInflater.from(context);

    final int buttonCount = getButtonCount(position);
    getTimeAndClickTarget(values, position);
    holder.radioGroup.removeAllViews();

    // TODO This is super fucking ugly

    for (int i = 0; i < buttonCount; ++i) {
      String buttonName;
      final long expectedTime;
      switch (position) {
        case POSITION_DELAY:
          buttonName = context.getString(DELAY_RESID[i]);
          expectedTime = DELAY_VALUES[i];
          break;
        case POSITION_INTERVAL:
          buttonName = context.getString(INTERVAL_RESID[i]);
          expectedTime = INTERVAL_VALUES[i];
          break;
        case POSITION_REOPEN:
          // TODO REOPEN
          buttonName = context.getString(INTERVAL_RESID[i]);
          expectedTime = INTERVAL_VALUES[i];
          break;
        default:
          buttonName = null;
          expectedTime = 0;
      }

      final CheckedTextView chtv =
          (CheckedTextView) inflater.inflate(R.layout.layout_checkedtext_single, holder.radioGroup,
              false);
      chtv.setText(buttonName);
      chtv.setChecked(expectedTime == values.realTime);
      chtv.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          if (values.onClick != null) {
            values.onClick.run(expectedTime);
          }
        }
      });
      holder.radioGroup.addView(chtv);
    }
  }

  @Override public void onBindViewHolder(final ViewHolder holder, final int position) {
    final ValueHolder values = new ValueHolder();
    fillViewHolderWithRadioButtons(values, holder, position);
    setCardNameAndIcon(values, holder, position);
  }

  public Spannable getDelayName() {
    String str;
    String explain;
    String s;
    String ss;
    final String name = radio.getName();
    s = context.getString(R.string.radio_delay);
    str = StringUtil.formatString(s, name) + "\n";
    ss = context.getString(R.string.radio_delay_explain);
    explain = StringUtil.formatString(ss, name);
    return createSpannable(context, str, explain);
  }

  public Spannable getIntervalName() {
    String str;
    String explain;
    String s;
    final String name = radio.getName();
    str = context.getString(R.string.reopen_interval_delay) + "\n";
    s = context.getString(R.string.reopen_interval_delay_explain);
    explain = StringUtil.formatString(s, name, name);
    return createSpannable(context, str, explain);
  }

  public Spannable getReOpenName() {
    String str;
    String explain;
    String s;
    str = context.getString(R.string.reopen_interval_duration) + "\n";
    s = context.getString(R.string.reopen_interval_duration_explain);
    explain = StringUtil.formatString(s, radio.getName());
    return createSpannable(context, str, explain);
  }

  private Spannable createSpannable(final Context context, final String str, final String explain) {
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

      return span;
    } else {
      return null;
    }
  }

  private void setCardNameAndIcon(final ValueHolder values, final ViewHolder holder,
      final int position) {
    Spannable span;
    switch (position) {
      case POSITION_DELAY:
        span = getDelayName();
        break;
      case POSITION_INTERVAL:
        span = getIntervalName();
        break;
      case POSITION_REOPEN:
        span = getReOpenName();
        break;
      default:
        span = null;
    }
    holder.title.setText(span);
    holder.iconText.setText(String.format("%s", Long.toString(values.realTime / 1000)));
  }

  @Override public int getItemCount() {
    return NUMBER_ITEMS;
  }

  @Override public int getStatusbarColor() {
    return radio.getStatusbarColor();
  }

  @Override public int getToolbarColor() {
    return radio.getToolbarColor();
  }

  public static final class ValueHolder {
    private long realTime;
    private ValueRunnableBase<Long> onClick;
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
