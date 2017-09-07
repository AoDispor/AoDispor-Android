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
import android.widget.LinearLayout;
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
import pt.aodispor.android.api.aodispor.RequestBuilder;
import pt.aodispor.android.utils.Permission;
import pt.aodispor.android.utils.TypefaceManager;
import pt.aodispor.android.data.models.aodispor.AODISPOR_JSON_WEBAPI;
import pt.aodispor.android.api.HttpRequestTask;
import pt.aodispor.android.data.models.aodispor.Professional;
import pt.aodispor.android.data.models.aodispor.SearchQueryResult;
import pt.aodispor.android.data.local.UserData;
import pt.aodispor.android.data.models.aodispor.meta.CurrencyType;
import pt.aodispor.android.data.models.aodispor.meta.PaymentType;
import pt.aodispor.android.utils.ViewUtils;

import static android.app.Activity.RESULT_OK;
import static pt.aodispor.android.utils.Permission.PERMISSIONS_REQUEST_STORAGE;

public class ProfileFragment extends Fragment implements LocationDialog.LocationDialogListener, PriceDialog.PriceDialogListener {
    private static final int SELECT_PICTURE = 0;
    private static final String LOCATION_TAG = "location";
    private static final String PRICE_DIALOG_TAG = "price-dialog";

    private TextView imageView;
    private LinearLayout noConnectionView;
    private EditText nameEdit, professionEdit, locationEdit, priceEdit, descriptionEdit;
    private ImageView profileImage;//, noConnectionImg;
    private View root;
    private String prefix, suffix; //cp4 & cp3
    private int rate = -1;
    private boolean isFinal;
    private CurrencyType currency = null;
    private PaymentType type = null;

    //static boolean profileLoaded = false;
    static final int WAIT_4RETRY_GET_PROFILE = 5000;
    Professional previous;

    //TODO maybe do later... boolean alreadyRegistered;

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

        root = inflater.inflate(R.layout.profile__base, container, false);

        // Get Text Views
        imageView = (TextView) root.findViewById(R.id.imageText);
        noConnectionView = (LinearLayout) root.findViewById(R.id.not_loaded_page_layout);

        // Get Edit Text Views
        nameEdit = (EditText) root.findViewById(R.id.nameEdit);
        professionEdit = (EditText) root.findViewById(R.id.professionEdit);
        locationEdit = (EditText) root.findViewById(R.id.locationEdit);
        priceEdit = (EditText) root.findViewById(R.id.priceEdit);
        descriptionEdit = (EditText) root.findViewById(R.id.descriptionEdit);

        // Get image
        profileImage = (ImageView) root.findViewById(R.id.profileImage);

        setFonts();

        //Set Listeners
        locationEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocationDialog dialog = new LocationDialog();
                dialog.setListener(ProfileFragment.this);
                dialog.show(ProfileFragment.this.getFragmentManager(), LOCATION_TAG);
            }
        });

        priceEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PriceDialog dialog = PriceDialog.newInstance(rate, isFinal, type, currency);
                dialog.setListener(ProfileFragment.this);
                dialog.show(ProfileFragment.this.getFragmentManager(), PRICE_DIALOG_TAG);
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Permission.checkPermission(getActivity(), PERMISSIONS_REQUEST_STORAGE,
                        new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                intent.setType("image/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                ProfileFragment.this.startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
                            }
                        }, null);

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
        /*Utility.apply2AllChildrenBFS(getView(), new Utility.IViewModifier() {
            @Override
            public void apply(View v) {
                if (v instanceof EditText
                        || v instanceof TextView
                        || v instanceof ImageView
                        ) v.setVisibility(View.GONE);
            }
        });*/
        ViewUtils.changeVisibilityOfAllViewChildren(getView(), View.GONE);
        ViewUtils.changeVisibilityOfAllViewChildren(noConnectionView, View.VISIBLE);

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

        if (p == null) {
            Log.e("POST_Profile", "NULL OBJECT!");
            return;
        }

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

        HttpRequestTask<AODISPOR_JSON_WEBAPI> request = RequestBuilder.buildUpdateUserProfileInfosRequest(p);
        request.addOnSuccessHandlers(new HttpRequestTask.IOnHttpRequestCompleted<AODISPOR_JSON_WEBAPI>() {
            @Override
            public void exec(AODISPOR_JSON_WEBAPI answer) {
                UserData.getInstance().updateProfileState(
                        (Professional) ((SearchQueryResult) answer).data.get(0)
                )
                ;
            }
        });
        request.addOnFailHandlers(new HttpRequestTask.IOnHttpRequestCompleted<AODISPOR_JSON_WEBAPI>() {
            @Override
            public void exec(AODISPOR_JSON_WEBAPI answer) {
                updateView(previous);
                Toast.makeText(getContext(), "Não foi possível atualizar dados...", Toast.LENGTH_SHORT).show();
            }
        });
        request.execute();
    }

    void PUT_Image(final Bitmap image) {
        HttpRequestTask<AODISPOR_JSON_WEBAPI> imageRequest = RequestBuilder.buildUpdateUserProfilePhotoRequest(image);
        imageRequest.addOnSuccessHandlers(new HttpRequestTask.IOnHttpRequestCompleted<AODISPOR_JSON_WEBAPI>() {
            @Override
            public void exec(AODISPOR_JSON_WEBAPI answer) {
                profileImage.setImageBitmap(image);
            }
        });
        imageRequest.addOnEndHandlers(new HttpRequestTask.IOnHttpRequestCompleted<AODISPOR_JSON_WEBAPI>() {
            @Override
            public void exec(AODISPOR_JSON_WEBAPI answer) {
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
                        Uri tempUri = Uri.fromFile(tempFile);
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
     * Makes a GET HTTP request to get user profile__base information.
     */
    public void getProfileInfo() {
        HttpRequestTask<AODISPOR_JSON_WEBAPI> request = RequestBuilder.buildGetUserProfileRequest();
        request.addOnSuccessHandlers(updateLocalUserProfile);
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
        currency = c;
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
            ProfileEditText.scheduleHandler();
        }
    }

    @Override
    public void onPriceChanged(int rate, boolean isFinal, PaymentType type, CurrencyType currency) {
        setPrice(rate, isFinal, type, currency);
        ProfileEditText.scheduleHandler();
    }

    private void setFonts() {
        TypefaceManager.singleton.setTypeface(root.findViewById(R.id.profile_base), TypefaceManager.singleton.YANONE[1]);
        TypefaceManager.singleton.setTypeface(imageView, TypefaceManager.singleton.YANONE[0]);
    }

    /**
     * updates UserData and Profile View
     */
    private final HttpRequestTask.IOnHttpRequestCompleted updateLocalUserProfile =
            new HttpRequestTask.IOnHttpRequestCompleted<AODISPOR_JSON_WEBAPI>() {
                @Override
                public void exec(AODISPOR_JSON_WEBAPI answer) {
                    Professional professional = (Professional) ((SearchQueryResult) answer).data.get(0);
                    UserData.getInstance().updateProfileState(professional);
                    updateView(professional);
                    setProfileImageFromUrl(professional.avatar_url);
                    //alreadyRegistered = isProfessionalRegistered(professional);
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

    /*public static boolean isProfessionalRegistered(Professional info) {
        return !(info.full_name == null || info.full_name.equals("")) &&
                !(info.avatar_url == null || info.avatar_url.equals("")) &&
                !(info.title == null || info.title.equals("")) &&
                !(info.currency == null || info.currency.equals("")) &&
                !(info.type == null || info.type.equals("")) &&
                !(info.phone == null || info.phone.equals("")) &&
                !(info.rate == null || info.rate.equals("")) &&
                !(info.location == null || info.location.equals("")) &&
                !(info.description == null || info.description.equals(""));
    }*/

}
