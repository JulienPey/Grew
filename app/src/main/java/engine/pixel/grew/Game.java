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

import java.util.concurrent.Future;

public class Game  {
    private static final String LOGTAG = "Game";
    private final Context context;
    private final int width;
    private final int height;
    private final WorldHandler worldhandler;
    private final ThreadPool threadpool;
    private int paintID;
    private  int t;
    public static Matrix matrix = new Matrix();
    public GameLoop gameLoop;
    public static final int pixelSize =7 ;
    private int x;
    private int y;
    private boolean isdown;


    public Game(Context context,GameLoop gameLoop){


        this.gameLoop = gameLoop;
        this.context = context;
        this.width = (int) (Resources.getSystem().getDisplayMetrics().widthPixels);
        this.height =(int) (Resources.getSystem().getDisplayMetrics().heightPixels);

        this.paintID = 2;

        matrix.postScale(pixelSize, pixelSize);

        this.worldhandler = new WorldHandler(context,gameLoop,this);

        this.threadpool = new ThreadPool();
        this.t = 0;

        Future<?> task1 = threadpool.addThread(() -> {
            while (true) {
                worldhandler.update();
                Touch();
            }
        });

    }



    public void draw(Canvas canvas) {

        worldhandler.draw(canvas);
        this.Console(canvas);

    }


    public void Console(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setTextSize(20);
        canvas.drawRect(0,0,80,30,paint);
        paint.setColor(Color.WHITE);
        canvas.drawText("UPS: " + gameLoop.getAverageUPS(), 10, 30, paint);

        Paint paint2 = new Paint();
        paint2.setColor(Color.RED); // Couleur du contour
        paint2.setStrokeWidth(1); // Épaisseur de la ligne du contour
        paint2.setStyle(Paint.Style.STROKE);


        for(int i = 0; i < worldhandler.chunkhandler.AmountChunkX * worldhandler.chunkhandler.AmountChunkY;i++) {
            if (worldhandler.chunkhandler.ChunkList[i].willbeActive) {
                int left = ((i % worldhandler.chunkhandler.AmountChunkX) * ChunkHandler.ChunkSize) * pixelSize;
                int top = ((i / worldhandler.chunkhandler.AmountChunkX) * ChunkHandler.ChunkSize) * pixelSize;
                canvas.drawRect(left, top, left + ChunkHandler.ChunkSize * pixelSize, top +  ChunkHandler.ChunkSize * pixelSize, paint2);
            }
        }

    }

    public void update() {
      //  worldhandler.update();
       // Touch();
    }
    public void Touch() {

        if(!isdown){
            return;
        }

        for(int i =0;i < 5;i++){
            for(int j =0;j < 5;j++) {

                int chunkX = (x+i) / ChunkHandler.ChunkSize;
                int chunkY =  (y+j) / ChunkHandler.ChunkSize;

                int chunkID = chunkX + (chunkY * worldhandler.chunkhandler.AmountChunkX);
                if(paintID == 0){
                    worldhandler.chunkhandler.ChunkList[chunkID].setPixel( (x+i) % ChunkHandler.ChunkSize,  (y+j) % ChunkHandler.ChunkSize, Color.rgb(0, 0, 0), ChunkHandler.setType( 0,0) );

                } else if(paintID == 1) {
                    worldhandler.chunkhandler.ChunkList[chunkID].setPixel( (x+i) % ChunkHandler.ChunkSize,  (y+j) % ChunkHandler.ChunkSize, Color.rgb((t * 10)%30, 100, 100), ChunkHandler.setType( (1 << 31),0) );
                } else if(paintID == 2) {
                    worldhandler.chunkhandler.ChunkList[chunkID].setPixel( (x+i) % ChunkHandler.ChunkSize,  (y+j) % ChunkHandler.ChunkSize, Color.rgb(t * 10, 255, 255), ChunkHandler.setType( (1 << 31),1) + 1 );
                } else if(paintID == 3) {
                    worldhandler.chunkhandler.ChunkList[chunkID].setPixel( (x+i) % ChunkHandler.ChunkSize,  (y+j) % ChunkHandler.ChunkSize, Color.rgb((t * 10)%200, 0, 255), ChunkHandler.setType( (1 << 31),2) + 1 );
                }

            }
        }
    }

    public void TouchEvent(MotionEvent motionEvent) {
        t += 1;
        int action = motionEvent.getAction();

        if(motionEvent.getY()> height){
            if(motionEvent.getX() < 100) {
                paintID = 0;
            } else  if(100 < motionEvent.getX()  && motionEvent.getX() < 200) {
                paintID = 1;
            }  if(200 < motionEvent.getX()  && motionEvent.getX() < 300) {
                paintID = 2;
            } if(300 < motionEvent.getX()  && motionEvent.getX() < 400) {
                paintID = 3;
            }

            return;
        }

        if(action == MotionEvent.ACTION_DOWN){
            this.isdown = true;
        }

        if(action == MotionEvent.ACTION_UP){
            this.isdown = false;
        }



        this.x = (int) motionEvent.getX() / pixelSize;
        this.y = (int) motionEvent.getY() / pixelSize;


    }
}
