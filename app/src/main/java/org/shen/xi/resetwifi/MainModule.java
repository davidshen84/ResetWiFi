package org.shen.xi.resetwifi;

import android.content.Context;
import android.net.wifi.WifiManager;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import static android.content.Context.WIFI_SERVICE;

public class MainModule extends AbstractModule {
  private final Context ctx;

  public MainModule(Context context) {
    ctx = context;
  }

  @Override
  protected void configure() {
    if (BuildConfig.DEBUG) {
      bind(RootProcess.class).to(FakeRootProcess.class);
    } else {
      bind(RootProcess.class).to(SuRootProcess.class).in(Singleton.class);
    }

    bind(GoogleAnalytics.class).toInstance(GoogleAnalytics.getInstance(ctx));
  }

  @Provides
  @Singleton
  public WifiManager provideWifiManager() {
    return (WifiManager) ctx.getSystemService(WIFI_SERVICE);
  }

  @Provides
  @Named("app tracker")
  public Tracker providesAppTracker(GoogleAnalytics googleAnalytics) {
    return googleAnalytics.newTracker(R.xml.app_tracker);
  }
}
