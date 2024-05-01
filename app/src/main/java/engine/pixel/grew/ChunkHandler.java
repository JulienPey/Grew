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
    private final int Screenwidth;
    private final int Screenheight;
    public final Bitmap Chunksbitmap;
    private final Matrix matrix;
    private final ThreadPool threadpool;
    private final Paint paint;
    private final int worldSize;
    private final int worldSizeX;
    private final int worldSizeY;
    private int t;
    public Chunk[] ChunkList;

    public int[] PixelList;
    public int[] PixelColor;

    public ChunkHandler(Context context, GameLoop gameLoop, WorldHandler worldHandler){

        this.pixelSize = Game.pixelSize;
        this.worldHandler = worldHandler;
        this.Screenwidth = (int) (Resources.getSystem().getDisplayMetrics().widthPixels);
        this.Screenheight = context.getResources().getDisplayMetrics().heightPixels;


        this.Chunksbitmap = Bitmap.createBitmap(Screenwidth/Game.pixelSize, Screenheight/Game.pixelSize, Bitmap.Config.ARGB_8888);
        Chunksbitmap.eraseColor( Color.BLACK);

        this.matrix = new Matrix();
        this.matrix.postScale(Game.pixelSize, Game.pixelSize);

        worldSize = (Screenwidth/Game.pixelSize)*(Screenheight/Game.pixelSize);
        worldSizeX = (Screenwidth/Game.pixelSize);
        worldSizeY = (Screenheight/Game.pixelSize);

        PixelList = new int[worldSize];
        PixelColor = new int[worldSize];
        for(int i =0; i < worldSize;i++){
            PixelColor[i] = Color.BLACK;
        }

         paint = new Paint();

        this.threadpool = new ThreadPool();
        t = 0;
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(Chunksbitmap, matrix, null);
    }

    public void setPixel(int x, int y, int color, int data) {
        PixelList[x+y*worldSizeX] =  data;
        PixelColor[x+y*worldSizeX] = color;
        Chunksbitmap.setPixel(x , y,color);

    }
    public void update() {
        t++;



        for(int y = worldSizeY - 1; y >= 0; y--) {
            if(t%2 == 0){

            for (int x = 0; x < worldSizeX; x++) {

                if ((PixelList[x+y*worldSizeX] << 31) == 0) { // pixelActive
                    continue;
                }
                updateList(x+y*worldSizeX);

            }

            } else {
                for (int x = worldSizeX - 1; x >= 0; x--) {
                    if ((PixelList[x+y*worldSizeX] << 31) == 0) { // pixelActive
                        continue;
                    }
                    updateList(x+y*worldSizeX);

                }



            }
        }

        /*
        if(t%2 == 0){

            for(int i = worldSize - 1; i >= 0; i--) {
                            if((PixelList[i] << 31) == 0) { // pixelActive
                                continue;
                            }
                            updateList(i);

            }

        } else{

            for(int y = worldSizeY - 1; y >= 0; y--) {
                for (int x = 0; x < worldSizeX; x++) {

                    if ((PixelList[x+y*worldSizeX] << 31) == 0) { // pixelActive
                        continue;
                    }
                    updateList(x+y*worldSizeX);

                }
            }

        }

         */


    }

    private void updateList(int i) {
        int chunkX = (i % worldSizeX);
        int chunkY = (i / worldSizeX);

        if(getType(PixelList[i]) == 1) { // Type Sable
            update_sable(chunkX,chunkY);
            return;
        }

        if(getType(PixelList[i]) == 2) { // Type eau
            update_eau(chunkX,chunkY);
            return;
        }
    }


    private void update_eau(int worldX, int worldY) {


        int spreadTime = 7;

        for(int i = 0;i < spreadTime; i++) {

            if(worldY <= 0 || worldY >= worldSizeY -1){return;}
            if(worldX <= 0 || worldX >= worldSizeX -1){return;}

            if ((getPixelData(worldX, worldY + 1) >> 31) == 0) {
                swappixel(worldX, worldY, worldX, worldY + 1);
                worldY += 1;
            } else if ((getPixelData(worldX + 1, worldY) >> 31) == 0 && worldY%2 == 0) {
                swappixel(worldX, worldY, worldX + 1, worldY);
                worldX += 1;
            } else if ((getPixelData(worldX - 1, worldY) >> 31) == 0 && worldY%2 == 1) {
                swappixel(worldX, worldY, worldX - 1, worldY);
                worldX -= 1;
            } else {
                return;
            }
        }
    }



    private void update_sable(int worldX, int worldY) {


        int spreadTime = 5;

        for(int i = 0;i < spreadTime; i++) {
            if(worldY <= 0 || worldY >= worldSizeY -1){return;}
            if(worldX <= 0 || worldX >= worldSizeX -1){return;}

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
            else{
                return;
            }
        }
    }

    public int getPixelData(int x,int y) {

        if(y <= 0 || y >= worldSizeY -1){return (1 | (1 << 31));}
        if(x <= 0 || x >= worldSizeX -1){return (1 | (1 << 31));}
;
        return PixelList[x+(y*worldSizeX)];
    }

    public void swappixel(int x1, int y1, int x2, int y2) {

        // Échanger les valeurs des pixels dans les deux chunks
        int tempColor = PixelColor[x1 + y1 * worldSizeX];
        int tempData = PixelList[x1 + y1 * worldSizeX];

        PixelColor[x1 + y1 * worldSizeX] = PixelColor[x2 + y2 * worldSizeX];
        PixelList[x1 + y1 * worldSizeX] = PixelList[x2 + y2 * worldSizeX];

        PixelColor[x2 + y2 * worldSizeX] = tempColor;
        PixelList[x2 + y2 * worldSizeX] = tempData;

        Chunksbitmap.setPixel(x1 , y1, PixelColor[x1 + y1 * worldSizeX] );
        Chunksbitmap.setPixel(x2, y2, tempColor);


    }


    public int getType(int nbr) {
        return (nbr << 4) >>24;
    }

    public static int setType(int originalNumber, int newType) {
        int mask = ~(255 << 20);
        int clearedNumber = originalNumber & mask;
        return clearedNumber | (newType << 20);
    }
    /*
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

     */
    /*
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

     */


    public void setpixel(int x, int y, int color, int data) {
        PixelColor[x  + y * worldSizeX] = color;
        Chunksbitmap.setPixel(x , y, color);
        PixelList[x  + y * worldSizeX] = data;
    }


}
