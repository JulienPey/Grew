package engine.pixel.grew;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.appcompat.app.AppCompatActivity;

public class GameLoop extends SurfaceView implements SurfaceHolder.Callback, Runnable{

    private Game Game;
    private SurfaceHolder holder;
    private Thread drawThread;
    private boolean surfaceReady = false;
    private boolean drawingActive = false;
    private static final int MAX_FRAME_TIME = (int) (1000.0 / 60.0);
    private static final String LOGTAG = "surface";
    private SurfaceHolder surfaceHolder;
    private Context c;

    public double averageUPS;
    private double averageFPS;

    public GameLoop(Context context) {
        super(context);
        this.Game = new Game(context,this);
        init(context);
    }
    public GameLoop(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
    public GameLoop(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }
    @TargetApi(21)
    public GameLoop(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    public int getAverageUPS() {
        return (int) averageUPS;
    }

    public int getAverageFPS() {
        return (int) averageFPS;
    }

    public void init(Context c) {
        this.c = c;
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        setFocusable(true);

        //Initialize other stuff here later
    }

    public void render(Canvas c){
        Game.draw(c);
    }

    public void tick(){
        Game.update();
        //Game logic here
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {
        if (width == 0 || height == 0){
            return;
        }

        // resize your UI
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder){
        this.holder = holder;

        if (drawThread != null){
            drawingActive = false;
            try{
                drawThread.join();
            } catch (InterruptedException e){}
        }

        surfaceReady = true;
        startDrawThread();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder){
        // Surface is not used anymore - stop the drawing thread
        stopDrawThread();
        // and release the surface
        holder.getSurface().release();

        this.holder = null;
        surfaceReady = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        Game.TouchEvent(event);
        return true;
    }

    /**
     * Stops the drawing thread
     */
    public void stopDrawThread(){
        if (drawThread == null){
            return;
        }
        drawingActive = false;
        while (true){
            try{
                drawThread.join(5000);
                break;
            } catch (Exception e) {
            }
        }
        drawThread = null;
    }

    /**
     * Creates a new draw thread and starts it.
     */
    public void startDrawThread(){
        if (surfaceReady && drawThread == null){
            drawThread = new Thread(this, "Draw thread");
            drawingActive = true;
            drawThread.start();
        }
    }
    public void run() {
        int updateCount = 0;
        int frameCount = 0;

        long startTime;
        long elapsedTime;
        long sleepTime;


        startTime = System.currentTimeMillis();
        while (drawingActive) {
            Canvas canvas = null;

            try {
                canvas = surfaceHolder.lockCanvas();
                synchronized (surfaceHolder) {
                    // Mettre Ã  jour la logique du jeu
                    Game.update();
                    //updateCount++;

                    // Dessiner sur le canvas
                    if (canvas != null) {
                        Game.draw(canvas);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (canvas != null) {
                    try {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                        frameCount++;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }



            // Calculate UPS et FPS
            elapsedTime = System.currentTimeMillis() - startTime;
            if (elapsedTime >= 1000) {

                averageUPS = updateCount / (1E-3 * elapsedTime);
                averageFPS = frameCount / (1E-3 * elapsedTime);


                updateCount = 0;
                frameCount = 0;
                startTime = System.currentTimeMillis();
            }
        }


        }

}

