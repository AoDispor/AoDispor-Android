package pt.aodispor.android;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


public class LoadingWidget {

    public interface OnSwitch {
        public void execute();
    }

    OnSwitch beforeStartLoading;
    OnSwitch afterStartLoading;
    OnSwitch beforeEndLoading;
    OnSwitch afterEndLoading;

    public void startLoading(LinearLayout loadingWidget, RelativeLayout hideViews) {
        if (beforeStartLoading != null) beforeStartLoading.execute();
        if (hideViews != null) hideViews(hideViews);
        loadingWidget.setVisibility(View.VISIBLE);
        if (afterStartLoading != null) afterStartLoading.execute();
    }

    public void endLoading(LinearLayout loadingWidget, RelativeLayout showViews) {
        if (beforeEndLoading != null) beforeEndLoading.execute();
        if (showViews != null) showViews(showViews);
        loadingWidget.setVisibility(LinearLayout.INVISIBLE);
        if (afterEndLoading != null) afterEndLoading.execute();
    }

    public void hideViews(RelativeLayout relativeLayout) {
        for (int i = 0; i < relativeLayout.getChildCount(); i++) {
            relativeLayout.getChildAt(i).setVisibility(View.INVISIBLE);
        }
    }

    public void showViews(RelativeLayout relativeLayout) {
        for (int i = 0; i < relativeLayout.getChildCount(); i++) {
            relativeLayout.getChildAt(i).setVisibility(View.VISIBLE);
        }
    }

}
