package object;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Message;
import android.view.View;
import java.util.Random;

import ConstantUtil.ConstantUtil;
import view.MainView;

/**
 * Created by yubin on 2016/11/13.
 */
/*玩家鱼的类*/
public class MyFish implements GameImage{
    //random

    public MyFish(MainView mainView, float touchX, float touchY) {
        x = touchX;
        y = touchY;
        degrees = new Random().nextInt(90);
    }

    float distancePerSecond = 50;
    static final int PIC_LENGTH = 10;
    public static float width;
    public static float height;
    float x,y;
    int currentFrame = 0;
    double degrees;
    public static Bitmap bitmap;
    public static int grow = 0;

    public static boolean isAlive = true;		// 判断是否存活

    void updateFrame(){
        currentFrame++;
        if (currentFrame >= PIC_LENGTH) {
            currentFrame = 0;
        }
    }

    boolean beCatched(float touchx,float touchy){
        Rect rect = new Rect((int)x,(int)y,(int)(x+width),(int)(y+height));
        return rect.contains((int)touchx,(int)touchy);
    }

    public void draw(MainView view, Canvas canvas, Bitmap[] fishBmp, Matrix matrix, float waterY ){
        updateFrame();
        if(view.rocker.WORKING&&!Double.isNaN(view.rocker.degreesByNormalSystem)){
            degrees  = view.rocker.degreesByNormalSystem;
            double rad = view.rocker.rad;
            x = (float) (x+distancePerSecond*Math.cos(rad ));
            y = (float) (y+distancePerSecond*Math.sin(rad ));
        }
        generateBmpWithDegree(fishBmp,matrix,degrees);
        checkBorder(view,waterY);
        width = bitmap.getWidth();
        height = bitmap.getHeight();
        //canvas.drawBitmap(bitmap, x, y, new Paint());
        canvas.drawBitmap(bitmap,
                new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()),
                new Rect((int)x, (int)y, (int)(x + width + grow), (int)(y + height + grow)),
                new Paint());
    }

    private void generateBmpWithDegree(Bitmap[] fishBmp,Matrix matrix,  double degrees2) {
        if(degrees2>90&&degrees2<270){
            matrix.reset();
            matrix.postScale(1, -1);
            bitmap = Bitmap.createBitmap(fishBmp[currentFrame],0,0,fishBmp[currentFrame].getWidth(),fishBmp[currentFrame].getHeight(),matrix,true);
        }else{
            bitmap = fishBmp[currentFrame];
        }
        matrix.reset();
        matrix.postRotate((float)degrees,bitmap.getWidth()/2,bitmap.getHeight()/2);
        bitmap = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
    }

    private void checkBorder(View view, float waterY) {
        if(x<=0){
            x = 0;
        }
        if(x>=view.getWidth()-width){
            x=view.getWidth()-width;
        }
        if(y<waterY){
            y = waterY;
        }
        if(y>=view.getHeight()-height){
            y=view.getHeight()-height;
        }
    }

    public static Bitmap eatAndBigger(Bitmap bitmap, float x, float y) {
        Matrix matrix = new Matrix();
        matrix.postScale((1 + x), (1 + y)); //长和宽放大缩小的比例
        Bitmap resizeBmp =
                Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
        return resizeBmp;
    }

    @Override
    public Bitmap getBitmap() {
        return bitmap;
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
