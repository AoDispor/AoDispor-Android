package pt.aodispor.android.utils;

import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class ViewUtils {

    private ViewUtils(){}

    public interface IViewModifier {
        void apply(View v);
    }

    static public void apply2AllChildrenBFS(View v, IViewModifier mod) {
        List<View> unvisited = new ArrayList<View>();
        unvisited.add(v);

        while (!unvisited.isEmpty()) {
            View child = unvisited.remove(0);
            if (!(child instanceof ViewGroup)) {
                mod.apply(child);
                continue;
            }
            ViewGroup group = (ViewGroup) child;
            final int childCount = group.getChildCount();
            for (int i = 0; i < childCount; i++) unvisited.add(group.getChildAt(i));
        }
    }

    static public void changeVisibilityOfAllViewChildren(View v, final int visibility) {
        //if(v==null) return;
        apply2AllChildrenBFS(v,
                new IViewModifier() {
                    @Override
                    public void apply(View v) {
                        v.setVisibility(visibility);
                    }
                }
        );
    }

}
