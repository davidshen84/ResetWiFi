package org.shen.xi.resetwifi;


import android.net.wifi.WifiManager;

import javax.inject.Inject;

/**
 * Created on 2/18/2016.
 * <p/>
 * wrappers android.net.wifi.WifiManager
 */
class WifiManagerWrapperImpl implements WifiManagerWrapper {

  private final WifiManager wifiManager;

  @Inject
  public WifiManagerWrapperImpl(WifiManager wifiManager) {
    this.wifiManager = wifiManager;
  }

  @Override
  public boolean isOn() {
    return wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED
      || wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING;
  }

  @Override
  public void on() {
    wifiManager.setWifiEnabled(true);
  }

  @Override
  public void off() {
    wifiManager.setWifiEnabled(false);
  }
}
