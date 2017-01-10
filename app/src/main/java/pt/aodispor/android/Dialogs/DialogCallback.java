package pt.aodispor.android.Dialogs;

import pt.aodispor.android.ProfileFragment;

/**
 * Interface for classes that want to implement pt.aodispor.android.Dialogs and want to
 * do something when the dialog is dismissed.
 */
public interface DialogCallback {

    void onPriceDialogCallBack(int value, boolean isFinal, ProfileFragment.PriceType type);

    void onLocationDialogCallBack(String location, String cp4, String cp3, boolean isSet);

}
