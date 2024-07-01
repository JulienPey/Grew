package engine.pixel.grew;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

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
    private int rdm;
    public  int pixelUpdatingNbr;
    private int t;
    private Random random = new Random();
    public int[] PixelList;
    public int[] PixelColor;


    public ChunkHandler(Context context, GameLoop gameLoop, WorldHandler worldHandler){

        this.pixelSize = Game.pixelSize;
        this.worldHandler = worldHandler;
        this.Screenwidth = (int) (Resources.getSystem().getDisplayMetrics().widthPixels);
        this.Screenheight = context.getResources().getDisplayMetrics().heightPixels;


        this.Chunksbitmap = Bitmap.createBitmap(Screenwidth, Screenheight, Bitmap.Config.RGB_565);
        //Chunksbitmap.eraseColor( Color.BLACK);
        Chunksbitmap.eraseColor( Color.rgb(16,7,23));
        this.matrix = new Matrix();
        this.matrix.postScale(Game.pixelSize, Game.pixelSize);

        worldSize = (Screenwidth/Game.pixelSize)*(Screenheight/Game.pixelSize);
        worldSizeX = (Screenwidth/Game.pixelSize);
        worldSizeY = (Screenheight/Game.pixelSize);

        PixelList = new int[worldSize];
        PixelColor = new int[worldSize];

        this.pixelUpdatingNbr = 0;
        for(int i =0; i < worldSize;i++){
            PixelColor[i] = Color.rgb(16,7,23);
        }


         paint = new Paint();

        this.threadpool = new ThreadPool();
        t = 0;
        rdm = 0;
    }

    public void draw(Canvas canvas) {
       canvas.drawBitmap(Chunksbitmap, matrix, null);
    }

    public void setPixel(int x, int y, int color, int data) {
        PixelList[x+y*worldSizeX] =  data;
        PixelColor[x+y*worldSizeX] = color;
        Chunksbitmap.setPixel(x , y,color);
        rdm++;

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
        int worldX = (i % worldSizeX);
        int worldY = (i / worldSizeX);
        if(getType(PixelList[i]) == 1) { // Type Sable
            update_sable(worldX,worldY);
            return;
        }

        if(getType(PixelList[i]) == 2) { // Type eau
            update_eau(worldX,worldY);
            return;
        }

        if(getType(PixelList[i]) == 4) { // Type CreatorSable
            update_creatorSable(worldX,worldY);
            return;
        }

        if(getType(PixelList[i]) == 5) { // Type CreatorEau
            update_creatorWater(worldX,worldY);
            return;
        }
        if(getType(PixelList[i]) == 6) { // Type Deletor
            update_deletor(worldX,worldY);
            return;
        }
    }

    private void update_deletor(int worldX, int worldY) {
        int cell = getPixelData(worldX, worldY);
        if(getType(getPixelData(worldX+ 1, worldY)) != 6 || getType(getPixelData(worldX- 1, worldY)) != 6  || getType(getPixelData(worldX, worldY+1)) != 6 || getType(getPixelData(worldX, worldY-1)) != 6){

            if(getBoolean(cell,1) == 0) {
                Chunksbitmap.setPixel(worldX, worldY, Color.rgb(41, 25, 69));
                setPixelData(worldX,worldY,setBoolean(cell,1,1));

            }
          //  Chunksbitmap.setPixel(worldX, worldY, Color.rgb(50, 0, (int) (Math.sin((t+worldX+worldY)/2)*50%(50)+200)));
        } else {
            if(getBoolean(cell,1) == 0){

                int rdm = (int) (Math.random()*50);
                Chunksbitmap.setPixel(worldX, worldY, Color.rgb(26, 17, 46));
                setPixelData(worldX,worldY,setBoolean(cell,1,1));

            }}


        if ((getPixelData(worldX, worldY - 1) << 31) != 0 && getType(getPixelData(worldX, worldY - 1)) != 6) {
            setPixel(worldX, worldY - 1, Color.rgb(16,7,23), 0);
            return;
        }
        if ((getPixelData(worldX, worldY + 1) << 31) != 0 && getType(getPixelData(worldX, worldY + 1)) != 6) {
            setPixel(worldX, worldY + 1,Color.rgb(16,7,23), 0);
            return;
        }
        if ((getPixelData(worldX- 1, worldY ) << 31) != 0 && getType(getPixelData(worldX- 1, worldY)) != 6) {
            setPixel(worldX- 1, worldY , Color.rgb(16,7,23), 0);
            return;
        }
        if ((getPixelData(worldX+ 1, worldY ) << 31) != 0 && getType(getPixelData(worldX+ 1, worldY)) != 6) {
            setPixel(worldX+ 1, worldY , Color.rgb(16,7,23), 0);
            return;
        }


            /* a réutiliser pour la lave
             int cell = getPixelData(worldX, worldY);
        if(getType(getPixelData(worldX+ 1, worldY)) != 6 || getType(getPixelData(worldX- 1, worldY)) != 6  || getType(getPixelData(worldX, worldY+1)) != 6 || getType(getPixelData(worldX, worldY-1)) != 6){

                Chunksbitmap.setPixel(worldX, worldY, Color.rgb((int) (Math.sin((t+worldX+worldY)/2)*50%(50)+200), 0, 50));
        } else {
            if(getBoolean(cell,1) == 0){
                Chunksbitmap.setPixel(worldX, worldY, Color.rgb(240, 0, 50));
               setPixelData(worldX,worldY,setBoolean(cell,1,1));

}
             */


                }
    private void update_creatorWater (int worldX, int worldY) {

          if((t+worldX+worldY)%3 == 0) {

            if ((getPixelData(worldX, worldY + 1) << 31) == 0) {
                setPixel(worldX, worldY + 1, Color.rgb(5, 186, 243), ChunkHandler.setType(3, 2));
            }
        }
        }

    private void update_creatorSable(int worldX, int worldY) {

        if((t+worldX+worldY)%2 == 0){

            if ((getPixelData(worldX, worldY + 1) << 31) == 0) {
                setPixel(worldX, worldY + 1, Color.rgb(255-rdm%30, 215-rdm%30, 168-rdm%30), ChunkHandler.setType(1,  1));
            }
        }


    }

    private void update_eau(int worldX, int worldY) {

        int spreadTime = 20;
        int xswap = 0;
        int yswap = 0;
        int flyingWater = 0;
        for(int i = 0;i < spreadTime; i++) {
            Game.randomIncr++;
            if ((getPixelData(worldX+xswap, worldY + 1 + yswap) << 31) == 0) {
                yswap += 1;
                i += 5;

                if(((Game.randomIncr)%10> 8)){
                    i-=5;
                }
            }
            else if ( (getPixelData(worldX+1+xswap, worldY + yswap) << 31) == 0&& (worldY + yswap)%2 == 0) {
                xswap += 1;
            } else if ( (getPixelData(worldX-1+xswap, worldY + yswap) << 31) == 0&& (worldY + yswap)%2 == 1) {
                xswap -= 1;
            } else {
                break;
            }
        }

        if(xswap != 0 || yswap != 0){
            swappixel(worldX+xswap, worldY+yswap, worldX, worldY);
        }


    }



    private void update_sable(int worldX, int worldY) {


        int spreadTime = 5;
        int xswap = 0;
        int yswap = 0;
        for(int i = 0;i < spreadTime; i++) {
            if ((getPixelData(worldX+xswap, worldY + 1 + yswap) << 31) == 0 || getType(PixelList[(worldX+xswap)+(worldY + 1 + yswap)*worldSizeX]) == 2 ) {
                yswap += 1;
            }  else if ((getPixelData(worldX - 1+xswap, worldY + 1+yswap) << 31) == 0 || getType(PixelList[(worldX+xswap-1)+(worldY + 1 + yswap)*worldSizeX]) == 2 ) {
                yswap += 1;
                xswap -= 1;
            }else if ((getPixelData(worldX + 1+xswap, worldY + 1+yswap) << 31) == 0 || getType(PixelList[(worldX+xswap+1)+(worldY + 1 + yswap)*worldSizeX]) == 2 ) {
                yswap += 1;
                xswap += 1;
            }else{
                break;
            }
        }

        if(xswap != 0 || yswap != 0){

            int particleSpread = 4;
            if(getType(PixelList[(worldX+xswap)+(worldY + yswap)*worldSizeX]) == 2 ){
                for(int i = -particleSpread; i<particleSpread;i++){
                    if((worldX+xswap+i) <= 0 || (worldX+xswap+i) >= worldSizeX -1){break;}

                    if(getType(PixelList[(worldX+xswap+i)+(worldY + yswap)*worldSizeX]) == 0 ) {
                        swappixel(worldX+xswap, worldY+yswap, worldX+xswap+i, worldY+yswap);
                    }
                    }
                }

            swappixel(worldX+xswap, worldY+yswap, worldX, worldY);
            }



    }

    public int getPixelData(int x,int y) {

        if(y <= 0 || y >= worldSizeY -1){return (1);}
        if(x <= 0 || x >= worldSizeX -1){return (1);}
;
        return PixelList[x+(y*worldSizeX)];
    }
    public void setPixelData(int x,int y,int data) {
         PixelList[x+(y*worldSizeX)] = data;
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


    public int getBoolean(int nbr,int id) {
        return nbr<<(31 -id) >> 31;
    }

    public int setBoolean(int originalNumber, int newType,int id) {
        int mask = ~(1 << id);
        int clearedNumber = originalNumber & mask;
        return (clearedNumber | (newType << id));
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
        Chunksbitmap.eraseColor( Color.rgb(16,7,23));
        for(int i =0; i < worldSize;i++){
            PixelColor[i] = Color.rgb(16,7,23);
            PixelList[i] = 0;
        }
    }
}
