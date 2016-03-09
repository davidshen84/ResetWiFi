package org.shen.xi.resetwifi.aspect;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.shen.xi.resetwifi.aspect.annotation.RequirePermissions;

import static org.shen.xi.resetwifi.aspect.Utility.getAnnotationOnMethod;

/**
 * Created on 3/4/2016.
 */
@Aspect
public class PermissionAspect {

  @Pointcut("execution(@org.shen.xi.resetwifi.aspect.annotation.RequirePermissions void org.shen.xi.resetwifi.*.*(..))")
  public void requirePermissions() {
  }

  @After("requirePermissions()")
  public void weaveRequirePermission(JoinPoint joinPoint) throws Throwable {
    Object target = joinPoint.getTarget();
    Activity activity = Utility.castTarget(target, Activity.class);
    RequirePermissions annotation =
      getAnnotationOnMethod(joinPoint.getSignature(), RequirePermissions.class);

    boolean missingPermission = false;
    for (String p : annotation.permissions()) {
      if (ContextCompat.checkSelfPermission(activity, p) == PackageManager.PERMISSION_DENIED) {
        missingPermission = true;
        break;
      }
    }

    if (missingPermission) {
      Toast.makeText(activity, "missing required permissions", Toast.LENGTH_SHORT).show();
      activity.finish();
    }
  }
}