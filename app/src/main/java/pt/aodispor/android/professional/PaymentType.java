package pt.aodispor.android.professional;

import pt.aodispor.android.AoDisporApplication;
import pt.aodispor.android.ISpinnerEnum;
import pt.aodispor.android.R;

public enum PaymentType {
    ByHour("H", R.string.hour) {
        @Override
        public String convertToStringToDisplay() {
            return "hora";
        }
    },
    ByDay("D", R.string.day) {
        @Override
        public String convertToStringToDisplay() {
            return "dia";
        }
    },
    ByService("S", R.string.service) {
        @Override
        public String convertToStringToDisplay() {
            return "serviço";
        }
    };

    private String apiCode;

    public String getAPICode() {
        return apiCode;
    }

    private String displayString;

    public String getDisplayString() {
        return displayString;
    }

    PaymentType(String api, int displayRes) {
        apiCode = api;
        displayString = AoDisporApplication.getInstance().getResources().getString(displayRes);
    }

    static public PaymentType parsePayment(String api_value) {
        for (PaymentType val : PaymentType.values())
            if (val.getAPICode().equals(api_value)) return val;
        //Default in case missing:
        return PaymentType.ByHour;
    }

    abstract public String convertToStringToDisplay();
}
