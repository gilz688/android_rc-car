package ph.edu.msuiit.rccarserver.utils;

import android.app.Activity;
import android.os.Build;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import ph.edu.msuiit.rccarserver.R;

public class KitKatTweaks {

    public static void enableStatusBarTint(Activity activity){
        int version = android.os.Build.VERSION.SDK_INT;
        if (version == Build.VERSION_CODES.KITKAT) {
            SystemBarTintManager tintManager = new SystemBarTintManager(activity);
            // enable status bar tint
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setTintResource(R.color.primary_dark);
        }
    }
}
