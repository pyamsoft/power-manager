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

package com.pyamsoft.powermanager.backend.trigger;

public final class PowerTrigger {

  public static final int ENABLED = 1;
  public static final int DISABLED = 0;
  public static final int AVAILABLE = 1;
  public static final int UNAVAILABLE = 0;
  public static final int TOGGLE_STATE_OFF = 1;
  public static final int TOGGLE_STATE_ON = 2;
  private static final int TOGGLE_STATE_NONE = 0;
  private final int id;
  private String name;
  private int level;
  private int manageWifi;
  private int manageData;
  private int manageBluetooth;
  private int manageSync;
  private int stateWifi;
  private int stateData;
  private int stateBluetooth;
  private int stateSync;
  private int reopenWifi;
  private int reopenData;
  private int reopenBluetooth;
  private int reopenSync;
  private int autoBrightness;
  private int brightnessLevel;
  private int volume;
  private int enabled;
  private int available;

  public PowerTrigger(final int id, final String name, final int level) {
    this.id = id;
    this.name = name;
    this.level = level;
    this.enabled = ENABLED;
    this.manageWifi = DISABLED;
    this.manageData = DISABLED;
    this.manageBluetooth = DISABLED;
    this.manageSync = DISABLED;
    this.stateWifi = TOGGLE_STATE_NONE;
    this.stateData = TOGGLE_STATE_NONE;
    this.stateBluetooth = TOGGLE_STATE_NONE;
    this.stateSync = TOGGLE_STATE_NONE;
    this.reopenWifi = DISABLED;
    this.reopenData = DISABLED;
    this.reopenBluetooth = DISABLED;
    this.reopenSync = DISABLED;
    this.autoBrightness = DISABLED;
    this.brightnessLevel = DISABLED;
    this.volume = DISABLED;
    this.available = AVAILABLE;
  }

  public final int getId() {
    return id;
  }

  public final int getAutoBrightness() {
    return autoBrightness;
  }

  public final void setAutoBrightness(final int autoBrightness) {
    this.autoBrightness = autoBrightness;
  }

  public final int getManageBluetooth() {
    return manageBluetooth;
  }

  public final void setManageBluetooth(final int manageBluetooth) {
    this.manageBluetooth = manageBluetooth;
  }

  public final int getManageData() {
    return manageData;
  }

  public final void setManageData(final int manageData) {
    this.manageData = manageData;
  }

  public final int getManageSync() {
    return manageSync;
  }

  public final void setManageSync(final int manageSync) {
    this.manageSync = manageSync;
  }

  public final int getManageWifi() {
    return manageWifi;
  }

  public final void setManageWifi(final int manageWifi) {
    this.manageWifi = manageWifi;
  }

  public final int getStateBluetooth() {
    return stateBluetooth;
  }

  public final int getStateData() {
    return stateData;
  }

  public final int getStateSync() {
    return stateSync;
  }

  public final int getStateWifi() {
    return stateWifi;
  }

  public final void setStateNoneWifi() {
    this.stateWifi = TOGGLE_STATE_NONE;
  }

  public final void setStateOffWifi() {
    this.stateWifi = TOGGLE_STATE_OFF;
  }

  public final void setStateOnWifi() {
    this.stateWifi = TOGGLE_STATE_ON;
  }

  public final void setStateNoneData() {
    this.stateWifi = TOGGLE_STATE_NONE;
  }

  public final void setStateOffData() {
    this.stateWifi = TOGGLE_STATE_OFF;
  }

  public final void setStateOnData() {
    this.stateWifi = TOGGLE_STATE_ON;
  }

  public final void setStateNoneBluetooth() {
    this.stateWifi = TOGGLE_STATE_NONE;
  }

  public final void setStateOffBluetooth() {
    this.stateWifi = TOGGLE_STATE_OFF;
  }

  public final void setStateOnBluetooth() {
    this.stateWifi = TOGGLE_STATE_ON;
  }

  public final void setStateNoneSync() {
    this.stateWifi = TOGGLE_STATE_NONE;
  }

  public final void setStateOffSync() {
    this.stateWifi = TOGGLE_STATE_OFF;
  }

  public final void setStateOnSync() {
    this.stateWifi = TOGGLE_STATE_ON;
  }

  public final int getBrightnessLevel() {
    return brightnessLevel;
  }

  public final void setBrightnessLevel(final int brightnessLevel) {
    this.brightnessLevel = brightnessLevel;
  }

  public final int getLevel() {
    return level;
  }

  public final int getVolume() {
    return volume;
  }

  public final void setVolume(final int volume) {
    this.volume = volume;
  }

  public final String getName() {
    return name;
  }

  public final int getEnabled() {
    return enabled;
  }

  public final void setEnabled(final int enabled) {
    this.enabled = enabled;
  }

  public final int getAvailable() {
    return available;
  }

  public final void setAvailable(final int available) {
    this.available = available;
  }

  public int getReopenBluetooth() {
    return reopenBluetooth;
  }

  public void setReopenBluetooth(final int reopenBluetooth) {
    this.reopenBluetooth = reopenBluetooth;
  }

  public int getReopenData() {
    return reopenData;
  }

  public void setReopenData(final int reopenData) {
    this.reopenData = reopenData;
  }

  public int getReopenSync() {
    return reopenSync;
  }

  public void setReopenSync(final int reopenSync) {
    this.reopenSync = reopenSync;
  }

  public int getReopenWifi() {
    return reopenWifi;
  }

  public void setReopenWifi(final int reopenWifi) {
    this.reopenWifi = reopenWifi;
  }

  public final void adopt(final PowerTrigger from) {
    name = from.name;
    level = from.level;
    manageWifi = from.manageWifi;
    manageData = from.manageData;
    manageBluetooth = from.manageBluetooth;
    manageSync = from.manageSync;
    stateWifi = from.stateWifi;
    stateData = from.stateData;
    stateBluetooth = from.stateBluetooth;
    stateSync = from.stateSync;
    reopenWifi = from.reopenWifi;
    reopenData = from.reopenData;
    reopenBluetooth = from.reopenBluetooth;
    reopenSync = from.reopenSync;
    autoBrightness = from.autoBrightness;
    brightnessLevel = from.brightnessLevel;
    volume = from.volume;
    enabled = from.enabled;
    available = from.available;
  }

  @Override public final String toString() {
    return String.valueOf(id) + " " + name + " " + String.valueOf(level) +
        " " + String.valueOf(manageWifi) + " " + String.valueOf(manageData) + " " +
        String.valueOf(manageBluetooth) + " " +
        String.valueOf(manageSync) + " " + String.valueOf(enabled == ENABLED) + " " +
        String.valueOf(available == ENABLED);
  }
}
