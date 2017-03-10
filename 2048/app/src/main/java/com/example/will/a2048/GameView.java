package com.example.will.a2048;

import android.content.Context;
import android.graphics.Point;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.GridLayout;

import java.util.ArrayList;

/**
 * Created by will on 2016/07/20,0020.
 */
public class GameView extends GridLayout {
    private Card[][] mCards;
    private ArrayList<Point> mEmptyPoints;
    private Model mModel;
    private int mLines;
    private boolean mMerge = false;
    private double mProbability;

    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GameView(Context context) {
        super(context);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mModel = Model.getInstance(MainActivity.getThis());
        mModel.setCardWidth((Math.min(w, h) - 10) / mModel.getLines());
        initGameView(mModel.getCardWidth(), mModel.getCardWidth());
        startGame();
    }

    public void startGame() {
        mEmptyPoints = new ArrayList<>();
        for (int y = 0; y < mLines; y++) {
            for (int x = 0; x < mLines; x++) {
                mCards[x][y].setNum(0);
            }
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                addRandomNum();
                addRandomNum();
            }
        }, 100);
    }

    private void initGameView(int width, int height) {
        mLines = mModel.getLines();
        mCards = new Card[mLines][mLines];
        setColumnCount(mLines);
        Card card;
        for (int x = 0; x < mLines; x++) {
            for (int y = 0; y < mLines; y++) {
                card = new Card(getContext());
                card.setNum(0);
                addView(card, width, height);
                mCards[x][y] = card;
            }
        }
        switch (mLines) {
            case 3:
                mProbability = 0.7;
                break;
            case 4:
                mProbability = 0.8;
                break;
            case 5:
                mProbability = 0.9;
                break;
        }
    }

    public void addRandomNum() {
        mEmptyPoints.clear();
        for (int x = 0; x < mLines; x++)
            for (int y = 0; y < mLines; y++) {
                if (mCards[x][y].getNum() <= 0) {
                    mEmptyPoints.add(new Point(x, y));
                }
            }
        if (mEmptyPoints.size() > 0) {
            Point removedPoint = mEmptyPoints.remove((int) (Math.random() * mEmptyPoints.size()));
            mCards[removedPoint.x][removedPoint.y].setNum(Math.random() < mProbability ? 2 : 4);
            MainActivity.getThis().getAnimation().createScaleTo(mCards[removedPoint.x][removedPoint.y]);
        }
    }

    public void undo() {
        int num[][] = mModel.getSavedNumber();
        for (int x = 0; x < mLines; x++)
            for (int y = 0; y < mLines; y++) {
                MainActivity.getThis().getAnimation().createScaleTo(mCards[x][y]);
                mCards[x][y].setNum(num[x][y]);
            }
        mModel.undoScore();
        mModel.undoBestScore();
    }

    public void swipeLeft() {
        mModel.setOk(false);
        mModel.copyData(mCards, mModel.getScore(), mModel.getBestScore());
        mMerge = false;
        for (int x = 0; x < mLines; x++) {
            for (int y = 0; y < mLines; y++) {

                for (int y1 = y + 1; y1 < mLines; y1++) {
                    if (mCards[x][y1].getNum() > 0) {

                        if (mCards[x][y].getNum() <= 0) {
                            MainActivity.getThis().getAnimation().createMoveAnim(mCards[x][y1], mCards[x][y], y1, y, x, x);

                            mCards[x][y].setNum(mCards[x][y1].getNum());
                            mCards[x][y1].setNum(0);

                            y--;
                            mMerge = true;

                        } else if (mCards[x][y].equals(mCards[x][y1])) {
                            MainActivity.getThis().getAnimation().createMoveAnim(mCards[x][y1], mCards[x][y], y1, y, x, x);
                            mCards[x][y].setNum(mCards[x][y].getNum() * 2);
                            mCards[x][y1].setNum(0);
                            mModel.addScore(mCards[x][y].getNum());
                            mMerge = true;
                        }
                        break;
                    }
                }
            }
        }

        if (mMerge) {
            if (mModel.isSound())
                MainActivity.getThis().sp.play(MainActivity.getThis().music, 1, 1, 0, 0, 1);
            MainActivity.getThis().showScore();
            MainActivity.getThis().setUndoButton(true);
            mModel.saveData();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    addRandomNum();
                    checkComplete();
                    mModel.setOk(true);
                }
            }, 200);
        } else {
            if (mModel.isVibrate())
                MainActivity.getThis().vibrator.vibrate(50);
            mModel.setOk(true);
        }
    }

    public void swipeRight() {
        mModel.setOk(false);
        mModel.copyData(mCards, mModel.getScore(), mModel.getBestScore());
        mMerge = false;
        for (int x = 0; x < mLines; x++) {
            for (int y = mLines - 1; y >= 0; y--) {

                for (int y1 = y - 1; y1 >= 0; y1--) {
                    if (mCards[x][y1].getNum() > 0) {

                        if (mCards[x][y].getNum() <= 0) {
                            MainActivity.getThis().getAnimation().createMoveAnim(mCards[x][y1], mCards[x][y], y1, y, x, x);
                            mCards[x][y].setNum(mCards[x][y1].getNum());
                            mCards[x][y1].setNum(0);

                            y++;
                            mMerge = true;
                        } else if (mCards[x][y].equals(mCards[x][y1])) {
                            MainActivity.getThis().getAnimation().createMoveAnim(mCards[x][y1], mCards[x][y], y1, y, x, x);
                            mCards[x][y].setNum(mCards[x][y].getNum() * 2);
                            mCards[x][y1].setNum(0);
                            mModel.addScore(mCards[x][y].getNum());
                            mMerge = true;
                        }
                        break;
                    }
                }
            }
        }

        if (mMerge) {
            if (mModel.isSound())
                MainActivity.getThis().sp.play(MainActivity.getThis().music, 1, 1, 0, 0, 1);
            MainActivity.getThis().showScore();
            MainActivity.getThis().setUndoButton(true);
            mModel.saveData();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    addRandomNum();
                    checkComplete();
                    mModel.setOk(true);
                }
            }, 200);
        } else {
            if (mModel.isVibrate())
                MainActivity.getThis().vibrator.vibrate(50);
            mModel.setOk(true);
        }
    }

    public void swipeUp() {
        mModel.setOk(false);
        mModel.copyData(mCards, mModel.getScore(), mModel.getBestScore());
        mMerge = false;
        for (int y = 0; y < mLines; y++) {
            for (int x = 0; x < mLines; x++) {

                for (int x1 = x + 1; x1 < mLines; x1++) {
                    if (mCards[x1][y].getNum() > 0) {

                        if (mCards[x][y].getNum() <= 0) {
                            MainActivity.getThis().getAnimation().createMoveAnim(mCards[x1][y], mCards[x][y], y, y, x1, x);
                            mCards[x][y].setNum(mCards[x1][y].getNum());
                            mCards[x1][y].setNum(0);

                            x--;

                            mMerge = true;
                        } else if (mCards[x][y].equals(mCards[x1][y])) {
                            MainActivity.getThis().getAnimation().createMoveAnim(mCards[x1][y], mCards[x][y], y, y, x1, x);
                            mCards[x][y].setNum(mCards[x][y].getNum() * 2);
                            mCards[x1][y].setNum(0);
                            mModel.addScore(mCards[x][y].getNum());
                            mMerge = true;
                        }
                        break;
                    }
                }
            }
        }

        if (mMerge) {
            if (mModel.isSound())
                MainActivity.getThis().sp.play(MainActivity.getThis().music, 1, 1, 0, 0, 1);
            MainActivity.getThis().showScore();
            MainActivity.getThis().setUndoButton(true);
            mModel.saveData();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    addRandomNum();
                    checkComplete();
                    mModel.setOk(true);
                }
            }, 200);
        } else {
            if (mModel.isVibrate())
                MainActivity.getThis().vibrator.vibrate(50);
            mModel.setOk(true);
        }
    }

    public void swipeDown() {
        mModel.setOk(false);
        mModel.copyData(mCards, mModel.getScore(), mModel.getBestScore());
        mMerge = false;
        for (int y = 0; y < mLines; y++) {
            for (int x = mLines - 1; x >= 0; x--) {

                for (int x1 = x - 1; x1 >= 0; x1--) {
                    if (mCards[x1][y].getNum() > 0) {
                        if (mCards[x][y].getNum() <= 0) {
                            MainActivity.getThis().getAnimation().createMoveAnim(mCards[x1][y], mCards[x][y], y, y, x1, x);
                            mCards[x][y].setNum(mCards[x1][y].getNum());
                            mCards[x1][y].setNum(0);

                            x++;
                            mMerge = true;
                        } else if (mCards[x][y].equals(mCards[x1][y])) {
                            MainActivity.getThis().getAnimation().createMoveAnim(mCards[x1][y], mCards[x][y], y, y, x1, x);
                            mCards[x][y].setNum(mCards[x][y].getNum() * 2);
                            mCards[x1][y].setNum(0);
                            mModel.addScore(mCards[x][y].getNum());
                            mMerge = true;
                        }
                        break;
                    }
                }
            }
        }

        if (mMerge) {
            if (mModel.isSound())
                MainActivity.getThis().sp.play(MainActivity.getThis().music, 1, 1, 0, 0, 1);
            MainActivity.getThis().showScore();
            MainActivity.getThis().setUndoButton(true);
            mModel.saveData();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    addRandomNum();
                    checkComplete();
                    mModel.setOk(true);
                }
            }, 200);
        } else {
            if (mModel.isVibrate())
                MainActivity.getThis().vibrator.vibrate(50);
            mModel.setOk(true);
        }
    }

    private void checkComplete() {

        boolean complete = true;

        ALL:
        for (int y = 0; y < mLines; y++) {
            for (int x = 0; x < mLines; x++) {
                if (mCards[x][y].getNum() == 0 ||
                        (x > 0 && mCards[x][y].equals(mCards[x - 1][y])) ||
                        (x < mLines - 1 && mCards[x][y].equals(mCards[x + 1][y])) ||
                        (y > 0 && mCards[x][y].equals(mCards[x][y - 1])) ||
                        (y < mLines - 1 && mCards[x][y].equals(mCards[x][y + 1]))) {
                    complete = false;
                    break ALL;
                }
            }
        }
        if (complete) {
            MainActivity.getThis().showGameOverPopupWindow();
        }
    }

}
