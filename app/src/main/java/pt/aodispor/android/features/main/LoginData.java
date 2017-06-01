package pt.aodispor.android.features.main;

import io.t28.shade.annotation.Preferences;
import io.t28.shade.annotation.Property;

@Preferences("pt.aodispor.android.login")
public abstract class LoginData {
    @Property(key = "telephone")
    public abstract String telephone();

    @Property(key = "password")
    public abstract String password();

    boolean hasValidPair() {
        return (!telephone().equals("") && !password().equals(""));
    }
}
