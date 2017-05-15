package pt.aodispor.android;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.lamudi.phonefield.PhoneEditText;
import com.redbooth.WelcomeCoordinatorLayout;

import org.w3c.dom.Text;

import java.util.Date;
import java.util.Objects;

import io.fabric.sdk.android.Fabric;
import pt.aodispor.android.api.ApiJSON;
import pt.aodispor.android.api.HttpRequest;
import pt.aodispor.android.api.HttpRequestTask;
import pt.aodispor.android.api.Professional;
import pt.aodispor.android.api.Register;
import pt.aodispor.android.api.Error;
import pt.aodispor.android.api.SearchQueryResult;

import static pt.aodispor.android.AppDefinitions.PASSWORD_SMS_PHONES;

public class OnBoardingActivity extends AppCompatActivity implements HttpRequest, Advanceable {
    private static final String REGISTER_URL = "https://api.aodispor.pt/users/register";
    private static final String MYSELF_URL = "https://api.aodispor.pt/users/me";

    /**
     * stores last sms received from AoDispor before sending a new sms request)
     */
    String[] prevSMS = null;

    @VisibleForTesting
    protected enum RequestType {
        register, validate
    }

    @VisibleForTesting
    protected OnBoardingActivity.RequestType requestType;

    String phoneNumber;
    WelcomeCoordinatorLayout coordinatorLayout;

    private Button newUserButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boarding);

        HttpRequestTask.setToken(getResources().getString(R.string.ao_dispor_api_key));

        if (AppDefinitions.SKIP_LOGIN) {
            AppDefinitions.smsLoginDone=true;
            AppDefinitions.phoneNumber = AppDefinitions.testPhoneNumber;
            AppDefinitions.userPassword = AppDefinitions.testPassword;
            showMainActivity();
            return;
        }

        coordinatorLayout = (WelcomeCoordinatorLayout) findViewById(R.id.coordinator);
        coordinatorLayout.addPage(R.layout.welcome_page_1, R.layout.welcome_page_2, R.layout.welcome_page_3);
        coordinatorLayout.setCurrentPage(0, false);
        coordinatorLayout.setScrollingEnabled(false);

        // Página 1
        final Button nextButton = (Button) findViewById(R.id.next_button);
        final TextView skipView = (TextView) findViewById(R.id.skip_text);
        // Página 2
        final PhoneEditText phoneNumberField = (PhoneEditText) findViewById(R.id.phone_number);
        newUserButton = (Button) findViewById(R.id.new_user);
        final TextView skipText2 = (TextView) findViewById(R.id.skip_text2);
        // Página 3
        final EditText validationCodeField = (EditText) findViewById(R.id.validation_code);
        final Button validate = (Button) findViewById(R.id.validate_button);
        final TextView sendAnother = (TextView) findViewById(R.id.send_another_text);
        final TextView skipText3 = (TextView) findViewById(R.id.skip_text3);

        final View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMainActivity();
            }
        };

        // Página 1
        // Continuar
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                coordinatorLayout.setCurrentPage(coordinatorLayout.getPageSelected() + 1, true);
                phoneNumberField.requestFocus();

                Permission.requestPermission(OnBoardingActivity.this, AppDefinitions.PERMISSIONS_REQUEST_PHONENUMBER);
            }
        });
        // Saltar
        skipView.setOnClickListener(clickListener);

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
                    //TODO may add code t check SMSs
                    //prevSMS = Utility.getLastMessage(getApplicationContext(), AppDefinitions.PASSWORD_SMS_PHONES);
                    sendRegistrationSMS();
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(validate.getWindowToken(), 0);
                }
            }
        });

        // Saltar
        skipText2.setOnClickListener(clickListener);

        // Página 3
        // Campo de texto para a password
        validationCodeField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().length() == 6) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(validate.getWindowToken(), 0);

                    AppDefinitions.userPassword = editable.toString();
                    validatePassword();
                }
            }
        });


        // Validar
        validate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText validation_code = (EditText) findViewById(R.id.validation_code);
                if (validation_code.getText().length() < 6) {
                    Toast.makeText(OnBoardingActivity.this, R.string.password_incomplete, Toast.LENGTH_LONG).show();
                    return;
                }

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(validate.getWindowToken(), 0);

                AppDefinitions.userPassword = validation_code.getText().toString();
                validatePassword();
            }
        });
        // Enviar de novo
        sendAnother.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO may add code t check SMSs
                sendRegistrationSMS();
            }
        });
        // Saltar
        skipText3.setOnClickListener(clickListener);
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

    private void validatePassword() {
        requestType = RequestType.validate;
        HttpRequestTask request = new HttpRequestTask(SearchQueryResult.class, OnBoardingActivity.this, MYSELF_URL);
        request.addAPIAuthentication(AppDefinitions.phoneNumber, AppDefinitions.userPassword);
        request.execute();
    }

    private void sendRegistrationSMS() {
        requestType = RequestType.register;
        HttpRequestTask request_register = HttpRequestTask.POST(String.class, null, REGISTER_URL);
        //        new HttpRequestTask(String.class, null, REGISTER_URL);
        //request_register.setMethod(HttpRequestTask.POST_REQUEST);
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
                // havendo um número de telefone, enviar a SMS de registo se o número de telefone for válido
                newUserButton.callOnClick();
                break;
            default:
                break;
        }
    }

    @Override
    public void onHttpRequestSuccessful(ApiJSON answer, int type) {
        switch (requestType) {
            case register:
                //do things with the register field
            case validate:
                SearchQueryResult getProfile = (SearchQueryResult) answer;
                Professional p = (Professional) getProfile.data.get(0);

                if (p == null) {
                    AppDefinitions.userPassword = "";
                    return;
                }

                LoginDataPreferences preferences = new LoginDataPreferences(getApplicationContext());
                LoginData loginData = new LoginDataPreferences.LoginDataImpl(AppDefinitions.phoneNumber, AppDefinitions.userPassword);
                preferences.edit().put(loginData).apply();

                AppDefinitions.smsLoginDone=true;

                showMainActivity();
            default:
                break;
        }
    }

    @Override
    public void onHttpRequestFailed(ApiJSON errorData, int type) {
        Error error = (Error) errorData;
        Toast.makeText(this, error.message, Toast.LENGTH_LONG).show();
    }

}
