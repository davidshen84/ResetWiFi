package org.shen.xi.resetwifi;

import android.content.Context;
import android.net.wifi.WifiManager;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import javax.inject.Named;
import javax.inject.Singleton;

import eu.chainfire.libsuperuser.Shell.Builder;


class MainModule extends AbstractModule {
  private final Context ctx;

  public MainModule(Context context) {
    ctx = context;
  }

  @Override
  protected void configure() {
    bind(Builder.class).in(Singleton.class);

    bind(Shell.class).to(SUShell.class).in(Singleton.class);
    bind(WifiManagerWrapper.class).to(WifiManagerWrapperImpl.class).in(Singleton.class);
    bind(OSHelper.class).to(OSHelperImpl.class).in(Singleton.class);
    bind(GoogleAnalytics.class).toInstance(GoogleAnalytics.getInstance(ctx));
  }

  @Provides
  @Singleton
  private WifiManager providesWifiManager() {
    return (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
  }

  @Provides
  @Named("app tracker")
  private Tracker providesAppTracker(GoogleAnalytics googleAnalytics) {
    return googleAnalytics.newTracker(R.xml.app_tracker);
  }
}
