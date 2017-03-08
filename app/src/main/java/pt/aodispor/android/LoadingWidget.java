package pt.aodispor.android;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


public class LoadingWidget {

    public interface OnLoading {
        public void beforeStartLoading();
        public void afterStartLoading();
        public void beforeEndLoading();
        public void afterEndLoading();
    }

    OnLoading additionalBehaviour;

    public void startLoading(LinearLayout loadingWidget, RelativeLayout hideViews) {
        if (additionalBehaviour != null) additionalBehaviour.beforeStartLoading();
        if (hideViews != null) hideViews(hideViews);
        if (loadingWidget!=null) loadingWidget.setVisibility(View.VISIBLE);
        if (additionalBehaviour != null) additionalBehaviour.afterStartLoading();
    }

    public void endLoading(LinearLayout loadingWidget, RelativeLayout showViews) {
        if (additionalBehaviour != null) additionalBehaviour.beforeEndLoading();
        if (showViews != null) showViews(showViews);
        if (loadingWidget!=null) loadingWidget.setVisibility(LinearLayout.INVISIBLE);
        if (additionalBehaviour != null) additionalBehaviour.afterEndLoading();
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
