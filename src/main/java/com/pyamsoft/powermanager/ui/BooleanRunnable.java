package com.pyamsoft.powermanager.ui;

public abstract class BooleanRunnable implements Runnable {

  private boolean state;

  public final void run(final boolean newState) {
    setState(newState);
    run();
  }

  public final boolean isState() {
    return state;
  }

  public final void setState(boolean state) {
    this.state = state;
  }
}
