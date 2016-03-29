package org.shen.xi.resetwifi;

import android.content.Context;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;

public class PermissionManagerUnitTest {

  private Injector injector;
  private PermissionManager permissionManager;

  @Before
  public void before() {
    Context context = mock(Context.class);

    AbstractModule module = new MainModule(context);
    injector = Guice.createInjector(module);
    permissionManager = injector.getInstance(PermissionManager.class);
  }

  @Test
  public void resolve() throws Exception {
    Assert.assertNotNull(permissionManager);
  }
}
