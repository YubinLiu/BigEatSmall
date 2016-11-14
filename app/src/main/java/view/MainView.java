package view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import com.example.bigeatsmall.R;
import java.util.ArrayList;
import direction.Rocker;
import object.BackgroundImage;
import object.EnemyFishFromLeft;
import object.EnemyFishFromRight;
import object.GameImage;
import object.MyFish;
import sounds.GameSoundsPool;

public class MainView extends BaseView {
    private SurfaceHolder sfh;
    private Paint paint;
    private Thread th;
    private boolean flag;
    public static int screenW, screenH;
    private static Bitmap fishBmp[] = new Bitmap[10];
    public MyFish myFish;
    public Rocker rocker;

    private Bitmap enemyFishLeftImage;
    private Bitmap enemyFishRightImage;
    private Bitmap background;
    private Bitmap background2;

    public static ArrayList<GameImage> gameImages = new ArrayList<>();

    private Bitmap twoLevelBitmap;  //二级缓存

    private void init() {
        enemyFishLeftImage = BitmapFactory.decodeResource(getResources(),
                R.drawable.enemies_plane_small);
        enemyFishRightImage = BitmapFactory.decodeResource(getResources(),
                R.drawable.enemies_plane_small);
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
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 游戏逻辑
     */
    private void logic() {
    }

    public void run() {
        int enemy_count = 0;
        Paint p1 = new Paint();
        try {
            while (flag) {
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

                if (enemy_count == 50) {
                    enemy_count = 0;
                    gameImages.add(new EnemyFishFromLeft(enemyFishLeftImage));
                }
                if (enemy_count == 40) {
                    gameImages.add(new EnemyFishFromRight(enemyFishRightImage));
                }
                enemy_count++;

                //绘制大圆
                rocker.draw(newCanvas);
                //绘制自己的鱼
                myFish.draw(this, newCanvas, fishBmp, matrix, waterY);

                Canvas canvas = sfh.lockCanvas();

                canvas.drawBitmap(twoLevelBitmap, 0, 0, p1);

                sfh.unlockCanvasAndPost(canvas);


                Thread.sleep(50);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
}