package pt.aodispor.android.features.shared;

import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

/**
 * An AppCompatActivity that deals with permission requests using Runnables
 */
public class AppCompatActivityPP extends AppCompatActivity implements ActivityWithPermission {

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        onPermissionRequestPopupClosed(requestCode, permissions, grantResults);
    }

    //TODO pls Review this implementation, might not be ideal
    Runnable onPermissionsGranted;
    Runnable onAPermissionDenied;

    public void setRunnables(Runnable onPermissionsGranted, Runnable onAPermissionDenied) {
        this.onPermissionsGranted = onPermissionsGranted;
        this.onAPermissionDenied = onAPermissionDenied;
    }

    public void onPermissionRequestPopupClosed(int requestCode, String[] permissions, int[] grantResults) {
        boolean granted = true;
        for (int result : grantResults)
            if (result != PackageManager.PERMISSION_GRANTED) granted = false;

        if (granted) {
            if (onPermissionsGranted != null) onPermissionsGranted.run();
        } else {
            if (onAPermissionDenied != null) onAPermissionDenied.run();
        }
        onPermissionsGranted = null;
        onAPermissionDenied = null;
    }
}



