package engine.pixel.grew;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class Game  {
    private static final String LOGTAG = "Game";
    private final Context context;
    private final int width;
    private final int height;
    private final WorldHandler worldhandler;
    private  int t;
    public static Matrix matrix = new Matrix();
    public GameLoop gameLoop;
    public static final int pixelSize = 7;


    public Game(Context context,GameLoop gameLoop){


        this.gameLoop = gameLoop;
        this.context = context;
        this.width = (int) (Resources.getSystem().getDisplayMetrics().widthPixels);
        this.height =(int) (Resources.getSystem().getDisplayMetrics().heightPixels);


        matrix.postScale(pixelSize, pixelSize);

        this.worldhandler = new WorldHandler(context,gameLoop,this);


        this.t = 0;

    }



    public void draw(Canvas canvas) {

        worldhandler.draw(canvas);
        this.Console(canvas);
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


    public void TouchEvent(MotionEvent motionEvent) {
        t += 1;
        int action = motionEvent.getAction();

        if(motionEvent.getY()> height){
            return;
        }

        int x = (int) motionEvent.getX() / pixelSize;
        int y = (int) motionEvent.getY() / pixelSize;

        int chunkX = x / ChunkHandler.ChunkSize;
        int chunkY = y / ChunkHandler.ChunkSize;

        int chunkID = chunkX + (chunkY * worldhandler.chunkhandler.AmountChunkX);

        //worldhandler.chunkhandler.ChunkList[chunkID].bitmap.eraseColor(Color.RED);

        worldhandler.chunkhandler.ChunkList[chunkID].setPixel(x % ChunkHandler.ChunkSize, y % ChunkHandler.ChunkSize, Color.rgb(t * 10, t, t), 0);




    }
}
