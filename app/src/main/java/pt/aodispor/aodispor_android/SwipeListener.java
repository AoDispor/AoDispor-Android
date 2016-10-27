package com.example.pedrobarbosa.tabapplication;

import android.animation.Animator;
import android.content.Intent;
import android.net.Uri;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.util.Date;

public class SwipeListener implements View.OnTouchListener{
    private View view;
    private MyViewPager viewPager;
    private CardFragment cardFragment;
    private TextView discard;
    private float initialX, initialY;
    private boolean enableCall;

    //TODO this can be removed for simplification since animation is not 100%
    static final int DISCARD_ANIMATION_MAX_DURATION = 1000;
    static final int DISCARD_ANIMATION_MIN_DURATION = 200;
    Date cardTouchStart;
    int discardAnimationDuration =0;

    public SwipeListener(View v, MyViewPager vp, CardFragment cf){
        view = v;
        viewPager = vp;
        cardFragment = cf;
        discard = (TextView) view.findViewById(R.id.discard);
        enableCall = false;
    }

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
                //discard.setAlpha((1.7f*side1)/side2);
                discard.setAlpha(Math.max(
                        3.8f*(x-initialX)*(x-initialX)/(view.getWidth()*view.getWidth()),
                        3.6f*(y-initialY)*(y-initialY)/(view.getHeight()*view.getHeight()) ));
                break;
            case (MotionEvent.ACTION_UP):
                if(enableCall){
                    Intent phoneIntent = new Intent(Intent.ACTION_CALL);
                    phoneIntent.setData(Uri.parse("tel:9123456789"));//TODO replace with actual professional number
                    cardFragment.startActivity(phoneIntent);
                    enableCall = false;
                }
                viewPager.setSwipeEnabled(true);
                if(Math.abs(x-initialX) < view.getWidth()/2 && Math.abs(y-initialY) < view.getHeight()/2) {
                    view.animate().translationX(0).translationY(0).rotation(0).setDuration(400).setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            view.setEnabled(false);
                            discardAnimationDuration =0;
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
                    discardAnimationDuration = (int) ((new Date()).getTime() - cardTouchStart.getTime());
                    if(discardAnimationDuration >DISCARD_ANIMATION_MAX_DURATION||discardAnimationDuration<=0) {discardAnimationDuration = DISCARD_ANIMATION_MAX_DURATION;}
                    if(discardAnimationDuration<DISCARD_ANIMATION_MIN_DURATION) {discardAnimationDuration =DISCARD_ANIMATION_MIN_DURATION;}
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
