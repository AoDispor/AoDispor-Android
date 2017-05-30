package pt.aodispor.android.professional;

import pt.aodispor.android.ISpinnerEnum;

//TODO move display strings to resources
public enum CurrencyType implements ISpinnerEnum {
    Euro("EUR", "Euro", "€"),
    US_Dollar("USD", "US Dollar", "$"),
    MacauPataca("MOP", "MOP", "MOP$");

    private String apiCode;

    public String getAPICode() {
        return apiCode;
    }

    private String displayString;

    public String getDisplayString() {
        return displayString;
    }

    private String symbol;

    public String getSymbol() {
        return symbol;
    }

    CurrencyType(String api, String display_name, String symb) {
        apiCode = api;
        displayString = display_name;
        symbol = symb;
    }

    static public CurrencyType parseCurrency(String api_value) {
        for (CurrencyType val : CurrencyType.values())
            if (val.getAPICode().equals(api_value)) return val;
        //Default in case missing:
        return CurrencyType.Euro;
    }
}
