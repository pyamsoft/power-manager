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

package com.pyamsoft.powermanager.ui.trigger;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.pyamsoft.powermanager.R;
import com.pyamsoft.powermanager.backend.trigger.PowerTrigger;
import com.pyamsoft.powermanager.backend.trigger.PowerTriggerDataSource;
import com.pyamsoft.powermanager.ui.BindableRecyclerAdapter;
import com.pyamsoft.powermanager.ui.trigger.dialog.PowerTriggerDialogFragment;
import com.pyamsoft.pydroid.util.LogUtil;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class PowerTriggerAdapter
    extends BindableRecyclerAdapter<PowerTriggerAdapter.ViewHolder>
    implements PowerTriggerInterface {

  private static final int VIEW_TYPE_NORMAL = 0;
  private static final int VIEW_TYPE_ADD = 1;
  private static final String TAG = PowerTriggerAdapter.class.getSimpleName();
  private final FragmentActivity activity;
  private final FragmentManager fm;
  private final List<PowerTrigger> triggerList = new ArrayList<>();
  private final PowerTriggerPresenter presenter;

  public PowerTriggerAdapter(final AppCompatActivity activity) {
    this.activity = activity;
    fm = activity.getSupportFragmentManager();
    presenter = new PowerTriggerPresenter(activity, this);

    create();
  }

  public static void merge(final List<PowerTrigger> items) {
    final int size = items.size();
    if (size > 1) {
      LogUtil.d(TAG, "Running MERGE sort");
      merge(items, 0, size - 1);
    }
  }

  private static void merge(final List<PowerTrigger> items, final int start, final int end) {
    if (start < end) {
      final int middle = (start + end) / 2;
      merge(items, start, middle);
      merge(items, middle + 1, end);
      merge(items, start, middle, end);
    }
  }

  private static void merge(final List<PowerTrigger> items, final int start, final int middle,
      final int end) {
    final List<PowerTrigger> sortedItems = new ArrayList<>();

    int left = start;
    int right = middle + 1;

    // merge the smaller of the items until it is used up
    while (left <= middle && right <= end) {
      final PowerTrigger leftItem = items.get(left);
      final PowerTrigger rightItem = items.get(right);
      final String leftLabel = leftItem.getName();
      final String rightLabel = rightItem.getName();
      LogUtil.d(TAG, "Comparing (L)" + leftLabel + " to (R)" + rightLabel);
      if (leftLabel.compareToIgnoreCase(rightLabel) < 0) {
        sortedItems.add(leftItem);
        LogUtil.d(TAG, "Pick (L)" + leftLabel);
        ++left;
      } else {
        sortedItems.add(rightItem);
        LogUtil.d(TAG, "Pick (R)" + rightLabel);
        ++right;
      }
    }

    // merge the remaining left side
    while (left <= middle) {
      sortedItems.add(items.get(left++));
    }

    while (right <= end) {
      sortedItems.add(items.get(right++));
    }

    // Copy the items over to the original list
    int i = 0;
    int sortedIndex = start;
    final int sortedLength = sortedItems.size();
    while (i < sortedLength) {
      items.set(sortedIndex++, sortedItems.get(i++));
    }
  }

  @Override public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
    final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    final int layout = (viewType == VIEW_TYPE_NORMAL) ? R.layout.adapter_item_trigger
        : R.layout.adapter_item_add_trigger;
    final View view = inflater.inflate(layout, parent, false);
    return new ViewHolder(view, viewType);
  }

  @Override public int getItemViewType(final int position) {
    int viewType;
    if (position == 0) {
      // The Add button
      viewType = VIEW_TYPE_ADD;
    } else {
      viewType = VIEW_TYPE_NORMAL;
    }
    return viewType;
  }

  @Override public void onBindViewHolder(final ViewHolder holder, final int position) {
    // Assume triggers are sorted at this point by refreshDataSet()
    final PowerTrigger trigger = triggerList.get(position);
    // setup the viewholder
    holder.title.setText(trigger.getName());
    if (holder.addButton != null) {
      holder.addButton.setOnClickListener(new View.OnClickListener() {

        @Override public void onClick(final View v) {
          if (presenter != null) {
            presenter.onAddButtonClicked();
          }
        }
      });
      // Add button does not react to long presses
      holder.itemView.setOnLongClickListener(null);

      // Add button does not activate a trigger on press
      holder.itemView.setOnClickListener(null);
    } else {
      holder.itemView.setLongClickable(true);
      holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {

        @Override public boolean onLongClick(final View v) {
          presenter.onLongClick(trigger);
          return true;
        }
      });
    }
  }

  @Override public int getItemCount() {
    return triggerList.size();
  }

  @Override public final void refreshDataSet(final Context context) {
    // sorts the triggers/triggerNames list and then calls notifyDataSetChanged
    // merge sort
    // before the merge, set the placeholder (ID -1) to the largest ID possible
    // which is size + 1;
    //        merge(triggers);
    triggerList.clear();
    final Set<PowerTrigger> triggers = PowerTriggerDataSource.TriggerSet.with(context).asSet();
    for (final PowerTrigger trigger : triggers) {
      triggerList.add(trigger);
    }

    merge(triggerList);
    notifyDataSetChanged();
  }

  @Override public void onCreateDialogFragment(PowerTriggerDialogFragment fragment) {
    if (fragment != null) {
      fragment.setContext(activity);
      fragment.setParentAdapter(this);
      fragment.show(fm, null);
    }
  }

  @Override public void onItemRemoved(PowerTrigger removed) {
    final int index = triggerList.indexOf(removed);
    if (index == PowerTriggerDataSource.TriggerSet.NO_INDEX) {
      return;
    }
    triggerList.remove(removed);
    notifyItemRemoved(index);
  }

  @Override public void onCreateDeleteWarningDialog(final PowerTrigger trigger) {
    final AlertDialog.Builder builder = new AlertDialog.Builder(activity).setCancelable(true);
    builder.setMessage("Really delete trigger: [" + trigger.getId() + "] " +
        trigger.getName() + "?")
        .setTitle("Delete Trigger")
        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
          private WeakReference<PowerTrigger> weakTrigger = new WeakReference<>(trigger);
          private WeakReference<PowerTriggerPresenter> weakPresenter =
              new WeakReference<>(presenter);

          @Override public void onClick(DialogInterface dialog, int which) {
            final PowerTriggerPresenter p = weakPresenter.get();
            if (p != null) {
              final PowerTrigger t = weakTrigger.get();
              if (t != null) {
                p.onItemRemoveClicked(t);
              }
            }
          }
        })
        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
          @Override public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
          }
        });

    final AlertDialog dialog = builder.create();

    if (dialog != null) {
      dialog.show();
    }
  }

  @Override public int getStatusbarColor() {
    return R.color.yellow700;
  }

  @Override public int getToolbarColor() {
    return R.color.yellow500;
  }

  @Override protected void onCreate() {
  }

  @Override protected void onDestroy() {
  }

  @Override protected void onStart() {

  }

  @Override protected void onStop() {

  }

  public static final class ViewHolder extends RecyclerView.ViewHolder {

    private final TextView title;
    private final ImageView addButton;

    public ViewHolder(final View itemView, final int viewType) {
      super(itemView);
      if (viewType == VIEW_TYPE_ADD) {
        title = (TextView) itemView.findViewById(R.id.add_new_trigger_text);
        addButton = (ImageView) itemView.findViewById(R.id.add_new_trigger_image);
      } else {
        title = (TextView) itemView.findViewById(R.id.trigger_title);
        addButton = null;
      }
    }
  }
}
