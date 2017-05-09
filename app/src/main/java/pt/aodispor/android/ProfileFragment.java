package pt.aodispor.android;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import pt.aodispor.android.api.ApiJSON;
import pt.aodispor.android.api.HttpRequestTask;
import pt.aodispor.android.api.HttpRequest;
import pt.aodispor.android.api.Professional;
import pt.aodispor.android.api.SearchQueryResult;
import pt.aodispor.android.dialogs.*;
import pt.aodispor.android.notifications.RegistrationIntentService;
import pt.aodispor.android.dialogs.LocationDialog;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment implements HttpRequest, DialogCallback, LocationDialog.LocationDialogListener {
    private static final String LOCATION_TAG = "location";
    private static final String PRICE_DIALOG_TAG = "price-dialog";
    private static final String URL_MY_PROFILE = "https://api.aodispor.pt/profiles/me";
    private static final String URL_UPLOAD_IMAGE = "https://api.aodispor.pt/users/me/profile/avatar";
    private static final int SELECT_PICTURE = 0;
    private RelativeLayout rootView;
    private RelativeLayout professionalCard;
    private PriceDialog priceDialog;
    private LinearLayout loadingMessage;
    private TextView priceView, locationView;
    private String oldName, oldProfession, oldDescription;
    private CustomEditText nameEditText, professionEditText, descriptionEditText;
    private ImageView imageView;
    private InputMethodManager inputManager;
    private final ProfileFragment thisObject = this;

    public enum PriceType {
        ByHour,
        ByDay,
        ByService
    }

    /**
     * Factory method to create a new instance of ProfileFragment class. This is needed because of how
     * a ViewPager handles the creation of a Fragment.
     *
     * @return the ProfileFragment object created.
     */
    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inputManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        HttpRequestTask.setToken(getResources().getString(R.string.ao_dispor_api_key));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = new RelativeLayout(getActivity());
        rootView.setBackgroundResource(R.drawable.tabletop1);
        professionalCard = (RelativeLayout) inflater.inflate(R.layout.profile_card, container, false);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(professionalCard.getLayoutParams());
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        int p = getResources().getDimensionPixelSize(R.dimen.register_layout_margin);
        params.setMargins(p, p, p, p);
        professionalCard.setLayoutParams(params);

        getViews();
        setFonts();

        // Location
        locationView.setClickable(true);
        locationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocationDialog dialog = new LocationDialog();
                dialog.setListener(thisObject);
                dialog.show(getFragmentManager(), LOCATION_TAG);
            }
        });

        // Price
        priceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show Price Dialog
                if (!priceDialog.isAdded()) {
                    priceDialog.show(getFragmentManager(), PRICE_DIALOG_TAG);
                }
            }
        });

        configureNameEditText();
        configureProfessionEditText();
        configureDescriptionEditText();

        // Loading Message
        loadingMessage = (LinearLayout) professionalCard.findViewById(R.id.loadingWidgetLayout);

        imageView.setClickable(true);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery(SELECT_PICTURE);
            }
        });

        rootView.addView(professionalCard);

        startLoading();
        getProfileInfo();

        rootView.requestFocus();
        return rootView;
    }



    /**
     * Makes a GET HTTP request to get user profile information.
     */
    public void getProfileInfo() {
        /* //TODO UNCOMMENT
        if(AppDefinitions.SKIP_LOGIN == true) {
            return;
        }
        */
        HttpRequestTask request = new HttpRequestTask(SearchQueryResult.class, this, URL_MY_PROFILE);
        request.setMethod(HttpRequestTask.POST_REQUEST);
        request.setType(HttpRequest.UPDATE_PROFILE);
        request.addAPIAuthentication(AppDefinitions.phoneNumber, AppDefinitions.userPassword);
        request.execute();
    }


    /**
     * Started when the HTTP request has finished and succeeded. It then updates the views of the
     * fragment in order to show profile information.
     *
     * @param answer the ApiJSON formatted answer.
     */
    @Override
    public void onHttpRequestSuccessful(ApiJSON answer, int type) {
        Professional p = new Professional();
        switch (type) {
            case HttpRequest.GET_PROFILE:
                SearchQueryResult getProfile = (SearchQueryResult) answer;
                p = (Professional) getProfile.data.get(0);
                break;
            case HttpRequest.UPDATE_PROFILE:
                p = (Professional) ((SearchQueryResult) answer).data.get(0);
                break;
        }
        updateProfileCard(p);

        if (Utility.isProfessionalRegistered(p)) {
            TextView registered = (TextView) professionalCard.findViewById(R.id.registered_note);
            //registered.setVisibility(View.VISIBLE);
            /*  TODO WORKAROUND 'FIX'
                sets registered note visible without using the visibility (solves visibility problem)
            */
            registered.setText(R.string.profile_registered_note_msg);
            registered.setPadding(10,10,10,10);
        }

        endLoading();
    }

    /**
     * Started when the HTTP request is unsuccessful. Shows an error message and ends loading.
     */
    @Override
    public void onHttpRequestFailed(ApiJSON errorData, int type) {
        //TODO -> this fragment shouldn't even be loaded in the first place.
        if(AppDefinitions.smsLoginDone) {
            Toast.makeText(getContext(), R.string.timeout, Toast.LENGTH_LONG).show();
        }
        nameEditText.setText(oldName);
        professionEditText.setText(oldProfession);
        descriptionEditText.setText(oldDescription);
        endLoading();
    }

    /**
     * Started when the price dialog is dismissed. Then it sends a POST request to store the new
     * professional data.
     *
     * @param value   Professional rate.
     * @param isFinal Whether the price is final.
     * @param type    The type of the service.
     */
    @Override
    public void onPriceDialogCallBack(int value, boolean isFinal, PriceType type, String currency) {
        startLoading();
        HttpRequestTask request = new HttpRequestTask(SearchQueryResult.class, this, URL_MY_PROFILE);
        request.setMethod(HttpRequestTask.POST_REQUEST);
        request.setType(HttpRequest.UPDATE_PROFILE);
        request.addAPIAuthentication(AppDefinitions.phoneNumber, AppDefinitions.userPassword);
        Professional p = new Professional();
        if (value > 0) {
            p.rate = value + "";
        }
        switch (type) {
            case ByHour:
                p.type = "H";
                break;
            case ByDay:
                p.type = "D";
                break;
            case ByService:
                p.type = "S";
                break;
        }
        p.currency = currency;
        request.setJSONBody(p);
        request.execute();
    }

    @Override
    public void onDismiss(boolean set, String locationName, String prefix, String suffix) {
        if (set && !locationName.equals(locationView.getText().toString())) {
            startLoading();
            HttpRequestTask request = new HttpRequestTask(SearchQueryResult.class, this, URL_MY_PROFILE);
            request.setMethod(HttpRequestTask.POST_REQUEST);
            request.setType(HttpRequest.UPDATE_PROFILE);
            request.addAPIAuthentication(AppDefinitions.phoneNumber, AppDefinitions.userPassword);
            Professional p = new Professional();
            p.location = locationName;
            p.cp4 = prefix;
            p.cp3 = suffix;
            request.setJSONBody(p);
            request.execute();
            AppDefinitions.postal_code = Integer.parseInt(prefix);
            if (Utility.checkPlayServices(this.getActivity())) {
                // Start IntentService to register this application with GCM.
                Intent intent = new Intent(this.getActivity(), RegistrationIntentService.class);
                this.getActivity().startService(intent);
            }
        }
    }

    private void sendUpdateRequest(Professional p) {
        HttpRequestTask request = new HttpRequestTask(SearchQueryResult.class, this, URL_MY_PROFILE);
        request.setMethod(HttpRequestTask.POST_REQUEST);
        request.setType(HttpRequest.UPDATE_PROFILE);
        request.addAPIAuthentication(AppDefinitions.phoneNumber, AppDefinitions.userPassword);
        request.setJSONBody(p);
        request.execute();
    }

    private void configureNameEditText() {
        oldName = nameEditText.getText().toString();
        nameEditText.setOnBackPressedListener(new CustomEditText.OnBackPressedListener() {
            @Override
            public void onBackPressed() {
                rootView.requestFocus();
                editName();
            }
        });
        nameEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if(actionId == EditorInfo.IME_ACTION_DONE) {
                    rootView.requestFocus();
                    hideSoftKeyboard();
                    editName();
                }
                return false;
            }
        });
        nameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(hasFocus) {
                    disableSwiping();
                    setAllViewsEnabledExcept(false, view);
                    oldName = nameEditText.getText().toString();
                } else {
                    enableSwiping();
                    enableAllViews();
                }
            }
        });
    }

    private void configureProfessionEditText() {
        oldProfession = professionEditText.getText().toString();
        professionEditText.setOnBackPressedListener(new CustomEditText.OnBackPressedListener() {
            @Override
            public void onBackPressed() {
                rootView.requestFocus();
                editProfession();
            }
        });
        professionEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if(actionId == EditorInfo.IME_ACTION_DONE) {
                    rootView.requestFocus();
                    hideSoftKeyboard();
                    editProfession();
                }
                return false;
            }
        });
        professionEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(hasFocus) {
                    disableSwiping();
                    setAllViewsEnabledExcept(false, view);
                    oldProfession = professionEditText.getText().toString();
                } else {
                    enableSwiping();
                    enableAllViews();
                }
            }
        });
    }

    private void configureDescriptionEditText() {
        oldDescription = descriptionEditText.getText().toString();
        descriptionEditText.setOnBackPressedListener(new CustomEditText.OnBackPressedListener() {
            @Override
            public void onBackPressed() {
                rootView.requestFocus();
                editDescription();
            }
        });
        descriptionEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if(actionId == EditorInfo.IME_ACTION_DONE) {
                    rootView.requestFocus();
                    hideSoftKeyboard();
                    editDescription();
                }
                return false;
            }
        });
        descriptionEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(hasFocus) {
                    disableSwiping();
                    setAllViewsEnabledExcept(false, view);
                    oldDescription = descriptionEditText.getText().toString();
                } else {
                    enableSwiping();
                    enableAllViews();
                }
            }
        });
    }

    private void editName() {
        String newName = nameEditText.getText().toString().trim().replaceAll("\\s{2,}", " ");
        if (!newName.equals(oldName)) {
            Professional p = new Professional();
            p.full_name = newName;
            startLoading();
            sendUpdateRequest(p);
        } else {
            nameEditText.setText(oldName);
        }
    }

    private void editProfession() {
        String newProfession = professionEditText.getText().toString().trim().replaceAll("\\s{2,}", " ");
        if (!newProfession.equals(oldProfession)) {
            Professional p = new Professional();
            p.title = newProfession;
            startLoading();
            sendUpdateRequest(p);
        } else {
            professionEditText.setText(oldProfession);
        }
    }

    private void editDescription() {
        String newDescription = descriptionEditText.getText().toString().trim().replaceAll("\\s{2,}", " ");
        if (!newDescription.equals(oldDescription)) {
            Professional p = new Professional();
            p.description = newDescription;
            startLoading();
            sendUpdateRequest(p);
        } else {
            descriptionEditText.setText(oldDescription);
        }
    }

    /*
     * Updates professional profile views and fills the views which aren't filled yet by the user
     * with placeholder text
     */
    private void updateProfileCard(Professional p) {
        // Colors
        int grey = ContextCompat.getColor(getActivity(), R.color.grey);
        int black = ContextCompat.getColor(getActivity(), R.color.black);

        // Price View
        String priceText = p.rate;
        String type = p.type;
        String curr = p.currency;
        int rate;
        PriceType t = PriceType.ByHour;
        if (priceText != null && type != null && curr != null) {
            rate = Integer.parseInt(priceText);
            priceText += " " + curr;
            switch (type) {
                case "H":
                    t = PriceType.ByHour;
                    priceText += "/h";
                    priceView.setTextColor(ContextCompat.getColor(getContext(), R.color.by_hour));
                    break;
                case "S":
                    t = PriceType.ByService;
                    priceView.setTextColor(ContextCompat.getColor(getContext(), R.color.by_service));
                    break;
                case "D":
                    t = PriceType.ByDay;
                    priceText += "/por dia";
                    priceView.setTextColor(ContextCompat.getColor(getContext(), R.color.aoDispor2));
                    break;
            }
            priceView.setText(priceText);
            priceDialog = PriceDialog.newInstance(this, rate, true, t.ordinal());
        } else {
            priceView.setTextColor(grey);
            priceView.setText(R.string.register_price);
            priceDialog = PriceDialog.newInstance(this, 0, true, PriceType.ByHour.ordinal());
        }

        // Location
        String locationText = p.location;
        if (locationText != null) {
            locationView.setText(locationText);
            locationView.setTextColor(black);
        } else {
            locationView.setTextColor(grey);
            locationView.setText(R.string.register_location);
        }

        // Profession
        String professionText = p.title;
        if (professionText != null) {
            professionEditText.setText(professionText);
            oldProfession = professionText;
        } else {
            professionEditText.setText("");
        }

        // Name
        String nameText = p.full_name;
        if (nameText != null) {
            nameEditText.setText(nameText);
            oldName = nameText;
        } else {
            nameEditText.setText("");
        }

        // Description
        String descriptionText = p.description;
        if (descriptionText != null) {
            descriptionEditText.setText(descriptionText);
            oldDescription = descriptionText;
        } else {
            descriptionEditText.setText("");
        }

        // Profile Image
        String imageUrl = p.avatar_url;
        imageView.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.image_placeholder));
        if (imageUrl != null) {
            ImageLoader imageLoader = ImageLoader.getInstance();
            DisplayImageOptions options = new DisplayImageOptions.Builder().displayer(new RoundedBitmapDisplayer(getResources().getDimensionPixelSize(R.dimen.image_border))).build();
            imageLoader.displayImage(imageUrl, imageView, options);
        }
    }

    private void startLoading() {
        hideViews();
        loadingMessage.setVisibility(View.VISIBLE);
    }

    private void endLoading() {
        showViews();
        loadingMessage.setVisibility(LinearLayout.INVISIBLE);
    }

    private void setAllViewsEnabledExcept(boolean enable, View view) {
        for (int i = 0; i < professionalCard.getChildCount(); i++) {
            if(view.getId() != professionalCard.getChildAt(i).getId()) {
                professionalCard.getChildAt(i).setEnabled(enable);
            }
        }
    }

    private void enableAllViews() {
        for (int i = 0; i < professionalCard.getChildCount(); i++) {
            professionalCard.getChildAt(i).setEnabled(true);
        }
    }

    private void hideSoftKeyboard() {
        View view = getActivity().getCurrentFocus();
        if(view != null) {
            inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }
    }

    private void hideViews() {
        for (int i = 0; i < professionalCard.getChildCount(); i++) {
            professionalCard.getChildAt(i).setVisibility(View.INVISIBLE);
        }
    }

    private void showViews() {
        for (int i = 0; i < professionalCard.getChildCount(); i++) {
            professionalCard.getChildAt(i).setVisibility(View.VISIBLE);
        }
    }

    public void openGallery(int req_code) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        startActivityForResult(intent, req_code);
    }

    private void getViews() {
        priceView = (TextView) professionalCard.findViewById(R.id.price);
        locationView = (TextView) professionalCard.findViewById(R.id.location);
        nameEditText = (CustomEditText) professionalCard.findViewById(R.id.nameEditText);
        professionEditText = (CustomEditText) professionalCard.findViewById(R.id.professionEditText);
        descriptionEditText = (CustomEditText) professionalCard.findViewById(R.id.descriptionEditText);
        imageView = (ImageView) professionalCard.findViewById(R.id.profile_image);
    }

    private void setFonts() {
        priceView.setTypeface(AppDefinitions.yanoneKaffeesatzRegular);
        locationView.setTypeface(AppDefinitions.yanoneKaffeesatzRegular);
        nameEditText.setTypeface(AppDefinitions.yanoneKaffeesatzRegular);
        professionEditText.setTypeface(AppDefinitions.yanoneKaffeesatzRegular);
        descriptionEditText.setTypeface(AppDefinitions.yanoneKaffeesatzRegular);
    }

    private void disableSwiping() {
        ((MainActivity)getActivity()).getViewPager().setSwipeEnabled(false);
    }

    private void enableSwiping() {
        ((MainActivity)getActivity()).getViewPager().setSwipeEnabled(true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == SELECT_PICTURE && data != null) {
            try {
                startLoading();

                Uri uri = data.getData();
                InputStream imageStream = null;

                imageStream = this.getContext().getContentResolver().openInputStream(uri);
                Bitmap originalImage = BitmapFactory.decodeStream(imageStream);
                Bitmap image = Bitmap.createScaledBitmap(originalImage, 1024, 1024, true);

                if(image != null) {
                    int byteNum = image.getByteCount();
                    ByteBuffer buffer = ByteBuffer.allocate(byteNum);
                    image.copyPixelsToBuffer(buffer);
                    HttpRequestTask request = new HttpRequestTask(SearchQueryResult.class, this, URL_UPLOAD_IMAGE);
                    request.setMethod(HttpRequestTask.PUT_REQUEST);
                    request.setType(HttpRequest.UPDATE_PROFILE);
                    request.addAPIAuthentication(AppDefinitions.phoneNumber, AppDefinitions.userPassword);
                    request.setBitmapBody(Utility.convertBitmapToBinary(image));
                    request.execute();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }
    }

}
