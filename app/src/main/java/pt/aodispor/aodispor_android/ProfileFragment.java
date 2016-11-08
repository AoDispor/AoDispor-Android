package pt.aodispor.aodispor_android;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ProfileFragment extends Fragment {
    private RelativeLayout professionalCard;

    /**
     * Factory method to create a new instance of ProfileFragment class. This is needed because of how
     * a ViewPager handles the creation of a Fragment.
     * @return the ProfileFragment object created.
     */
    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        RelativeLayout rootView = new RelativeLayout(getActivity());
        professionalCard = (RelativeLayout) inflater.inflate(R.layout.professional_card, container, false);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(professionalCard.getLayoutParams());
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        int p = Utility.dpToPx(getResources().getDimension(R.dimen.register_layout_margin));
        params.setMargins(p,p,p,p);
        professionalCard.setLayoutParams(params);

        createPlaceholderText();

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

        TextView price = (TextView) professionalCard.findViewById(R.id.price);
        price.setTextColor(grey);
        price.setTypeface(AppDefinitions.yanoneKaffeesatzRegular);
        price.setText(R.string.register_price);

        TextView profession = (TextView) professionalCard.findViewById(R.id.profession);
        profession.setTextColor(grey);
        profession.setTypeface(AppDefinitions.yanoneKaffeesatzRegular);
        profession.setText(R.string.register_profession);

        TextView description = (TextView) professionalCard.findViewById(R.id.description);
        description.setTextColor(grey);
        description.setTypeface(AppDefinitions.yanoneKaffeesatzRegular);
        description.setText(R.string.register_description);
    }
}
