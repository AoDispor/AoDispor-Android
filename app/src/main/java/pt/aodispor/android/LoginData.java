package pt.aodispor.android;

import io.t28.shade.annotation.Preferences;
import io.t28.shade.annotation.Property;

@Preferences("pt.aodispor.android.login")
public abstract class LoginData {
    @Property(key = "telephone")
    abstract String telephone();

    @Property(key = "password")
    abstract String password();

    boolean hasValidPair() {
        return (telephone() != "" && password() != "") ? true : false;
    }
}
