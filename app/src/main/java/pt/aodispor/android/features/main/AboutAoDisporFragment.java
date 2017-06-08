package pt.aodispor.android.features.main;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pt.aodispor.android.R;
import pt.aodispor.android.utils.TypefaceManager;

public class AboutAoDisporFragment extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.about_aodispor, container, false);
        TypefaceManager.singleton.setTypeface(root.findViewById(R.id.about_aodispor), TypefaceManager.singleton.YANONE[0]);
        return root;
    }
}
