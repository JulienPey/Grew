package engine.pixel.grew;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;

public class Game {
    private static final String LOGTAG = "Game";
    private final Context context;
    private final int width;
    private final int height;
    private final Bitmap Bigbitmap;
    private  int t;
    private Matrix matrix = new Matrix();
    public GameLoop gameLoop;
    private static final int pixelSize = 7;


    public Game(Context context,GameLoop gameLoop){

        this.gameLoop = gameLoop;
        this.context = context;
        this.width = (int) (Resources.getSystem().getDisplayMetrics().widthPixels/pixelSize);
        this.height =(int) (Resources.getSystem().getDisplayMetrics().heightPixels/pixelSize);

        this.matrix.postScale(pixelSize, pixelSize);
        this.Bigbitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);


        Canvas canvas = new Canvas(Bigbitmap);

        Paint paint = new Paint();
        paint.setColor(Color.RED);
        canvas.drawRect(0, 0, Bigbitmap.getWidth(), Bigbitmap.getHeight(), paint);
        this.t = 0;
        for(int i =0; i < Bigbitmap.getWidth();i++){
            for(int j =0; j < Bigbitmap.getHeight();j++){
                int color = Color.argb(255,((i+t)%3)*50,((j+t)%3)*50,0);
                Bigbitmap.setPixel(i, j, color);
            }
        }
    }


    public void draw(Canvas canvas) {
        t += 1;
        canvas.drawBitmap(Bigbitmap, matrix, null);
        this.Console(canvas);
       /*
        for(int i =0; i < Bigbitmap.getWidth();i++){
            for(int j =0; j < Bigbitmap.getHeight();j++){
                int color = Color.rgb((i*i+t)%255,(i*j*t)%255,(i+j+t)%255);
                Bigbitmap.setPixel(i, j, color);
            }
        }

        */

    }


    public void Console(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(20);

        canvas.drawText("UPS: " + gameLoop.getAverageUPS(), 10, 30, paint);
        canvas.drawText("FPS: " + gameLoop.getAverageFPS(), 10, 70, paint);

    }

    public void update() {

    }
}
