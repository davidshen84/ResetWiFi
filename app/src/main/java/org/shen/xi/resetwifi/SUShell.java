package org.shen.xi.resetwifi;


import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import eu.chainfire.libsuperuser.Shell.Builder;
import eu.chainfire.libsuperuser.Shell.Interactive;
import eu.chainfire.libsuperuser.Shell.OnCommandResultListener;

/**
 * Created by on 2/23/2016.
 */
public class SUShell implements Shell {


  private Interactive interactive;
  private Builder builder;

  @Inject
  public SUShell(Builder builder) {
    this.builder = builder;
  }

  /**
   * Always open shell with SU privilege
   */
  @Override
  public void open() {
    interactive = builder.useSU().open();
  }

  @Override
  public boolean hasPrivilege() {
    return eu.chainfire.libsuperuser.Shell.SU.available();
  }

  @Override
  public void execute(List<String> commands, OnCommandResultListener listener) {
    interactive.addCommand(commands, -1, listener);
  }

  @Override
  public void close() throws IOException {
    interactive.close();
  }
}
