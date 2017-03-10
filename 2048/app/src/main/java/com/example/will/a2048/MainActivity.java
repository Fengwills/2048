package com.example.will.a2048;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.will.a2048.R;
import com.tbruyelle.rxpermissions.RxPermissions;

import rx.functions.Action1;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {

    private static MainActivity sThis;
    private Model mModel;
    public Typeface tf;
    public SoundPool sp;//声明一个SoundPool
    public int music;//定义一个整型用load（）；来设置suondID
    public Vibrator vibrator;

    private TextView mScore;
    private TextView mBestScore;
    private ImageButton mPause;
    private ImageButton mUndo;
    private GameView gameView;
    private Animation mAnimation = null;
    private LinearLayout mContainer;
    private int mWidth;
    private PopupWindow mPopupWindow;

    public MainActivity() {
        super();
        sThis = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mModel = Model.getInstance(this);
        tf = Typeface.createFromAsset(getAssets(), "font/Comfortaa-Bold.ttf");
        mWidth = MainActivity.this.getResources().getDisplayMetrics().widthPixels - dipToPx(this, 40);
        mScore = (TextView) findViewById(R.id.score);
        mBestScore = (TextView) findViewById(R.id.best_score);
        mPause = (ImageButton) findViewById(R.id.pause_button);
        mUndo = (ImageButton) findViewById(R.id.undo_button);
        gameView = (GameView) findViewById(R.id.game_view);
        mAnimation = (Animation) findViewById(R.id.game_view_container);
        mAnimation.setSpeed(200);
        mContainer = (LinearLayout) findViewById(R.id.container);
        mContainer.setOnTouchListener(this);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        mPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPausePopupWindow();
            }
        });
        setUndoButton(false);
        mUndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setUndoButton(false);
                gameView.undo();
                showScore();
            }
        });
        ViewGroup.LayoutParams layoutParams = gameView.getLayoutParams();
        layoutParams.width = mWidth;
        layoutParams.height = mWidth;
        gameView.setLayoutParams(layoutParams);
        findViewById(R.id.game_view_container).setBackgroundResource(R.drawable.background_nine);
        mScore.setText(0 + "");
        mBestScore.setText(mModel.getBestScore() + "");
        applyFont(this, findViewById(R.id.container), "font/Comfortaa-Bold.ttf");
        initSound();
    }

    public void initSound() {
        sp = new SoundPool(10, AudioManager.STREAM_MUSIC, 5);//第一个参数为同时播放数据流的最大个数，第二数据流类型，第三为声音质量
        music = sp.load(this, R.raw.move, 1); //把你的声音素材放到res/raw里，第2个参数即为资源文件，第3个为音乐的优先级
    }

    public Animation getAnimation() {
        return mAnimation;
    }

    public static MainActivity getThis() {
        return sThis;
    }


    public void showScore() {
        int maxScore = Math.max(mModel.getScore(), mModel.getBestScore());
        if (maxScore == mModel.getScore())
            mModel.saveBestScore(maxScore);
        mScore.setText(mModel.getScore() + "");
        mBestScore.setText(maxScore + "");
    }

    private int dipToPx(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5);
    }

    public void setUndoButton(Boolean isClickable) {
        if (isClickable) {
            mUndo.setEnabled(true);
            mUndo.getBackground().setAlpha(255);
        } else {
            mUndo.setEnabled(false);
            mUndo.getBackground().setAlpha(100);
        }
    }

    private Bitmap myShot(AppCompatActivity activity, PopupWindow popupWindow) {
        // 获取windows中最顶层的view
        View backgroundView = activity.getWindow().getDecorView();
        View foregroundView = popupWindow.getContentView();
        backgroundView.setDrawingCacheEnabled(true);
        backgroundView.buildDrawingCache();
        foregroundView.setDrawingCacheEnabled(true);
        foregroundView.buildDrawingCache();

        // 获取状态栏高度
        Rect rect = new Rect();
        backgroundView.getWindowVisibleDisplayFrame(rect);
        int statusBarHeights = rect.top;
        Display display = activity.getWindowManager().getDefaultDisplay();

        // 获取屏幕宽和高
        int widths = display.getWidth();
        int heights = display.getHeight();


        // 去掉状态栏
        Bitmap background = Bitmap.createBitmap(backgroundView.getDrawingCache(), 0,
                statusBarHeights, widths, heights - statusBarHeights);
        Bitmap foreground = Bitmap.createBitmap(foregroundView.getDrawingCache(), 0,
                statusBarHeights, widths, heights - statusBarHeights);
        Bitmap bmp = Bitmap.createBitmap(widths, heights - statusBarHeights, Bitmap.Config.ARGB_8888);
        Canvas cv = new Canvas(bmp);
        cv.drawBitmap(background, 0, 0, null);//
        cv.drawBitmap(foreground, 0, 0, null);//
        cv.save(Canvas.ALL_SAVE_FLAG);//
        cv.restore();
        // 销毁缓存信息
        backgroundView.destroyDrawingCache();
        foregroundView.destroyDrawingCache();
        return bmp;
    }

    public void shareMsg(String activityTitle, Bitmap bmp) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/jpg");
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bmp, null, null);
        Uri uri = Uri.parse(path);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(intent, activityTitle));
    }

    private void showPausePopupWindow() {
        View contentView = LayoutInflater.from(this).inflate(R.layout.pop_window_pause, null);
        applyFont(this, contentView, "font/Comfortaa-Bold.ttf");
        ImageButton soundButton = (ImageButton) contentView.findViewById(R.id.sound_switch);
        ImageButton vibrateButton = (ImageButton) contentView.findViewById(R.id.vibrate_switch);
        if (mModel.isSound())
            soundButton.setBackgroundResource(R.drawable.mode_sound_on);
        else
            soundButton.setBackgroundResource(R.drawable.mode_sound_off);
        if (mModel.isVibrate())
            vibrateButton.setBackgroundResource(R.drawable.mode_vibrate_on);
        else
            vibrateButton.setBackgroundResource(R.drawable.mode_vibrate_off);
        mPopupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return false;
            }
        });
        ColorDrawable drawable = new ColorDrawable(0);
        mPopupWindow.setBackgroundDrawable(drawable);
        mPopupWindow.setAnimationStyle(R.style.PopupAnimation);
        mPopupWindow.showAtLocation(mContainer, Gravity.CENTER | Gravity.CENTER, 0, 0);
    }

    public void showGameOverPopupWindow() {
        View contentView = LayoutInflater.from(this).inflate(R.layout.pop_window_game_over, null);
        applyFont(this, contentView, "font/Comfortaa-Bold.ttf");
        final TextView score = (TextView) contentView.findViewById(R.id.score_pop_window);
        TextView bestScore = (TextView) contentView.findViewById(R.id.best_score_pop_window);
        ImageButton restartButton = (ImageButton) contentView.findViewById(R.id.restart_button);
        ImageButton exitButton = (ImageButton) contentView.findViewById(R.id.exit_button);
        ImageButton shareButton = (ImageButton) contentView.findViewById(R.id.share_button);
        score.setText("" + mModel.getScore());
        bestScore.setText("" + mModel.getBestScore());
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
                mModel.clearScore();
                showScore();
                gameView.startGame();
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
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RxPermissions.getInstance(MainActivity.this).request(Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        if (aBoolean)
                            shareMsg(MainActivity.this.getTitle().toString(), myShot(MainActivity.this, mPopupWindow));
                    }
                });
            }
        });
        ColorDrawable drawable = new ColorDrawable(0);
        mPopupWindow.setBackgroundDrawable(drawable);
        mPopupWindow.setAnimationStyle(R.style.PopupAnimation);
        mPopupWindow.showAtLocation(findViewById(R.id.container), Gravity.CENTER | Gravity.CENTER, 0, 0);
    }

    public static void applyFont(final Context context, final View root, final String fontName) {
        try {
            if (root instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) root;
                for (int i = 0; i < viewGroup.getChildCount(); i++)
                    applyFont(context, viewGroup.getChildAt(i), fontName);
            } else if (root instanceof TextView)
                ((TextView) root).setTypeface(Typeface.createFromAsset(context.getAssets(), fontName));
        } catch (Exception e) {
            Log.e("Font", String.format("Error occured when trying to apply %s font for %s view", fontName, root));
            e.printStackTrace();
        }
    }

    public void onClick(View view) {
        Log.d("view", " :" + view.toString());
        switch (view.getId()) {
            case R.id.restart:
                mPopupWindow.dismiss();
                mPopupWindow = null;
                mModel.clearScore();
                showScore();
                gameView.startGame();
                break;
            case R.id.sound_switch:
                if (mModel.isSound()) {
                    mModel.setSound(false);
                    view.setBackgroundResource(R.drawable.mode_sound_off);
                } else {
                    mModel.setSound(true);
                    view.setBackgroundResource(R.drawable.mode_sound_on);
                }
                break;
            case R.id.vibrate_switch:
                if (mModel.isVibrate()) {
                    mModel.setVibrate(false);
                    view.setBackgroundResource(R.drawable.mode_vibrate_off);
                } else {
                    mModel.setVibrate(true);
                    view.setBackgroundResource(R.drawable.mode_vibrate_on);
                }
                break;
            case R.id.back:
                mPopupWindow.dismiss();
                mPopupWindow = null;
                finish();
                break;
            default:
                mPopupWindow.dismiss();
                mPopupWindow = null;
                break;
        }
    }

    private float startX;
    private float startY;
    private float offsetX;
    private float offsetY;

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        Model model = Model.getInstance(this);
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = motionEvent.getX();
                startY = motionEvent.getY();
                break;
            case MotionEvent.ACTION_UP:
                offsetX = motionEvent.getX() - startX;
                offsetY = motionEvent.getY() - startY;
                if (model.isOk()) {
                    if (Math.abs(offsetX) > Math.abs(offsetY)) {
                        //水平滑动
                        if (offsetX > 5) {
                            //向右滑动
                            gameView.swipeRight();
                        } else if (offsetX < -5) {
                            //向左滑动
                            gameView.swipeLeft();
                        }
                    } else {
                        //垂直滑动
                        if (offsetY > 5) {
                            //向下滑动
                            gameView.swipeDown();
                        } else if (offsetY < -5) {
                            //向上滑动
                            gameView.swipeUp();
                        }
                    }
                }
                break;
        }
        return true;
    }
}
