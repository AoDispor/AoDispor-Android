package pt.aodispor.android;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.lamudi.phonefield.PhoneEditText;
import com.redbooth.WelcomeCoordinatorLayout;

import java.util.Date;
import java.util.Objects;

import io.fabric.sdk.android.Fabric;
import pt.aodispor.android.api.ApiJSON;
import pt.aodispor.android.api.HttpRequest;
import pt.aodispor.android.api.HttpRequestTask;
import pt.aodispor.android.api.Professional;
import pt.aodispor.android.api.Register;
import pt.aodispor.android.api.SearchQueryResult;

import static pt.aodispor.android.AppDefinitions.PASSWORD_SMS_PHONES;

public class OnBoardingActivity extends AppCompatActivity implements HttpRequest, Advanceable {
    private static final String REGISTER_URL = "https://api.aodispor.pt/users/register";
    private static final String MYSELF_URL = "https://api.aodispor.pt/users/me";

    @VisibleForTesting
    protected enum RequestType {
        register, validate
    }

    @VisibleForTesting
    protected OnBoardingActivity.RequestType requestType;

    String phoneNumber;
    WelcomeCoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boarding);

        if (AppDefinitions.SKIP_LOGIN) {
            /*AppDefinitions.phoneNumber = AppDefinitions.testPhoneNumber;
            AppDefinitions.userPassword = AppDefinitions.testPassword;*/
            showMainActivity();
        }

        coordinatorLayout = (WelcomeCoordinatorLayout)findViewById(R.id.coordinator);
        coordinatorLayout.addPage(R.layout.welcome_page_1, R.layout.welcome_page_2, R.layout.welcome_page_3);
        coordinatorLayout.setCurrentPage(0, false);
        coordinatorLayout.setScrollingEnabled(false);

        // Página 1
        final Button nextButton = (Button) findViewById(R.id.next_button);
        final Button skipButton = (Button) findViewById(R.id.skip_button);
        // Página 2
        final PhoneEditText phoneNumberField = (PhoneEditText) findViewById(R.id.phone_number);
        final Button newUserButton = (Button) findViewById(R.id.new_user);
        final Button returningUserButton = (Button) findViewById(R.id.returning_user);
        final Button skipButton2 = (Button) findViewById(R.id.skip_button2);
        // Página 3
        final Button validate = (Button) findViewById(R.id.validate_button);
        final Button sendAnother = (Button) findViewById(R.id.send_another_button);
        final Button skipButton3 = (Button) findViewById(R.id.skip_button3);

        final View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMainActivity();
            }
        };

        // Página 1
        // Continuar
        nextButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                coordinatorLayout.setCurrentPage(coordinatorLayout.getPageSelected() + 1, true);
                phoneNumberField.requestFocus();

                Permission.requestPermission(OnBoardingActivity.this, AppDefinitions.PERMISSIONS_REQUEST_PHONENUMBER);
            }
        });
        // Saltar
        skipButton.setOnClickListener(clickListener);

        // Página 2
        // Telefone
        phoneNumberField.setHint(R.string.phone_number);
        phoneNumberField.setDefaultCountry("PT");
        phoneNumberField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });
        // Enviar SMS - Novo Utilizador
        newUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean valid = true;

                if (phoneNumberField.isValid()) {
                    phoneNumberField.setError(null);
                } else {
                    phoneNumberField.setError("Número de telefone inválido");
                    valid = false;
                }

                if (valid) {
                    coordinatorLayout.setCurrentPage(coordinatorLayout.getPageSelected() + 1, true);
                    AppDefinitions.phoneNumber = phoneNumberField.getPhoneNumber();
                    //Permission.requestPermission(OnBoardingActivity.this, AppDefinitions.PERMISSIONS_REQUEST_READ_SMS);
                    sendRegistrationSMS();
                }
            }
        });
        // Enviar SMS - Já tenho password
        returningUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean valid = true;

                if (phoneNumberField.isValid()) {
                    phoneNumberField.setError(null);
                } else {
                    phoneNumberField.setError("Número de telefone inválido");
                    valid = false;
                }

                if (valid) {
                    coordinatorLayout.setCurrentPage(coordinatorLayout.getPageSelected() + 1, true);
                    AppDefinitions.phoneNumber = phoneNumberField.getPhoneNumber();
                    //Permission.requestPermission(OnBoardingActivity.this, AppDefinitions.PERMISSIONS_REQUEST_READ_SMS);
                }
            }
        });

        // Saltar
        skipButton2.setOnClickListener(clickListener);

        // Página 3
        // Validar
        validate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText validation_code = (EditText) findViewById(R.id.validation_code);
                if(validation_code.getText().length() < 6) {
                    Toast.makeText(OnBoardingActivity.this, R.string.password_incomplete, Toast.LENGTH_LONG).show();
                    return;
                }

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(validate.getWindowToken(), 0);

                AppDefinitions.userPassword = validation_code.getText().toString();
                requestType = RequestType.validate;
                HttpRequestTask request = new HttpRequestTask(SearchQueryResult.class, OnBoardingActivity.this, MYSELF_URL);
                request.addAPIAuthentication(AppDefinitions.phoneNumber, AppDefinitions.userPassword);
                request.execute();
            }
        });
        // Enviar de novo
        sendAnother.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRegistrationSMS();
            }
        });
        // Saltar
        skipButton3.setOnClickListener(clickListener);
    }

    private void showMainActivity() {
        /*InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm != null){
            imm.toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }*/
        Intent showMainActivity = new Intent(OnBoardingActivity.this, MainActivity.class);
        showMainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(showMainActivity);
    }

    private void sendRegistrationSMS() {
        requestType = RequestType.register;
        HttpRequestTask request_register = new HttpRequestTask(
                String.class, null, REGISTER_URL);
        request_register.setMethod(HttpRequestTask.POST_REQUEST);
        request_register.setJSONBody(new Register(AppDefinitions.phoneNumber));
        request_register.execute();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        advance(requestCode, permissions, grantResults);
    }

    /**
     * Proceeds to next dialog (or ends) the login process.
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    public void advance(int requestCode, String[] permissions, int[] grantResults) {
        //Realizado dependendo do tipo de permissao
        switch (requestCode) {
            case AppDefinitions.PERMISSIONS_REQUEST_PHONENUMBER:
                String phoneNumber = null;
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    phoneNumber = Utility.getPhoneNumber(getApplicationContext());
                }
                final PhoneEditText phoneNumberField = (PhoneEditText) findViewById(R.id.phone_number);
                phoneNumberField.setPhoneNumber(phoneNumber);
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onHttpRequestCompleted(ApiJSON answer, int type) {

        switch (requestType) {
            case register:
                //do things with the register field
            case validate:
                SearchQueryResult getProfile = (SearchQueryResult) answer;
                Professional p = getProfile.data.get(0);

                if(p == null) {
                    AppDefinitions.userPassword = "";
                    return;
                }

                LoginDataPreferences preferences = new LoginDataPreferences(getApplicationContext());
                LoginData loginData = new LoginDataPreferences.LoginDataImpl(AppDefinitions.phoneNumber, AppDefinitions.userPassword);
                preferences.edit().put(loginData).apply();

                showMainActivity();
            default:
                break;
        }
    }

    @Override
    public void onHttpRequestFailed() {
        Toast.makeText(this, getResources().getString(R.string.http_error), Toast.LENGTH_SHORT).show();
    }
}
