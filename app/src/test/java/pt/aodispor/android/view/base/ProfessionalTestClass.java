package pt.aodispor.android.view.base;

import pt.aodispor.android.API.Professional;

public class ProfessionalTestClass extends Professional {

    public static Professional testProfessional(String location, String title)
    {
        ProfessionalTestClass prof = new ProfessionalTestClass();
        prof.location = location;
        prof.title = title;
        prof.full_name = "";
        prof.rate = "1";
        prof.type = "H";
        prof.currency = "eur";
        prof.description = "";
        prof.avatar_url = "";
        return prof;
    }

}
