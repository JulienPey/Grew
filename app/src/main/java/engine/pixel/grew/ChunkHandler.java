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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class ChunkHandler extends AppCompatActivity {

    public final int pixelSize;
    private final WorldHandler worldHandler;
    public final int AmountChunkX;
    public final int AmountChunkY;
    private final int Screenwidth;
    private final int Screenheight;
    public static final int ChunkSize = 20;
    public final Bitmap Chunksbitmap;
    private final Matrix matrix;
    private final ThreadPool threadpool;
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
           // ChunkList[i].drawOnBitmap(Chunksbitmap);
        }

        this.threadpool = new ThreadPool();

    }

    public void draw(Canvas canvas) {

        //canvas.drawBitmap(Chunksbitmap, 0,0, null);
        for(int i = 0; i < this.AmountChunkX * this.AmountChunkY;i++){
           // if( ChunkList[i].hasUpdated){
                ChunkList[i].drawOnBitmap(Chunksbitmap,canvas);
           // }
        }


    }

    public void update() {

        for(int i = 0; i < this.AmountChunkX * this.AmountChunkY;i++){
            ChunkList[i].hasUpdated = ChunkList[i].willbeActive;
            ChunkList[i].willbeActive = false;
        }

        for(int i = this.AmountChunkX * this.AmountChunkY - 1; i >= 0; i--) {
            Chunk chunk = ChunkList[i];
            if(chunk.hasUpdated) {

                for(int j = ChunkSize * ChunkSize - 1; j >= 0; j--) {
                        if((chunk.PixelList[j] << 31) == 0) { // pixelActive
                            continue;
                        }
                        // Calculez les coordonnées x et y dans le chunk actuel
                        int chunkX = (i % AmountChunkX) * ChunkSize + (j % ChunkSize);
                        int chunkY = (i / AmountChunkX) * ChunkSize + (j / ChunkSize);

                        if(chunkY> Screenheight/pixelSize){continue;}
                        if(getType(chunk.PixelList[j]) == 1) { // Type Sable
                            update_sable(chunkX,chunkY);
                            continue;
                        }

                        if(getType(chunk.PixelList[j]) == 2) { // Type eau
                            update_eau(chunkX,chunkY);
                            continue;
                        }



                }

                chunk.hasUpdated = false;
            }
        }


    }

    private void update_eau(int worldX, int worldY) {


        int spreadTime = 5;

        for(int i = 0;i < spreadTime; i++) {

            if(worldY <= 0 || worldY >= Screenheight/Game.pixelSize -1){return;}
            if(worldX <= 0 || worldX >= Screenwidth/Game.pixelSize -1){return;}

            if ((getPixelData(worldX, worldY + 1) >> 31) == 0) {
                swappixel(worldX, worldY, worldX, worldY + 1);
                worldY += 1;
            } else if ((getPixelData(worldX + 1, worldY) >> 31) == 0 && worldY%2 == 0) {
                swappixel(worldX, worldY, worldX + 1, worldY);
                worldX += 1;
            } else if ((getPixelData(worldX - 1, worldY) >> 31) == 0 && worldY%2 == 1) {
                swappixel(worldX, worldY, worldX - 1, worldY);
                worldX -= 1;
            }
        }
    }


    private void update_sable(int worldX, int worldY) {


        int spreadTime = 1;

        for(int i = 0;i < spreadTime; i++) {

            if(worldY <= 0 || worldY >= Screenheight/Game.pixelSize -1){return;}
            if(worldX <= 0 || worldX >= Screenwidth/Game.pixelSize -1){return;}

            if ((getPixelData(worldX, worldY + 1) >> 31) == 0) {
                swappixel(worldX, worldY, worldX, worldY + 1);
                worldY += 1;
            }  else if ((getPixelData(worldX - 1, worldY + 1) >> 31) == 0) {
                swappixel(worldX, worldY, worldX - 1, worldY + 1);
                worldY += 1;
                worldX -= 1;
            }else if ((getPixelData(worldX + 1, worldY + 1) >> 31) == 0) {
                swappixel(worldX, worldY, worldX + 1, worldY + 1);
                worldY += 1;
                worldX += 1;
            }
        }
    }

    public int getPixelData(int x,int y) {

        if(y <= 0 || y >= Screenheight/Game.pixelSize -1){return (1 | (1 << 31));}
        if(x <= 0 || x >= Screenwidth/Game.pixelSize -1){return (1 | (1 << 31));}
;
        Chunk chunk = ChunkList[(x/ChunkSize + (y/ChunkSize)*AmountChunkX)];
        return chunk.PixelList[(x%ChunkSize)+(y%ChunkSize)*ChunkSize];
    }

    public void swappixel(int x1, int y1, int x2, int y2) {

        updateChunkAround(x1,y1);
        updateChunkAround(x2,y2);


        // Récupérer les chunks pour les deux cellules
        Chunk chunk1 = ChunkList[x1 / ChunkSize + (y1 / ChunkSize) * AmountChunkX];
        Chunk chunk2 = ChunkList[x2 / ChunkSize + (y2 / ChunkSize) * AmountChunkX];

        // Échanger les valeurs des pixels dans les deux chunks
        int tempColor = chunk1.PixelColor[x1 % ChunkSize + (y1 % ChunkSize) * ChunkSize];
        int tempData = chunk1.PixelList[x1 % ChunkSize + y1 % ChunkSize * ChunkSize];

        chunk1.PixelColor[x1 % ChunkSize + (y1 % ChunkSize) * ChunkSize] = chunk2.PixelColor[x2 % ChunkSize + (y2 % ChunkSize) * ChunkSize];
        chunk1.PixelList[x1 % ChunkSize + y1 % ChunkSize * ChunkSize] = chunk2.PixelList[x2 % ChunkSize + y2 % ChunkSize * ChunkSize];

        chunk2.PixelColor[x2 % ChunkSize + (y2 % ChunkSize) * ChunkSize] = tempColor;
        chunk2.PixelList[x2 % ChunkSize + y2 % ChunkSize * ChunkSize] = tempData;

        chunk1.bitmap.setPixel(x1 % ChunkSize, y1 % ChunkSize, chunk1.PixelColor[x1 % ChunkSize + (y1 % ChunkSize) * ChunkSize] );
        chunk2.bitmap.setPixel(x2 % ChunkSize, y2 % ChunkSize, tempColor);

        // Marquer les chunks comme actifs
        chunk1.willbeActive = true;
        chunk2.willbeActive = true;
    }


    public int getType(int nbr) {
        return (nbr << 4) >>24;
    }

    public static int setType(int originalNumber, int newType) {
        int mask = ~(255 << 20);
        int clearedNumber = originalNumber & mask;
        return clearedNumber | (newType << 20);
    }

    public void updatePixelAround(int x, int y) {


        Chunk chunk1 = ChunkList[(x) / ChunkSize + ((y) / ChunkSize) * AmountChunkX];
        int i1 =  (x % ChunkSize) + ((y % ChunkSize)) * ChunkSize;
        chunk1.PixelList[i1] = chunk1.PixelList[i1] | 1;

        chunk1 = ChunkList[(x-1) / ChunkSize + ((y-1) / ChunkSize) * AmountChunkX];
        i1 =  ((x-1) % ChunkSize) + (((y-1) % ChunkSize)) * ChunkSize;
        chunk1.PixelList[i1] = chunk1.PixelList[i1] | 1;

        chunk1 = ChunkList[(x+1) / ChunkSize + ((y-1) / ChunkSize) * AmountChunkX];
        i1 =  ((x+1) % ChunkSize) + (((y-1) % ChunkSize)) * ChunkSize;
        chunk1.PixelList[i1] = chunk1.PixelList[i1] | 1;


    }
    public void updateChunkAround(int x, int y) {

       // updatePixelAround(x,y);

        if(y% ChunkSize == 0){
            int chunkIndexUp = (x) / ChunkSize + ((y-1) / ChunkSize) * AmountChunkX;
            ChunkList[chunkIndexUp].willbeActive = true;
        }


        if(x% ChunkSize == 0){
            int chunkIndexLeft = (x-1) / ChunkSize + ((y) / ChunkSize) * AmountChunkX;
            ChunkList[chunkIndexLeft].willbeActive = true;
        }

        if(x% ChunkSize == ChunkSize-1){
            int chunkIndexRight = (x+1) / ChunkSize + ((y) / ChunkSize) * AmountChunkX;
            ChunkList[chunkIndexRight].willbeActive = true;
        }

        if(y% ChunkSize == ChunkSize-1){
            int chunkIndexRight = (x) / ChunkSize + ((y+1) / ChunkSize) * AmountChunkX;
            ChunkList[chunkIndexRight].willbeActive = true;
        }




    }


    public void setpixel(int x, int y, int color, int data) {
        Chunk chunk = ChunkList[x / ChunkSize + (y/ChunkSize)* AmountChunkX];
        chunk.PixelColor[x % ChunkSize + (y % ChunkSize) * ChunkSize] = color; // Correction ici
        chunk.bitmap.setPixel(x % ChunkSize, y % ChunkSize, color);
        chunk.PixelList[x % ChunkSize + y % ChunkSize * ChunkSize] = data;
        chunk.willbeActive = true;
    }


}
