package pt.aodispor.aodispor_android.Dialogs;

import pt.aodispor.aodispor_android.ProfileFragment;

/**
 * Interface for classes that want to implement pt.aodispor.aodispor_android.Dialogs and want to
 * do something when the dialog is dismissed.
 */
public interface DialogCallback {

    void onPriceDialogCallBack(int value, boolean isFinal, ProfileFragment.PriceType type);

}
