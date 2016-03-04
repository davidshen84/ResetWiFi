package org.shen.xi.resetwifi.aspect;

import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.annotation.Annotation;

/**
 * Created on 3/4/2016.
 */
final class Utility {
  @SuppressWarnings("unchecked")
  public static <T> T castTarget(Object target, Class<T> expectedClass) throws Throwable {
    if (!expectedClass.isInstance(target)) {
      throw new ClassCastException(String.format("cannot cast %s to %s",
        target.getClass().getCanonicalName(), expectedClass.getCanonicalName()));
    }

    return (T) target;
  }

  public static <T extends Annotation> T
  getAnnotationOnMethod(Signature signature, Class<T> annotationClass) throws Throwable {
    MethodSignature methodSignature = (MethodSignature) signature;

    return methodSignature.getMethod().getAnnotation(annotationClass);
  }
}
