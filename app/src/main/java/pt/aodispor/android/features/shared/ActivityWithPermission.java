package pt.aodispor.android.features.shared;

public interface ActivityWithPermission {
    public void onPermissionRequestPopupClosed(int requestCode, String[] permissions, int[] grantResults);
    public void setRunnables(Runnable onPermissionsGranted, Runnable onAPermissionDenied);
}
