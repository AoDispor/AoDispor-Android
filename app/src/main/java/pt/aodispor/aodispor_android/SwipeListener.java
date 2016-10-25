package pt.aodispor.aodispor_android;

import android.animation.Animator;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

/**
 * Class for controlling the motion of the professional cards with the gestures the user makes.
 * <p>
 *     This card manipulates the cards position through the use of animations. To see the
 *     animations working the system must have the animations turned on.
 * </p>
 */
public class SwipeListener implements View.OnTouchListener{
    private View view;
    private MyViewPager viewPager;
    private CardFragment cardFragment;
    private TextView discard;
    private float initialX, initialY;

    /**
     * The constructor of the SwipeListener.
     * @param v the card view to control.
     * @param vp the MyViewPager object that controls the pager.
     * @param cf the CradFragment object where this card is on.
     */
    public SwipeListener(View v, MyViewPager vp, CardFragment cf){
        view = v;
        viewPager = vp;
        cardFragment = cf;
        discard = (TextView) view.findViewById(R.id.discard);
    }

    /**
     * This method is fired when the user touches the card.
     * <p>
     *     When the card is touched it disables the ViewPager so that the user can move the card
     *     without interfering with the ViewPager gesture detection. If the card is dropped with a
     *     certain distance of the center of the fragment view the card is discarded. If not, the
     *     card goes back to it's original position.
     * </p>
     * @param v the view of the card or its children views.
     * @param event the event triggered on the card.
     * @return whether the event propagates or not.
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        final float x = event.getRawX();
        final float y = event.getRawY();
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                initialX = x;
                initialY = y;
                viewPager.setSwipeEnabled(false);
                break;
            case MotionEvent.ACTION_MOVE:
                view.setTranslationX(x-initialX);
                view.setTranslationY(y-initialY);
                view.setRotation((x-initialX)*0.05f);
                float side1 = (float) Math.sqrt((x-initialX)*(x-initialX) + (y-initialY)*(y-initialY));
                float side2 = (float) Math.sqrt(view.getWidth()*view.getWidth() + view.getHeight()*view.getHeight());
                discard.setRotation(-45);
                discard.setAlpha((1.7f*side1)/side2);
                break;
            case (MotionEvent.ACTION_UP):
                viewPager.setSwipeEnabled(true);
                if(Math.abs(x-initialX) < view.getWidth()/2 && Math.abs(y-initialY) < view.getHeight()/2) {
                    //Return to the stack
                    view.animate().translationX(0).translationY(0).rotation(0).setDuration(400).setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            view.setEnabled(false);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            view.setEnabled(true);
                            discard.setAlpha(0);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
                }else{
                    //Discard card
                    view.animate().translationX((x-initialX)*2).translationY((y-initialY)*2).setDuration(300).setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            cardFragment.discardTopCard();
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
