package pt.aodispor.android.features.profile;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
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
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import pt.aodispor.android.AppDefinitions;
import pt.aodispor.android.R;
import pt.aodispor.android.utils.Utility;
import pt.aodispor.android.data.models.aodispor.ApiJSON;
import pt.aodispor.android.api.HttpRequestTask;
import pt.aodispor.android.data.models.aodispor.Professional;
import pt.aodispor.android.data.models.aodispor.SearchQueryResult;
import pt.aodispor.android.data.local.UserData;
import pt.aodispor.android.data.models.aodispor.meta.CurrencyType;
import pt.aodispor.android.data.models.aodispor.meta.PaymentType;

import static android.app.Activity.RESULT_OK;

public class Profile extends Fragment implements LocationDialog.LocationDialogListener, NewPriceDialog.PriceDialogListener {
    private static final int SELECT_PICTURE = 0;
    private static final String LOCATION_TAG = "location";
    private static final String PRICE_DIALOG_TAG = "price-dialog";
    private Profile thisObject;
    private TextView imageView, nameView, professionView, locationView, priceView, descriptionView, noConnectionView;
    private EditText nameEdit, professionEdit, locationEdit, priceEdit, descriptionEdit;
    private ImageView profileImage, noConnectionImg;
    private View root;
    private String prefix, suffix; //cp4 & cp3
    private int rate = -1;
    private boolean isFinal;
    private CurrencyType currency = null;
    private PaymentType type =null;
    private Uri tempUri;

    //static boolean profileLoaded = false;
    static final int WAIT_4RETRY_GET_PROFILE = 5000;
    Professional previous;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //createHandler();
    }

    Context context;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(!AppDefinitions.smsLoginDone) return null;//TODO quick fix, might not be the best solution

        context = this.getContext();
        HttpRequestTask.setToken(context.getResources().getString(R.string.ao_dispor_api_key));
        thisObject = this;
        root = inflater.inflate(R.layout.profile, container, false);

        // Get Text Views
        imageView = (TextView) root.findViewById(R.id.imageText);
        nameView = (TextView) root.findViewById(R.id.name);
        professionView = (TextView) root.findViewById(R.id.profession);
        priceView = (TextView) root.findViewById(R.id.price);
        locationView = (TextView) root.findViewById(R.id.location);
        descriptionView = (TextView) root.findViewById(R.id.description);
        noConnectionView = (TextView) root.findViewById(R.id.profile_not_loaded_text);

        // Get Edit Text Views
        nameEdit = (EditText) root.findViewById(R.id.nameEdit);
        professionEdit = (EditText) root.findViewById(R.id.professionEdit);
        locationEdit = (EditText) root.findViewById(R.id.locationEdit);
        priceEdit = (EditText) root.findViewById(R.id.priceEdit);
        descriptionEdit = (EditText) root.findViewById(R.id.descriptionEdit);

        // Get image
        profileImage = (ImageView) root.findViewById(R.id.profileImage);
        noConnectionImg = (ImageView) root.findViewById(R.id.profile_not_loaded_img);

        setFonts();

        //Set Listeners
        locationEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocationDialog dialog = new LocationDialog();
                dialog.setListener(thisObject);
                dialog.show(Profile.this.getFragmentManager(), LOCATION_TAG);
            }
        });

        priceEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewPriceDialog dialog = NewPriceDialog.newInstance(rate, isFinal, type, currency);
                dialog.setListener(thisObject);
                dialog.show(Profile.this.getFragmentManager(), PRICE_DIALOG_TAG);
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                Profile.this.startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
            }
        });

        ProfileEditText.setUpdateFragment(
                new Runnable() {
                    @Override
                    public void run() {
                        POST_Profile();
                    }
                }
        );

        //hide stuff until page is loaded
        Utility.apply2AllChildrenBFS(getView(), new Utility.IViewModifier() {
            @Override
            public void apply(View v) {
                if (v instanceof EditText
                        || v instanceof TextView
                        || v instanceof ImageView
                        ) v.setVisibility(View.GONE);
            }
        });
        noConnectionView.setVisibility(View.VISIBLE);
        noConnectionImg.setVisibility(View.VISIBLE);

        getProfileInfo();

        return root;
    }

    @Override
    public View getView() {
        return root;
    }

    //@Override
    public void POST_Profile() {
        previous = UserData.getInstance().getProfileState();
        Professional p = UserData.getInstance().getProfileState();
        if (p == null) Log.e("POST_Profile", "NULL OBJECT!");

        p.full_name = nameEdit.getText().toString().trim().replaceAll("\\s{2,}", " ");
        p.title = professionEdit.getText().toString().trim().replaceAll("\\s{2,}", " ");
        String location = locationEdit.getText().toString().trim().replaceAll("\\s{2,}", " ");
        if (!location.isEmpty() && prefix != null && suffix != null) {
            p.location = location;
            p.cp4 = prefix;
            p.cp3 = suffix;
        } else p.location = null; //must not be sent if cp3 and cp4 not defined!

        if (rate >= 0) {
            p.rate = Integer.toString(rate);
        }
        p.type = type.getAPICode();
        if (currency != null) p.currency = currency.getAPICode();
        p.description = descriptionEdit.getText().toString();

        HttpRequestTask request = HttpRequestTask.POST(SearchQueryResult.class, AppDefinitions.URL_MY_PROFILE);
        request.addAPIAuthentication(AppDefinitions.phoneNumber, AppDefinitions.userPassword);
        request.setJSONBody(p);
        request.addOnSuccessHandlers(new HttpRequestTask.IOnHttpRequestCompleted() {
            @Override
            public void exec(ApiJSON answer) {
                UserData.getInstance().updateProfileState(
                        (Professional) ((SearchQueryResult) answer).data.get(0)
                )
                ;
            }
        });
        request.addOnFailHandlers(new HttpRequestTask.IOnHttpRequestCompleted() {
            @Override
            public void exec(ApiJSON answer) {
                updateView(previous);
                Toast.makeText(getContext(), "Não foi possível atualizar dados...", Toast.LENGTH_SHORT).show();
            }
        });
        request.execute();
    }

    void PUT_Image(final Bitmap image) {
        HttpRequestTask imageRequest = HttpRequestTask.PUT(SearchQueryResult.class, AppDefinitions.URL_UPLOAD_IMAGE);
        imageRequest.addAPIAuthentication(AppDefinitions.phoneNumber, AppDefinitions.userPassword);
        imageRequest.setBitmapBody(Utility.convertBitmapToBinary(image));
        imageRequest.addOnSuccessHandlers(new HttpRequestTask.IOnHttpRequestCompleted() {
            @Override
            public void exec(ApiJSON answer) {
                profileImage.setImageBitmap(image);
            }
        });
        imageRequest.addOnEndHandlers(new HttpRequestTask.IOnHttpRequestCompleted() {
            @Override
            public void exec(ApiJSON answer) {
                ImageLoader.getInstance().clearDiskCache();
            }
        });
        imageRequest.execute();
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
                        UCrop.of(data.getData(), tempUri).withAspectRatio(1, 1).withOptions(options).start(context, this);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case UCrop.REQUEST_CROP:
                    final Uri resultUri = UCrop.getOutput(data);
                    if (resultUri != null) {
                        try {
                            InputStream imageStream = context.getContentResolver().openInputStream(resultUri);
                            Bitmap image = BitmapFactory.decodeStream(imageStream);
                            PUT_Image(image);
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
        HttpRequestTask request = HttpRequestTask.POST(SearchQueryResult.class, AppDefinitions.URL_MY_PROFILE);
        request.addOnSuccessHandlers(updateLocalUserProfile);
        request.addAPIAuthentication(AppDefinitions.phoneNumber, AppDefinitions.userPassword);
        Log.d("QWE",AppDefinitions.phoneNumber + "_" + AppDefinitions.userPassword);
        request.addOnSuccessHandlers(new HttpRequestTask.IOnHttpRequestCompleted() {
            @Override
            public void exec(ApiJSON answer) {
                //show stuff
                Utility.apply2AllChildrenBFS(getView(), new Utility.IViewModifier() {
                    @Override
                    public void apply(View v) {
                        if (v instanceof EditText
                                || v instanceof TextView
                                || v instanceof ImageView
                                ) v.setVisibility(View.VISIBLE);
                    }
                });
                noConnectionView.setVisibility(View.GONE);
                noConnectionImg.setVisibility(View.GONE);
            }
        });
        request.addOnFailHandlers(
                new HttpRequestTask.IOnHttpRequestCompleted() {
                    @Override
                    public void exec(ApiJSON answer) {
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                getProfileInfo();
                            }
                        }, WAIT_4RETRY_GET_PROFILE);
                    }
                }
        );
        request.execute();
    }

    public void setName(String n) {
        nameEdit.setText(n);
    }

    public void setProfession(String p) {
        professionEdit.setText(p);
    }

    public void setLocation(String l, String p, String s) {
        locationEdit.setText(l);
        prefix = p;
        suffix = s;
    }

    public void setPrice(int p, boolean f, PaymentType t, CurrencyType c) {
        rate = p;
        isFinal = f;
        type = t;
        currency = c ;
        String priceTag = rate + " " + currency.getSymbol();
        priceTag += "/" + type.convertToStringToDisplay();

        priceEdit.setText(priceTag);
    }

    public void setDescription(String d) {
        descriptionEdit.setText(d);
    }

    public void setProfileImageFromUrl(String url) {
        if (url != null) {
            ImageLoader imageLoader = ImageLoader.getInstance();
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .cacheOnDisk(true)
                    .build();
            imageLoader.displayImage(url, profileImage, options, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {

                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    //lastImage = loadedImage;
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {

                }
            });
        }
    }

    @Override
    public void onDismiss(boolean set, String locationName, String prefix, String suffix) {
        if (set) {
            setLocation(locationName, prefix, suffix);
            ProfileEditText.runHandler();
        }
    }

    @Override
    public void onPriceChanged(int rate, boolean isFinal, PaymentType type, CurrencyType currency) {
        setPrice(rate, isFinal, type, currency);
        ProfileEditText.runHandler();
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

    /**
     * updates UserData and Profile View
     */
    private final HttpRequestTask.IOnHttpRequestCompleted updateLocalUserProfile =
            new HttpRequestTask.IOnHttpRequestCompleted() {
                @Override
                public void exec(ApiJSON answer) {
                    Professional professional = (Professional) ((SearchQueryResult) answer).data.get(0);
                    UserData.getInstance().updateProfileState(professional);
                    updateView(professional);
                    setProfileImageFromUrl(professional.avatar_url);
                }
            };

    private void updateView(Professional professional) {
        setName(professional.full_name);
        setProfession(professional.title);
        setLocation(professional.location, professional.cp4, professional.cp3);
        int rate = Integer.parseInt(professional.rate);
        boolean isFinal = Boolean.parseBoolean("true");
        PaymentType type = PaymentType.parsePayment(professional.type);
        CurrencyType curr = CurrencyType.parseCurrency(professional.currency);
        setPrice(rate, isFinal, type, curr);
        setDescription(professional.description);
    }
}
