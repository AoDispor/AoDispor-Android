package pt.aodispor.android;

import android.animation.Animator;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import pt.aodispor.android.API.Professional;

import static pt.aodispor.android.AppDefinitions.DISCARD_ANIMATION_MILLISECONDS;

/**
 * Class for controlling the motion of the professional cards with the gestures the user makes.
 * <p>
 * This card manipulates the cards position through the use of animations. To see the
 * animations working the system must have the animations turned on.
 * </p>
 */
public class SwipeListener implements View.OnTouchListener {
    private View view;
    private MyViewPager viewPager;
    private CardFragment cardFragment;
    private TextView discard;
    private float initialX, initialY;
    private boolean enableCall;

    /**
     * The constructor of the SwipeListener.
     *
     * @param v  the card view to control.
     * @param vp the MyViewPager object that controls the pager.
     * @param cf the CradFragment object where this card is on.
     */
    public SwipeListener(View v, MyViewPager vp, CardFragment cf) {
        view = v;
        viewPager = vp;
        cardFragment = cf;
        discard = (TextView) view.findViewById(R.id.discard);
        enableCall = false;
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
                enableCall = true;
                initialX = x;
                initialY = y;
                viewPager.setSwipeEnabled(false);
                view.animate().cancel();
                view.clearAnimation();
                break;
            case MotionEvent.ACTION_MOVE:
                enableCall = false;
                view.setTranslationX(x - initialX);
                view.setTranslationY(y - initialY);
                view.setRotation(view.getX() * 0.05f);
                discard.setRotation(-45);
                discard.setAlpha(Math.max(
                        3.8f * view.getX() * view.getX() / (view.getWidth() * view.getWidth()),
                        3.6f * view.getY() * view.getY() / (view.getHeight() * view.getHeight())));
                break;
            case (MotionEvent.ACTION_UP):
                if (enableCall) {
                    final Professional p = cardFragment.getProfessionalOnTop();
                    final AlertDialog.Builder builder = new AlertDialog.Builder(cardFragment.getActivity());
                    final CharSequence[] items = {"Ligar", "Ver Perfil"};
                    builder.setTitle(p.full_name)
                            .setItems(items, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    switch (i) {
                                        case 0:
                                            Intent phoneIntent = new Intent(Intent.ACTION_CALL);
                                            phoneIntent.setData(Uri.parse("tel:9123456789"));//TODO replace with actual professional number
                                            cardFragment.startActivity(phoneIntent);
                                            break;
                                        case 1:
                                            Intent intent = new Intent(cardFragment.getActivity(), ProfessionalProfileActivity.class);

                                            intent.putExtra("name", p.full_name);
                                            intent.putExtra("profession", p.title);
                                            intent.putExtra("location", p.location);
                                            intent.putExtra("description", p.description);
                                            intent.putExtra("price", p.rate);
                                            intent.putExtra("currency", p.currency);
                                            intent.putExtra("type", p.type);
                                            intent.putExtra("avatar_url", p.avatar_url);

                                            cardFragment.startActivity(intent);
                                            break;
                                    }
                                }
                            });
                    AlertDialog dialog = builder.create();

                    dialog.show();
                    enableCall = false;
                }

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
                                    discard.setAlpha(0);
                                    viewPager.setSwipeEnabled(true);
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
                            .setDuration(DISCARD_ANIMATION_MILLISECONDS)
                            .setListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    cardFragment.discardTopCard();
                                    viewPager.setSwipeEnabled(true);
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
