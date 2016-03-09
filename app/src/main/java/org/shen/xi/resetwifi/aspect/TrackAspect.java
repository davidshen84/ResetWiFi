package org.shen.xi.resetwifi.aspect;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.shen.xi.resetwifi.MainActivity;
import org.shen.xi.resetwifi.MainApplication;
import org.shen.xi.resetwifi.aspect.annotation.TrackAction;

import static org.shen.xi.resetwifi.aspect.Utility.getAnnotationOnMethod;

/**
 * Created on 3/1/2016.
 */
@Aspect
public class TrackAspect {

  @Pointcut("execution(@org.shen.xi.resetwifi.aspect.annotation.TrackStart * org.shen.xi.resetwifi.MainActivity.*(..))")
  public void trackStart() {
  }

  @After("trackStart()")
  public void weaveTrackStart(JoinPoint joinPoint) throws Throwable {
    Object target = joinPoint.getTarget();
    MainActivity activity = Utility.castTarget(target, MainActivity.class);

    MainApplication mainApplication = (MainApplication) activity.getApplication();
    Tracker appTracker = mainApplication.getAppTracker();

    appTracker.setScreenName(MainActivity.TAG);
    appTracker.send(new HitBuilders.ScreenViewBuilder().build());
  }

  @Pointcut("execution(@org.shen.xi.resetwifi.aspect.annotation.TrackAction * org.shen.xi.resetwifi.MainActivity.*(..))")
  public void trackAction() {
  }

  @After("trackAction()")
  public void weaveTrackAction(JoinPoint joinPoint) throws Throwable {
    Object target = joinPoint.getTarget();
    MainActivity mainActivity = Utility.castTarget(target, MainActivity.class);

    MainApplication mainApplication = (MainApplication) mainActivity.getApplication();
    Tracker appTracker = mainApplication.getAppTracker();

    TrackAction annotation = getAnnotationOnMethod(joinPoint.getSignature(), TrackAction.class);

    appTracker.send(new HitBuilders.EventBuilder("action", annotation.action()).build());
  }
}
