package pt.aodispor.android.dialogs;

import pt.aodispor.android.ProfileFragment;

/**
 * Interface for classes that want to implement pt.aodispor.android.dialogs and want to
 * do something when the dialog is dismissed.
 */
public interface DialogCallback {
    void onPriceDialogCallBack(int value, boolean isFinal, ProfileFragment.PriceType type, String currency);
}
