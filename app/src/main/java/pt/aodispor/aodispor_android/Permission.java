package pt.aodispor.aodispor_android;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;

public final class Permission {

    private static void showMessageOKCancel(final MainActivity activity, String message,
                                           final String[] permission, final int requestCode) {
        new AlertDialog.Builder(activity)
                .setMessage(message)
                .setPositiveButton("OK",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (android.os.Build.VERSION.SDK_INT >= 23) activity.requestPermissions(permission,requestCode);
                        else ActivityCompat.requestPermissions(activity, permission, requestCode);
                    }})
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                    @Override public void onClick(DialogInterface dialog, int which){
                        activity.onRequestPermissionsResult(requestCode,permission ,new int[]{PackageManager.PERMISSION_DENIED});
                    }
                }).setCancelable(false)
                .create()
                .show();
    }

    /**<p>note the "true ||" code segments inside conditions. Were added to force the request for testing purposes. May be removed later!!!</p>
     * <p>request one, and one only, permission</p>
     * <p>the MainActivity's onRequestPermissionResult(...) will be called even if the user doesn't accept the permission with a PackageManager.PERMISSION_DENIED
     * or if the permission was already granted with a PackageManager.PERMISSION_GRANTED</p>
     * <p>behaviour resulting from accepting/denying the permission (or that starts after either) should be added in onRequestPermissionResult(...)</p>
     * */
    public static void requestPermission(final MainActivity activity, final int requestCode ){
        String permission_dialog_message = null;
        final String[] permission;
        switch (requestCode) {
            case AppDefinitions.PERMISSIONS_REQUEST_READ_SMS:
                permission_dialog_message = activity.getResources().getString(R.string.request_permission_sms);
                permission =  new String[] {Manifest.permission.READ_SMS};
                break;
            case AppDefinitions.PERMISSIONS_REQUEST_INTERNET:
                permission_dialog_message = activity.getResources().getString(R.string.request_permission_internet);
                permission =  new String[] {Manifest.permission.INTERNET};
                break;
            case AppDefinitions.PERMISSIONS_REQUEST_PHONENUMBER:
                permission_dialog_message = activity.getResources().getString(R.string.request_permission_phone);
                permission =  new String[] {Manifest.permission.READ_PHONE_STATE};
                break;
            default:
                permission=null;
                break;
        }

        if (android.os.Build.VERSION.SDK_INT >= 23) {
            Log.d("PERMISSION:","VERSION>=23");
            int hasWriteContactsPermission = activity.checkSelfPermission(permission[0]);
            if (true || hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
                /*if (true || !activity.shouldShowRequestPermissionRationale(permission[0])) {
                    showMessageOKCancel(activity,permission_dialog_message,permission,requestCode);
                    return;
                }*/
                activity.requestPermissions(permission, requestCode);
                return;
            }

        } else {
            Log.d("PERMISSION:","VERSION<23");
            int hasWriteContactsPermission = ContextCompat.checkSelfPermission(activity,
                    permission[0]);
            if (true || hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
                if (true||!ActivityCompat.shouldShowRequestPermissionRationale(activity, permission[0])) {
                    showMessageOKCancel(activity, permission_dialog_message,permission,requestCode);
                    return;
                }
                ActivityCompat.requestPermissions(activity, permission, requestCode);
                return;
            }

        }

        activity.onRequestPermissionsResult(requestCode,permission,new int[]{AppDefinitions.PERMISSION_NOT_REQUESTED});
    }

}
