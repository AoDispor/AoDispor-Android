package pt.aodispor.android.base;

import pt.aodispor.android.utils.DateUtils;

public class MockDateUtils extends DateUtils{

    static public void setMockupPeriodSuffixes(String[] suffixes){
        periodSuffixes = suffixes;
    }

}
