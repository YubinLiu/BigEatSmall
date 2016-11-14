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
public class EnemyFishFromRight implements GameImage{

    private Bitmap enemyFish = null;

    private List<Bitmap> enemiesBitmaps = new ArrayList<>();

    private float x;
    private float y;

    public EnemyFishFromRight(Bitmap enemyFish) {
        this.enemyFish = enemyFish;

        enemiesBitmaps.add(Bitmap.createBitmap(
                enemyFish,
                0, 0,
                enemyFish.getWidth() / 4, enemyFish.getHeight()));
        enemiesBitmaps.add(Bitmap.createBitmap(enemyFish,
                enemyFish.getWidth() / 4, 0,
                enemyFish.getWidth() / 4, enemyFish.getHeight()));
        enemiesBitmaps.add(Bitmap.createBitmap(enemyFish,
                (enemyFish.getWidth() / 4) * 2, 0,
                enemyFish.getWidth() / 4, enemyFish.getHeight()));
        enemiesBitmaps.add(Bitmap.createBitmap(enemyFish,
                (enemyFish.getWidth() / 4) * 3, 0,
                enemyFish.getWidth() / 4, enemyFish.getHeight()));

        x = MainView.screenW + enemyFish.getWidth() / 4;
        Random random = new Random();
        y = random.nextInt(MainView.screenH - enemyFish.getHeight());
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
        x-=5;

        if (x < -enemyFish.getWidth()) {
            MainView.gameImages.remove(this);
        }

        num++;
        return bitmap;
    }

    //被吃掉
    public void rightBeEaten(MyFish myFish) {
        if (myFish.getX() >= x && myFish.getY() >= y
                && myFish.getX() <= x + enemyFish.getWidth() / 4
                && myFish.getY() <= y + enemyFish.getHeight()) {
            MainView.gameImages.remove(this);
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
