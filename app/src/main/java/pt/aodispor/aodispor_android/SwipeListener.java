package pt.aodispor.aodispor_android;

import android.animation.Animator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.util.Date;

import pt.aodispor.aodispor_android.API.Professional;

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
    private boolean enableCall;

    //TODO this can be removed for simplification since animation is not 100%
    static final int DISCARD_ANIMATION_MAX_DURATION = 850;
    static final int DISCARD_ANIMATION_MIN_DURATION = 200;
    Date cardTouchStart;
    Date cardLastMove;
    int discardAnimationDuration =0;

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
        enableCall = false;
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
                cardTouchStart = new Date();
                enableCall = true;
                initialX = x;
                initialY = y;
                viewPager.setSwipeEnabled(false);
                break;
            case MotionEvent.ACTION_MOVE:
                enableCall = false;
                view.setTranslationX(x-initialX);
                view.setTranslationY(y-initialY);
                view.setRotation((x-initialX)*0.05f);
                //float side1 = (float) Math.sqrt((x-initialX)*(x-initialX) + (y-initialY)*(y-initialY));
                //float side2 = (float) Math.sqrt(view.getWidth()*view.getWidth() + view.getHeight()*view.getHeight());
                discard.setRotation(-45);
                cardLastMove = new Date();
                //discard.setAlpha((1.7f*side1)/side2);
                discard.setAlpha(Math.max(
                        3.8f*(x-initialX)*(x-initialX)/(view.getWidth()*view.getWidth()),
                        3.6f*(y-initialY)*(y-initialY)/(view.getHeight()*view.getHeight()) ));
                break;
            case (MotionEvent.ACTION_UP):
                if(enableCall){
                    final Professional p = cardFragment.getProfessionalOnTop();
                    final AlertDialog.Builder builder = new AlertDialog.Builder(cardFragment.getActivity());
                    final CharSequence[] items = {"Ligar","Ver Perfil"};
                    builder.setTitle(p.full_name)
                            .setItems(items, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    switch(i){
                                        case 0:
                                            Intent phoneIntent = new Intent(Intent.ACTION_CALL);
                                            phoneIntent.setData(Uri.parse("tel:9123456789"));//TODO replace with actual professional number
                                            cardFragment.startActivity(phoneIntent);
                                            break;
                                        case 1:
                                            Intent intent = new Intent(cardFragment.getActivity(), ProfessionalProfileActivity.class);

                                            intent.putExtra("name",p.full_name);
                                            intent.putExtra("profession",p.title);
                                            intent.putExtra("location",p.location);
                                            intent.putExtra("description",p.description);
                                            intent.putExtra("price",p.rate);
                                            intent.putExtra("currency",p.currency);
                                            intent.putExtra("type",p.type);
                                            intent.putExtra("avatar_url",p.avatar_url);

                                            cardFragment.startActivity(intent);
                                            break;
                                    }
                                }
                            });
                    AlertDialog dialog = builder.create();

                    dialog.show();
                    //Intent phoneIntent = new Intent(Intent.ACTION_CALL);
                    //phoneIntent.setData(Uri.parse("tel:9123456789"));//TODO replace with actual professional number
                    //cardFragment.startActivity(phoneIntent);
                    enableCall = false;
                }
                viewPager.setSwipeEnabled(true);
                if(Math.abs(x-initialX) < view.getWidth()/2 && Math.abs(y-initialY) < view.getHeight()/2) {
                    CardFragment.blockAccess=true;
                    view.animate().translationX(0).translationY(0).rotation(0).setDuration(400).setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            view.setEnabled(false);
                            discardAnimationDuration =0;
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            CardFragment.blockAccess=false;
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
                    long timenow = (new Date()).getTime();
                    discardAnimationDuration = (int) (timenow - cardTouchStart.getTime());
                    if(timenow - cardLastMove.getTime()>150 ) discardAnimationDuration = DISCARD_ANIMATION_MAX_DURATION;
                    if(discardAnimationDuration >DISCARD_ANIMATION_MAX_DURATION||discardAnimationDuration<=0) {discardAnimationDuration = DISCARD_ANIMATION_MAX_DURATION;}
                    if(discardAnimationDuration<DISCARD_ANIMATION_MIN_DURATION) {discardAnimationDuration =DISCARD_ANIMATION_MIN_DURATION;}
                    CardFragment.blockAccess=true;
                    view.animate().rotation((x-initialX)*0.09f).translationX((x-initialX)*2.6f).translationY((y-initialY)*2.6f).setDuration(discardAnimationDuration).setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            discardAnimationDuration =0;
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
