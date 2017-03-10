package com.example.will.a2048;


import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by will on 2016/07/20,0020.
 */
public class Model {
    private static Model sModel;
    private static final String SP_KEY_VIBRATE = "vibrate";
    private static final String SP_KEY_SOUND = "sound";
    private static final String SP_KEY_SETTING = "setting";
    private static final String SP_KEY_BEST_SCORE = "bestScore";
    private static String SP_KEY_LINES;
    private static int sLines = 4;
    private static Context mContext;
    private SharedPreferences settingPreferences;
    private SharedPreferences scorePreferences;
    private SharedPreferences.Editor settingEditor;
    private SharedPreferences.Editor scoreEditor;
    private int mScore = 0;
    private int mBestScore = 0;
    private int mSavedNumber[][];
    private int mCopyNumber[][];
    private int mSavedScore;
    private int mSavedBestScore;
    private int mCopyScore;
    private int mCopyBestScore;
    private int mCardWidth = 0;
    private boolean mIsOk;
    private boolean mVibrate;
    private boolean mSound;

    public static Model getInstance(Context context) {
        if (sModel == null || mContext != context) {
            sModel = new Model(context);
        }
        return sModel;
    }

    public static void setLines(int lines) {
        sLines = lines;
    }

    private Model(Context context) {
        mContext = context;
        SP_KEY_LINES = "" + sLines;
        mSavedNumber = new int[sLines][sLines];
        mCopyNumber = new int[sLines][sLines];
        settingPreferences = mContext.getSharedPreferences(SP_KEY_SETTING, mContext.MODE_PRIVATE);
        settingEditor = settingPreferences.edit();
        scorePreferences = mContext.getSharedPreferences(SP_KEY_BEST_SCORE, mContext.MODE_PRIVATE);
        scoreEditor = scorePreferences.edit();
        mBestScore = scorePreferences.getInt(SP_KEY_LINES, 0);
        mVibrate = settingPreferences.getBoolean(SP_KEY_VIBRATE, true);
        mSound = settingPreferences.getBoolean(SP_KEY_SOUND, true);
        mIsOk = true;
    }

    public void saveData() {
        for (int x = 0; x < sLines; x++)
            for (int y = 0; y < sLines; y++)
                mSavedNumber[x][y] = mCopyNumber[x][y];
        mSavedScore = mCopyScore;
        mSavedBestScore = mCopyBestScore;
    }

    public void copyData(Card[][] cards, int score, int bestScore) {
        for (int x = 0; x < sLines; x++)
            for (int y = 0; y < sLines; y++)
                mCopyNumber[x][y] = cards[x][y].getNum();
        mCopyScore = score;
        mCopyBestScore = bestScore;
    }

    public boolean isVibrate() {
        return mVibrate;
    }

    public void setVibrate(boolean vibrate) {
        mVibrate = vibrate;
        settingEditor.putBoolean(SP_KEY_VIBRATE, vibrate);
        settingEditor.commit();
    }

    public boolean isSound() {
        return mSound;
    }

    public void setSound(boolean sound) {
        mSound = sound;
        settingEditor.putBoolean(SP_KEY_SOUND, sound);
        settingEditor.commit();
    }


    public void saveBestScore(int s) {
        mBestScore = s;
        scoreEditor.putInt(SP_KEY_LINES, s);
        scoreEditor.commit();
    }


    public int[][] getSavedNumber() {
        return mSavedNumber;
    }

    public void clearScore() {
        mScore = 0;
    }

    public boolean isOk() {
        return mIsOk;
    }

    public void setOk(boolean ok) {
        mIsOk = ok;
    }

    public void undoScore() {
        mScore = mSavedScore;
    }

    public int getScore() {
        return mScore;
    }

    public void undoBestScore() {
        saveBestScore(mSavedBestScore);
    }

    public int getBestScore() {
        return mBestScore;
    }

    public void addScore(int s) {
        mScore += s;
    }

    public int getCardWidth() {
        return mCardWidth;
    }

    public void setCardWidth(int cardWidth) {
        mCardWidth = cardWidth;
    }

    public int getLines() {
        return sLines;
    }
}
