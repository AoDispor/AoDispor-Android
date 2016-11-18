package pt.aodispor.aodispor_android;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

import pt.aodispor.aodispor_android.API.ApiJSON;
import pt.aodispor.aodispor_android.API.HttpRequestTask;
import pt.aodispor.aodispor_android.API.HttpRequest;
import pt.aodispor.aodispor_android.API.Professional;
import pt.aodispor.aodispor_android.API.SearchQueryResult;
import pt.aodispor.aodispor_android.Dialogs.DialogCallback;
import pt.aodispor.aodispor_android.Dialogs.PriceDialog;

import static android.app.Activity.RESULT_OK;
import static pt.aodispor.aodispor_android.R.id.location;

public class ProfileFragment extends Fragment implements HttpRequest, DialogCallback {
    private static final String URL_MY_PROFILE = "https://api.aodispor.pt/profiles/me";
    private static final String URL_UPLOAD_IMAGE = "https://api.aodispor.pt/users/me/profile/avatar";
    private static final int SELECT_PICTURE = 0;

    private final String phoneNumber = "+351 912 488 434";
    private final String password = "123456";
    private RelativeLayout professionalCard;
    private PriceDialog priceDialog;
    private LinearLayout loadingMessage;
    private TextView priceView, locationView, professionView, descriptionView;
    private ImageView imageView;

    public enum PriceType { ByHour, ByDay, ByService }

    /**
     * Factory method to create a new instance of ProfileFragment class. This is needed because of how
     * a ViewPager handles the creation of a Fragment.
     * @return the ProfileFragment object created.
     */
    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RelativeLayout rootView = new RelativeLayout(getActivity());
        professionalCard = (RelativeLayout) inflater.inflate(R.layout.professional_card, container, false);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(professionalCard.getLayoutParams());
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        int p = getResources().getDimensionPixelSize(R.dimen.register_layout_margin);
        params.setMargins(p,p,p,p);
        professionalCard.setLayoutParams(params);

        // Get Views
        priceView = (TextView) professionalCard.findViewById(R.id.price);
        locationView = (TextView) professionalCard.findViewById(location);
        professionView = (TextView) professionalCard.findViewById(R.id.profession);
        descriptionView = (TextView) professionalCard.findViewById(R.id.description);
        imageView = (ImageView) professionalCard.findViewById(R.id.profile_image);

        priceView.setTypeface(AppDefinitions.yanoneKaffeesatzRegular);
        locationView.setTypeface(AppDefinitions.yanoneKaffeesatzRegular);
        professionView.setTypeface(AppDefinitions.yanoneKaffeesatzRegular);
        descriptionView.setTypeface(AppDefinitions.yanoneKaffeesatzRegular);

        locationView.setClickable(true);
        locationView.setOnClickListener(new LocationOnClickListener(this.getActivity(), this, locationView));

        // Price
        priceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show Price Dialog
                if(!priceDialog.isAdded()){
                    priceDialog.show(getFragmentManager(),"dialog");
                }
            }
        });

        // Loading Message
        loadingMessage = (LinearLayout) professionalCard.findViewById(R.id.loadingMessage);

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

        return rootView;
    }

    /**
     * Makes a GET HTTP request to get user profile information.
     */
    public void getProfileInfo(){
        HttpRequestTask request = new HttpRequestTask(Professional.class, this, URL_MY_PROFILE);
        request.setMethod(HttpRequestTask.POST_REQUEST);
        request.setType(HttpRequest.UPDATE_PROFILE);
        request.addAPIAuthentication(phoneNumber, password);
        /*
        HttpRequestTask request = new HttpRequestTask(SearchQueryResult.class, this, "https://api.aodispor.pt/profiles/porto5125"); //TODO change this
        request.setMethod(HttpRequestTask.GET_REQUEST);
        */
        request.addAPIAuthentication("+351 912 488 434","123456");
        request.execute();
    }

    /**
     * Started when the HTTP request has finished and succeeded. It then updates the views of the
     * fragment in order to show profile information.
     * @param answer the ApiJSON formatted answer.
     */
    @Override
    public void onHttpRequestCompleted(ApiJSON answer, int type) {
        Professional p = new Professional();
        switch (type) {
            case HttpRequest.GET_PROFILE:
                SearchQueryResult getProfile = (SearchQueryResult) answer;
                p = getProfile.data.get(0);
                break;
            case HttpRequest.UPDATE_PROFILE:
                p = (Professional) answer;
                break;
        }
        updateProfileCard(p);
        endLoading();
    }

    /**
     * Started when the HTTP request is unsuccessful. Shows an error message and ends loading.
     */
    @Override
    public void onHttpRequestFailed() {
        Toast.makeText(getContext(), R.string.timeout, Toast.LENGTH_LONG).show();
        endLoading();
    }

    /**
     * Started when the price dialog is dismissed. Then it sends a POST request to store the new
     * professional data.
     * @param value Professional rate.
     * @param isFinal Whether the price is final.
     * @param type The type of the service.
     */
    @Override
    public void onPriceDialogCallBack(int value, boolean isFinal, PriceType type) {
        startLoading();
        HttpRequestTask request = new HttpRequestTask(Professional.class, this, URL_MY_PROFILE);
        request.setMethod(HttpRequestTask.POST_REQUEST);
        request.setType(HttpRequest.UPDATE_PROFILE);
        request.addAPIAuthentication(phoneNumber, password);
        Professional p = new Professional();
        p.rate = value + "";
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
        request.setJSONBody(p);
        request.execute();
    }

    @Override
    public void onLocationDialogCallBack(String location, String cp4, String cp3, boolean isSet) {
        if(isSet){
            startLoading();
            HttpRequestTask request = new HttpRequestTask(Professional.class, this, URL_MY_PROFILE);
            request.setMethod(HttpRequestTask.POST_REQUEST);
            request.setType(HttpRequest.UPDATE_PROFILE);
            request.addAPIAuthentication(phoneNumber, password);
            Professional p = new Professional();
            p.location = location;
            p.cp4 = cp4;
            p.cp3 = cp3;
            request.setJSONBody(p);
            request.execute();
        }
    }

    /*
     * Updates professional profile views and fills the views which aren't filled yet by the user
     * with placeholder text
     */
    private void updateProfileCard(Professional p){
        // Colors
        int grey = ContextCompat.getColor(getActivity(), R.color.grey);
        int black = ContextCompat.getColor(getActivity(), R.color.black);

        // Price View
        String priceText = p.rate;
        String type = p.type;
        String curr = p.currency;
        int rate;
        PriceType t = PriceType.ByHour;
        if (priceText != null && type != null && curr != null ) {
            rate = Integer.parseInt(priceText);
            priceText += " " +curr;
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
        }

        // Location
        String locationText = p.location;
        if (locationText != null) {
            locationView.setText(locationText);
            locationView.setTextColor(black);
        }else {
            locationView.setTextColor(grey);
            locationView.setText(R.string.register_location);
        }

        // Profession
        String professionText = p.title;
        if (professionText != null) {
            professionView.setText(professionText);
            professionView.setTextColor(black);
        } else {
            professionView.setTextColor(grey);
            professionView.setText(R.string.register_profession);
        }

        // Description
        String descriptionText = p.description;
        if (descriptionText != null) {
            descriptionView.setText(descriptionText);
            descriptionView.setTextColor(black);
        } else {
            descriptionView.setTextColor(grey);
            descriptionView.setText(R.string.register_description);
        }

        // Profile Image
        String imageUrl = p.avatar_url;
        if (imageUrl != null) {
            ImageLoader imageLoader = ImageLoader.getInstance();
            DisplayImageOptions options = new DisplayImageOptions.Builder().displayer(new RoundedBitmapDisplayer(getResources().getDimensionPixelSize(R.dimen.image_border))).build();
            imageView.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.image_placeholder));
            imageLoader.displayImage(imageUrl, imageView, options);
        } else {
            imageView.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.image_placeholder));
        }
    }

    private void startLoading(){
        hideViews();
        loadingMessage.setVisibility(LinearLayout.VISIBLE);
    }

    private void endLoading(){
        showViews();
        loadingMessage.setVisibility(LinearLayout.INVISIBLE);
    }

    private void hideViews(){
        for (int i = 0; i < professionalCard.getChildCount(); i++){
            professionalCard.getChildAt(i).setVisibility(View.INVISIBLE);
        }
    }

    private void showViews(){
        for (int i = 0; i < professionalCard.getChildCount(); i++){
            professionalCard.getChildAt(i).setVisibility(View.VISIBLE);
        }
    }

    public void openGallery(int req_code) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.putExtra("crop","true");
        intent.putExtra("aspectX",1);
        intent.putExtra("aspectY",1);
        intent.putExtra("outputX",200);
        intent.putExtra("outputY",200);
        intent.putExtra("return-data",true);
        startActivityForResult(intent, req_code);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == SELECT_PICTURE && data != null) {
            startLoading();
            Bundle bundle = data.getExtras();
            Bitmap image = bundle.getParcelable("data");

            HttpRequestTask request = new HttpRequestTask(Professional.class, this, URL_UPLOAD_IMAGE);
            request.setMethod(HttpRequestTask.PUT_REQUEST);
            request.setType(HttpRequest.UPDATE_PROFILE);
            request.addAPIAuthentication(phoneNumber, password);

            int byteNum = image.getByteCount();
            ByteBuffer buffer = ByteBuffer.allocate(byteNum);
            image.copyPixelsToBuffer(buffer);

            request.setBitmapBody(buffer.array());
            request.execute();

            //imageView.setImageBitmap(image);
        }
    }

    public byte[] convertToBinary(Bitmap image) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, stream); //not lossless
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

}
