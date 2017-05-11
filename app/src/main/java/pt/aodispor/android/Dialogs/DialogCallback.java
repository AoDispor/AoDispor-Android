package pt.aodispor.android.dialogs;

import pt.aodispor.android.professional.PaymentType;

/**
 * Interface for classes that want to implement pt.aodispor.android.dialogs and want to
 * do something when the dialog is dismissed.
 */
public interface DialogCallback {
    void onPriceDialogCallBack(int value, boolean isFinal, PaymentType type, String currency);
}
