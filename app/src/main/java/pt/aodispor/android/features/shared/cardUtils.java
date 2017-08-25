package pt.aodispor.android.features.shared;

import android.content.res.Resources;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import pt.aodispor.android.AoDisporApplication;
import pt.aodispor.android.R;

        /*note
        * the margins were already defined in dimens.xml
        * possibly allows multiple configs for different builds or fragments???
        *
        * this class was implemented so that these dimensions don't always
        * need to be set on each new place were a card is used
        *
        * ... not sure if this is the best approach though.
        * ...maybe the margins may be defined in the layouts???
        * at least this solution is compatible with already developed code
        * if someone has the time please ascretain what is the best solution
        * */

/**
 * Allow usage of card related methods in different places (orthogonally)
 */
public class CardUtils {

    private CardUtils() {
    }

    /**
     * <p>
     * This method sets a card's margin from the stack so that it gives the illusion of seeing the
     * stack in perspective with the cards on top of each other.
     * </p>
     *
     * <p>
     * this version is easier to use but more expensive (uses reflexion)
     *</p>
     *
     * @param card_layout     a relative layout that should correspond to a card
     * @param positionOnStack the position in the card stack (0 if not in a card stack). used to set the correct offset. 0 value sets no offset.
     * @param animate         animates cards to their position. set true only in card stack.
     * @param clazz           specify the class the card_layout's parent
     */
    static public void setCardMargins(RelativeLayout card_layout, int positionOnStack, boolean animate
                                      ,Class<? extends ViewGroup.MarginLayoutParams> clazz) {
        try {
            ViewGroup.MarginLayoutParams params =
                    (ViewGroup.MarginLayoutParams) clazz.getConstructor(new Class[]{ViewGroup.LayoutParams.class}).newInstance(card_layout.getLayoutParams());
            //this-> doesnt work ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(card_layout.getLayoutParams());
            setCardMargins(card_layout,positionOnStack,animate,params);
        } catch (Exception e) {
            throw new RuntimeException("setCardMargins failed >> " + e.getMessage());
        }
    }

    /**
     * <p>
     * This method sets a card's margin from the stack so that it gives the illusion of seeing the
     * stack in perspective with the cards on top of each other.
     * </p>
     *
     * <p>this version does not use reflexion and is more efficient. be aware how the LayoutParams work.</p>
     *
     * @param card_layout     a relative layout that should correspond to a card
     * @param positionOnStack the position in the card stack (0 if not in a card stack). used to set the correct offset. 0 value sets no offset.
     * @param animate         set true only in card stack
     * @param params          must be initiated outside the method with the correct View/Layout type and with the cardLayout params
     */
    static public void setCardMargins(RelativeLayout card_layout, int positionOnStack, boolean animate,
                                      ViewGroup.MarginLayoutParams params) {
            Resources res = AoDisporApplication.getInstance().getResources();
            params.setMargins(
                    res.getDimensionPixelSize(R.dimen.card_margin_left),
                    res.getDimensionPixelSize(R.dimen.card_margin_top),
                    res.getDimensionPixelSize(R.dimen.card_margin_right),
                    res.getDimensionPixelSize(R.dimen.card_margin_bottom));
            card_layout.setLayoutParams(params);
            if (!animate) return;
            card_layout.setTranslationX(res.getDimensionPixelSize(R.dimen.card_offset) * (positionOnStack + 1));
            card_layout
                    .animate()
                    .translationX(res.getDimensionPixelSize(R.dimen.card_offset) * positionOnStack)
                    .setInterpolator(new DecelerateInterpolator());
            card_layout.setTranslationY(res.getDimensionPixelSize(R.dimen.card_offset) * (positionOnStack + 1));
            card_layout
                    .animate()
                    .translationY(res.getDimensionPixelSize(R.dimen.card_offset) * positionOnStack)
                    .setInterpolator(new DecelerateInterpolator());
    }

}
