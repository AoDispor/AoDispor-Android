package pt.aodispor.aodispor_android;

import android.os.Bundle;
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

import pt.aodispor.aodispor_android.API.ApiJSON;
import pt.aodispor.aodispor_android.API.HttpRequestTask;
import pt.aodispor.aodispor_android.API.OnHttpRequestCompleted;
import pt.aodispor.aodispor_android.API.Professional;
import pt.aodispor.aodispor_android.API.SearchQueryResult;
import pt.aodispor.aodispor_android.Dialogs.PriceDialog;

public class ProfileFragment extends Fragment implements OnHttpRequestCompleted{
    private RelativeLayout professionalCard;
    private PriceDialog priceDialog;
    private LinearLayout loadingMessage;
    private Professional professional;
    private int rate;
    private PriceType priceType;
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
        locationView = (TextView) professionalCard.findViewById(R.id.location);
        professionView = (TextView) professionalCard.findViewById(R.id.profession);
        descriptionView = (TextView) professionalCard.findViewById(R.id.description);
        imageView = (ImageView) professionalCard.findViewById(R.id.profile_image);

        // Create Placeholder Text
        createPlaceholderText();

        // Loading Message
        loadingMessage = (LinearLayout) professionalCard.findViewById(R.id.loadingMessage);

        rootView.addView(professionalCard);

        startLoading();
        update();

        return rootView;
    }

    /**
     * Creates a placeholder text in each profile view.
     */
    private void createPlaceholderText(){
        int grey = ContextCompat.getColor(getActivity(), R.color.grey);

        // Location
        locationView.setTextColor(grey);
        locationView.setTypeface(AppDefinitions.yanoneKaffeesatzRegular);
        locationView.setText(R.string.register_location);

        // Profile Image
        imageView.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.image_placeholder));

        // Price
        priceView = (TextView) professionalCard.findViewById(R.id.price);
        priceView.setTextColor(grey);
        priceView.setTypeface(AppDefinitions.yanoneKaffeesatzRegular);
        priceView.setText(R.string.register_price);
        priceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show Price Dialog
                if(!priceDialog.isAdded()){
                    priceDialog.show(getFragmentManager(),"dialog");
                }
            }
        });

        // Profession
        professionView.setTextColor(grey);
        professionView.setTypeface(AppDefinitions.yanoneKaffeesatzRegular);
        professionView.setText(R.string.register_profession);

        // Description
        descriptionView.setTextColor(grey);
        descriptionView.setTypeface(AppDefinitions.yanoneKaffeesatzRegular);
        descriptionView.setText(R.string.register_description);
    }

    public void setPrice(int value, boolean isFinal, PriceType type) {
        /*if(value != rate || type != priceType ) {
            startLoading();
            //TODO Send price to api
            update();
        }*/
        Log.v("debug","BEFORE");
        Log.v("debug",rate+"");
        Log.v("debug",""+true);
        Log.v("debug",""+priceType.name());
        Log.v("debug","AFTER");
        Log.v("debug",""+value);
        Log.v("debug",""+isFinal);
        Log.v("debug",""+type);
    }

    public void update(){
        new HttpRequestTask(SearchQueryResult.class, this, "https://api.aodispor.pt/profiles/porto5125").execute();
    }

    /**
     * Started when the HTTP request has finished and succeeded. It then updates the views of the
     * fragment in order to show profile information.
     * @param answer the ApiJSON formatted answer.
     */
    @Override
    public void onHttpRequestCompleted(ApiJSON answer) {
        SearchQueryResult result = (SearchQueryResult) answer;
        professional = result.data.get(0);

        // Price View
        String priceText = professional.getRate();
        rate = Integer.parseInt(priceText);
        switch (professional.getType()){
            case "H":
                priceType = PriceType.ByHour;
                priceText += "/h";
                priceView.setTextColor(ContextCompat.getColor(getContext(), R.color.by_hour));
                break;
            case "S":
                priceType = PriceType.ByService;
                priceView.setTextColor(ContextCompat.getColor(getContext(), R.color.by_service));
                break;
            case "D":
                priceType = PriceType.ByDay;
                priceText += "/por dia";
                priceView.setTextColor(ContextCompat.getColor(getContext(), R.color.aoDispor2));
                break;
        }
        priceView.setText(priceText);

        // Location
        locationView.setText(professional.getLocation());

        // Profession
        professionView.setText(professional.getTitle());

        // Description
        descriptionView.setText(professional.getDescription());

        // Profile Image
        ImageLoader imageLoader = ImageLoader.getInstance();
        DisplayImageOptions options = new DisplayImageOptions.Builder().displayer(new RoundedBitmapDisplayer(getResources().getDimensionPixelSize(R.dimen.image_border))).build();
        imageLoader.displayImage(professional.getAvatar_url(), imageView, options);

        // Create Dialogs
        priceDialog = PriceDialog.newInstance(this,rate,true,priceType.ordinal());

        endLoading();
    }

    @Override
    public void onHttpRequestFailed() {
        Toast.makeText(getContext(), R.string.timeout, Toast.LENGTH_LONG).show();
        endLoading();
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

    public int getPriceRate(){
        return rate;
    }

    public PriceType getPriceType(){
        return PriceType.values()[priceType.ordinal()];
    }

}
