package in.abhi9.apphistory;

import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;


public class AppLogService extends Service {
    private static AppLogService self = null;
    String TAG = "AppLogService";
    private Integer nextLeft = 0;
    private Integer nextRight = 0;
    private String lastLaunchedBySwipe = "";
    private ActivityManager mActivityManager;

    public static AppLogService getServiceObject() {
        return self;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // The service is starting, due to a call to startService()
        Log.d(TAG, "Service started");

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Log.d(TAG, "Running");
            }
        }, 5000, 1000);

        self = this;

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "FirstService destroyed");
        super.onDestroy();
    }

    public void swipeLeft() {
        List<ActivityManager.RecentTaskInfo> recents = getRecents();
        ActivityManager.RecentTaskInfo nextApp;
        nextRight = 0;

        if (!Objects.equals(getCurrentActivity(), lastLaunchedBySwipe)) {
            Log.d(TAG, "App launch sequence changed externally");
            nextLeft = 0;
        }

        if (Objects.equals(getCurrentActivity(), getHomeLauncher())) {
            nextLeft = -1;
            Log.d(TAG, "Currently on Home");
        }
        nextLeft++;

        try {
            nextApp = recents.get(nextLeft);
        } catch (IndexOutOfBoundsException e) {
            nextLeft--;
            Log.d(TAG, "No more next Index");
            return;
        }

        mActivityManager.moveTaskToFront(
                nextApp.id,
                ActivityManager.MOVE_TASK_NO_USER_ACTION,
                getStartActivityOption("left").toBundle()
        );
        lastLaunchedBySwipe = nextApp.baseIntent.getComponent().getPackageName();
    }

    public void swipeRight() {
        List<ActivityManager.RecentTaskInfo> recents = getRecents();
        ActivityManager.RecentTaskInfo nextApp;

        if (!Objects.equals(getCurrentActivity(), lastLaunchedBySwipe)) {
            Log.d(TAG, "App launch sequence changed externally");
            nextRight = 0;
            nextLeft = 0;
        }

        if (nextLeft == 0) {
            Intent launchIntent = getPackageManager().getLaunchIntentForPackage(getHomeLauncher());
            startActivity(launchIntent, getStartActivityOption("right").toBundle());
            Log.d(TAG, "No more right step");
            return;
        }

        if (Objects.equals(getCurrentActivity(), getHomeLauncher())) {
            nextRight = 0;
            Log.d(TAG, "Currently on Home");
            return;
        }
        nextRight++;

        try {
            nextApp = recents.get(nextRight);
        } catch (IndexOutOfBoundsException e) {
            nextRight = 0;
            Log.d(TAG, "No more next Index");
            return;
        }

        mActivityManager.moveTaskToFront(
                nextApp.id,
                ActivityManager.MOVE_TASK_NO_USER_ACTION,
                getStartActivityOption("right").toBundle()
        );
        nextLeft--;
        lastLaunchedBySwipe = nextApp.baseIntent.getComponent().getPackageName();
    }

    private List<ActivityManager.RecentTaskInfo> getRecents() {

        ArrayList<ActivityManager.RecentTaskInfo> packages = new ArrayList<>();
        final String homeLauncherPackage = getHomeLauncher();

        final List<ActivityManager.RecentTaskInfo> recentTasks = mActivityManager
                .getRecentTasks(Integer.MAX_VALUE,
                        ActivityManager.RECENT_IGNORE_UNAVAILABLE | 4 |
                                ActivityManager.RECENT_WITH_EXCLUDED);

        Log.d(TAG, "---------------------------------------------");
        for (ActivityManager.RecentTaskInfo task : recentTasks) {
            if (task.id < 0) continue;
            String packageName = task.baseIntent.getComponent().getPackageName();

            // Ignoring this app
            if (Objects.equals(packageName, getPackageName())) continue;
            // Ignoring home package
            if (Objects.equals(packageName, homeLauncherPackage)) continue;

            packages.add(task);
        }

        Log.d(TAG, "Recents: " + packages.toString());
        return packages;
    }

    private String getCurrentActivity() {
        final ActivityManager mActivityManager = (ActivityManager)
                getSystemService(Context.ACTIVITY_SERVICE);

        final List<ActivityManager.RecentTaskInfo> recentTasks = mActivityManager
                .getRecentTasks(2,
                        ActivityManager.RECENT_IGNORE_UNAVAILABLE | 4 |
                                ActivityManager.RECENT_WITH_EXCLUDED);

        String currentActivity = recentTasks.get(1).baseIntent.getComponent().getPackageName();

        Log.d(TAG, "Current activity: " + currentActivity);

        return currentActivity;
    }

    public String getHomeLauncher() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        ResolveInfo defaultLauncher = getPackageManager().resolveActivity(intent,
                PackageManager.MATCH_DEFAULT_ONLY
        );

        return defaultLauncher.activityInfo.packageName;
    }

    private ActivityOptions getStartActivityOption(String animDirection) {
        // Animation options
        ArrayList<Integer> anim = new ArrayList<>();
        if (Objects.equals(animDirection, "right")) {
            anim.add(android.R.anim.slide_in_left);
            anim.add(android.R.anim.slide_out_right);
        } else {
            anim.add(R.anim.slide_in_right);
            anim.add(R.anim.slide_out_left);
        }

        return ActivityOptions.makeCustomAnimation(this,
                anim.get(0),
                anim.get(1)
        );
    }
}
