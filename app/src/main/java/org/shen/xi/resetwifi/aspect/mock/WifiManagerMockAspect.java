package org.shen.xi.resetwifi.aspect.mock;

import android.util.Log;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

import java.util.Arrays;

/**
 * Created on 2/29/2016.
 */
//@Aspect
public class WifiManagerMockAspect {
  private static final String TAG = WifiManagerMockAspect.class.getSimpleName();

  @Pointcut("execution(* org.shen.xi.resetwifi.WifiManagerWrapper.*(..))")
  public void allPointcut() {
  }

  @Before("allPointcut()")
  public void weaveAllPointcut(JoinPoint joinPoint) {
    Signature signature = joinPoint.getSignature();
    String classSimpleName = signature.getDeclaringType().getSimpleName();
    String methodName = signature.getName();

    Log.d(TAG, String.format("would have executed %s.%s(%s)", classSimpleName, methodName, Arrays.toString(joinPoint.getArgs())));
  }

  @Pointcut("execution(boolean org.shen.xi.resetwifi.WifiManagerWrapper.isOn())")
  public void mockIsOn() {
  }

  @Around("mockIsOn()")
  public boolean weaveIsOn(ProceedingJoinPoint joinPoint) {
    return true;
  }

  @Pointcut("execution(void org.shen.xi.resetwifi.WifiManagerWrapper.*())")
  public void mockOnOff() {
  }

  @Around("mockOnOff()")
  public void weaveOnOff(ProceedingJoinPoint joinPoint) {
    // nop
  }
}
