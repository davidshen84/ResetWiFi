package org.shen.xi.resetwifi;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Created by david on 2/1/2016.
 *
 * Use `su` command to get root permission
 */
public class RootProcess {
  private static final String TAG = RootProcess.class.getName();
  private static final RootProcess instance = new RootProcess();
  private OutputStreamWriter out = null;
  private BufferedReader in = null;
  private boolean hasRootPermission = false;

  private RootProcess() {
    try {
      Process process = Runtime.getRuntime().exec("su\n");
      out = new OutputStreamWriter(process.getOutputStream());
      in = new BufferedReader(new InputStreamReader((process.getInputStream())));

      out.write("id\n");
      out.flush();
      String id = in.readLine();
      hasRootPermission = id.startsWith("uid=0");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static RootProcess getInstance() {
    return instance;
  }

  /**
   * Execute a command in the root process
   *
   * @param cmd       command to execute
   * @param readStdIn read from stdin
   */
  public String execute(String cmd, boolean readStdIn) {
    if (out == null) return "";

    try {
      out.write(cmd + "\n");
      out.flush();

      return readStdIn ? in.readLine() : "";
    } catch (IOException e) {
      Log.e(TAG, String.format("cannot execute %s", cmd));
      try {
        out.close();
      } catch (IOException ignored) {
      } finally {
        out = null;
      }

      try {
        in.close();
      } catch (IOException ignored) {
      } finally {
        in = null;
      }

      return "";
    }
  }

  public boolean hasRootPermission() {
    return hasRootPermission;
  }

  @Override
  protected void finalize() throws Throwable {
    super.finalize();
    if (out != null && hasRootPermission) {
      out.write("exit\n");
      Log.d("RootProcess", "RootProcess finalize");
    }
  }
}
