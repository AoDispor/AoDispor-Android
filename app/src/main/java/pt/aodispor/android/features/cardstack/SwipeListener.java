package pt.aodispor.android.features.cardstack;

import android.animation.Animator;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import pt.aodispor.android.AppDefinitions;
//import pt.aodispor.android.features.main.MyViewPager;
import pt.aodispor.android.R;

/**
 * Class for controlling the motion of the professional cards with the gestures the user makes.
 * <p>
 * This card manipulates the cards position through the use of animations. To see the
 * animations working the system must have the animations turned on.
 * </p>
 */
class SwipeListener implements View.OnTouchListener {
    private View view;
    //private MyViewPager viewPager;
    private CardFragment cardFragment;
    private TextView discard;
    private float initialX, initialY;
    //private boolean enableCall;

    /**
     * The constructor of the SwipeListener.
     *
     * @param v  the card view to control.
     * @param cf the CradFragment object where this card is on.
     */
    SwipeListener(View v, /*MyViewPager vp,*/ CardFragment cf) {
        view = v;
        //viewPager = vp;
        cardFragment = cf;
        discard = (TextView) view.findViewById(R.id.discard);
        //enableCall = false;
    }

    /**
     * This method is fired when the user touches the card.
     * <p>
     * When the card is touched it disables the ViewPager so that the user can move the card
     * without interfering with the ViewPager gesture detection. If the card is dropped with a
     * certain distance of the center of the fragment view the card is discarded. If not, the
     * card goes back to it's original position.
     * </p>
     *
     * @param v     the view of the card or its children views.
     * @param event the event triggered on the card.
     * @return whether the event propagates or not.
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (CardFragment.blockAccess)
            return false;

        final float x = event.getRawX();
        final float y = event.getRawY();
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                //enableCall = true;
                initialX = x;
                initialY = y;
                //viewPager.setSwipeEnabled(false);
                view.animate().cancel();
                view.clearAnimation();
                break;
            case MotionEvent.ACTION_MOVE:
                //enableCall = false;
                view.setTranslationX(x - initialX);
                view.setTranslationY(y - initialY);
                view.setRotation(view.getX() * 0.05f);
                if (discard != null) {
                    discard.setRotation(-45);
                    discard.setAlpha(Math.max(
                            3.8f * view.getX() * view.getX() / (view.getWidth() * view.getWidth()),
                            3.6f * view.getY() * view.getY() / (view.getHeight() * view.getHeight())));
                }
                break;
            case (MotionEvent.ACTION_UP):
                //if (enableCall) {...}

                boolean card_is_inside_bounds = Math.abs(view.getX()) < view.getWidth() / 2
                        && Math.abs(view.getY()) < view.getHeight() / 2;

                if (card_is_inside_bounds || CardFragment.blockAccess) {
                    //card was released within bounds or some animation was still playing
                    view.animate().translationX(0)
                            .translationY(0)
                            .rotation(0)
                            .setDuration(400)
                            .setListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    view.setEnabled(false);
                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    view.setEnabled(true);
                                    if (discard != null) discard.setAlpha(0);
                                    //                  viewPager.setSwipeEnabled(true);
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {
                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {

                                }
                            });
                } else //the card must have been released near boarders wile no animation was playing and needs to be discarded
                {
                    CardFragment.blockAccess = true;
                    view.animate().rotation((x - initialX) * 0.09f)
                            .translationX((x - initialX) * 2.6f)
                            .translationY((y - initialY) * 2.6f)
                            .setDuration(AppDefinitions.DISCARD_ANIMATION_MILLISECONDS)
                            .setListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    cardFragment.discardTopCard();
                                    //                viewPager.setSwipeEnabled(true);
                                    CardFragment.blockAccess = false;
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {
                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {

                                }
                            });
                }
                break;
        }
        return true;
    }
}
