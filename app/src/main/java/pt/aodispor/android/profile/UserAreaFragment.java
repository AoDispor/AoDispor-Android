package pt.aodispor.android.profile;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import pt.aodispor.android.AppDefinitions;
import pt.aodispor.android.R;
import pt.aodispor.android.api.ApiJSON;
import pt.aodispor.android.api.HttpRequest;
import pt.aodispor.android.api.HttpRequestTask;
import pt.aodispor.android.api.Professional;
import pt.aodispor.android.api.SearchQueryResult;
import pt.aodispor.android.dialogs.NewPriceDialog;

public class UserAreaFragment extends Fragment implements HttpRequest, Notification{
    private static final String LOCATION_TAG = "location";
    private static final String PRICE_DIALOG_TAG = "price-dialog";
    private static final String URL_MY_PROFILE = "https://api.aodispor.pt/profiles/me";
    private static final String URL_UPLOAD_IMAGE = "https://api.aodispor.pt/users/me/profile/avatar";
    private static final int SELECT_PICTURE = 0;
    private ListView listView;
    private LinearLayout loadingMessage;
    private CustomAdapter arrayAdapter;
    private Button saveButton;
    private boolean[] updatedItems;
    private View root;

    public static UserAreaFragment newInstance() {
        return new UserAreaFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.new_profile, container, false);
        listView = (ListView) root.findViewById(R.id.list);
        loadingMessage = (LinearLayout) root.findViewById(R.id.loadingMessage);
        saveButton = (Button) root.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveButton.setEnabled(false);
                startLoading();
                updateListItems();
            }
        });

        List<ListItem> list = new ArrayList<>();
        list.add(new Profile(getContext(), getActivity()));
        arrayAdapter = new CustomAdapter(getContext(), R.layout.profile, list);
        listView.setAdapter(arrayAdapter);

        getProfileInfo();

        startLoading();

        return root;
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

    private void startListItems() {
        for(int i = 0; i < arrayAdapter.getCount(); i++){
            ListItem item = arrayAdapter.getItem(i);
            if(item != null) {
                item.onStart();
            }
        }
    }

    private void updateListItems() {
        for(int i = 0; i < arrayAdapter.getCount(); i++){
            ListItem item = arrayAdapter.getItem(i);
            if(item != null) {
                item.onUpdate();
            }
        }
    }

    private void startLoading() {
        listView.setVisibility(View.INVISIBLE);
        loadingMessage.setVisibility(View.VISIBLE);
    }

    private void endLoading() {
        loadingMessage.setVisibility(View.INVISIBLE);
        listView.setVisibility(View.VISIBLE);
    }

    /**
     * Started when the HTTP request has finished and succeeded. It then updates the views of the
     * fragment in order to show profile information.
     *
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
                p = ((SearchQueryResult) answer).data.get(0);
                break;
        }

        updateProfile(p);

        endLoading();
    }

    public void updateProfile(Professional professional) {
        Profile profile = new Profile(getContext(), getActivity());
        profile.setName(professional.full_name);
        profile.setProfession(professional.title);
        profile.setLocation(professional.location);
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
        profile.setPrice(rate, isFinal, type, professional.currency);
        profile.setDescription(professional.description);
        arrayAdapter.add(profile);
        arrayAdapter.notifyDataSetChanged();
    }

    @Override
    public void onHttpRequestFailed(ApiJSON errorData) {

    }

    @Override
    public void notify(ListItem item) {

    }

    /**
     * Custom adapter that is responsible for creating the views according to each item's type.
     */
    public class CustomAdapter extends ArrayAdapter<ListItem> {
        private List<ListItem> listItems;

        public CustomAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<ListItem> objects) {
            super(context, resource, objects);
            listItems = objects;
        }


        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ListItem item = listItems.get(position);
            return item.getView();
        }
    }

}
