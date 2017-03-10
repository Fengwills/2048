package com.example.benjaminchou.myapplication;

import android.graphics.drawable.ColorDrawable;
import android.support.v4.widget.PopupWindowCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private PopupWindow mPopupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showGameOverPopupWindow();
            }
        });
    }

    public void showGameOverPopupWindow() {
        View contentView = LayoutInflater.from(this).inflate(R.layout.p, null);
        ImageButton restartButton = (ImageButton) contentView.findViewById(R.id.restart_button);
        ImageButton exitButton = (ImageButton) contentView.findViewById(R.id.exit_button);
        TextView score = (TextView) contentView.findViewById(R.id.score_pop_windows);
        TextView bestScore = (TextView) contentView.findViewById(R.id.best_score_pop_window);
        score.setText("cao ");
        bestScore.setText("ni ma");
        mPopupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return false;
            }
        });
        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopupWindow.dismiss();
                mPopupWindow = null;
            }
        });
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopupWindow.dismiss();
                mPopupWindow = null;
            }
        });
        ColorDrawable drawable = new ColorDrawable(0);
        mPopupWindow.setBackgroundDrawable(drawable);
        mPopupWindow.showAtLocation(findViewById(R.id.ca), Gravity.CENTER | Gravity.CENTER, 0, 0);
    }
}
