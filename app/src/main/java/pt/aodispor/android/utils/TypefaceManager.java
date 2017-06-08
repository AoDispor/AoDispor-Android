package pt.aodispor.android.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.Hashtable;

public class TypefaceManager {

    public static final TypefaceManager singleton = new TypefaceManager();

    private TypefaceManager() {
    }

    public class FontMetaData {
        public final String id;
        public final String assetPath;

        FontMetaData(String id, String assetPath) {
            this.id = id;
            this.assetPath = assetPath;
        }
    }

    private static final String ROOT = "fonts/";

    private FontMetaData tf(String id, String assetPath_minusroot) {
        return new FontMetaData(id, ROOT + assetPath_minusroot);
    }

    public final FontMetaData DANCING_SCRIPT = tf("DANCING_SCRIPT", "dancing-script-ot/DancingScript-Regular.otf");

    public final FontMetaData[] YANONE = new FontMetaData[]{
            tf("YANONE_REGULAR", "Yanone-Kaffeesatz/YanoneKaffeesatz-Regular.otf"),
            tf("YANONE_BOLD", "Yanone-Kaffeesatz/YanoneKaffeesatz-Bold.otf"),
            tf("YANONE_LIGHT", "Yanone-Kaffeesatz/YanoneKaffeesatz-Light.otf"),
            tf("YANNONE_THIN", "Yanone-Kaffeesatz/YanoneKaffeesatz-Thin.otf")
    };

    private final Hashtable<String, Typeface> loaded = new Hashtable<>();

    /** return typeface id*/
    public String load(Context context, FontMetaData fontMetaData) {
        if (loaded.containsKey(fontMetaData.id)) return fontMetaData.id;
        loaded.put(fontMetaData.id,
                Typeface.createFromAsset(
                        context.getAssets(),
                        fontMetaData.assetPath)
        );
        return fontMetaData.id;
    }

    public void load(Context context, FontMetaData[] fontsMetaData) {
        for (FontMetaData f : fontsMetaData) load(context, f);
    }

    /**
     * must have been loaded
     */
    public Typeface getTypeFace(String id) {
        if (loaded.containsKey(id)) return loaded.get(id);
        //if font doesn't exist or is no loaded
        Log.e("getTypeFace", "No Typeface found");
        return null; //TODO replace with some default typeface for robustness
    }

    /**
     * will load if not loaded (lazy style approach) and set the typeface
     * also sets typeface on children
     */
    public void setTypeface(View v, FontMetaData fmd) {
        if (!loaded.containsKey(fmd.id)) {
            load(v.getContext(), fmd);
        }

        final Typeface typeface = loaded.get(fmd.id);

        if (v instanceof TextView) {
            ((TextView) v).setTypeface(typeface);
        }

        Utility.apply2AllChildrenBFS(v, new Utility.IViewModifier() {
            @Override
            public void apply(View vv) {
                if (vv instanceof TextView) {
                    ((TextView) vv).setTypeface(typeface);
                }
            }
        });
    }
}
