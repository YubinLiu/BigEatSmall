package object;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import view.MainView;

/**
 * Created by yubin on 2016/11/13.
 */
/*敌鱼类*/
public class EnemyFishFromLeft implements GameImage{

    private Bitmap enemyFish = null;

    private List<Bitmap> enemiesBitmaps = new ArrayList<>();

    private float x;
    private float y;

    private int width;
    private int height;

    public EnemyFishFromLeft(Bitmap enemyFish) {
        this.enemyFish = enemyFish;

        enemiesBitmaps.add(Bitmap.createBitmap(
                enemyFish,
                0, 0,
                enemyFish.getWidth() / 8, enemyFish.getHeight()));
        enemiesBitmaps.add(Bitmap.createBitmap(enemyFish,
                enemyFish.getWidth() / 8, 0,
                enemyFish.getWidth() / 8, enemyFish.getHeight()));
        enemiesBitmaps.add(Bitmap.createBitmap(enemyFish,
                (enemyFish.getWidth() / 8) * 2, 0,
                enemyFish.getWidth() / 8, enemyFish.getHeight()));
        enemiesBitmaps.add(Bitmap.createBitmap(enemyFish,
                (enemyFish.getWidth() / 8) * 3, 0,
                enemyFish.getWidth() / 8, enemyFish.getHeight()));
        enemiesBitmaps.add(Bitmap.createBitmap(enemyFish,
                (enemyFish.getWidth() / 8) * 4, 0,
                enemyFish.getWidth() / 8, enemyFish.getHeight()));
        enemiesBitmaps.add(Bitmap.createBitmap(enemyFish,
                (enemyFish.getWidth() / 8) * 5, 0,
                enemyFish.getWidth() / 8, enemyFish.getHeight()));
        enemiesBitmaps.add(Bitmap.createBitmap(enemyFish,
                (enemyFish.getWidth() / 8) * 6, 0,
                enemyFish.getWidth() / 8, enemyFish.getHeight()));
        enemiesBitmaps.add(Bitmap.createBitmap(enemyFish,
                (enemyFish.getWidth() / 8) * 7, 0,
                enemyFish.getWidth() / 8, enemyFish.getHeight()));

        x = -enemyFish.getWidth() / 4;
        Random random = new Random();
        y = random.nextInt(MainView.screenH - enemyFish.getHeight());

        width = enemyFish.getWidth() / 4;
        height = enemyFish.getHeight();
    }

    private int index = 0;
    private int num = 0;

    @Override
    public Bitmap getBitmap() {
        Bitmap bitmap = enemiesBitmaps.get(index);
        if (num == 7) {
            index++;
            if (index == enemiesBitmaps.size()) {
                index = 0;
            }
            num = 0;
        }
        x+=5;

        if (x > MainView.screenW) {
            MainView.gameImages.remove(this);
        }

        num++;
        return bitmap;
    }

    //被吃掉
    public void leftBeEaten(MyFish myFish) {
        if (myFish.getX() > x + width
                || myFish.getX() + MyFish.width + MyFish.grow < x
                || myFish.getY() > y + height
                || myFish.getY() + MyFish.height + MyFish.grow < y) {

        } else {
            if (MyFish.width * MyFish.height < width * height) {
                MyFish.isAlive = false;
            } else {
                MyFish.grow += 0.5;
                MainView.mark += 10;
                MainView.gameImages.remove(this);
            }
        }
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public float getY() {
        return y;
    }
}