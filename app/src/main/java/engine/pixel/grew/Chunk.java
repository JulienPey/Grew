package engine.pixel.grew;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;

public class Chunk {
/*
    public final Bitmap bitmap;
    private final Paint paint;
    private final ChunkHandler chunkHandler;
    private final int chunkPosY;
    private final int chunkPosX;
    private Matrix matrix;
    public boolean hasUpdated = true;
    public boolean willbeActive = true;
    public int[] PixelList;
    public int[] PixelColor;
    /*
    0000 // R
    0000 // G
    0000 // B

    0000 // [IsSolide][][][]
    0000 // Type
    0000 // Type
    0000 // [][][][]

    0000 //
    0000 //
    0000 //
    0000 // [][][][IsActive]
     */


    public Chunk(int x, int y, ChunkHandler chunkHandler) {
        /*
        PixelList = new int[ChunkHandler.ChunkSize*ChunkHandler.ChunkSize];

        PixelColor = new int[ChunkHandler.ChunkSize*ChunkHandler.ChunkSize];
        for(int i =0; i < ChunkHandler.ChunkSize*ChunkHandler.ChunkSize;i++){
            PixelColor[i] = Color.BLACK;
           // PixelColor[i] = Color.rgb((x*50)%255,(y*50)%255,(x*50*y)%255);
        }

        this.chunkHandler = chunkHandler;

        this.chunkPosX = x*ChunkHandler.ChunkSize*Game.pixelSize;
        this.chunkPosY = y*ChunkHandler.ChunkSize*Game.pixelSize;

        this.matrix = new Matrix();
        this.matrix.setTranslate(x*ChunkHandler.ChunkSize, y*ChunkHandler.ChunkSize);
        this.matrix.postScale(Game.pixelSize, Game.pixelSize);

        paint = new Paint();


        this.bitmap = Bitmap.createBitmap(ChunkHandler.ChunkSize, ChunkHandler.ChunkSize, Bitmap.Config.RGB_565);

         */
    }
/*
    public void drawOnBitmap(Bitmap chunksbitmap,Canvas canvas){

        //Canvas canvas = new Canvas(chunksbitmap);
        canvas.drawBitmap(bitmap, matrix, null);
        //canvas.drawBitmap(bitmap, null, new RectF(chunkPosX, chunkPosY,chunkPosX+(Game.pixelSize*ChunkHandler.ChunkSize), chunkPosY+(Game.pixelSize*ChunkHandler.ChunkSize)), null);

      //  hasUpdated = false;
    }

    public void drawOnScreen(int i, int amountChunkX, int amountChunkY, Canvas canvas) {
        canvas.drawBitmap(bitmap, matrix, null);
    }

    public void initBitmap() {
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();

        for(int i =0 ; i < ChunkHandler.ChunkSize*ChunkHandler.ChunkSize; i++){
            paint.setColor(PixelColor[i]);
            canvas.drawPoint(i%ChunkHandler.ChunkSize ,(int) (i/ChunkHandler.ChunkSize),paint);
        }
    }

    public void setPixel(int x, int y, int color, int data) {
       PixelList[x+y*ChunkHandler.ChunkSize] =  data;
       PixelColor[x+y*ChunkHandler.ChunkSize] = color;

       bitmap.setPixel(x , y,color);
        willbeActive = true;
    }

 */
}
