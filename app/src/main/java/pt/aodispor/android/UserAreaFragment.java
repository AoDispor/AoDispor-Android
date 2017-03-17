package pt.aodispor.android;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import pt.aodispor.android.api.ApiJSON;
import pt.aodispor.android.api.HttpRequest;
import pt.aodispor.android.api.HttpRequestTask;
import pt.aodispor.android.api.Professional;
import pt.aodispor.android.api.SearchQueryResult;

public class UserAreaFragment extends Fragment implements HttpRequest{
    private static final String LOCATION_TAG = "location";
    private static final String PRICE_DIALOG_TAG = "price-dialog";
    private static final String URL_MY_PROFILE = "https://api.aodispor.pt/profiles/me";
    private static final String URL_UPLOAD_IMAGE = "https://api.aodispor.pt/users/me/profile/avatar";
    private static final int SELECT_PICTURE = 0;
    private ListView listView;
    private CustomAdapter arrayAdapter;
    private View root;

    public static UserAreaFragment newInstance() {
        return new UserAreaFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.new_profile, container, false);
        listView = (ListView) root.findViewById(R.id.list);
        ListItem[] list = new ListItem[]{};
        arrayAdapter = new CustomAdapter(getContext(), R.layout.profile, list);
        listView.setAdapter(arrayAdapter);
        Profile profile = new Profile(getContext());
        profile.setName("loool");
        arrayAdapter.add(profile);
        arrayAdapter.notifyDataSetChanged();
        return root;
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

    public void startLoading() { //TODO

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

        /* TODO
        if (Utility.isProfessionalRegistered(p)) {
            TextView registered = (TextView) professionalCard.findViewById(R.id.registered_note);
            //registered.setVisibility(View.VISIBLE);
            /*  TODO WORKAROUND 'FIX'
                sets registered note visible without using the visibility (solves visibility problem)
            registered.setText(R.string.profile_registered_note_msg);
            registered.setPadding(10,10,10,10);
        }*/

        //TODO endLoading();
    }

    public void updateProfile(Professional professional) {
        Profile profile = new Profile(getContext());
        profile.setName(professional.full_name);
        profile.setProfession(professional.title);
        profile.setLocation(professional.location);
        profile.setPrice(professional.rate);
        profile.setDescription(professional.description);
        arrayAdapter.add(profile);
        arrayAdapter.notifyDataSetChanged();
    }

    @Override
    public void onHttpRequestFailed(ApiJSON errorData) {

    }

    /**
     * Custom adapter that is responsible for creating the views according to each item's type.
     */
    public class CustomAdapter extends ArrayAdapter<ListItem> {
        private ListItem[] listItems;

        public CustomAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull ListItem[] objects) {
            super(context, resource, objects);
            listItems = objects;
        }


        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ListItem item = listItems[position];
            return item.getView();
        }
    }

}
