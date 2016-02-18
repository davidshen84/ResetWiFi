package org.shen.xi.resetwifi;

import android.content.Context;
import android.net.wifi.WifiManager;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import org.shen.xi.resetwifi.debug.FakeRootProcess;
import org.shen.xi.resetwifi.debug.FakeWifiManagerWrapper;

import javax.inject.Named;
import javax.inject.Singleton;


public class MainModule extends AbstractModule {
  private final Context ctx;

  public MainModule(Context context) {
    ctx = context;
  }

  @Override
  protected void configure() {
    if (BuildConfig.FLAVOR.startsWith("fakeRoot")) {
      bind(RootProcess.class).to(FakeRootProcess.class);
      bind(WifiManagerWrapper.class).to(FakeWifiManagerWrapper.class);
    } else {
      bind(RootProcess.class).to(SuRootProcess.class).in(Singleton.class);
      bind(WifiManagerWrapper.class).to(WifiManagerWrapperImpl.class);
    }

    bind(GoogleAnalytics.class).toInstance(GoogleAnalytics.getInstance(ctx));
  }

  @Provides
  @Singleton
  public WifiManager provideWifiManager() {
    return (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
  }

  @Provides
  @Named("app tracker")
  public Tracker providesAppTracker(GoogleAnalytics googleAnalytics) {
    return googleAnalytics.newTracker(R.xml.app_tracker);
  }
}
