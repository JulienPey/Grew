package engine.pixel.grew;

import static java.lang.Thread.sleep;

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

    private int paintIDs = 6;

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
        /*
        Future<?> task1 = threadpool.addThread(() -> {
            long startTime;
            long waitTime;
            while (true) {
                startTime = System.currentTimeMillis();

                worldhandler.update();
                gameLoop.averageUPS++;
                Touch();

                waitTime = System.currentTimeMillis() - startTime;
                long sleepTime = (long) (1E+3 / 60) - waitTime;
                if (sleepTime > 0) {
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt(); // Re-set the interrupt flag
                    }
                }
            }
        });


         */
    }



    public void draw(Canvas canvas) {

        worldhandler.draw(canvas);
        this.Console(canvas);

    }


    public void Console(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setTextSize(20);
        canvas.drawRect(0,0,100,60,paint);
        paint.setColor(Color.WHITE);
        canvas.drawText("FPS: " + gameLoop.getAverageFPS(), 10, 30, paint);

        canvas.drawText("Pixels Swaps: " + worldhandler.chunkhandler.pixelUpdatingNbr, 10, 60, paint);

        Paint paint2 = new Paint();
        for(int i = 0; i < paintIDs;i++){
            paint2.setColor(Color.rgb(i*50+50,i*20,i*70));
            canvas.drawRect(i*100, this.height, 100*i+100, this.height+100, paint2);
        }
        //debugchunk(canvas);



    }
    /*
    private void debugchunk(Canvas canvas) {
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

     */

    public void update() {
        t += 1;
        worldhandler.update();
        gameLoop.updateCount++;
        Touch();
    }
    public void Touch() {

        if(!isdown){
            return;
        }

        for(int i =0;i < 5;i++){
            for(int j =0;j < 5;j++) {
                if(paintID == 0){
                    worldhandler.chunkhandler.setPixel( (x+i) ,  (y+j), Color.rgb(0, 0, 0), ChunkHandler.setType( 0,0) );

                } else if(paintID == 1) {
                    worldhandler.chunkhandler.setPixel( (x+i),  (y+j) , Color.rgb((t * 10)%30, 100, 100), ChunkHandler.setType( 1 ,0) );
                } else if(paintID == 2 && i%2 + j%2 + (i+j+t)%3 == 0) {
                    worldhandler.chunkhandler.setPixel( (x+i) ,  (y+j) , Color.rgb(t * 10, 255, 255), ChunkHandler.setType( 1 ,1)  );
                } else if(paintID == 3 && (t+i+j)%4 == 0) {
                    worldhandler.chunkhandler.setPixel( (x+i),  (y+j), Color.rgb((t)%20, 0, 255), ChunkHandler.setType( 3 ,2)  );
                }else if(paintID == 4) {
                    worldhandler.chunkhandler.setPixel( (x+i),  (y+j), Color.rgb(200, (t*40)%90, 20), ChunkHandler.setType( 1 ,4) );
                }else if(paintID == 5) {
                    worldhandler.chunkhandler.setPixel( (x+i),  (y+j), Color.rgb(40, (t*40)%20, 100), ChunkHandler.setType( 1 ,5) );
                }


            }
        }
    }

    public void TouchEvent(MotionEvent motionEvent) {

        int action = motionEvent.getAction();
        if(motionEvent.getY()< 0){
            clearSimulation();

        }
        if(motionEvent.getY()> height){
            for(int i =0;i < paintIDs; i++){
                if(100*i < motionEvent.getX()  && motionEvent.getX() < 100*i+100) {
                    paintID = i;
                }

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

    private void clearSimulation() {
            worldhandler.clearSimulation();

    }
}
