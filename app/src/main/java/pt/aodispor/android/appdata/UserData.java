package pt.aodispor.android.appdata;

import android.util.Base64;
import android.util.Log;

import org.springframework.http.HttpHeaders;
import pt.aodispor.android.api.Professional;

public class UserData {
    private static UserData ourInstance = new UserData();

    public static UserData getInstance() {
        return ourInstance;
    }

    private UserData() {
    }


    public void setUserLoginAuth(String phone_number,String validation_code){
        user_authentication = new UserAuthentication(phone_number,validation_code);
    }
    public UserAuthentication getUserLoginAuth(){
        try {
            return (UserAuthentication) user_authentication.clone();
        }catch (Exception e){
            Log.e("",e.getMessage());
        }
        return null;
    }
    public void updateProfileState(Professional profile_state){
        this.profile_state = profile_state;
    }
    public Professional getProfileState(){
        try {
            return (Professional) profile_state.clone();
        }catch (Exception e){
            Log.e("XXX",e.getMessage());
        }
        return null;
    }

    private UserAuthentication user_authentication;
    private Professional profile_state;

    public class UserAuthentication implements Cloneable{
        public String phone_number;
        public String validation_code;

        public UserAuthentication(String phone_number, String validation_code){
            this.phone_number=phone_number;
            this.validation_code=validation_code;
        }

        @SuppressWarnings("CloneDoesntCallSuperClone")
        @Override
        protected Object clone() throws CloneNotSupportedException {
            return new UserAuthentication(this.phone_number,this.validation_code);
        }
    }

}
