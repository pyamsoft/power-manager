package com.pyamsoft.powermanager.ui;

public abstract class ValueRunnable<T> implements Runnable {

  private T value;

  public final void run(final T newValue) {
    setValue(newValue);
    run();
  }

  public final T getValue() {
    return value;
  }

  public final void setValue(final T value) {
    this.value = value;
  }
}
