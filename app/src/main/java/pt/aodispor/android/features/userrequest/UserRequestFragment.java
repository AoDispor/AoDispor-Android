package pt.aodispor.android.features.userrequest;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import java.util.Date;

import pt.aodispor.android.AppDefinitions;
import pt.aodispor.android.R;
import pt.aodispor.android.api.HttpRequestTask;
import pt.aodispor.android.api.aodispor.RequestBuilder;
import pt.aodispor.android.data.local.UserData;
import pt.aodispor.android.data.models.aodispor.*;
import pt.aodispor.android.features.profile.LocationDialog;
import pt.aodispor.android.features.profile.ProfileEditText;
import pt.aodispor.android.utils.TextUtils;
import pt.aodispor.android.utils.TypefaceManager;
import pt.aodispor.android.utils.ViewUtils;

public class UserRequestFragment extends Fragment implements LocationDialog.LocationDialogListener {

    private static final String LOCATION_TAG = "location";
    private UserRequestFragment thisObject;
    private LinearLayout noConnectionView, requestActiveView, formView;
    private EditText requestNameEdit, requestDescriptionEdit, locationEdit;
    private View root;
    private String prefix, suffix; //cp4 & cp3

    //static boolean profileLoaded = false;
    static final int WAIT_4RETRY_GET_USERREQUEST = 5000;

    UserRequestTempData userRequest;

    private Button sendRequestButton;
    private View.OnClickListener sendRequestButtonAction = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String who = requestNameEdit.getText().toString();
            String where = prefix + "-" + suffix;
            String what = requestDescriptionEdit.getText().toString();

            //form completed?
            for (String s : new String[]{who, where, what})
                if (s == null || s.equals("")) {
                    //form is not complete so the request can't be uploaded
                    Context context = UserRequestFragment.this.getContext();
                    Toast.makeText(context,
                            context.getResources().getString(R.string.missing_fields_toast)
                            , Toast.LENGTH_LONG).show();
                    return;
                }

            POST_Request(new UserRequestCreationData(who, what, where));
        }
    };


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //createHandler();
    }

    Context context;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (!AppDefinitions.smsLoginDone)
            return null;//TODO quick fix, might not be the best solution

        context = this.getContext();
        //should be done in the activity startup HttpRequestTask.setToken(context.getResources().getString(R.string.ao_dispor_api_key));
        thisObject = this;
        root = inflater.inflate(R.layout.user_request__base, container, false);

        // Get Main Views
        noConnectionView = (LinearLayout) root.findViewById(R.id.not_loaded_page_layout);
        requestActiveView = (LinearLayout) root.findViewById(R.id.user_requests_active_request);
        formView = (LinearLayout) root.findViewById(R.id.user_requests_creation_form);

        // Get Edit Text Views
        requestNameEdit = (EditText) root.findViewById(R.id.requestNameEdit);
        requestDescriptionEdit = (EditText) root.findViewById(R.id.requestDescriptionEdit);
        locationEdit = (EditText) root.findViewById(R.id.locationEdit);

        sendRequestButton = (Button) root.findViewById(R.id.sendUserRequestButton);

        //Set Fonts
        TypefaceManager.singleton.setTypeface(root.findViewById(R.id.user_requests_base), TypefaceManager.singleton.YANONE[1]);

        //Set Listeners
        sendRequestButton.setOnClickListener(sendRequestButtonAction);
        locationEdit.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                LocationDialog dialog = new LocationDialog();
                                                dialog.setListener(thisObject);
                                                dialog.show(UserRequestFragment.this.getFragmentManager(), LOCATION_TAG);
                                            }
                                        }
        );

        //TODO check STATE !!!

        //hide & show views
        ViewUtils.changeVisibilityOfAllViewChildren(getView(), View.GONE);
        ViewUtils.changeVisibilityOfAllViewChildren(noConnectionView, View.VISIBLE);

        //web api background call
        if (UserData.getInstance().getUserRequest() == null)
            getUserRequest();

        /*Log.d("CC", "Basic "
                + Base64.encodeToString((
                UserData.getInstance().getUserLoginAuth().phone_number
                        + ":" +
                        UserData.getInstance().getUserLoginAuth().validation_code).getBytes(), Base64.DEFAULT));*/

        return root;
    }

    @Override
    public View getView() {
        return root;
    }


    public void POST_Request(UserRequestCreationData userRequest) {
        HttpRequestTask<AODISPOR_JSON_WEBAPI> request =
                RequestBuilder.buildCreateUserRequest(userRequest);
        request.addOnSuccessHandlers(new HttpRequestTask.IOnHttpRequestCompleted<AODISPOR_JSON_WEBAPI>() {
            @Override
            public void exec(AODISPOR_JSON_WEBAPI answer) {
                sendRequestButton.setVisibility(View.GONE);
                Toast.makeText(UserRequestFragment.this.getContext(),
                        getResources().getString(R.string.request_uploaded_successfully)
                        , Toast.LENGTH_LONG).show();
            }
        });
        request.addOnFailHandlers(new HttpRequestTask.IOnHttpRequestCompleted<AODISPOR_JSON_WEBAPI>() {
            @Override
            public void exec(AODISPOR_JSON_WEBAPI answer) {
                Toast.makeText(UserRequestFragment.this.getContext(),
                        getResources().getString(R.string.request_upload_failed)
                        , Toast.LENGTH_LONG).show();
            }
        });
        request.execute();
    }


    /**
     * Makes a GET HTTPS request to get user request information.
     */
    public void getUserRequest() {
        HttpRequestTask<AODISPOR_JSON_WEBAPI> request = RequestBuilder.buildGetUserRequest();
        request.addOnSuccessHandlers(processReceivedData);
        request.addOnSuccessHandlers(new HttpRequestTask.IOnHttpRequestCompleted<AODISPOR_JSON_WEBAPI>() {
            @Override
            public void exec(AODISPOR_JSON_WEBAPI answer) {
                setProperView();
            }
        });
        request.addOnFailHandlers(
                new HttpRequestTask.IOnHttpRequestCompleted<AODISPOR_JSON_WEBAPI>() {
                    @Override
                    public void exec(AODISPOR_JSON_WEBAPI answer) {
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                getUserRequest();
                            }
                        }, WAIT_4RETRY_GET_USERREQUEST);
                    }
                }
        );
        request.execute();
    }

    public void setLocation(String l, String p, String s) {
        locationEdit.setText(l);
        prefix = p;
        suffix = s;
    }

    @Override
    public void onDismiss(boolean set, String locationName, String prefix, String suffix) {
        if (set) {
            setLocation(locationName, prefix, suffix);
            ProfileEditText.runHandler();
        }
    }

    private final HttpRequestTask.IOnHttpRequestCompleted processReceivedData =
            new HttpRequestTask.IOnHttpRequestCompleted<AODISPOR_JSON_WEBAPI>() {
                @Override
                public void exec(AODISPOR_JSON_WEBAPI answer) {

                    if (answer == null) {
                        //No valid answer was received
                        //this should never occur, since an invalid answer should call fail handler
                        Crashlytics.log(Log.WARN, "UserRequestFragment", "processReceivedData: null answer");
                        return;
                    }

                    UserRequests requests = ((UserRequests) answer);
                    if (requests.data == null || requests.data.size() <= 0) {
                        //answer was received but the user hasn't got any request
                        return;
                    }

                    UserData.getInstance().setUserRequest(requests.data.get(0));
                    Log.d("request date", requests.data.get(0).data_expiracao);
                }
            };

    private void setProperView() {
        ViewUtils.changeVisibilityOfAllViewChildren(getView(), View.GONE);
        UserRequestTempData request = UserData.getInstance().getUserRequest();
        if (request != null) {
            updateViewWithAnExistingRequest(request);
            ViewUtils.changeVisibilityOfAllViewChildren(requestActiveView, View.VISIBLE);
        } else {
            ViewUtils.changeVisibilityOfAllViewChildren(formView, View.VISIBLE);
        }
    }

    private void updateViewWithAnExistingRequest(UserRequestTempData request) {
        /*//TODO to finish
        requestNameEdit.setText(request.titulo);
        requestDescriptionEdit.setText(request.codigo_postal_localizacao);
        locationEdit.setText(request.descricao);

        //Log.d("",request.data_expiracao);

        sendRequestButton.setVisibility(View.GONE);*/
    }

    private void cleanFormView(UserRequestTempData request) {
        //TODO to finish
        requestNameEdit.setText("");
        requestDescriptionEdit.setText("");
        locationEdit.setText("");
    }

    private final static int viewsUpdateInterval = 1000;
    private Handler handler;
    /**
     * should only be started when the user has an active request *
     */
    private Runnable updateRequestView = new Runnable() {
        @Override
        public void run() {
            UserRequestTempData request = UserData.getInstance().getUserRequest();

            if (request == null) return;

            long timenow = new Date().getTime();
            Date carddate = request.getExpirationDate();
            if (carddate == null) {
                Crashlytics.log(Log.WARN, "UserRequestFragment", "null carddate");
            }
            long cardTime = carddate.getTime();

            String date = TextUtils.timeDifference(timenow, cardTime, getContext());

            //Request expired. change view to a form
            if (date == null) {
                ViewUtils.changeVisibilityOfAllViewChildren(getView(), View.GONE);
                ViewUtils.changeVisibilityOfAllViewChildren(formView, View.VISIBLE);
                return; //no more updates until new request is sent
            }
            //else if date != null
            //Request is still active. update request view
            ((TextView) root.findViewById(R.id.expiration_date))
                    .setText(date);

            if (handler == null) handler = new Handler();
            handler.postDelayed(this, viewsUpdateInterval);
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        if (handler == null) handler = new Handler();
        handler.postDelayed(updateRequestView, viewsUpdateInterval);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (handler == null) handler = new Handler();
        handler.postDelayed(updateRequestView, viewsUpdateInterval);
    }

    @Override
    public void onPause() {
        handler.removeCallbacksAndMessages(null);
        super.onPause();
    }

    @Override
    public void onStop() {
        handler.removeCallbacksAndMessages(null);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }


}
