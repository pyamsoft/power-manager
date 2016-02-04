package com.pyamsoft.powermanager.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.pyamsoft.powermanager.R;
import java.lang.ref.WeakReference;

public final class ExplanationDialog {

  private AlertDialog dialog;
  private View explainView;
  private TextView explainText;

  protected ExplanationDialog(final AlertDialog dialog) {
    this.dialog = dialog;
  }

  @SuppressLint("InflateParams")
  public static ExplanationDialog createDialog(final Context context) {
    final AlertDialog.Builder builder = new AlertDialog.Builder(context).setCancelable(true);
    final LayoutInflater inflater = LayoutInflater.from(context);

    // Alert dialog, we must pass null as container
    final View view = inflater.inflate(R.layout.layout_explain, null, false);

    // Set the view as the alert dialog view
    final AlertDialog alert = builder.setView(view).create();
    final ExplanationDialog dialog = new ExplanationDialog(alert);

    // Tie the dialog parts to the class
    dialog.explainView = view;
    dialog.explainText = (TextView) dialog.explainView.findViewById(R.id.explain_text);
    final View explainRead = dialog.explainView.findViewById(R.id.explain_read);
    explainRead.setOnClickListener(new View.OnClickListener() {

      private WeakReference<AlertDialog> weakDialog = new WeakReference<>(alert);

      @Override public void onClick(View v) {
        final AlertDialog d = weakDialog.get();
        if (d != null) {
          d.dismiss();
        }
      }
    });

    return dialog;
  }

  public void show() {
    if (dialog != null) {
      if (!dialog.isShowing()) {
        dialog.show();
      }
    }
  }

  public void dismiss() {
    if (dialog != null) {
      if (dialog.isShowing()) {
        dialog.dismiss();
      }
    }
  }

  public ExplanationDialog setText(final Spannable explanation) {
    if (explainText != null) {
      explainText.setText(explanation);
    }
    return this;
  }

  public ExplanationDialog setBackgroundColor(final int color) {
    if (explainView != null) {
      explainView.setBackgroundColor(ContextCompat.getColor(dialog.getContext(), color));
    }
    return this;
  }
}
