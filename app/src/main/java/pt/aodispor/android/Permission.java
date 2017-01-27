package pt.aodispor.android;

import android.Manifest;
import android.app.Activity;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import java.lang.annotation.Target;

public final class Permission {

    private static void showMessageOKCancel(final Activity activity, String message,
                                            final String[] permission, final int requestCode) {
        new AlertDialog.Builder(activity)
                .setMessage(message)
                .setPositiveButton(R.string.allow, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (android.os.Build.VERSION.SDK_INT >= 23)
                            activity.requestPermissions(permission, requestCode);
                        else ActivityCompat.requestPermissions(activity, permission, requestCode);
                    }
                })
                .setNegativeButton(R.string.deny, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        activity.advance(requestCode, permission, new int[]{PackageManager.PERMISSION_DENIED});
                    }
                }).setCancelable(false)
                .create()
                .show();
    }

    /**
     * <p>note the "true ||" code segments inside conditions. Were added to force the request for testing purposes. May be removed later!!!</p>
     * <p>request one, and one only, permission</p>
     * <p>the MainActivity's onRequestPermissionResult(...) will be called even if the user doesn't accept the permission with a PackageManager.PERMISSION_DENIED
     * or if the permission was already granted with a PackageManager.PERMISSION_GRANTED</p>
     * <p>behaviour resulting from accepting/denying the permission (or that starts after either) should be added in onRequestPermissionResult(...)</p>
     */
    public static void requestPermission(final Activity activity, final int requestCode) {
        String permission_dialog_message = null;
        final String[] permission;
        switch (requestCode) {
            case AppDefinitions.PERMISSIONS_REQUEST_READ_SMS:
                permission_dialog_message = activity.getResources().getString(
                        android.os.Build.VERSION.SDK_INT >= 23 ?
                                R.string.request_permission_sms_version_plus23 :
                                R.string.request_permission_sms
                );
                permission = new String[]{Manifest.permission.READ_SMS};
                break;
            case AppDefinitions.PERMISSIONS_REQUEST_INTERNET:
                permission_dialog_message = activity.getResources().getString(
                        android.os.Build.VERSION.SDK_INT >= 23 ?
                                R.string.request_permission_internet_version_plus23 : R.string.request_permission_internet
                );
                permission = new String[]{Manifest.permission.INTERNET};
                break;
            case AppDefinitions.PERMISSIONS_REQUEST_PHONENUMBER:
                permission_dialog_message = activity.getResources().getString(
                        android.os.Build.VERSION.SDK_INT >= 23 ?
                                R.string.request_permission_phone_version_plus23 :
                                R.string.request_permission_phone
                );
                permission = new String[]{Manifest.permission.READ_PHONE_STATE};
                break;
            case AppDefinitions.PERMISSIONS_REQUEST_GPS:
                permission_dialog_message = activity.getString(R.string.request_permission_gps);
                permission = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
                break;
            default:
                permission = null;
                break;
        }

        if (Build.VERSION.SDK_INT >= 23) {
            Log.d("PERMISSION:", "VERSION>=23");
            int hasWriteContactsPermission = activity.checkSelfPermission(permission[0]);
            if (true || hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {

                new AlertDialog.Builder(activity)
                        .setMessage(permission_dialog_message)
                        .setPositiveButton(R.string.understood, new DialogInterface.OnClickListener() {
                            @Override
                            @TargetApi(Build.VERSION_CODES.M)
                            public void onClick(DialogInterface dialog, int which) {
                                activity.requestPermissions(permission, requestCode);
                            }
                        }).setCancelable(false)
                        .create()
                        .show();

                return;
            }
        } else {
            Log.d("PERMISSION:", "VERSION<23");
            int hasWriteContactsPermission = ContextCompat.checkSelfPermission(activity,
                    permission[0]);
            if (true || hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
                if (true || !ActivityCompat.shouldShowRequestPermissionRationale(activity, permission[0])) {
                    showMessageOKCancel(activity, permission_dialog_message, permission, requestCode);
                    return;
                }
                ActivityCompat.requestPermissions(activity, permission, requestCode);
                return;
            }

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.onRequestPermissionsResult(requestCode, permission, new int[]{AppDefinitions.PERMISSION_NOT_REQUESTED});
        }
    }

}
