package org.shen.xi.resetwifi.aspect.mock;

import android.util.Log;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.shen.xi.resetwifi.BuildConfig;

import java.util.Arrays;
import java.util.Collections;

import eu.chainfire.libsuperuser.Shell.OnCommandResultListener;

/**
 * Created on 2/24/2016.
 * <p/>
 * Mock functions on OSHelper when testing/debugging
 */
@Aspect
public class OSHelperMockAspect {

  private static final String TAG = OSHelperMockAspect.class.getSimpleName();

  @Pointcut("execution(* org.shen.xi.resetwifi.OSHelper.*(..)) && if()")
  public static boolean tracePointcut() {
    return BuildConfig.DEBUG;
  }

  @Pointcut("execution(void org.shen.xi.resetwifi.OSHelper.hasNetworkHistoryTxt(eu.chainfire.libsuperuser.Shell.OnCommandResultListener)) && if()")
  public static boolean mockHasNetworkHistoryTxt() {
    return BuildConfig.DEBUG;
  }

  @Pointcut("execution(void org.shen.xi.resetwifi.OSHelper.removeNetworkHistoryTxt(eu.chainfire.libsuperuser.Shell.OnCommandResultListener)) && if()")
  public static boolean mockRemoveNetworkHistoryTxt() {
    return BuildConfig.DEBUG;
  }

  @Pointcut("execution(boolean org.shen.xi.resetwifi.OSHelper.hasPrivilege()) && if()")
  public static boolean mockHasPrivilege() {
    return BuildConfig.DEBUG;
  }

  @Pointcut("execution(void org.shen.xi.resetwifi.OSHelper.*()) && if()")
  public static boolean mockOpenClose() {
    return BuildConfig.DEBUG;
  }

  @Before("tracePointcut()")
  public void weaveAllPointcut(JoinPoint joinPoint) {
    Signature signature = joinPoint.getSignature();
    String classSimpleName = signature.getDeclaringType().getSimpleName();
    String methodName = signature.getName();

    Log.d(TAG, String.format("would have executed %s.%s(%s)", classSimpleName, methodName, Arrays.toString(joinPoint.getArgs())));
  }

  @Around("mockHasNetworkHistoryTxt() || mockRemoveNetworkHistoryTxt()")
  public void weaveOSOperation(ProceedingJoinPoint joinPoint) {
    // handle the callback
    ((OnCommandResultListener) joinPoint.getArgs()[0]).onCommandResult(0, 0, Collections.singletonList("true"));
  }

  @Around("mockHasPrivilege()")
  public boolean weaveHasPrivilege(ProceedingJoinPoint joinPoint) throws Throwable {
    return BuildConfig.hasPrivilege;
  }

  @Around("mockOpenClose()")
  public void weaveOpenClose(ProceedingJoinPoint joinPoint) {
    // nop
  }
}
