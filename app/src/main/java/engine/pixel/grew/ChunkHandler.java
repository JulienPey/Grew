package engine.pixel.grew;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ChunkHandler extends AppCompatActivity {

    public final int pixelSize;
    private final WorldHandler worldHandler;
    public final int AmountChunkX;
    public final int AmountChunkY;
    private final int Screenwidth;
    private final int Screenheight;
    public static final int ChunkSize = 10;
    public final Bitmap Chunksbitmap;
    private final Matrix matrix;
    public Chunk[] ChunkList;


    public ChunkHandler(Context context, GameLoop gameLoop, WorldHandler worldHandler){

        this.pixelSize = Game.pixelSize;
        this.worldHandler = worldHandler;
        this.Screenwidth = (int) (Resources.getSystem().getDisplayMetrics().widthPixels);
        this.Screenheight = context.getResources().getDisplayMetrics().heightPixels;


        this.AmountChunkX = (int) Math.ceil((this.Screenwidth) / (float) (this.ChunkSize*pixelSize));
        this.AmountChunkY = (int) Math.ceil((this.Screenheight) / (float) (this.ChunkSize*pixelSize));


        this.Chunksbitmap = Bitmap.createBitmap(Screenwidth, Screenheight, Bitmap.Config.ARGB_8888);
        Chunksbitmap.eraseColor( Color.BLACK);

        this.matrix = new Matrix();
        this.matrix.postScale(Game.pixelSize, Game.pixelSize);


        // chunk
        ChunkList = new Chunk[this.AmountChunkX * this.AmountChunkY];
        for (int i = 0; i < this.AmountChunkX * this.AmountChunkY; i++) {
            ChunkList[i] = new Chunk(i % this.AmountChunkX, i / this.AmountChunkX,this);
            ChunkList[i].initBitmap();
            ChunkList[i].drawOnBitmap(Chunksbitmap);
        }



    }

    public void draw(Canvas canvas) {

        canvas.drawBitmap(Chunksbitmap, 0,0, null);
        for(int i = 0; i < this.AmountChunkX * this.AmountChunkY;i++){
            if( ChunkList[i].hasUpdated){

                ChunkList[i].drawOnBitmap(Chunksbitmap);
            }
        }


    }
}
