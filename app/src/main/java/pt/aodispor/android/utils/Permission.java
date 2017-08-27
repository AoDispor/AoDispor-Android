package pt.aodispor.android.utils;

import android.Manifest;
import android.app.Activity;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import pt.aodispor.android.AppDefinitions;
import pt.aodispor.android.R;
import pt.aodispor.android.features.shared.ActivityWithPermission;
import pt.aodispor.android.features.shared.AppCompatActivityPP;

public final class Permission {

    //region PERMISSIONS IDS (may refer to a set of permissions)
    public static final int PERMISSIONS_REQUEST_READ_SMS = 2;
    public static final int PERMISSIONS_REQUEST_PHONENUMBER = 3;
    public static final int PERMISSIONS_REQUEST_INTERNET = 4;
    public static final int PERMISSIONS_REQUEST_GPS = 5;
    public static final int PERMISSIONS_REQUEST_STORAGE = 6;
    public static final int PERMISSIONS_CALL_PHONE = 7;
    //endregion

    public static boolean enabled = true;

    private static void showMessageOKCancel(final Activity activity, String message,
                                            final String[] permissions, final int requestCode
    ) {
        new AlertDialog.Builder(activity)
                .setMessage(message)
                .setPositiveButton(R.string.allow, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (android.os.Build.VERSION.SDK_INT >= 23)
                            activity.requestPermissions(permissions, requestCode);
                        else ActivityCompat.requestPermissions(activity, permissions, requestCode);
                    }
                })
                .setNegativeButton(R.string.deny, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!(activity instanceof ActivityWithPermission)) return;
                        ((ActivityWithPermission) activity).onPermissionRequestPopupClosed(
                                requestCode, permissions, new int[]{PackageManager.PERMISSION_DENIED});
                    }
                }).setCancelable(false)
                .create()
                .show();
    }

    /**
     * checks if the permission is granted or not.
     * if permission was already granted onGranted is executed.
     * if not asks the user for permission. then
     * if permission is successfully granted onGranted is executed.
     * if permission was denied onDenied is executed;
    */
    public static void checkPermission(final Activity activity, final int requestCode,
                                       Runnable onGranted, Runnable onDenied
    ) {
        if (!enabled) return;

        String permission_dialog_message = null;
        final String permissions[];
        switch (requestCode) {
            case PERMISSIONS_REQUEST_READ_SMS:
                permission_dialog_message = activity.getResources().getString(
                        android.os.Build.VERSION.SDK_INT >= 23 ?
                                R.string.request_permission_sms_version_plus23 :
                                R.string.request_permission_sms
                );
                permissions = new String[]{Manifest.permission.READ_SMS};
                break;
            case PERMISSIONS_REQUEST_INTERNET:
                permission_dialog_message = activity.getResources().getString(
                        android.os.Build.VERSION.SDK_INT >= 23 ?
                                R.string.request_permission_internet_version_plus23 : R.string.request_permission_internet
                );
                permissions = new String[]{Manifest.permission.INTERNET};
                break;
            case PERMISSIONS_REQUEST_PHONENUMBER:
                permission_dialog_message = activity.getResources().getString(
                        android.os.Build.VERSION.SDK_INT >= 23 ?
                                R.string.request_permission_phone_version_plus23 :
                                R.string.request_permission_phone
                );
                permissions = new String[]{Manifest.permission.READ_PHONE_STATE};
                break;
            case PERMISSIONS_REQUEST_GPS:
                permission_dialog_message = activity.getString(R.string.request_permission_gps);
                permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
                break;
            case PERMISSIONS_REQUEST_STORAGE:
                permission_dialog_message = "Permitir Ao Dispor aceder aos seus ficheiros?";//TODO string
                permissions = new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE};
                break;
            case PERMISSIONS_CALL_PHONE:
                permission_dialog_message = "Permitir Ao Dispor realizar telefonemas";//TODO string
                permissions = new String[]{Manifest.permission.CALL_PHONE};
                break;
            default:
                throw new RuntimeException("Invalid Permission Request");
        }

        boolean hasPermission = true;
        boolean shouldShowRationale = false;

        if (Build.VERSION.SDK_INT >= 23) {
            Log.d("PERMISSION:", "VERSION>=23");
            for (String permission : permissions)
                if (activity.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    hasPermission = false;
                    break;
                }
            if (!hasPermission) {
                if (activity instanceof ActivityWithPermission)
                    ((ActivityWithPermission) activity).setRunnables(onGranted, onDenied);
                new AlertDialog.Builder(activity)
                        .setMessage(permission_dialog_message)
                        .setPositiveButton(R.string.understood, new DialogInterface.OnClickListener() {
                            @Override
                            @TargetApi(Build.VERSION_CODES.M)
                            public void onClick(DialogInterface dialog, int which) {
                                activity.requestPermissions(permissions, requestCode);
                            }
                        }).setCancelable(false)
                        .create()
                        .show();

                return;
            }
        } else {
            Log.d("PERMISSION:", "VERSION<23");
            for (String permission : permissions)
                if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                    hasPermission = false;
                    break;
                }
            if (!hasPermission) {
                if (activity instanceof ActivityWithPermission)
                    ((ActivityWithPermission) activity).setRunnables(onGranted, onDenied);
                for (String permission : permissions)
                    if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                        shouldShowRationale = true;
                        break;
                    }
                if (shouldShowRationale) {
                    showMessageOKCancel(activity, permission_dialog_message, permissions, requestCode);
                    return;
                }
                //ActivityCompat.requestPermissions(activity, permissions, requestCode);
                return;
            }
        }
        //if(hasPermission)
        onGranted.run();

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.onRequestPermissionsResult(requestCode, permissions, new int[]{PERMISSION_NOT_REQUESTED});
        }*/
    }

}
