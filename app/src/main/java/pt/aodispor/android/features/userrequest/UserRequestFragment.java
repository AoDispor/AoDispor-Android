package pt.aodispor.android.features.userrequest;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import pt.aodispor.android.AppDefinitions;
import pt.aodispor.android.R;
import pt.aodispor.android.api.HttpRequestTask;
import pt.aodispor.android.api.aodispor.RequestBuilder;
import pt.aodispor.android.data.models.aodispor.*;
import pt.aodispor.android.features.profile.LocationDialog;
import pt.aodispor.android.features.profile.ProfileEditText;
import pt.aodispor.android.utils.TypefaceManager;
import pt.aodispor.android.utils.ViewUtils;

public class UserRequestFragment extends Fragment implements LocationDialog.LocationDialogListener {

    private static final String LOCATION_TAG = "location";
    private UserRequestFragment thisObject;
    private LinearLayout noConnectionView;
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
        root = inflater.inflate(R.layout.profile, container, false);

        // Get Text Views
        noConnectionView = (LinearLayout) root.findViewById(R.id.not_loaded_page_layout);

        // Get Edit Text Views
        requestNameEdit = (EditText) root.findViewById(R.id.requestNameEdit);
        requestDescriptionEdit = (EditText) root.findViewById(R.id.requestDescriptionEdit);
        locationEdit = (EditText) root.findViewById(R.id.locationEdit);

        sendRequestButton = (Button) root.findViewById(R.id.sendUserRequestButton);

        setFonts();

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

        //hide & show views
        ViewUtils.changeVisibilityOfAllViewChildren(getView(), View.GONE);
        ViewUtils.changeVisibilityOfAllViewChildren(noConnectionView, View.VISIBLE);

        //web api background call
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
        request.addOnSuccessHandlers(updateFragmentRequest);
        request.addOnSuccessHandlers(new HttpRequestTask.IOnHttpRequestCompleted<AODISPOR_JSON_WEBAPI>() {
            @Override
            public void exec(AODISPOR_JSON_WEBAPI answer) {
                ViewUtils.changeVisibilityOfAllViewChildren(getView(), View.VISIBLE);
                ViewUtils.changeVisibilityOfAllViewChildren(noConnectionView, View.GONE);
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

    private void setFonts() {
        TypefaceManager.singleton.setTypeface(root.findViewById(R.id.profile_base), TypefaceManager.singleton.YANONE[1]);
    }

    private final HttpRequestTask.IOnHttpRequestCompleted updateFragmentRequest =
            new HttpRequestTask.IOnHttpRequestCompleted<AODISPOR_JSON_WEBAPI>() {
                @Override
                public void exec(AODISPOR_JSON_WEBAPI answer) {
                    if (answer == null) return;
                    UserRequests requests = ((UserRequests) answer);
                    if (requests.data == null || requests.data.size() <= 0) return;
                    updateViewWithAnExistingRequest(requests.data.get(0));
                }
            };

    private void updateViewWithAnExistingRequest(UserRequestTempData request) {
        //TODO to finish
        requestNameEdit.setText(request.titulo);
        ;
        requestDescriptionEdit.setText(request.codigo_postal_localizacao);
        locationEdit.setText(request.descricao);
        sendRequestButton.setVisibility(View.GONE);
    }

}
