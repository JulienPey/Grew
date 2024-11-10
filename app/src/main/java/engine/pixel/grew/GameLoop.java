package engine.pixel.grew;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameLoop extends SurfaceView implements SurfaceHolder.Callback, Runnable{

    public Game Game;
    private Thread drawThread;
    private boolean surfaceReady = false;
    private boolean drawingActive = false;
    private SurfaceHolder surfaceHolder;

    private double averageFPS;
    public int updateCount;

    public GameLoop(Context context) {
        super(context);
        this.Game = new Game(context,this);
        init();
    }
    public GameLoop(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    public GameLoop(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public int getAverageFPS() {
        return (int) averageFPS;
    }

    public void init() {
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        setFocusable(true);
    }

    @Override
    //Jamais utilisé car l'orientation est bloqué
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder){
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
        stopDrawThread();
        holder.getSurface().release();
        surfaceReady = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        Game.TouchEvent(event);
        return true;
    }

    public void stopDrawThread(){
        if (drawThread == null){
            return;
        }
        drawingActive = false;
        while (true){
            try{
                drawThread.join(5000);
                break;
            } catch (Exception ignored) {
            }
        }
        drawThread = null;
    }

    public void startDrawThread(){
        if (surfaceReady && drawThread == null){
            drawThread = new Thread(this, "Draw thread");
            drawingActive = true;
            drawThread.start();
        }
    }

    //Boucle principal qui va update le jeux
    public void run() {
        updateCount = 0;
        int frameCount = 0;

        long startTime;
        long elapsedTime;

        startTime = System.currentTimeMillis();
        while (drawingActive) {
            Canvas canvas = null;
            try {
                canvas = surfaceHolder.lockCanvas();

                synchronized (surfaceHolder) {
                    // Mettre à  jour la logique du jeu
                    Game.update();

                    // Modifier le canvas
                    if (canvas != null) {
                        Game.draw(canvas);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (canvas != null) {
                    try {
                        //Publier le canvas sur l'écrant
                        surfaceHolder.unlockCanvasAndPost(canvas);
                        frameCount++;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            // Calculer FPS
            elapsedTime = System.currentTimeMillis() - startTime;
            if (elapsedTime >= 1000) {

                averageFPS = frameCount / (1E-3 * elapsedTime);

                updateCount = 0;
                frameCount = 0;
                startTime = System.currentTimeMillis();
            }
        }


        }

}

