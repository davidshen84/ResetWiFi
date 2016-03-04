package org.shen.xi.resetwifi;

import android.app.Application;

import com.google.android.gms.analytics.Tracker;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;

import java.lang.ref.WeakReference;

/**
 * Created by on 3/1/2016.
 */
public class MainApplication extends Application {

  private WeakReference<Injector> injectorWeakReference;

  synchronized public Injector getInjector() {
    if (injectorWeakReference == null || injectorWeakReference.get() == null)
      injectorWeakReference = new WeakReference<>(Guice.createInjector(new MainModule(this)));

    return injectorWeakReference.get();
  }

  /**
   * Get the app {@link Tracker} for {@link MainApplication}
   *
   * @return tracker
   */
  public Tracker getAppTracker() {
    Key<Tracker> key = Key.get(Tracker.class, Names.named("app tracker"));

    return getInjector().getInstance(key);
  }
}
