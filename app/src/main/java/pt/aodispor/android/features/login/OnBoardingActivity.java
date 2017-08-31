package pt.aodispor.android.features.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.lamudi.phonefield.PhoneEditText;
import com.redbooth.WelcomeCoordinatorLayout;

import pt.aodispor.android.AppDefinitions;
import pt.aodispor.android.api.aodispor.RequestBuilder;
import pt.aodispor.android.data.local.UserData;
import pt.aodispor.android.features.main.LoginData;
import pt.aodispor.android.features.main.LoginDataPreferences;
import pt.aodispor.android.features.main.MainActivity;
import pt.aodispor.android.R;
import pt.aodispor.android.data.models.aodispor.AODISPOR_JSON_WEBAPI;
import pt.aodispor.android.api.HttpRequestTask;
import pt.aodispor.android.data.models.aodispor.Professional;
import pt.aodispor.android.data.models.aodispor.Error;
import pt.aodispor.android.data.models.aodispor.SearchQueryResult;
import pt.aodispor.android.features.shared.AppCompatActivityPP;
import pt.aodispor.android.utils.Permission;
import pt.aodispor.android.utils.Utility;

public class OnBoardingActivity extends AppCompatActivityPP {

    /**
     * stores last sms received from AoDispor before sending a new sms request)
     */
    String[] prevSMS = null;

    String phoneNumber;
    String loginCode;
    WelcomeCoordinatorLayout coordinatorLayout;

    private Button newUserButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boarding);

        if (AppDefinitions.SKIP_LOGIN) {
            AppDefinitions.smsLoginDone = true;
            UserData.getInstance().setUserLoginAuth(
                    AppDefinitions.testPhoneNumber,
                    AppDefinitions.testPassword);
            showMainActivity();
            return;
        }

        coordinatorLayout = (WelcomeCoordinatorLayout) findViewById(R.id.coordinator);
        coordinatorLayout.addPage(R.layout.welcome__page_1, R.layout.welcome__page_2, R.layout.welcome__page_3);
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

                Permission.checkPermission(OnBoardingActivity.this,
                        Permission.PERMISSIONS_REQUEST_PHONENUMBER,
                        new Runnable() {
                            @Override
                            public void run() {
                                String phoneNumber = Utility.getPhoneNumber(getBaseContext());
                                if (phoneNumber == null || phoneNumber.equals("")) {
                                    Toast.makeText(OnBoardingActivity.this.getBaseContext(),"Ao Dispor não conseguio obter número automaticamente",Toast.LENGTH_LONG).show();
                                    //TODO string
                                    return;
                                }
                                final PhoneEditText phoneNumberField = (PhoneEditText) findViewById(R.id.phone_number);
                                phoneNumberField.setPhoneNumber(phoneNumber);
                                // havendo um número de telefone, enviar a SMS de registo se o número de telefone for válido
                                newUserButton.callOnClick();
                            }
                        },
                        null
                        //TODO? maybe add something when not granted
                );
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
                    phoneNumber = phoneNumberField.getPhoneNumber();
                    //TODO may add code to check SMSs
                    //prevSMS = Utility.getLastMessage(getApplicationContext(), AppDefinitions.PASSWORD_SMS_PHONES);
                    sendRegistrationSMS(phoneNumber);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
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
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length() == 6) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(validate.getWindowToken(), 0);

                    loginCode = editable.toString();
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

                loginCode = validation_code.getText().toString();
                validatePassword();
            }
        });
        // Enviar de novo
        sendAnother.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO may add code to check SMSs
                sendRegistrationSMS(phoneNumber);
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
        HttpRequestTask<AODISPOR_JSON_WEBAPI> request = RequestBuilder.buildValidationRequest(phoneNumber, loginCode);
        request.addOnSuccessHandlers(onValidationSuccess);
        request.addOnFailHandlers(onRequestError);
        request.execute();

        Crashlytics.log(Log.INFO, "input pass", loginCode);
    }

    private void sendRegistrationSMS(String phoneNumber) {
        HttpRequestTask<String> request = RequestBuilder.buildSmsRequest(phoneNumber);
        request.addOnSuccessHandlers(onRegisterSuccess);
        //request.addOnFailHandlers(onRequestError);
        request.execute();

        Crashlytics.log(Log.INFO, "input phone", phoneNumber);
    }

    HttpRequestTask.IOnHttpRequestCompleted<String> onRegisterSuccess = new HttpRequestTask.IOnHttpRequestCompleted<String>() {
        @Override
        public void exec(String answer) {

        }
    };

    HttpRequestTask.IOnHttpRequestCompleted<AODISPOR_JSON_WEBAPI> onValidationSuccess = new HttpRequestTask.IOnHttpRequestCompleted<AODISPOR_JSON_WEBAPI>() {
        @Override
        public void exec(AODISPOR_JSON_WEBAPI answer) {
            SearchQueryResult getProfile = (SearchQueryResult) answer;
            Professional p = (Professional) getProfile.data.get(0);

            if (p == null) {
                loginCode = "";
                return;
            }

            LoginDataPreferences preferences = new LoginDataPreferences(getApplicationContext());
            LoginData loginData = new LoginDataPreferences.LoginDataImpl(phoneNumber, loginCode);
            preferences.edit().put(loginData).apply();

            UserData.getInstance().setUserLoginAuth(phoneNumber, loginCode);

            AppDefinitions.smsLoginDone = true;

            showMainActivity();
        }
    };

    HttpRequestTask.IOnHttpRequestCompleted<AODISPOR_JSON_WEBAPI> onRequestError = new HttpRequestTask.IOnHttpRequestCompleted<AODISPOR_JSON_WEBAPI>() {
        @Override
        public void exec(AODISPOR_JSON_WEBAPI answer) {
            try {
                Error error = (Error) answer;
                Toast.makeText(OnBoardingActivity.this, error.message, Toast.LENGTH_LONG).show();
            } catch (Exception ignored) {
            }
        }
    };

}
