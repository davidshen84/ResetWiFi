package org.shen.xi.resetwifi;

import com.google.inject.AbstractModule;

public class MainModule extends AbstractModule {
  @Override
  protected void configure() {
    if (BuildConfig.DEBUG) {
      bind(RootProcess.class).to(FakeRootProcess.class);
    } else {
      bind(RootProcess.class).to(SuRootProcess.class);
    }
  }
}
