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
  private final Context context;

  public MainModule(Context context) {
    this.context = context;
  }

  @Override
  protected void configure() {
    bind(Builder.class);
    bind(Shell.class).to(SUShell.class).in(Singleton.class);
    bind(OSHelper.class).to(OSHelperImpl.class).in(Singleton.class);
    bind(WifiManagerWrapper.class).to(WifiManagerWrapperImpl.class).in(Singleton.class);
    bind(PermissionManager.class).to(PermissionManagerImpl.class).in(Singleton.class);
    bind(Context.class).toInstance(context);
  }

  @Provides
  @Singleton
  private WifiManager providesWifiManager() {
    return (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
  }

  @Provides
  @Singleton
  private GoogleAnalytics providesGoogleAnalytics() {
    GoogleAnalytics instance = GoogleAnalytics.getInstance(context);
    instance.setDryRun(BuildConfig.DEBUG);

    return instance;
  }

  @Provides
  @Named("app tracker")
  private Tracker providesAppTracker(GoogleAnalytics googleAnalytics) {
    return googleAnalytics.newTracker(R.xml.app_tracker);
  }
}
