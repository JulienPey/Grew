package engine.pixel.grew;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;

import androidx.appcompat.app.AppCompatActivity;

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
    public  int pixelUpdatingNbr;
    private int t;

    public int[] PixelList;
    public int[] PixelColor;


    public ChunkHandler(Context context, GameLoop gameLoop, WorldHandler worldHandler){

        this.pixelSize = Game.pixelSize;
        this.worldHandler = worldHandler;
        this.Screenwidth = (int) (Resources.getSystem().getDisplayMetrics().widthPixels);
        this.Screenheight = context.getResources().getDisplayMetrics().heightPixels;


        this.Chunksbitmap = Bitmap.createBitmap(Screenwidth, Screenheight, Bitmap.Config.RGB_565);
        Chunksbitmap.eraseColor( Color.BLACK);

        this.matrix = new Matrix();
        this.matrix.postScale(Game.pixelSize, Game.pixelSize);

        worldSize = (Screenwidth/Game.pixelSize)*(Screenheight/Game.pixelSize);
        worldSizeX = (Screenwidth/Game.pixelSize);
        worldSizeY = (Screenheight/Game.pixelSize);

        PixelList = new int[worldSize];
        PixelColor = new int[worldSize];

        this.pixelUpdatingNbr = 0;
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
        pixelUpdatingNbr = 0;
        t++;
        for(int y = worldSizeY - 1; y >= 0; y--) {
            if(t%2 == 0){
            for (int x = 0; x < worldSizeX; x++) {
                updateList(x+y*worldSizeX);
            }
            } else {
                for (int x = worldSizeX - 1; x >= 0; x--) {
                    updateList(x+y*worldSizeX);
                }
            }
        }
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

        if(getType(PixelList[i]) == 4) { // Type CreatorEau
            update_creatorSable(chunkX,chunkY);
            return;
        }

        if(getType(PixelList[i]) == 5) { // Type CreatorEau
            update_creatorWater(chunkX,chunkY);
            return;
        }
    }

    private void update_creatorWater (int worldX, int worldY) {

          if((t+worldX+worldY)%4 == 0) {

            if ((getPixelData(worldX, worldY + 1) << 31) == 0) {
                setPixel(worldX, worldY + 1, Color.rgb((t) % 20, 0, 255), ChunkHandler.setType(3, 2));
            }
        }
        }

    private void update_creatorSable(int worldX, int worldY) {

        if((t+worldX+worldY)%4 == 0){

            if ((getPixelData(worldX, worldY + 1) << 31) == 0) {
                setPixel(worldX, worldY + 1, Color.rgb(t * 10, 255, 255), ChunkHandler.setType(1,  1));
            }
        }


    }


    private void update_eau(int worldX, int worldY) {

        int spreadTime = 5;
        int xswap = 0;
        int yswap = 0;
        for(int i = 0;i < spreadTime; i++) {
            if ((getPixelData(worldX, worldY + 1 + yswap) << 31) == 0) {
                yswap += 1;
            }
            else if ((getPixelData(worldX+1+xswap, worldY + yswap) << 31) == 0&& worldY%2 == 0) {
                xswap += 1;
            } else if ((getPixelData(worldX-1+xswap, worldY + yswap) << 31) == 0&& worldY%2 == 1) {
                xswap -= 1;
            } else {
                break;
            }
        }

        if(xswap != 0 || yswap != 0){
            swappixel(worldX+xswap, worldY+yswap, worldX, worldY);
        }

        /*

        int spreadTime = 7;

        for(int i = 0;i < spreadTime; i++) {


            if ((getPixelData(worldX, worldY + 1)  << 31) == 0) {
                swappixel(worldX, worldY, worldX, worldY + 1);
                worldY += 1;
            } else if ((getPixelData(worldX + 1, worldY)  << 31) == 0 && worldY%2 == 0) {
                swappixel(worldX, worldY, worldX + 1, worldY);
                worldX += 1;
            } else if ((getPixelData(worldX - 1, worldY)  << 31) == 0 && worldY%2 == 1) {
                swappixel(worldX, worldY, worldX - 1, worldY);
                worldX -= 1;
            } else {
                return;
            }
        }

         */
    }



    private void update_sable(int worldX, int worldY) {

        /*
        int spreadTime = 5;
        int yswap = 0;
        for(int i =1; i <spreadTime;i++){
            if((getPixelData(worldX, worldY + i) << 31) == 0){
                yswap = i;
            } else{
                break;
            }
        }

        if(yswap !=0){
            swappixel(worldX, worldY, worldX, worldY + yswap);
        }

         */

        int spreadTime = 5;
        int xswap = 0;
        int yswap = 0;
        for(int i = 0;i < spreadTime; i++) {
            if ((getPixelData(worldX, worldY + 1 + yswap) << 31) == 0 || (getPixelData(worldX, worldY + 1 + yswap) >> 1) << 31 != 0) {
                yswap += 1;
            }  else if ((getPixelData(worldX - 1+xswap, worldY + 1+yswap) << 31) == 0 || (getPixelData(worldX - 1+xswap, worldY + 1+yswap) >> 1) << 31 != 0) {
                yswap += 1;
                xswap -= 1;
            }else if ((getPixelData(worldX + 1+xswap, worldY + 1+yswap) << 31) == 0 || (getPixelData(worldX + 1+xswap, worldY + 1+yswap) >> 1) << 31 != 0) {
                yswap += 1;
                xswap += 1;
            }else{
                break;
            }
        }

        if(xswap != 0 || yswap != 0){
            swappixel(worldX+xswap, worldY+yswap, worldX, worldY);
        }

        /*

        int spreadTime = 5;

        for(int i = 0;i < spreadTime; i++) {

            if ((getPixelData(worldX, worldY + 1) << 31) == 0) {
                swappixel(worldX, worldY, worldX, worldY + 1);
                worldY += 1;
            }  else if ((getPixelData(worldX - 1, worldY + 1) << 31) == 0) {
                swappixel(worldX, worldY, worldX - 1, worldY + 1);
                worldY += 1;
                worldX -= 1;
            }else if ((getPixelData(worldX + 1, worldY + 1) << 31) == 0) {
                swappixel(worldX, worldY, worldX + 1, worldY + 1);
                worldY += 1;
                worldX += 1;
            }
            else{
                return;
            }
        }

         */
    }

    public int getPixelData(int x,int y) {

        if(y <= 0 || y >= worldSizeY -1){return (1);}
        if(x <= 0 || x >= worldSizeX -1){return (1);}
;
        return PixelList[x+(y*worldSizeX)];
    }

    public void swappixel(int x1, int y1, int x2, int y2) {
        int index1 = x1 + y1 * worldSizeX;
        int index2 = x2 + y2 * worldSizeX;

        int tempColor = PixelColor[index1];
        int tempData = PixelList[index1];

        PixelColor[index1] = PixelColor[index2];
        PixelList[index1] = PixelList[index2];

        PixelColor[index2] = tempColor;
        PixelList[index2] = tempData;

        Chunksbitmap.setPixel(x1, y1, PixelColor[index1]);
        Chunksbitmap.setPixel(x2, y2, tempColor);

        pixelUpdatingNbr++;
    }


    public int getType(int nbr) {
        return nbr>>24;
    }

    public static int setType(int originalNumber, int newType) {
        int mask = ~(255 << 24);
        int clearedNumber = originalNumber & mask;
        return (clearedNumber | (newType << 24));
    }


    public void clearSimulation() {
        Chunksbitmap.eraseColor( Color.BLACK);
        for(int i =0; i < worldSize;i++){
            PixelColor[i] = Color.BLACK;
            PixelList[i] = 0;
        }
    }
}
