package com.example.will.a2048;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.will.a2048.R;

/**
 * Created by will on 2016/07/20,0020.
 */
public class Card extends FrameLayout {
    private int num = 0;
    private TextView label;
    private View background;

    public Card(Context context) {
        super(context);
        LayoutParams layoutParams = new LayoutParams(-1,-1);
        background = new View(getContext());
        layoutParams.setMargins(10,10,0,0);
        background.setBackgroundResource(R.drawable.num_background);
        addView(background, layoutParams);
        label = new TextView(getContext());
        label.setTextSize(32);
        label.setTextColor(getResources().getColor(R.color.font));
        label.setTypeface(MainActivity.getThis().tf);
        label.setGravity(Gravity.CENTER);
        addView(label, layoutParams);
        setNum(0);
    }

    public TextView getLabel() {
        return label;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
        if (num<=0) {
            label.setText("");
        }else{
            label.setText(num+"");
        }

        switch (num) {
            case 0:
                label.setBackgroundResource(R.drawable.num0);
                break;
            case 2:
                label.setBackgroundResource(R.drawable.num2);
                break;
            case 4:
                label.setBackgroundResource(R.drawable.num4);
                break;
            case 8:
                label.setBackgroundResource(R.drawable.num8);
                break;
            case 16:
                label.setBackgroundResource(R.drawable.num16);
                break;
            case 32:
                label.setBackgroundResource(R.drawable.num32);
                break;
            case 64:
                label.setBackgroundResource(R.drawable.num64);
                break;
            case 128:
                label.setBackgroundResource(R.drawable.num128);
                break;
            case 256:
                label.setBackgroundResource(R.drawable.num256);
                break;
            case 512:
                label.setBackgroundResource(R.drawable.num512);
                break;
            case 1024:
                label.setBackgroundResource(R.drawable.num1024);
                break;
            case 2048:
                label.setBackgroundResource(R.drawable.num2048);
                break;
            default:
                label.setBackgroundResource(R.drawable.num);
                break;
        }
    }

    public boolean equals (Card card) {
        return this.num == card.getNum();
    }
}
