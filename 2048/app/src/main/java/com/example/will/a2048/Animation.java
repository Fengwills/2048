package com.example.will.a2048;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by will on 2016/07/20,0020.
 */
public class Animation extends FrameLayout {

    private List<Card> mCards = new LinkedList<>();
    private int mSpeed;

    public Animation(Context context) {
        super(context);
    }

    public Animation(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Animation(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setSpeed(int speed) {
        mSpeed = speed;
    }

    public void createMoveAnim(final Card from, final Card to, int fromX, int toX, int fromY, int toY) {

        final Card c = getCard(from.getNum());
        int width = Model.getInstance(MainActivity.getThis()).getCardWidth();
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(width, width);
        lp.leftMargin = fromX * width;
        lp.topMargin = fromY * width;
        c.setLayoutParams(lp);

        if (to.getNum() <= 0) {
            to.getLabel().setVisibility(View.INVISIBLE);
        }
        TranslateAnimation ta = new TranslateAnimation(0, width * (toX - fromX), 0, width * (toY - fromY));
        ta.setDuration(mSpeed);
        ta.setAnimationListener(new android.view.animation.Animation.AnimationListener() {

            @Override
            public void onAnimationStart(android.view.animation.Animation animation) {
            }

            @Override
            public void onAnimationRepeat(android.view.animation.Animation animation) {
            }

            @Override
            public void onAnimationEnd(android.view.animation.Animation animation) {
                to.getLabel().setVisibility(View.VISIBLE);
                recycleCard(c);
            }
        });
        c.startAnimation(ta);
    }

    private Card getCard(int num) {
        Card c;
        if (mCards.size() > 0) {
            c = mCards.remove(0);
        } else {
            c = new Card(getContext());
            addView(c);
        }
        c.setVisibility(View.VISIBLE);
        c.setNum(num);
        return c;
    }

    private void recycleCard(Card c) {
        c.setVisibility(View.INVISIBLE);
        c.setAnimation(null);
        mCards.add(c);
    }

    public void createScaleTo(Card target) {
        ScaleAnimation sa = new ScaleAnimation(0.1f, 1, 0.1f, 1, android.view.animation.Animation.RELATIVE_TO_SELF, 0.5f, android.view.animation.Animation.RELATIVE_TO_SELF, 0.5f);
        sa.setDuration(mSpeed);
        target.setAnimation(null);
        target.getLabel().startAnimation(sa);
    }
}
