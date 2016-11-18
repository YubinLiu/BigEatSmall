package view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import com.example.bigeatsmall.R;
import java.util.ArrayList;

import ConstantUtil.ConstantUtil;
import direction.Rocker;
import object.BackgroundImage;
import object.EnemyFishFromLeft;
import object.EnemyFishFromRight;
import object.GameImage;
import object.MyFish;
import sounds.GameSoundsPool;

public class MainView extends BaseView
        implements DialogInterface.OnClickListener{
    private SurfaceHolder sfh;
    private Paint paint;
    private Thread th;
    private boolean flag;
    public static int screenW, screenH;
    private static Bitmap fishBmp[] = new Bitmap[10];
    public MyFish myFish;
    public Rocker rocker;

    private Bitmap[] enemyFishLeftImage = new Bitmap[5];
    private Bitmap[] enemyFishRightImage = new Bitmap[6];
    private Bitmap background;
    private Bitmap background2;

    public static ArrayList<GameImage> gameImages = new ArrayList<>();

    private Bitmap twoLevelBitmap;  //二级缓存

    private void init() {
        for (int i = 0; i < 5; i++) {
            enemyFishLeftImage[0] = BitmapFactory.decodeResource(getResources(),
                    R.drawable.enemyfish_size1);
            enemyFishLeftImage[1] = BitmapFactory.decodeResource(getResources(),
                    R.drawable.enemyfish_size2);
            enemyFishLeftImage[2] = BitmapFactory.decodeResource(getResources(),
                    R.drawable.enemyfish_size3);
            enemyFishLeftImage[3] = BitmapFactory.decodeResource(getResources(),
                    R.drawable.enemyfish_size4);
            enemyFishLeftImage[4] = BitmapFactory.decodeResource(getResources(),
                    R.drawable.enemyfish_size5);
        }
        for (int i = 0; i < 6; i++) {
            enemyFishRightImage[0] = BitmapFactory.decodeResource(getResources(),
                    R.drawable.enemyfish1_size1);
            enemyFishRightImage[1] = BitmapFactory.decodeResource(getResources(),
                    R.drawable.enemyfish1_size2);
            enemyFishRightImage[2] = BitmapFactory.decodeResource(getResources(),
                    R.drawable.enemyfish1_size3);
            enemyFishRightImage[3] = BitmapFactory.decodeResource(getResources(),
                    R.drawable.enemyfish2);
            enemyFishRightImage[4] = BitmapFactory.decodeResource(getResources(),
                    R.drawable.enemyfish1_size4);
            enemyFishRightImage[5] = BitmapFactory.decodeResource(getResources(),
                    R.drawable.enemyfish1_size5);
        }
        background = BitmapFactory.decodeResource(getResources(),
                R.drawable.background);
        background2 = BitmapFactory.decodeResource(getResources(),
                R.drawable.background2);

        //生产二级缓存照片
        twoLevelBitmap = Bitmap.createBitmap(screenW, screenH,
                Bitmap.Config.ARGB_8888);

        gameImages.add(new BackgroundImage(background, background2));    //先加入背景照片
    }

    /**
     * SurfaceView初始化函数
     */
    public MainView(Context context, GameSoundsPool sounds) {
        super(context, sounds);
        sfh = this.getHolder();
        sfh.addCallback(this);
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setAntiAlias(true);
        setFocusable(true);
    }

    /**
     * SurfaceView视图创建，响应此函数
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        for (int i = 0; i < fishBmp.length; i++) {
            fishBmp[i] = BitmapFactory.decodeResource(this.getResources(), R.drawable.fish0 + i);
        }
        myFish = new MyFish(this, getWidth() / 2, this.getHeight() / 2);
        screenW = this.getWidth();
        screenH = this.getHeight();
        rocker = new Rocker(screenW, screenH);
        init();
        flag = true;
        //实例线程
        th = new Thread(this);
        //启动线程
        th.start();
    }



    Matrix matrix = new Matrix();
    float waterY = 0;

    /**
     * 触屏事件监听
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //当用户手指抬起，应该恢复小圆到初始位置
        if (event.getAction() == MotionEvent.ACTION_UP) {
            rocker.reset();
        } else {
            int pointX = (int) event.getX();
            int pointY = (int) event.getY();
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                rocker.begin(pointX, pointY);
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                rocker.update(pointX, pointY);
            }
        }
        return true;
    }

    /**
     * 按键事件监听
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.stop();

            AlertDialog.Builder alert = new AlertDialog.Builder(this.getContext());

            alert.setTitle("确定退出吗？");
            alert.setPositiveButton("确定", this);
            alert.setNegativeButton("取消", this);
            alert.create();

            //点击对话框以外的部分不消失
            alert.setCancelable(false);

            alert.show();

            return false;
        }

        return super.onKeyDown(keyCode, event);
    }

    private boolean isStop = false;

    public void stop() {
        isStop = true;
    }

    public void start() {
        isStop = false;
        th.interrupt();
    }

    public static int mark = 0;

    public void run() {
        if (!MyFish.isAlive) {
            flag = false;
        }
        int enemy_count_left = 0;
        int enemy_count_right = 0;
        Paint p1 = new Paint();
        Paint p2 = new Paint();
        p2.setColor(Color.BLACK);
        p2.setTextSize(80);
        p2.setAntiAlias(true);
        p2.setDither(true);
        try {
            while (flag) {

                while (isStop) {
                    try {
                        Thread.sleep(1000000000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                Canvas newCanvas = new Canvas(twoLevelBitmap);

                for (GameImage image :
                        (ArrayList<GameImage>)gameImages.clone()) {
                    if (image instanceof EnemyFishFromLeft) {
                        ((EnemyFishFromLeft) image).leftBeEaten(myFish);
                    }
                    if (image instanceof  EnemyFishFromRight) {
                        ((EnemyFishFromRight) image).rightBeEaten(myFish);
                    }
                    newCanvas.drawBitmap(image.getBitmap(),
                            image.getX(), image.getY(), p1);
                }

                if (enemy_count_left == 10) {
                    gameImages.add(new EnemyFishFromLeft(enemyFishLeftImage[0]));
                }
                if (enemy_count_left == 100) {
                    gameImages.add(new EnemyFishFromLeft(enemyFishLeftImage[1]));
                }
                if (enemy_count_left == 300) {
                    gameImages.add(new EnemyFishFromLeft(enemyFishLeftImage[0]));
                    gameImages.add(new EnemyFishFromLeft(enemyFishLeftImage[2]));
                }
                if (enemy_count_left == 400) {
                    gameImages.add(new EnemyFishFromLeft(enemyFishLeftImage[3]));
                }
                if (enemy_count_left == 600) {
                    enemy_count_left = 0;
                    gameImages.add(new EnemyFishFromLeft(enemyFishLeftImage[0]));
                    gameImages.add(new EnemyFishFromLeft(enemyFishLeftImage[4]));
                }

                if (enemy_count_right == 10) {
                    gameImages.add(new EnemyFishFromRight(enemyFishRightImage[0]));
                }
                if (enemy_count_right == 100) {
                    gameImages.add(new EnemyFishFromRight(enemyFishRightImage[1]));
                }
                if (enemy_count_right== 200) {
                    gameImages.add(new EnemyFishFromRight(enemyFishRightImage[0]));
                    gameImages.add(new EnemyFishFromRight(enemyFishRightImage[2]));
                }
                if (enemy_count_right== 400) {
                    gameImages.add(new EnemyFishFromRight(enemyFishRightImage[3]));
                }
                if (enemy_count_right == 500) {
                    gameImages.add(new EnemyFishFromRight(enemyFishRightImage[4]));
                }
                if (enemy_count_right == 800) {
                    enemy_count_right = 0;
                    gameImages.add(new EnemyFishFromRight(enemyFishRightImage[0]));
                    gameImages.add(new EnemyFishFromRight(enemyFishRightImage[5]));
                }

                enemy_count_left++;
                enemy_count_right++;

                //分数
                newCanvas.drawText("积分: " + mark, 0, 80, p2);

                //绘制大圆
                rocker.draw(newCanvas);

                //绘制自己的鱼
                myFish.draw(this, newCanvas, fishBmp, matrix, waterY);

                Canvas canvas = sfh.lockCanvas();

                canvas.drawBitmap(twoLevelBitmap, 0, 0, p1);

                sfh.unlockCanvasAndPost(canvas);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Message message = new Message();
        message.what = 	ConstantUtil.TO_END_VIEW;
        message.arg1 = mark;
        mainActivity.getHandler().sendMessage(message);
    }

    /**
     * SurfaceView视图状态发生改变，响应此函数
     */
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    /**
     * SurfaceView视图消亡时，响应此函数
     */
    public void surfaceDestroyed(SurfaceHolder holder) {
        flag = false;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == -2) {
            this.start();
        } else {
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }
}