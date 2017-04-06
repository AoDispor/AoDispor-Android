package pt.aodispor.android.profile;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import pt.aodispor.android.AppDefinitions;
import pt.aodispor.android.R;
import pt.aodispor.android.api.ApiJSON;
import pt.aodispor.android.api.HttpRequest;
import pt.aodispor.android.api.HttpRequestTask;
import pt.aodispor.android.api.Professional;
import pt.aodispor.android.api.SearchQueryResult;
import pt.aodispor.android.dialogs.LocationDialog;
import pt.aodispor.android.dialogs.NewPriceDialog;

import static android.app.Activity.RESULT_OK;

public class Profile extends ListItem implements HttpRequest, LocationDialog.LocationDialogListener, NewPriceDialog.PriceDialogListener {
    private static final int SELECT_PICTURE = 0;
    private static final String URL_MY_PROFILE = "https://api.aodispor.pt/profiles/me";
    private final String LOCATION_TAG = "location";
    private static final String PRICE_DIALOG_TAG = "price-dialog";
    private Profile thisObject;
    private Fragment parentFragment;
    private TextView imageView, nameView, professionView, locationView, priceView, descriptionView;
    private EditText nameEdit, professionEdit, locationEdit, priceEdit, descriptionEdit;
    private ImageView profileImage;
    private View root;
    private int rate;
    private boolean isFinal;
    private String currency;
    private NewPriceDialog.PriceType type;
    private Uri tempUri;

    public Profile(Context c, Fragment a) {
        super(c);
        HttpRequestTask.setToken(context.getResources().getString(R.string.ao_dispor_api_key));
        thisObject = this;
        parentFragment = a;
        root = LayoutInflater.from(context).inflate(R.layout.profile, null);

        // Get Text Views
        imageView = (TextView) root.findViewById(R.id.imageText);
        nameView = (TextView) root.findViewById(R.id.name);
        professionView = (TextView) root.findViewById(R.id.profession);
        priceView = (TextView) root.findViewById(R.id.price);
        locationView = (TextView) root.findViewById(R.id.location);
        descriptionView = (TextView) root.findViewById(R.id.description);

        // Get Edit Text Views
        nameEdit = (EditText) root.findViewById(R.id.nameEdit);
        professionEdit = (EditText) root.findViewById(R.id.professionEdit);
        locationEdit = (EditText) root.findViewById(R.id.locationEdit);
        priceEdit = (EditText) root.findViewById(R.id.priceEdit);
        descriptionEdit = (EditText) root.findViewById(R.id.descriptionEdit);

        // Get image
        profileImage = (ImageView) root.findViewById(R.id.profileImage);

        setFonts();

        locationEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocationDialog dialog = new LocationDialog();
                dialog.setListener(thisObject);
                dialog.show(parentFragment.getFragmentManager(), LOCATION_TAG);
            }
        });

        priceEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewPriceDialog dialog = NewPriceDialog.newInstance(rate, isFinal, type, currency);
                dialog.setListener(thisObject);
                dialog.show(parentFragment.getFragmentManager(), PRICE_DIALOG_TAG);
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                parentFragment.startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
            }
        });
    }

    @Override
    public View getView() {
        return root;
    }

    @Override
    public boolean onStart() {
        getProfileInfo();
        return false;
    }

    @Override
    public boolean onUpdate() {
        Professional p = new Professional();
        p.full_name = nameEdit.getText().toString();
        p.title = professionEdit.getText().toString();
        p.location = locationEdit.getText().toString();
        //TODO other fields
        HttpRequestTask request = new HttpRequestTask(SearchQueryResult.class, this, URL_MY_PROFILE);
        request.setMethod(HttpRequestTask.POST_REQUEST);
        request.setType(HttpRequest.UPDATE_PROFILE);
        request.addAPIAuthentication(AppDefinitions.phoneNumber, AppDefinitions.userPassword);
        request.setJSONBody(p);
        request.execute();
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            switch (requestCode) {
                case SELECT_PICTURE:
                   try {
                       File tempFile = File.createTempFile("crop", "", context.getCacheDir());
                       tempFile.deleteOnExit();
                       tempUri = Uri.fromFile(tempFile);
                       UCrop.Options options = new UCrop.Options();
                       options.setLogoColor(ContextCompat.getColor(context, R.color.aoDispor2));
                       options.setToolbarColor(ContextCompat.getColor(context, R.color.aoDispor));
                       options.setCropFrameColor(ContextCompat.getColor(context, R.color.white));
                       options.setCropGridColor(ContextCompat.getColor(context, R.color.white));
                       options.setActiveWidgetColor(ContextCompat.getColor(context, R.color.aoDispor));
                       options.setDimmedLayerColor(ContextCompat.getColor(context, R.color.aoDispor));
                       options.setStatusBarColor(ContextCompat.getColor(context, R.color.aoDispor));
                       options.setToolbarWidgetColor(ContextCompat.getColor(context, R.color.white));
                       UCrop.of(data.getData(), tempUri).withAspectRatio(1, 1).withOptions(options).start(context, parentFragment);
                   } catch (IOException e) {
                       e.printStackTrace();
                   }
                   break;
                case UCrop.REQUEST_CROP:
                    final Uri resultUri = UCrop.getOutput(data);
                    if(resultUri != null) {
                        try {
                            InputStream imageStream = context.getContentResolver().openInputStream(resultUri);
                            Bitmap image = BitmapFactory.decodeStream(imageStream);
                            profileImage.setImageBitmap(image);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
            }
        }
    }

    /**
     * Makes a GET HTTP request to get user profile information.
     */
    public void getProfileInfo() {
        HttpRequestTask request = new HttpRequestTask(SearchQueryResult.class, this, URL_MY_PROFILE);
        request.setMethod(HttpRequestTask.POST_REQUEST);
        request.setType(HttpRequest.UPDATE_PROFILE);
        request.addAPIAuthentication(AppDefinitions.phoneNumber, AppDefinitions.userPassword);
        request.execute();
    }

    public void updateProfile(Professional professional) {
        setName(professional.full_name);
        setProfession(professional.title);
        setLocation(professional.location);
        int rate = Integer.parseInt(professional.rate);
        boolean isFinal = Boolean.parseBoolean("true");
        NewPriceDialog.PriceType type = NewPriceDialog.PriceType.ByDay;
        switch (professional.type) {
            case "H":
                type = NewPriceDialog.PriceType.ByHour;
                break;
            case "D":
                type = NewPriceDialog.PriceType.ByDay;
                break;
            case "S":
                type = NewPriceDialog.PriceType.ByService;
                break;
        }
        setPrice(rate, isFinal, type, professional.currency);
        setDescription(professional.description);
    }

    public void setName(String n) {
        nameEdit.setText(n);
    }

    public void setProfession(String p) {
        professionEdit.setText(p);
    }

    public void setLocation(String l) {
        locationEdit.setText(l);
    }

    public void setPrice(int p, boolean f, NewPriceDialog.PriceType t, String c) {
        rate = p;
        isFinal = f;
        type = t;
        currency = c;
        priceEdit.setText(rate + " " + currency);
    }

    public void setDescription(String d) {
        descriptionEdit.setText(d);
    }

    public String getName() {
        return nameEdit.getText().toString();
    }

    public String getProfession() {
        return professionEdit.getText().toString();
    }

    public String getLocation() {
        return locationEdit.getText().toString();
    }

    public String getPrice() {
        return priceEdit.getText().toString();
    }

    public String getDescription() {
        return descriptionEdit.getText().toString();
    }

    @Override
    public void onDismiss(boolean set, String locationName, String prefix, String suffix) {
        if(set) {
            setLocation(locationName);
        } else {
            setLocation("");
        }
    }

    @Override
    public void onPriceChanged(int rate, boolean isFinal, NewPriceDialog.PriceType type, String currency) {
        setPrice(rate, isFinal, type, currency);
    }

    private void setFonts() {
        // Text Views
        imageView.setTypeface(AppDefinitions.yanoneKaffeesatzRegular);
        nameView.setTypeface(AppDefinitions.yanoneKaffeesatzBold);
        professionView.setTypeface(AppDefinitions.yanoneKaffeesatzBold);
        priceView.setTypeface(AppDefinitions.yanoneKaffeesatzBold);
        locationView.setTypeface(AppDefinitions.yanoneKaffeesatzBold);
        descriptionView.setTypeface(AppDefinitions.yanoneKaffeesatzBold);

        // Edit Views
        nameEdit.setTypeface(AppDefinitions.yanoneKaffeesatzBold);
        professionEdit.setTypeface(AppDefinitions.yanoneKaffeesatzBold);
        locationEdit.setTypeface(AppDefinitions.yanoneKaffeesatzBold);
        priceEdit.setTypeface(AppDefinitions.yanoneKaffeesatzBold);
        descriptionEdit.setTypeface(AppDefinitions.yanoneKaffeesatzBold);
    }

    @Override
    public void onHttpRequestCompleted(ApiJSON answer, int type) {
        Professional p = new Professional();
        switch (type) {
            case HttpRequest.GET_PROFILE:
                SearchQueryResult getProfile = (SearchQueryResult) answer;
                p = getProfile.data.get(0);
                break;
            case HttpRequest.UPDATE_PROFILE:
                p = ((SearchQueryResult) answer).data.get(0);
                break;
        }
        updateProfile(p);
        notification.notify(this, true, "");
    }

    @Override
    public void onHttpRequestFailed(ApiJSON errorData) {
        notification.notify(this, false, context.getString(R.string.timeout));
    }
}
