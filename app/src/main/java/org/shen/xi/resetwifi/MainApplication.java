package org.shen.xi.resetwifi;

import android.app.Application;
import android.content.Context;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

public class MainApplication extends Application {

  private GoogleAnalytics googleAnalytics;

  public void initializeContext(Context context) {
    // initialize analytics
    googleAnalytics = GoogleAnalytics.getInstance(context);
    googleAnalytics.setDryRun(BuildConfig.DEBUG);
  }

  public Tracker getAppTracker() {
    return googleAnalytics.newTracker(R.xml.app_tracker);
  }
}
