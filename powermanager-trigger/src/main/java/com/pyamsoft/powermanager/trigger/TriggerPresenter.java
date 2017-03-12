/*
 * Copyright 2016 Peter Kenji Yamanaka
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

package com.pyamsoft.powermanager.trigger;

import android.database.sqlite.SQLiteConstraintException;
import android.support.annotation.NonNull;
import com.pyamsoft.powermanager.model.sql.PowerTriggerEntry;
import com.pyamsoft.pydroid.helper.DisposableHelper;
import com.pyamsoft.pydroid.presenter.Presenter;
import com.pyamsoft.pydroid.presenter.SchedulerPresenter;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import javax.inject.Inject;
import javax.inject.Named;
import timber.log.Timber;

class TriggerPresenter extends SchedulerPresenter<Presenter.Empty> {

  @NonNull private final TriggerInteractor interactor;
  @NonNull private Disposable deleteDisposable = Disposables.empty();
  @NonNull private Disposable viewDisposable = Disposables.empty();
  @NonNull private Disposable createDisposable = Disposables.empty();
  @NonNull private Disposable updateDisposable = Disposables.empty();

  @Inject TriggerPresenter(@NonNull @Named("obs") Scheduler obsScheduler,
      @NonNull @Named("sub") Scheduler subScheduler, @NonNull TriggerInteractor interactor) {
    super(obsScheduler, subScheduler);
    this.interactor = interactor;
  }

  @Override protected void onUnbind() {
    super.onUnbind();
    deleteDisposable = DisposableHelper.unsubscribe(deleteDisposable);
    createDisposable = DisposableHelper.unsubscribe(createDisposable);
    updateDisposable = DisposableHelper.unsubscribe(updateDisposable);
    viewDisposable = DisposableHelper.unsubscribe(viewDisposable);
  }

  public void loadTriggerView(@NonNull TriggerLoadCallback callback, boolean forceRefresh) {
    viewDisposable = DisposableHelper.unsubscribe(viewDisposable);
    viewDisposable = interactor.queryAll(forceRefresh)
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .doAfterTerminate(callback::onTriggerLoadFinished)
        .subscribe(callback::onTriggerLoaded, throwable -> {
          Timber.e(throwable, "onError");
        });
  }

  public void showNewTriggerDialog(@NonNull ShowTriggerDialogCallback callback) {
    callback.onShowNewTriggerDialog();
  }

  public void createPowerTrigger(@NonNull PowerTriggerEntry entry,
      @NonNull TriggerCreateCallback callback) {
    Timber.d("Create new power trigger");
    createDisposable = DisposableHelper.unsubscribe(createDisposable);
    createDisposable = interactor.put(entry)
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(callback::onNewTriggerAdded, throwable -> {
          Timber.e(throwable, "onError");
          if (throwable instanceof SQLiteConstraintException) {
            Timber.e("Error inserting into DB");
            callback.onNewTriggerInsertError();
          } else {
            Timber.e("Issue creating trigger");
            callback.onNewTriggerCreateError();
          }
        });
  }

  public void deleteTrigger(int percent, @NonNull TriggerDeleteCallback callback) {
    deleteDisposable = DisposableHelper.unsubscribe(deleteDisposable);
    deleteDisposable = interactor.delete(percent)
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(callback::onTriggerDeleted, throwable -> {
          Timber.e(throwable, "onError");
        });
  }

  public void toggleEnabledState(int position, @NonNull PowerTriggerEntry entry, boolean enabled,
      @NonNull TriggerToggleCallback callback) {
    updateDisposable = DisposableHelper.unsubscribe(updateDisposable);
    updateDisposable = interactor.update(entry, enabled)
        .subscribeOn(getSubscribeScheduler())
        .observeOn(getObserveScheduler())
        .subscribe(result -> callback.updateViewHolder(position, result), throwable -> {
          Timber.e(throwable, "onError");
        });
  }

  interface TriggerLoadCallback {

    void onTriggerLoaded(@NonNull PowerTriggerEntry entry);

    void onTriggerLoadFinished();
  }

  interface ShowTriggerDialogCallback {
    void onShowNewTriggerDialog();
  }

  interface TriggerDeleteCallback {

    void onTriggerDeleted(int position);
  }

  interface TriggerCreateCallback {

    void onNewTriggerAdded(@NonNull PowerTriggerEntry entry);

    void onNewTriggerCreateError();

    void onNewTriggerInsertError();
  }

  interface TriggerToggleCallback {

    void updateViewHolder(int position, @NonNull PowerTriggerEntry entry);
  }
}
