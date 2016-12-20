package pt.aodispor.aodispor_android.view.base;

import pt.aodispor.aodispor_android.API.Professional;

public class ProfessionalTestClass extends Professional {

    public static Professional testProfessional(String location, String title)
    {
        //if (!test_class.getName().contains("Test")) throw new Exception("NOT AUTHORIZED");
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
