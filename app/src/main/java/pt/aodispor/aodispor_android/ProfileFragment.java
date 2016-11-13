package pt.aodispor.aodispor_android;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import pt.aodispor.aodispor_android.API.ApiJSON;
import pt.aodispor.aodispor_android.API.HttpRequestTask;
import pt.aodispor.aodispor_android.API.OnHttpRequestCompleted;
import pt.aodispor.aodispor_android.API.Professional;
import pt.aodispor.aodispor_android.API.SearchQueryResult;
import pt.aodispor.aodispor_android.Dialogs.PriceDialog;

public class ProfileFragment extends Fragment implements OnHttpRequestCompleted{
    private RelativeLayout professionalCard;
    private PriceDialog priceDialog;
    private TextView priceView;
    private ProgressBar progressBar;

    public enum PriceType { ByHour, ByDay, ByMonth }

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

        priceView = (TextView) professionalCard.findViewById(R.id.price);

        createPlaceholderText();

        priceDialog = new PriceDialog(getActivity(),this);

        progressBar = new ProgressBar(getContext());
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(ProgressBar.INVISIBLE);
        RelativeLayout.LayoutParams progressBarParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE);
        professionalCard.addView(progressBar,progressBarParams);

        rootView.addView(professionalCard);
        return rootView;
    }

    private void createPlaceholderText(){
        int grey = ContextCompat.getColor(getActivity(), R.color.grey);
        TextView location = (TextView) professionalCard.findViewById(R.id.location);
        location.setTextColor(grey);
        location.setTypeface(AppDefinitions.yanoneKaffeesatzRegular);
        location.setText(R.string.register_location);

        ImageView image = (ImageView) professionalCard.findViewById(R.id.profile_image);
        image.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.image_placeholder));

        priceView = (TextView) professionalCard.findViewById(R.id.price);
        priceView.setTextColor(grey);
        priceView.setTypeface(AppDefinitions.yanoneKaffeesatzRegular);
        priceView.setText(R.string.register_price);
        priceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                priceDialog.show();
            }
        });

        TextView profession = (TextView) professionalCard.findViewById(R.id.profession);
        profession.setTextColor(grey);
        profession.setTypeface(AppDefinitions.yanoneKaffeesatzRegular);
        profession.setText(R.string.register_profession);

        TextView description = (TextView) professionalCard.findViewById(R.id.description);
        description.setTextColor(grey);
        description.setTypeface(AppDefinitions.yanoneKaffeesatzRegular);
        description.setText(R.string.register_description);
    }

    public void setPrice(int value, boolean f, PriceType type){
        String text = String.format(getResources().getString(R.string.profile_price), value);
        priceView.setText(text);
        priceView.setTextColor(ContextCompat.getColor(getContext(),R.color.aoDispor2));
        update();
    }

    public void update(){
        hideViews();
        progressBar.setVisibility(ProgressBar.VISIBLE);
        new HttpRequestTask(SearchQueryResult.class, this, "https://api.aodispor.pt/profiles/porto5125").execute();
    }

    @Override
    public void onHttpRequestCompleted(ApiJSON answer) {
        showViews();
        progressBar.setVisibility(ProgressBar.INVISIBLE);

        SearchQueryResult result = (SearchQueryResult) answer;
        Professional p = result.data.get(0);
        String priceText = p.getRate();
        switch (p.getType()){
            case "H":
                priceText += "/h";
                priceView.setTextColor(ContextCompat.getColor(getContext(), R.color.by_hour));
                break;
            case "S":
                priceView.setTextColor(ContextCompat.getColor(getContext(), R.color.by_service));
                break;
            case "D":
                priceText += "/por dia";
                priceView.setTextColor(ContextCompat.getColor(getContext(), R.color.aoDispor2));
                break;
        }
        priceView.setText(priceText);
    }

    @Override
    public void onHttpRequestFailed() {
        showViews();
        progressBar.setVisibility(RelativeLayout.INVISIBLE);
        Toast.makeText(getContext(), R.string.timeout, Toast.LENGTH_LONG).show();
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

}
