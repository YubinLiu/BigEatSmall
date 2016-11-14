package object;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import view.MainView;

/**
 * Created by yubin on 2016/11/13.
 */
public class BackgroundImage implements GameImage {

    private Bitmap background;
    private Bitmap background2;

    public BackgroundImage(Bitmap background, Bitmap background2) {
        this.background = background;
        this.background2 = background2;
        newBitmap = Bitmap.createBitmap(MainView.screenW,
                MainView.screenH, Bitmap.Config.ARGB_8888);
    }

    private Bitmap newBitmap = null;
    private int x = 0;

    public Bitmap getBitmap() {
        Canvas canvas = new Canvas(newBitmap);

        Paint p = new Paint();

        canvas.drawBitmap(background,
                new Rect(0, 0, background.getWidth(), background.getHeight()),
                new Rect(x, 0, MainView.screenW + x, MainView.screenH),
                p);
        canvas.drawBitmap(background2,
                new Rect(0, 0, background.getWidth(), background.getHeight()),
                new Rect(-MainView.screenW + x, 0, x, MainView.screenH),
                p);
        x++;
        if (x == MainView.screenW) {
            x = 0;
        }
        return newBitmap;

    }

    @Override
    public float getX() {
        return 0;
    }

    @Override
    public float getY() {
        return 0;
    }
}

