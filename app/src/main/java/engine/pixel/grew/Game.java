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
import android.view.MotionEvent;

import android.os.VibrationEffect;
import android.os.Vibrator;

public class Game  {
    private static final String LOGTAG = "Game";
    private final Context context;
    public final int width;
    public final int height;
    private final WorldHandler worldhandler;
    private final ThreadPool threadpool;
    private final Bitmap bitmapIcones;
    public final ScreenShake screenShake;
    private int oldBrushY;
    private int oldBrushX;
    private int paintID;
    private  int t;
    public static Matrix matrix = new Matrix();
    public GameLoop gameLoop;
    public static final int pixelSize =6;
    private int brushX;
    private int brushY;
    private boolean isdown;
    private boolean isunder;
    private boolean isFirstClick = false;

    private boolean hasclickedOnThisFrame = false;
    private int hasclickedOnPaintID;


    boolean pauseSimulation = false;

    public static int randomIncr = 0;
    private int brushSize = 2;

    private int paintIDs = 24;

    private int decalagePaintBoxes = 0;
    private int boxWidth = convertDpToPx(63);

    public Game(Context context,GameLoop gameLoop){

        randomIncr++;
        this.gameLoop = gameLoop;
        this.context = context;
        this.width = (int) (Resources.getSystem().getDisplayMetrics().widthPixels);
        this.height =(int) (Resources.getSystem().getDisplayMetrics().heightPixels)-2*boxWidth;

        this.paintID = 5;

        matrix.postScale(pixelSize, pixelSize);

        this.worldhandler = new WorldHandler(context,gameLoop,this);

        this.threadpool = new ThreadPool();
        this.t = 0;

        this.oldBrushX = 0;
        this.oldBrushY = 0;
       // BitmapUtils.preloadBitmaps(this.context, paintIDs);
        bitmapIcones = BitmapFactory.decodeResource(context.getResources(), R.drawable.icones);

        this.screenShake = new ScreenShake();



    }




    public void draw(Canvas canvas) {
        screenShake.applyShakeEffect();
        randomIncr++;
        worldhandler.draw(canvas);


        this.Console(canvas);



    }


    public void Console(Canvas canvas) {

        Paint paint = new Paint();
        paint.setTextSize(20);
        paint.setColor(Color.WHITE);
        canvas.drawText("FPS: " + gameLoop.getAverageFPS(), 10, 30, paint);
       // canvas.drawText("Pixels Swaps: " + worldhandler.chunkhandler.pixelUpdatingNbr, 10, 60, paint);

       Paint paint2 = new Paint();
        for(int i = 0; i < paintIDs;i++){
            randomIncr++;
            int enbas = 0;
            if(i%2 == 1){
                enbas = boxWidth;
            }
            if((hasclickedOnThisFrame && i == hasclickedOnPaintID) || i == paintID){
                paint2.setColor(Color.rgb(230,230,230));
            } else {
                paint2.setColor(getcolor(i));
            }
            canvas.drawRect((i/2)*boxWidth-decalagePaintBoxes, this.height+enbas, boxWidth*(i/2)+boxWidth-decalagePaintBoxes, this.height+boxWidth+enbas, paint2);
               }
        hasclickedOnThisFrame = false;
        canvas.drawBitmap(bitmapIcones, -decalagePaintBoxes,this.height, null);
    }

    public int convertDpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }


    public static Bitmap getBitmapForIndex(Context context, int i) {
        int resId = context.getResources().getIdentifier("icone_" + i, "drawable", context.getPackageName());

        if (resId == 0) {
            // Ressource introuvable, retour du bitmap par défaut
            resId = R.drawable.defaut; // Remplacez "defaut" par le nom de votre image par défaut
        }

        return BitmapFactory.decodeResource(context.getResources(), resId);
    }

    private int getcolor(int i) {

        switch (i){
            case 0:
            case 1:
            case 2:
            case 3:
                return  Color.rgb(64, 93, 114);

            case 4:
                return  Color.rgb(117, 134, 148);
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
                return  Color.rgb(163, 201, 170);
            case 13:
            case 14:
            case 15:
                return  Color.rgb(128, 175, 129);
            case 23:
                return  Color.rgb(34, 9, 44);
            default:
                return  Color.rgb(135, 35, 65);
        }


    }


    public void update() {


        t += 1;
        randomIncr++;

        if(pauseSimulation){
            return;
        }

        worldhandler.update();
        gameLoop.updateCount++;

    }

    public void hasTouchedEffect(){
       if(screenShake.x < 2) screenShake.x = 2;
        if(screenShake.y < 2)screenShake.y = 2;

    }
    public void hasTouchedEffect(int b){
        if(screenShake.x < b) screenShake.x = b;
        if(screenShake.y < b) screenShake.y = b;
    }


    public void vibrate(int ms, int amplitude){

        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            vibrator.vibrate(VibrationEffect.createOneShot(ms, amplitude));
        }

    }

    public void Touch(MotionEvent motionEvent) {

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
        boolean onatTime = false;
        while (true) {
            // Dessine le pixel
            boolean hasDrawed = true;
            for(int i =0;i < brushSize;i++){
                for(int j =0;j < brushSize;j++) {

                    if(onatTime || i+x == 0 || i+x+1 >= width/pixelSize || y+j == 0 || y+j+2 >= height/pixelSize){
                        continue;
                    }

                    switch (paintID) {
                        case 4: // AIR
                            worldhandler.chunkhandler.setPixel((x + i), (y + j), Color.rgb(16, 7, 23), ChunkHandler.setType(0, 0));
                            break;
                        case 5: // PIERRE
                            worldhandler.chunkhandler.setPixel((x + i), (y + j), Color.rgb((t * 15) % 50, 100, 100), ChunkHandler.setType(1, 3));
                            break;
                        case 8: // SABLE
                            randomIncr++;
                            worldhandler.chunkhandler.setPixel((x + i), (y + j), Color.rgb(255 - (t * i * j) % 30, 215 - (t * i * j) % 30, 168 - (t * i * j) % 30), ChunkHandler.setType(1, 1));
                            break;
                        case 10: // EAU
                            worldhandler.chunkhandler.setPixel((x + i), (y + j), Color.rgb(5, 186, 243), ChunkHandler.setType(3, 2));
                            break;
                        case 23: // DELETOR
                            worldhandler.chunkhandler.setPixel((x + i), (y + j), Color.rgb((t) % 10 + 20, 0, (t) % 10 + 20), ChunkHandler.setType(1, 6));
                            break;
                        case 6: // BOIS
                            worldhandler.chunkhandler.setPixel((x + i), (y + j), Color.rgb((int) ((Math.sin(((x + i) / 3) + randomIncr) * 3 + (y + j) * 17) * 50) % 250, 76, 69), ChunkHandler.setType(1 | (1 << 3), 4));
                            break;
                        case 16: // FEUX
                            worldhandler.chunkhandler.setPixel((x + i), (y + j), Color.rgb(255, 201, 59), ChunkHandler.setType(0, 5));
                            break;
                        case 11: // LAVE
                            worldhandler.chunkhandler.setPixel((x + i), (y + j), Color.rgb(198, 29, 0), ChunkHandler.setType(1, 8));
                            break;
                        case 12: // Acide
                            worldhandler.chunkhandler.setPixel((x + i), (y + j), Color.rgb(59, 198, 0), ChunkHandler.setType(1, 10));
                            break;
                        case 18: // Dynamite
                            worldhandler.chunkhandler.setPixel((x + i), (y + j), Color.rgb(200 + randomIncr % 40, (randomIncr % 2) * 50, (randomIncr % 2) * 50), ChunkHandler.setType(1, 13));
                            break;
                        case 17: // DynamitePowder
                            worldhandler.chunkhandler.setPixel((x + i), (y + j), Color.rgb(40 + randomIncr % 30, 25 + randomIncr % 15, 25 + randomIncr % 15), ChunkHandler.setType(1, 14));
                            break;
                        case 9: // ColoredPowder
                            randomIncr++;
                            worldhandler.chunkhandler.setPixel((x + i), (y + j), Color.rgb(randomIncr * 17  % 255, randomIncr * 3 % 255, randomIncr * 8 % 255), ChunkHandler.setType(1, 1));
                            break;
                        case 19: // NytroGlicérine
                            randomIncr++;
                            worldhandler.chunkhandler.setPixel((x + i), (y + j), Color.rgb(209, 192, 88), ChunkHandler.setType(1, 16));
                            break;
                        case 7: // AntiCorosif
                            randomIncr++;
                            worldhandler.chunkhandler.setPixel((x + i), (y + j), Color.rgb(255, 255, 255), ChunkHandler.setType(1, 15));
                            break;
                        case 15: // humain
                           onatTime = true;
                            if(( isFirstClick)) {
                                randomIncr++;

                                int color = Color.rgb(randomIncr*7%200, randomIncr*3%200, randomIncr*17%200);

                                worldhandler.chunkhandler.setPixel( x,  y, Color.rgb(136, 0, 21), ChunkHandler.setType( 1 | (1 << 3) ,26) );
                                worldhandler.chunkhandler.setPixel( x+1,  y, Color.rgb(136, 0, 21), ChunkHandler.setType( 1 | (1 << 3) ,26) );

                                worldhandler.chunkhandler.setPixel( x,  y+1, Color.rgb(136, 0, 21), ChunkHandler.setType( 1 | (1 << 3) ,18) );
                                worldhandler.chunkhandler.setPixel( x+1,  y+1, Color.rgb(239, 228, 176), ChunkHandler.setType( 1 | (1 << 3) ,18) );

                                worldhandler.chunkhandler.setPixel( x,  y+2, Color.rgb(239, 228, 176), ChunkHandler.setType( 1 | (1 << 3) ,18) );
                                worldhandler.chunkhandler.setPixel( x+1,  y+2, Color.rgb(239, 228, 176), ChunkHandler.setType( 1 | (1 << 3) ,18) );

                                worldhandler.chunkhandler.setPixel( x,  y+3,color, ChunkHandler.setType( 1 | (1 << 3) ,18) );
                                worldhandler.chunkhandler.setPixel( x+1,  y+3,color, ChunkHandler.setType( 1 | (1 << 3) ,18) );

                                worldhandler.chunkhandler.setPixel( x,  y+4,color, ChunkHandler.setType( 1 | (1 << 3) ,18) );
                                worldhandler.chunkhandler.setPixel( x+1,  y+4, color, ChunkHandler.setType( 1 | (1 << 3) ,18) );

                                worldhandler.chunkhandler.setPixel( x,  y+5, color, ChunkHandler.setType( 1 | (1 << 3) ,18) );
                                worldhandler.chunkhandler.setPixel( x+1,  y+5, color, ChunkHandler.setType( 1 | (1 << 3) ,18) );

                                worldhandler.chunkhandler.setPixel( x,  y+6, Color.rgb(239, 228, 176), ChunkHandler.setType( 1 | (1 << 3) ,25) );
                                worldhandler.chunkhandler.setPixel( x+1,  y+6, Color.rgb(239, 228, 176), ChunkHandler.setType( 1 | (1 << 3) ,19) );

                            } else {
                                hasDrawed = false;
                            }
                            break;
                        case 21: // RayonX
                            onatTime = true;
                                randomIncr++;
                                int p = 0;
                                while (true){
                                    if( worldhandler.chunkhandler.getPixelData(x,y+p)<< 31 == 0 ){
                                        worldhandler.chunkhandler.setPixel((x), (y+p), Color.rgb(0, 0, 255), ChunkHandler.setType(0, 21));
                                         p++;
                                    } else{
                                        break;
                                    }
                                }
                                hasTouchedEffect(5);
                                hasDrawed = false;
                            break;
                        case 22: // RayonX
                            onatTime = true;
                            randomIncr++;
                            int m = 0;
                            while (true){
                                if( worldhandler.chunkhandler.getPixelData(x,y+m)<< 31 == 0 ){
                                    worldhandler.chunkhandler.setPixel((x), (y+m), Color.rgb(255, 0, 0), ChunkHandler.setType(0, 21));
                                    m++;
                                } else{
                                    break;
                                }
                            }
                            worldhandler.chunkhandler.setPixel((x), (y+m-1), Color.rgb(255, 0, 0), ChunkHandler.setType(0, 12));
                            break;
                        case 13: // Rats
                            if(randomIncr%3 == 0 || isFirstClick){
                                randomIncr++;
                                worldhandler.chunkhandler.setPixel((x), (y), Color.rgb(150 + randomIncr*7%30, 150+ randomIncr*7%30, 150+ randomIncr*7%30), ChunkHandler.setType(1 | (1 << 3), 22));
                            } else {
                                hasDrawed = false;
                            }
                            break;
                        case 20: // grenade
                            if(randomIncr%2 == 0 || isFirstClick) {
                                randomIncr++;
                                worldhandler.chunkhandler.setPixel((x), (y), Color.rgb(87, 98, 56), ChunkHandler.setType(1, 23));
                            } else {
                                hasDrawed = false;
                            }
                            break;
                        case 14: // Grennouille
                            if(randomIncr%3 == 0 || isFirstClick) {
                                randomIncr++;
                                worldhandler.chunkhandler.setPixel((x), (y), Color.rgb(0, 255, 0), ChunkHandler.setType(1 | (1 << 3), 24));
                            } else {
                                hasDrawed = false;
                            }
                            break;

                        default:
                            hasDrawed = false;
                            // Handle unknown paintID if necessary
                            break;
                    }
                }

            }


        if(hasDrawed){
            hasTouchedEffect();
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


        isFirstClick = false;
    }

    public void TouchEvent(MotionEvent motionEvent) {

        int action = motionEvent.getAction();
       /* if(motionEvent.getY()< 0){
            clearSimulation();

        }
        */


        // TOUT CE QU'IL SE PASSE DANS LES BOUTONS
        if(motionEvent.getY()>= height ){

            if(action == MotionEvent.ACTION_DOWN){
            if(motionEvent.getX() < boxWidth*paintIDs){

                vibrate(30,5);
                int xpos = (int)(motionEvent.getX() + decalagePaintBoxes)/boxWidth;
                int ypos = motionEvent.getY()>= height+boxWidth ? 1 : 0;

                int temppaintid = xpos*2 + ypos;

                switch(temppaintid){
                    case 0:
                        hasclickedOnThisFrame = true;
                        hasclickedOnPaintID = 0;
                        clearSimulation();
                        break;
                    case 1:
                        hasclickedOnThisFrame = true;
                        hasclickedOnPaintID = 1;
                        pauseSimulation = !pauseSimulation;
                        break;
                    case 2:
                        hasclickedOnThisFrame = true;
                        hasclickedOnPaintID = 2;
                        brushSize++;
                        if(brushSize >= 8){
                            brushSize = 8;
                        }
                        break;
                    case 3:
                        hasclickedOnThisFrame = true;
                        hasclickedOnPaintID = 3;
                        brushSize--;
                        if(brushSize<=0){
                            brushSize = 1;
                        }
                        break;
                    default:
                        paintID = xpos*2 + ypos;
                        break;


                }

            }
            this.brushX = (int) motionEvent.getX() / pixelSize;
            this.isdown = false;
            return;
            } else{
                decalagePaintBoxes += (brushX -  (int) motionEvent.getX() / pixelSize)*6;

                if (decalagePaintBoxes + width>= boxWidth*paintIDs/2){
                    decalagePaintBoxes = boxWidth*paintIDs/2 - width;
                }
                if(decalagePaintBoxes < 0){
                    decalagePaintBoxes = 0;
                }

            }
        }

        if(action == MotionEvent.ACTION_DOWN){
            this.brushX = (int) motionEvent.getX() / pixelSize;
            this.brushY = (int) motionEvent.getY() / pixelSize;
            this.oldBrushX = this.brushX;
            this.oldBrushY = this.brushY;
            this.isdown = true;
            isFirstClick = true;


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
        Touch(motionEvent);
    }

    private void clearSimulation() {
        pauseSimulation = true;
            worldhandler.clearSimulation();
        pauseSimulation = false;
    }
}
