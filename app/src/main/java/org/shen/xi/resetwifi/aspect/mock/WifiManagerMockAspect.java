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

/**
 * Created on 2/29/2016.
 */
@Aspect
public class WifiManagerMockAspect {
  private static final String TAG = WifiManagerMockAspect.class.getSimpleName();

  @Pointcut("execution(* org.shen.xi.resetwifi.WifiManagerWrapper.*(..)) && if()")
  public static boolean tracePointcut() {
    return BuildConfig.DEBUG;
  }

  @Pointcut("execution(boolean org.shen.xi.resetwifi.WifiManagerWrapper.isOn()) && if()")
  public static boolean mockIsOn() {
    return BuildConfig.DEBUG;
  }

  @Pointcut("execution(void org.shen.xi.resetwifi.WifiManagerWrapper.*()) && if()")
  public static boolean mockOnOff() {
    return BuildConfig.DEBUG;
  }

  @Before("tracePointcut()")
  public void weaveAllPointcut(JoinPoint joinPoint) {
    Signature signature = joinPoint.getSignature();
    String classSimpleName = signature.getDeclaringType().getSimpleName();
    String methodName = signature.getName();

    Log.d(TAG, String.format("would have executed %s.%s(%s)",
      classSimpleName, methodName, Arrays.toString(joinPoint.getArgs())));
  }

  @Around("mockIsOn()")
  public boolean weaveIsOn(ProceedingJoinPoint joinPoint) {
    return true;
  }

  @Around("mockOnOff()")
  public void weaveOnOff(ProceedingJoinPoint joinPoint) {
    // nop
  }
}
