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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
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
        this.Screenheight = worldHandler.game.height;//context.getResources().getDisplayMetrics().heightPixels;



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
        this.rdm++;

    }

    public void update() {
        this.rdm = (int) (Math.random()*100000);
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
        this.rdm++;
        int worldX = (i % worldSizeX);
        int worldY = (i / worldSizeX);
        int type = getType(PixelList[i]);
        switch (type) {
            case 1:
                update_sable(worldX, worldY);
                break;
            case 2:
                update_eau(worldX, worldY);
                break;
            case 6:
                update_deletor(worldX, worldY);
                break;
            case 5:
                update_feux(worldX, worldY);
                break;
            case 7:
                update_braise(worldX, worldY);
                break;
            case 8:
                update_lave(worldX, worldY);
                break;
            case 9:
                update_braiseLave(worldX, worldY);
                break;
            case 10:
                update_acide(worldX, worldY);
                break;
            case 11:
                update_AcideProjection(worldX, worldY);
                break;
            case 12:
                update_boum(worldX, worldY, 5);
                break;
            case 13:
                update_Dynamite(worldX, worldY);
                break;
            case 14:
                update_DynamitePowder(worldX, worldY);
                break;
            case 16:
                update_Nytroglicerine(worldX, worldY);
                break;
            case 17:
                update_boum(worldX, worldY, 25);
                break;
            case 18:
                update_HumainTeteMid(worldX,worldY);//update_HumainPied(worldX, worldY);
                break;
            case 19:
                update_HumainBrain(worldX,worldY);//update_HumainTete(worldX, worldY);
                break;
            case 20:
                update_blood(worldX, worldY);
                break;
            case 21:
                update_laserX(worldX, worldY);
                break;
            case 22:
                update_rats(worldX, worldY);
                break;
            case 23:
                update_grenade(worldX, worldY);
                break;
            case 24:
                update_grenouile(worldX, worldY);
                break;
            case 25:
                update_HumainPiedG(worldX, worldY);
                break;
            case 26:
                update_HumainTeteUp(worldX, worldY);
                break;
            default:
                // Handle unknown type if necessary
                break;
        }


    }

    private void update_HumainTeteUp(int worldX, int worldY) { // 26

        if(getType(getPixelData(worldX, worldY + 1)) != 18){
            setPixel(worldX, worldY, Color.rgb(255, 0, 0), ChunkHandler.setType(1,  20));//20
            return;
        }



    }

    private void update_HumainTeteMid(int worldX, int worldY) { // 18
        int up = getType(getPixelData(worldX, worldY - 1));
        if(up != 18 && up != 26){
            setPixel(worldX, worldY, Color.rgb(255, 0, 0), ChunkHandler.setType(1,  20));
            return;
        }

        int bas = getType(getPixelData(worldX, worldY + 1));
        if(bas != 18 && bas != 25 && bas != 19){
            setPixel(worldX, worldY, Color.rgb(255, 0, 0), ChunkHandler.setType(1,  20));
            return;
        }



    }


    private void update_HumainPiedG(int worldX, int worldY) { // 25

        if(getType(getPixelData(worldX+1, worldY)) != 19){
            setPixel(worldX, worldY, Color.rgb(255, 0, 0), ChunkHandler.setType(1,  20));
            return;
        }

        if(getType(getPixelData(worldX, worldY - 1)) != 18){
            setPixel(worldX, worldY, Color.rgb(255, 0, 0), ChunkHandler.setType(1,  20));
            return;
        }
    }



    private void update_HumainBrain(int worldX, int worldY) { // 19

        if(getType(getPixelData(worldX-1, worldY)) != 25){
            setPixel(worldX, worldY, Color.rgb(255, 0, 0), ChunkHandler.setType(1,  20));
            return;
        }

        if(getType(getPixelData(worldX, worldY - 1)) != 18){
            setPixel(worldX, worldY, Color.rgb(255, 0, 0), ChunkHandler.setType(1,  20));
            return;
        }

        int data = getPixelData(worldX,worldY);
        if(!oneUpdateAtATime(data,worldX,worldY)){
            return;
        }


        int goDroite =  getBoolean2(data,4);
        int goGauche =  getBoolean2(data,5);
        int goDroiteN;
        int goGaucheN;
        if(!IsMovable(18,(getPixelData(worldX, worldY + 1) ))&& !IsMovable(18,(getPixelData(worldX-1, worldY + 1) )) && rdm%20 == 0){
            int rdm2 = rdm*4%50;
            if(rdm2 < 20){
                goDroiteN = 1;
                goGaucheN = 0;
            } else if (rdm2 > 30){
                goDroiteN = 0;
                goGaucheN = 1;
            } else {
                goDroiteN = 0;
                goGaucheN = 0;
            }

            boolean haschanged = false;
            if(goDroite != goDroiteN){
                goDroite = goDroiteN;
                data = setBoolean(data,goDroiteN,4);
                haschanged = true;
            }

            if(goGauche != goGaucheN){
                goGauche = goGaucheN;
                data = setBoolean(data,goGaucheN,5);
                haschanged = true;
            }

            if(haschanged){
                setPixelData(worldX,worldY,data);
            }
        }

        int spreadTime = 10;
        int xswap = 0;
        int yswap = 0;

        boolean isjumping = false;

        for(int i = 0;i < spreadTime; i++) {
            if (!isjumping && IsMovable(18,(getPixelData(worldX+xswap, worldY + 1 + yswap) )) && IsMovable(18,(getPixelData(worldX+xswap-1, worldY + 1 + yswap) )) ) {
                yswap += 1;
                i++;
            } else if(goDroite == 1 && isEmptyRow(worldX+xswap+1,worldY+yswap)){
                xswap +=1;
                break;
            } else if(goGauche == 1 && isEmptyRow(worldX+xswap-2,worldY+yswap)){
                xswap -=1;
                break;
            } else if((goGauche == 1 || goDroite == 1) && IsMovable(18,(getPixelData(worldX+xswap, worldY -7 + yswap) )) && IsMovable(18,(getPixelData(worldX+xswap-1, worldY -7 + yswap) )) ){
                yswap -= 1;
                isjumping = true;
            }
            /*
           else if(goGauche == 1 && isEmptyRow(worldX+xswap-2,worldY+yswap-8) && isEmptyRow(worldX+xswap-1,worldY+yswap-8)){
                xswap -=1;
                yswap -= 7;
                break;
            }else if(goDroite == 1 && isEmptyRow(worldX+xswap+1,worldY+yswap-8)&& isEmptyRow(worldX+xswap,worldY+yswap-8)){
                xswap +=1;
                yswap -= 7;
                break;
            }
           */

            else {
                break;
            }
        }

        if(xswap != 0 || yswap != 0){
            Log.e("ALED", String.valueOf(xswap));
            moveHumain(worldX+xswap, worldY+yswap, worldX, worldY);
        }


    }


    private boolean isEmptyRow(int worldX, int worldY){
        for(int i =0; i < 7;i++){
            if((!IsMovable(18,(getPixelData(worldX, worldY - i) )))) {
                return false;
            }
        }
        return true;
    }

    private void moveHumain(int worldX2, int worldY2,int worldX,int worldY){
        if(worldX2 >= worldX && worldY2 >= worldY){
            for(int i =0; i < 7; i++){
                swappixel(worldX, worldY-i, worldX2, worldY2-i);
                swappixel(worldX-1, worldY-i, worldX2-1, worldY2-i);
            }
        } else if (worldX2 <= worldX && worldY2 >= worldY){
            for(int i =0; i < 7; i++){
                swappixel(worldX-1, worldY-i, worldX2-1, worldY2-i);
                swappixel(worldX, worldY-i, worldX2, worldY2-i);
            }

        }else if (worldX2 <= worldX){
            for(int i =7; i >= 0; i--){
                swappixel(worldX-1, worldY-i, worldX2-1, worldY2-i);
                swappixel(worldX, worldY-i, worldX2, worldY2-i);
            }
        } else {
            for(int i =7; i >= 0; i--){
                swappixel(worldX, worldY-i, worldX2, worldY2-i);
                swappixel(worldX-1, worldY-i, worldX2-1, worldY2-i);
            }

        }

    }



    private void update_grenouile(int worldX, int worldY) {

        int data = getPixelData(worldX,worldY);
        if(!oneUpdateAtATime(data,worldX,worldY)){
            return;
        }
        int spreadTime = 5;
        int xswap = 0;
        int yswap = 0;


        int goDroite =  getBoolean(data,4);
        int goGauche =  getBoolean(data,5);


        if(!IsMovable(18,(getPixelData(worldX+xswap, worldY + 1 + yswap) )) && rdm%20 == 0){
            int rdm2 = rdm*4%50; //(int) (Math.random()*50);
            if(rdm2 < 10){
                goDroite = 0;
                goGauche = 1;
            } else if (rdm2 > 30){
                goDroite = 1;
                goGauche = 0;
            } else {
                goDroite = 1;
                goGauche = 1;
            }

            data = setBoolean(data,goDroite,4);
            data = setBoolean(data,goGauche,5);
            setPixelData(worldX,worldY,data);
        }

        for(int i = 0;i < spreadTime; i++) {
            if (IsMovable(18,(getPixelData(worldX+xswap, worldY + 1 + yswap) )) ) {
                yswap += 1;
                if(!IsMovable(18,(getPixelData(worldX+xswap, worldY + 2 + yswap) ))){
                    break;
                }
            } else if (goGauche == 0 && IsMovable(18,(getPixelData(worldX+xswap-1, worldY + yswap-1) )) ) {
                xswap -= 1;
                yswap -= 1;
                break;
            }else if (goDroite == 0 && IsMovable(18,(getPixelData(worldX+xswap+1, worldY + yswap-1) )) ) {
                xswap += 1;
                yswap -= 1;
                break;
            }
            else{
                break;
            }
        }

        if(xswap != 0 || yswap != 0){
            swappixel(worldX+xswap, worldY+yswap, worldX, worldY);
        }

    }

    private void update_grenade(int worldX, int worldY) {


        boolean boum = false;
        int spreadTime = 5;
        int xswap = 0;
        int yswap = 0;
        for(int i = 0;i < spreadTime; i++) {
            if (IsMovable(1,(getPixelData(worldX+xswap, worldY + 1 + yswap) )) ) {
                yswap += 1;
            }  else if (IsMovable(1,(getPixelData(worldX - 1+xswap, worldY + 1+yswap) ))) {
                yswap += 1;
                xswap -= 1;
            }else if (IsMovable(1,(getPixelData(worldX + 1+xswap, worldY + 1+yswap)))) {
                yswap += 1;
                xswap += 1;
            }else{
                boum = true;
                break;
            }
        }

        if(xswap != 0 || yswap != 0){
            swappixel(worldX+xswap, worldY+yswap, worldX, worldY);
        }

    if(boum){
        setPixel(worldX+xswap, worldY+yswap, Color.rgb(255, 0, 0), ChunkHandler.setType(1,  12));

    }


    }

    private void update_rats(int worldX, int worldY) {
        int data = getPixelData(worldX,worldY);
        if(!oneUpdateAtATime(data,worldX,worldY)){
            return;
        }
        int spreadTime = 5;
        int xswap = 0;
        int yswap = 0;


        int goDroite =  getBoolean(data,4);
        int goGauche =  getBoolean(data,5);


        if(!IsMovable(18,(getPixelData(worldX+xswap, worldY + 1 + yswap) )) && rdm%20 == 0){
            int rdm2 = rdm*4%50; //(int) (Math.random()*50);
            if(rdm2 < 10){
                goDroite = 0;
                goGauche = 1;
            } else if (rdm2 > 30){
                goDroite = 1;
                goGauche = 0;
            } else {
                goDroite = 1;
                goGauche = 1;
            }

            data = setBoolean(data,goDroite,4);
            data = setBoolean(data,goGauche,5);
            setPixelData(worldX,worldY,data);
        }

        for(int i = 0;i < spreadTime; i++) {
            if (IsMovable(18,(getPixelData(worldX+xswap, worldY + 1 + yswap) )) ) {
                yswap += 1;
            } else if (goDroite == 0 && IsMovable(18,(getPixelData(worldX+xswap+1, worldY + yswap) )) ) {
                xswap += 1;
                break;
            } else if (goGauche == 0 && IsMovable(18,(getPixelData(worldX+xswap-1, worldY + yswap) )) ) {
                xswap -= 1;
                break;
            }else if (goGauche == 0 && IsMovable(18,(getPixelData(worldX+xswap-1, worldY + yswap-1) )) ) {
                xswap -= 1;
                yswap -= 1;
                break;
            }else if (goDroite == 0 && IsMovable(18,(getPixelData(worldX+xswap+1, worldY + yswap-1) )) ) {
                xswap += 1;
                yswap -= 1;
                break;
            }
            else{
                break;
            }
        }

        if(xswap != 0 || yswap != 0){
            swappixel(worldX+xswap, worldY+yswap, worldX, worldY);
        }

    }

    private void update_laserX(int worldX, int worldY) {

        int data = getPixelData(worldX, worldY);

        oneUpdateAtATime(data,worldX,worldY);
        rdm++;
        if((rdm)%3== 0) {
            setPixel(worldX, worldY, Color.rgb(16, 7, 23), ChunkHandler.setType(0, 0));
            return;
        }

        // Monter
        if(rdm%2 == 0){
            if ((getPixelData(worldX-1, worldY - 1) << 31) == 0  && (worldX+t)%2 == 0  ) {
                swappixel(worldX-1, worldY-1, worldX, worldY);
            }
        } else {
            if ((getPixelData(worldX+1, worldY - 1) << 31) == 0  && (worldX+t)%2 == 0  ) {
                swappixel(worldX+1, worldY-1, worldX, worldY);
            }

        }

        int pixelValueG = PixelList[worldX-1 + worldY * worldSizeX];
        if (((pixelValueG & (1 << 3)) >> 3) == 1 && (rdm)%4 == 0) {
            setPixel( (worldX-1),  (worldY), Color.rgb(255, 70, 50), ChunkHandler.setType( 1 ,7) );
        }

        int pixelValueD = PixelList[worldX+1 + worldY * worldSizeX];
        if (((pixelValueD & (1 << 3)) >> 3) == 1  ) {
            setPixel( (worldX+1),  (worldY), Color.rgb(255, 70, 50), ChunkHandler.setType( 1 ,7) );
        }

        int pixelValueU = PixelList[worldX + (worldY-1) * worldSizeX];
        if (((pixelValueU & (1 << 3)) >> 3) == 1 && (rdm)%4 == 0) {
            setPixel( (worldX),  (worldY-1), Color.rgb(255, 70, 50), ChunkHandler.setType( 1 ,7) );
        }



    }

    private void update_blood(int worldX, int worldY) {

        int spreadTime = 20;
        int xswap = 0;
        int yswap = 0;
        for(int i = 0;i < spreadTime; i++) {
            Game.randomIncr++;
            if (IsMovable(20,(getPixelData(worldX+xswap, worldY + 1 + yswap)))) {
                yswap += 1;
                i += 5;

                if(((Game.randomIncr)%10> 8)){
                    i-=5;
                }
            }
            else if ( IsMovable(20,(getPixelData(worldX+1+xswap, worldY + yswap) )) && (worldY + yswap)%2 == 0) {
                xswap += 1;
            } else if ( IsMovable(20,(getPixelData(worldX-1+xswap, worldY + yswap))) && (worldY + yswap)%2 == 1) {
                xswap -= 1;
            } else {
                break;
            }
        }

        if(xswap != 0 || yswap != 0){
            swappixel(worldX+xswap, worldY+yswap, worldX, worldY);
        }


    }



    private void update_HumainTete(int worldX, int worldY) {
        if(getType(getPixelData(worldX,worldY +1 )) != 18){
            setPixel(worldX, worldY, Color.rgb(255, 0, 0), ChunkHandler.setType(1,  20));
            return;
        }
    }

    private void update_HumainPied(int worldX, int worldY) {

        if(getType(getPixelData(worldX,worldY -1)) != 19){
            setPixel(worldX, worldY, Color.rgb(255, 0, 0), ChunkHandler.setType(1,  20));
            return;
        }
        int data = getPixelData(worldX,worldY);
        if(!oneUpdateAtATime(data,worldX,worldY)){
            return;
        }

        int spreadTime = 5;
        int xswap = 0;
        int yswap = 0;


        int goDroite =  getBoolean(data,4);
        int goGauche =  getBoolean(data,5);


        if(!IsMovable(18,(getPixelData(worldX+xswap, worldY + 1 + yswap) )) && rdm%20 == 0){
            int rdm2 = rdm*4%50; //(int) (Math.random()*50);
            if(rdm2 < 10){
                goDroite = 0;
                goGauche = 1;
            } else if (rdm2 > 30){
                goDroite = 1;
                goGauche = 0;
            } else {
                goDroite = 1;
                goGauche = 1;
            }

            data = setBoolean(data,goDroite,4);
            data = setBoolean(data,goGauche,5);
            setPixelData(worldX,worldY,data);
        }

        for(int i = 0;i < spreadTime; i++) {
            if (IsMovable(18,(getPixelData(worldX+xswap, worldY + 1 + yswap) )) ) {
                yswap += 1;
            } else if (goDroite == 0 && IsMovable(18,(getPixelData(worldX+xswap+1, worldY + yswap) )) && IsMovable(18,(getPixelData(worldX+xswap+1, worldY + yswap-1) ))) {
                xswap += 1;
                break;
            } else if (goGauche == 0 && IsMovable(18,(getPixelData(worldX+xswap-1, worldY + yswap) )) && IsMovable(18,(getPixelData(worldX+xswap-1, worldY + yswap-1) ))) {
                xswap -= 1;
                break;
            }else if (goGauche == 0 && IsMovable(18,(getPixelData(worldX+xswap-1, worldY + yswap-2) )) && IsMovable(18,(getPixelData(worldX+xswap-1, worldY + yswap-3) ))) {
                xswap -= 1;
                yswap -= 2;
                break;
            }else if (goDroite == 0 && IsMovable(18,(getPixelData(worldX+xswap+1, worldY + yswap-2) )) && IsMovable(18,(getPixelData(worldX+xswap+1, worldY + yswap-3) ))) {
                xswap += 1;
                yswap -= 2;
                break;
            }

            else{
                break;
            }
        }

        if(xswap != 0 || yswap != 0){

            swappixel(worldX+xswap, worldY+yswap, worldX, worldY);
            swappixel(worldX+xswap, worldY+yswap-1, worldX, worldY-1);

        }


    }

    private void update_Nytroglicerine(int worldX, int worldY) {
        for(int x =-1; x < 1; x++) {
            for (int y = -1; y < 1; y++) {
                if(y == 0 && x == 0){continue;}
                if(IsMovable( 13,getPixelData(worldX+x,worldY +y) )){
                    setPixel(worldX, worldY, Color.rgb(255, 0, 0), ChunkHandler.setType(0,  17));
                    return;
                }
            }
        }



        int spreadTime = 20;
        int xswap = 0;
        int yswap = 0;
        for(int i = 0;i < spreadTime; i++) {
            Game.randomIncr++;
            if (IsMovable(16,(getPixelData(worldX+xswap, worldY + 1 + yswap)))) {
                yswap += 1;
                i += 5;

                if(((Game.randomIncr)%10> 8)){
                    i-=5;
                }
            }
            else if ( IsMovable(16,(getPixelData(worldX+1+xswap, worldY + yswap) )) && (worldY + yswap)%2 == 0) {
                xswap += 1;
            } else if ( IsMovable(16,(getPixelData(worldX-1+xswap, worldY + yswap))) && (worldY + yswap)%2 == 1) {
                xswap -= 1;
            } else {
                break;
            }
        }

        if(xswap != 0 || yswap != 0){
            swappixel(worldX+xswap, worldY+yswap, worldX, worldY);
        }


    }

    private void update_DynamitePowder(int worldX, int worldY) {
        for(int x =-1; x < 1; x++) {
            for (int y = -1; y < 1; y++) {
                if(y == 0 && x == 0){continue;}
                if(IsMovable( 13,getPixelData(worldX+x,worldY +y) )){
                    setPixel(worldX, worldY, Color.rgb(255, 0, 0), ChunkHandler.setType(0,  12));
                    return;
                }
            }
        }

        int spreadTime = 5;
        int xswap = 0;
        int yswap = 0;
        for(int i = 0;i < spreadTime; i++) {
            if (IsMovable(14,(getPixelData(worldX+xswap, worldY + 1 + yswap) )) ) {
                yswap += 1;
            }  else if (IsMovable(14,(getPixelData(worldX - 1+xswap, worldY + 1+yswap) ))) {
                yswap += 1;
                xswap -= 1;
            }else if (IsMovable(14,(getPixelData(worldX + 1+xswap, worldY + 1+yswap)))) {
                yswap += 1;
                xswap += 1;
            }else{
                break;
            }
        }

        if(xswap != 0 || yswap != 0){
            swappixel(worldX+xswap, worldY+yswap, worldX, worldY);
        }



    }

    private void update_Dynamite(int worldX, int worldY) {
        for(int x =-2; x < 2; x++) {
            for (int y = -2; y < 2; y++) {
                if(y == 0 && x == 0){continue;}
                if(IsMovable( 13,getPixelData(worldX+x,worldY +y) )){
                    setPixel(worldX, worldY, Color.rgb(255, 0, 0), ChunkHandler.setType(0,  12));

                }
            }
        }
    }

    private void update_boum(int worldX, int worldY,int radius) {
        int r = radius+rdm%15;

        if(rdm%5 != 2){return;}

        for(int x =-r; x < r; x++){
            for(int y =-r; y < r; y++){
                    if(x*x  + y*y < r*r){
                        if(isInBound(worldX+x,worldY +y)&& IsMovable(12, getPixelData(worldX+x,worldY +y))){
                            if(rdm%50 == 1){
                               // worldHandler.particlehandler.particlesList.add(new Particle(worldX+x,worldY+y,0,-1,0,0, Color.argb( 130 + (rdm*7)%100,255, 255, 255),1,30+(rdm%20)));
                                setPixel(worldX+x, worldY +y, Color.rgb(255, 255, 255), ChunkHandler.setType(0,  5));
                            } else {
                                setPixel(worldX+x, worldY +y, Color.rgb(255, 100+100-(x*x  + y*y*4)%100, 0), ChunkHandler.setType(0,  5));
                            }
                        }
                    }

            }

        }




    }

    private void update_acide(int worldX, int worldY) {

        int spreadTime = 20;
        int xswap = 0;
        int yswap = 0;
        int flyingWater = 0;
        for(int i = 0;i < spreadTime; i++) {
            Game.randomIncr++;
            if (IsMovable(10,(getPixelData(worldX+xswap, worldY + 1 + yswap)))) {
                yswap += 1;
                i += 5;

                if(((Game.randomIncr)%10> 8)){
                    i-=5;
                }
            }
            else if ( IsMovable(10,(getPixelData(worldX+1+xswap, worldY + yswap) )) && (worldY + yswap)%2 == 0) {
                xswap += 1;
            } else if ( IsMovable(10,(getPixelData(worldX-1+xswap, worldY + yswap))) && (worldY + yswap)%2 == 1) {
                xswap -= 1;
            } else {
                break;
            }
        }

        if(xswap != 0 || yswap != 0){
            swappixel(worldX+xswap, worldY+yswap, worldX, worldY);
        }
        else if(rdm%400==7){
            worldHandler.particlehandler.particlesList.add(new Particle(worldX+xswap,worldY+yswap,0,-1,0,0, Color.argb( 130 + (rdm*7)%100,105, 255, 35),2,1+(rdm%40)));
        }


        boolean hasdestroyed = false;

        if(IsMovable(15,(getPixelData(worldX+xswap, worldY + yswap-1)))){
            setPixel(worldX+xswap, worldY +yswap- 1, Color.rgb(16,7,23), ChunkHandler.setType(0,  0));
            hasdestroyed = true;
        }

        if(IsMovable(15,(getPixelData(worldX+xswap, worldY + yswap+1)))){
            setPixel(worldX+xswap, worldY +yswap+ 1, Color.rgb(16,7,23), ChunkHandler.setType(0,  0));
            hasdestroyed = true;
        }
        if(IsMovable(15,(getPixelData(worldX+xswap, worldY + yswap+1)))){
            setPixel(worldX+xswap+1, worldY +yswap, Color.rgb(16,7,23), ChunkHandler.setType(0,  0));
            hasdestroyed = true;
        }
        if(IsMovable(15,(getPixelData(worldX+xswap, worldY + yswap+1)))){
            setPixel(worldX+xswap-1, worldY +yswap, Color.rgb(16,7,23), ChunkHandler.setType(0,  0));
            hasdestroyed = true;
        }

        // up
        if(IsMovable(0,(getPixelData(worldX+xswap, worldY + yswap-1)))){
            setPixel(worldX+xswap, worldY +yswap- 1, Color.rgb(76, 255, 0), ChunkHandler.setType(0,  11));

        }

        if(hasdestroyed){
            if(rdm%2  == 0){
                setPixel(worldX+xswap, worldY +yswap, Color.rgb(16,7,23), ChunkHandler.setType(0,  0));
            }
        }
    }

    private void update_lave(int worldX, int worldY) {


        int spreadTime = 5;
        int xswap = 0;
        int yswap = 0;

        int pixelValue = PixelList[worldX + worldY * worldSizeX];
       // oneUpdateAtATime(pixelValue,worldX,worldY);

        for(int i = 0;i < spreadTime; i++) {

            if (IsMovable(8,(getPixelData(worldX+xswap, worldY + 1 + yswap)) )) {
                yswap += 1;
                continue;
            }
            if(worldX%2 ==0) {
                if (IsMovable(8,(getPixelData(worldX - 1 + xswap, worldY + yswap)))) {
                    xswap -= 1;
                } else if (IsMovable(8,(getPixelData(worldX + 1 + xswap, worldY + yswap) ))) {
                    xswap += 1;
                } else {
                    break;
                }
            } else {
                if (IsMovable(8,(getPixelData(worldX + 1 + xswap, worldY + yswap)))) {
                    xswap += 1;
                } else if (IsMovable(8,(getPixelData(worldX - 1 + xswap, worldY + yswap) ))) {
                    xswap -= 1;
                }  else {
                    break;
                }

            }



        }

        if(xswap != 0 || yswap != 0){
            swappixel(worldX+xswap, worldY+yswap, worldX, worldY);
        }



        if (IsMovable(9,(getPixelData(worldX, worldY - 1)))) {
            setPixel(worldX, worldY - 1, Color.rgb(252, 61, 0), ChunkHandler.setType(0,  9));
        }



        int rdmx = ((int) (rdm)%8) - 4;
        int rdmy =  ((int) (Math.random()*8)) - 4;
        if(worldY+yswap +rdmy >= worldSizeY || worldY+yswap +rdmy <= 0 || worldX+xswap +rdmx >= worldSizeX || worldX+xswap +rdmx <= 0){
            return;
        }
        if( getType(PixelList[(worldX+xswap + rdmx)+(worldY + yswap + rdmy)*worldSizeX]) == 2){
            setPixel( worldX+xswap,  worldY+yswap, Color.rgb(0, 0, 30+((rdm*7)%90)), ChunkHandler.setType( 1 ,3));
            worldHandler.particlehandler.particlesList.add(new Particle(worldX+xswap,worldY+yswap,0,-1,0,0, Color.argb( 130 + (rdm*7)%100,200, 200, 200),1,1+(rdm%40)));
        }

    }

    private void update_braise(int worldX, int worldY) {

        if ((getPixelData(worldX, worldY - 1) << 31) == 0) {
            setPixel(worldX, worldY - 1, Color.rgb(255, 201, 59), ChunkHandler.setType(0,  5));
        }


        int pixelValueG = PixelList[worldX-1 + worldY * worldSizeX];
        if (((pixelValueG & (1 << 3)) >> 3) == 1 && (rdm)%4 == 0) {
            setPixel( (worldX-1),  (worldY), Color.rgb(255, 70, 50), ChunkHandler.setType( 1 ,7) );
        }

        int pixelValueD = PixelList[worldX+1 + worldY * worldSizeX];
        if (((pixelValueD & (1 << 3)) >> 3) == 1 && (rdm)%3 == 0) {
            setPixel( (worldX+1),  (worldY), Color.rgb(255, 70, 50), ChunkHandler.setType( 1 ,7) );
        }

        int pixelValueU = PixelList[worldX + (worldY-1) * worldSizeX];
        if (((pixelValueU & (1 << 3)) >> 3) == 1 && (rdm)%4 == 0) {
            setPixel( (worldX),  (worldY-1), Color.rgb(255, 70, 50), ChunkHandler.setType( 1 ,7) );
        }

        int pixelValueB = PixelList[worldX + (worldY+1) * worldSizeX];
        if (((pixelValueB & (1 << 3)) >> 3) == 1) {
            setPixel( (worldX),  (worldY+1), Color.rgb(255, 70, 50), ChunkHandler.setType( 1 ,7) );
        }


        if((rdm)%200 < 5){
            setPixel(worldX, worldY, Color.rgb(16, 7, 23), ChunkHandler.setType(0, 0));
            return;
        }


    }

    private void update_feux(int worldX, int worldY) {

        rdm++;

        ///// PAS 2 UPDATE PAR FRAME
        int pixelValue = PixelList[worldX + worldY * worldSizeX];
        if (((pixelValue & (1 << 2)) >> 2) == t % 2) {
            return;
        }
        if (t % 2 == 1) {
            PixelList[worldX + worldY * worldSizeX] += (1 << 2);
        } else {
            PixelList[worldX + worldY * worldSizeX] -= (1 << 2);
        }

        if((rdm)%6 == 0) {
            setPixel(worldX, worldY, Color.rgb(16, 7, 23), ChunkHandler.setType(0, 0));
            return;
        }




        // Monter
        if(rdm%5 == 2){
            if ((getPixelData(worldX-1, worldY - 1) << 31) == 0  && (worldX+t)%2 == 0  ) {
                swappixel(worldX-1, worldY-1, worldX, worldY);
            }
        } else if(rdm%5 == 1){
            if ((getPixelData(worldX+1, worldY - 1) << 31) == 0  && (worldX+t)%2 == 0  ) {
                swappixel(worldX+1, worldY-1, worldX, worldY);
            }

        } else {
            if ((getPixelData(worldX, worldY - 1) << 31) == 0  && (worldX+t)%2 == 0  ) {
                swappixel(worldX, worldY-1, worldX, worldY);
            }

        }


        int pixelValueG = PixelList[worldX-1 + worldY * worldSizeX];
        if (((pixelValueG & (1 << 3)) >> 3) == 1 && (rdm)%4 == 0) {
            setPixel( (worldX-1),  (worldY), Color.rgb(255, 70, 50), ChunkHandler.setType( 1 ,7) );
        }

        int pixelValueD = PixelList[worldX+1 + worldY * worldSizeX];
        if (((pixelValueD & (1 << 3)) >> 3) == 1  ) {
            setPixel( (worldX+1),  (worldY), Color.rgb(255, 70, 50), ChunkHandler.setType( 1 ,7) );
        }

        int pixelValueU = PixelList[worldX + (worldY-1) * worldSizeX];
        if (((pixelValueU & (1 << 3)) >> 3) == 1 && (rdm)%4 == 0) {
            setPixel( (worldX),  (worldY-1), Color.rgb(255, 70, 50), ChunkHandler.setType( 1 ,7) );
        }


    }

    private void update_AcideProjection(int worldX, int worldY) {
        rdm++;
        ///// PAS 2 UPDATE PAR FRAME
        int pixelValue = PixelList[worldX + worldY * worldSizeX];
        if (((pixelValue & (1 << 2)) >> 2) == t % 2) {
            return;
        }
        if (t % 2 == 1) {
            PixelList[worldX + worldY * worldSizeX] += (1 << 2);
        } else {
            PixelList[worldX + worldY * worldSizeX] -= (1 << 2);
        }

        if((rdm)%6 == 0) {
            setPixel(worldX, worldY, Color.rgb(16, 7, 23), ChunkHandler.setType(0, 0));
            return;
        }

        // Monter
        if(rdm%2 == 0){
            if ((getPixelData(worldX-1, worldY - 1) << 31) == 0  && (worldX+t)%2 == 0  ) {
                swappixel(worldX-1, worldY-1, worldX, worldY);
            }
        } else {
            if ((getPixelData(worldX+1, worldY - 1) << 31) == 0  && (worldX+t)%2 == 0  ) {
                swappixel(worldX+1, worldY-1, worldX, worldY);
            }

        }


    }

    private void update_braiseLave(int worldX, int worldY) {
        rdm++;

        ///// PAS 2 UPDATE PAR FRAME
        int pixelValue = PixelList[worldX + worldY * worldSizeX];
        if (((pixelValue & (1 << 2)) >> 2) == t % 2) {
            return;
        }
        if (t % 2 == 1) {
            PixelList[worldX + worldY * worldSizeX] += (1 << 2);
        } else {
            PixelList[worldX + worldY * worldSizeX] -= (1 << 2);
        }

        if((rdm)%6 == 0) {
            setPixel(worldX, worldY, Color.rgb(16, 7, 23), ChunkHandler.setType(0, 0));
            return;
        }

        // Monter
        if(rdm%2 == 0){
            if ((getPixelData(worldX-1, worldY - 1) << 31) == 0  && (worldX+t)%2 == 0  ) {
                swappixel(worldX-1, worldY-1, worldX, worldY);
            }
        } else {
            if ((getPixelData(worldX+1, worldY - 1) << 31) == 0  && (worldX+t)%2 == 0  ) {
                swappixel(worldX+1, worldY-1, worldX, worldY);
            }

        }

        int pixelValueG = PixelList[worldX-1 + worldY * worldSizeX];
        if (((pixelValueG & (1 << 3)) >> 3) == 1 && (rdm)%4 == 0) {
            setPixel( (worldX-1),  (worldY), Color.rgb(255, 70, 50), ChunkHandler.setType( 1 ,7) );
        }

        int pixelValueD = PixelList[worldX+1 + worldY * worldSizeX];
        if (((pixelValueD & (1 << 3)) >> 3) == 1  ) {
            setPixel( (worldX+1),  (worldY), Color.rgb(255, 70, 50), ChunkHandler.setType( 1 ,7) );
        }

        int pixelValueU = PixelList[worldX + (worldY-1) * worldSizeX];
        if (((pixelValueU & (1 << 3)) >> 3) == 1 && (rdm)%4 == 0) {
            setPixel( (worldX),  (worldY-1), Color.rgb(255, 70, 50), ChunkHandler.setType( 1 ,7) );
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



                }

    private void update_eau(int worldX, int worldY) {

        int spreadTime = 20;
        int xswap = 0;
        int yswap = 0;
        int flyingWater = 0;
        for(int i = 0;i < spreadTime; i++) {
            Game.randomIncr++;
            if (IsMovable(2,(getPixelData(worldX+xswap, worldY + 1 + yswap) ))) {
                yswap += 1;
                i += 5;

                if(((Game.randomIncr)%10> 8)){
                    i-=5;
                }
            }
            else if ( IsMovable(2,(getPixelData(worldX+1+xswap, worldY + yswap) ))&& (worldY + yswap)%2 == 0) {
                xswap += 1;
            } else if ( IsMovable(2,(getPixelData(worldX-1+xswap, worldY + yswap)))&& (worldY + yswap)%2 == 1) {
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
            if (IsMovable(1,(getPixelData(worldX+xswap, worldY + 1 + yswap) )) ) {
                yswap += 1;
            }  else if (IsMovable(1,(getPixelData(worldX - 1+xswap, worldY + 1+yswap) ))) {
                yswap += 1;
                xswap -= 1;
            }else if (IsMovable(1,(getPixelData(worldX + 1+xswap, worldY + 1+yswap)))) {
                yswap += 1;
                xswap += 1;
            }else{
                break;
            }
        }

        if(xswap != 0 || yswap != 0){
            swappixel(worldX+xswap, worldY+yswap, worldX, worldY);
            }

        // INTERACTION SABLE & LAVE
        int rdmx = (rdm*4%14) - 7;
        int rdmy =  ((int) (Math.random()*14)) - 7;
        if(worldY+yswap +rdmy >= worldSizeY || worldY+yswap +rdmy <= 0 || worldX+xswap +rdmx >= worldSizeX || worldX+xswap +rdmx <= 0){
            return;
        }
        if( getType(PixelList[(worldX+xswap + rdmx)+(worldY + yswap + rdmy)*worldSizeX]) == 8){
            setPixel( worldX+xswap,  worldY+yswap, Color.rgb(200 + (rdm*7%50), 200 + (rdm*6%50), 200 + (rdm*5%50)), ChunkHandler.setType( 1 ,3) );
            worldHandler.particlehandler.particlesList.add(new Particle(worldX+xswap,worldY+yswap,0,-1,0,0, Color.argb( 130 + (rdm*7)%100,200, 200, 200),1,1+(rdm%40)));
        }


    }

    public boolean isInBound(int x,int y) {
        return y > 0 && y < worldSizeY - 1 && x > 0 && x < worldSizeX - 1;
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

    private boolean oneUpdateAtATime(int pixelValue,int worldX,int worldY){
        if (((pixelValue & (1 << 2)) >> 2) == t % 2) {
            return false;
        }
        if (t % 2 == 1) {
            PixelList[worldX + worldY * worldSizeX] += (1 << 2);
        } else {
            PixelList[worldX + worldY * worldSizeX] -= (1 << 2);
        }
        return true;
    }

    public int getBoolean(int nbr,int id) {
        return nbr<<(31 -id) >> 31;
    }

    public int getBoolean2(int nbr,int id) {
       if(nbr<<(31 -id) >> 31 == 0){
           return 0;
       }
       return 1;
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

    private boolean IsMovable(int typeElement, int data) {
        int type = getType(data);
        switch (typeElement) {
            case 0:
                return data << 31 == 0 ;
            case 1:
                return data << 31 == 0 || type == 2 ||type == 10 ||type == 20 || type == 16;
            case 2:
                return data << 31 == 0 ;
            case 8:
                return data << 31 == 0 || type == 10;
            case 9:
                return data << 31 == 0 || type == 10;
            case 10:
                return data << 31 == 0 ||type == 2;
            case 12:
                return true;//(type != 12);
            case 13:
                return (type == 5 || type == 7 || type == 8 || type == 9|| type == 12 || type == 21);
            case 14:
                return data << 31 == 0 || type == 2 ||type == 10;
            case 15:
                return type != 15 && type != 10 & type != 0 && type != 6 && type != 11;
            case 16:
                return data << 31 == 0 ||type == 2 ||type == 10;
            case 20:
                return data << 31 == 0 ||type == 2 ||type == 10 || type == 16;

            case 18:
                return data << 31 == 0; //|| type == 2 ||type == 10  ||type == 20 || type == 16;

            case 24:
                return data << 31 == 0 && type != 24;
            default:
                return false;
        }
    }
}
