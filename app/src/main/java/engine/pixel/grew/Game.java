package engine.pixel.grew;

import static java.lang.Thread.sleep;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
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
    public final int width;
    public final int height;
    private final WorldHandler worldhandler;
    private final ThreadPool threadpool;
    private int oldBrushY;
    private int oldBrushX;
    private int paintID;
    private  int t;
    public static Matrix matrix = new Matrix();
    public GameLoop gameLoop;
    public static final int pixelSize =6 ;
    private int brushX;
    private int brushY;
    private boolean isdown;
    public static int randomIncr = 0;
    private int brushSize = 2;

    private int paintIDs = 11;

    public Game(Context context,GameLoop gameLoop){

        randomIncr++;
        this.gameLoop = gameLoop;
        this.context = context;
        this.width = (int) (Resources.getSystem().getDisplayMetrics().widthPixels);
        this.height =(int) (Resources.getSystem().getDisplayMetrics().heightPixels)-500;

        this.paintID = 2;

        matrix.postScale(pixelSize, pixelSize);

        this.worldhandler = new WorldHandler(context,gameLoop,this);

        this.threadpool = new ThreadPool();
        this.t = 0;

        this.oldBrushX = 0;
        this.oldBrushY = 0;

        /*
        Future<?> task1 = threadpool.addThread(() -> {
            long startTime;
            long waitTime;
            while (true) {
                startTime = System.currentTimeMillis();

                worldhandler.draw();
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

        randomIncr++;
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
            randomIncr++;
            paint2.setColor(Color.rgb(i*50+50,i*20,i*70));
            canvas.drawRect(i*100, this.height, 100*i+100, this.height+100, paint2);

          //  Bitmap d = BitmapFactory.decodeResource(context.getResources(), R.drawable.icone_1);
           // canvas.drawBitmap(d, i*100,this.height, null);
        }
//canvas.drawBitmap(d, 0,0, null);
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

        randomIncr++;
        worldhandler.update();
        gameLoop.updateCount++;

    }
    public void Touch() {

        if(!isdown){
            return;
        }



        int dx = Math.abs(brushX - oldBrushX);
        int dy = Math.abs(brushY - oldBrushY);
        int sx = oldBrushX < brushX ? 1 : -1;
        int sy = oldBrushY < brushY ? 1 : -1;
        int err = dx - dy;
        int e2;


        int x = oldBrushX;
        int y = oldBrushY;

        while (true) {
            // Dessine le pixel

            for(int i =0;i < brushSize;i++){
                for(int j =0;j < brushSize;j++) {
                    if(paintID == 0){ // AIR
                        worldhandler.chunkhandler.setPixel( (x+i) ,  (y+j), Color.rgb(16,7,23), ChunkHandler.setType( 0,0) );

                    } else if(paintID == 1) { // PIERRE
                        worldhandler.chunkhandler.setPixel( (x+i),  (y+j) , Color.rgb((t * 15)%50, 100, 100), ChunkHandler.setType( 1 ,3) );
                    } else if(paintID == 2 ) { // SABLE
                        worldhandler.chunkhandler.setPixel( (x+i) ,  (y+j) , Color.rgb(255-(t*i*j)%30, 215-(t*i*j)%30, 168-(t*i*j)%30), ChunkHandler.setType( 1 ,1)  );
                    } else if(paintID == 3) { // EAU
                        worldhandler.chunkhandler.setPixel( (x+i),  (y+j), Color.rgb(5, 186, 243), ChunkHandler.setType( 3 ,2)  );
                    }else if(paintID == 4) { // DELETOR
                        worldhandler.chunkhandler.setPixel( (x+i),  (y+j), Color.rgb((t)%10+20, 0, (t)%10+20), ChunkHandler.setType( 1 ,6) );
                    }else if(paintID == 5) { // BOIS
                        worldhandler.chunkhandler.setPixel( (x+i),  (y+j), Color.rgb((int)((Math.sin(((x+i)/3)+randomIncr)*3 + (y+j)*17)*50)%250, 76, 69), ChunkHandler.setType( 1 | (1 << 3)  ,4) );
                        //worldhandler.chunkhandler.setPixel( (x+i),  (y+j), Color.rgb(95+(y*j)*4%40, 76, 69), ChunkHandler.setType( 1 | (1 << 3)  ,4) );
                    }else if(paintID == 6) { // FEUX
                        worldhandler.chunkhandler.setPixel( (x+i),  (y+j), Color.rgb(255, 201, 59), ChunkHandler.setType( 0 ,5) );
                    }else if(paintID == 7) { // LAVE
                        worldhandler.chunkhandler.setPixel( (x+i),  (y+j), Color.rgb(198, 29, 0), ChunkHandler.setType( 1 ,8) );
                    }else if(paintID == 8) { // Acide
                        worldhandler.chunkhandler.setPixel( (x+i),  (y+j), Color.rgb(59, 198, 0), ChunkHandler.setType( 1 ,10) );
                    }else if(paintID == 9) { // Dynamite
                        worldhandler.chunkhandler.setPixel( (x+i),  (y+j), Color.rgb(255, 0, 0), ChunkHandler.setType( 1 ,13) );
                    }else if(paintID == 10) { // DynamitePowder
                        worldhandler.chunkhandler.setPixel( (x+i),  (y+j), Color.rgb(255, 0, 0), ChunkHandler.setType( 1 ,14) );
                    }

                }

            }



            // Vérifie si nous avons atteint le point final
            if (x == brushX && y == brushY) break;

            e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x += sx;
            }
            if (e2 < dx) {
                err += dx;
                y += sy;
            }
        }



    }

    public void TouchEvent(MotionEvent motionEvent) {

        int action = motionEvent.getAction();
        if(motionEvent.getY()< 0){
            clearSimulation();

        }
        if(motionEvent.getY()>= height ){
            for(int i =0;i < paintIDs; i++){
                if(100*i < motionEvent.getX()  && motionEvent.getX() < 100*i+100) {
                    paintID = i;
                }

            }
            return;
        }

        if(action == MotionEvent.ACTION_DOWN){
            this.brushX = (int) motionEvent.getX() / pixelSize;
            this.brushY = (int) motionEvent.getY() / pixelSize;
            this.oldBrushX = this.brushX;
            this.oldBrushY = this.brushY;
            this.isdown = true;

            return;
        }

        if(action == MotionEvent.ACTION_UP){
            this.isdown = false;
            return;
        }

        this.oldBrushX = this.brushX;
        this.oldBrushY = this.brushY;

        this.brushX = (int) motionEvent.getX() / pixelSize;
        this.brushY = (int) motionEvent.getY() / pixelSize;

        if(brushX < 0){brushX = 0;}
        if(brushY < 0){brushY = 0;}
        Touch();
    }

    private void clearSimulation() {
            worldhandler.clearSimulation();

    }
}
