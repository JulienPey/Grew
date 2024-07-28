package engine.pixel.grew;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.List;


public class ParticleHandler {

    private final WorldHandler worldHandler;
    public List<Particle> particlesList;

    public ParticleHandler(Context context, GameLoop gameLoop, WorldHandler worldHandler) {

      this.particlesList = new ArrayList<>();
      this.worldHandler = worldHandler;
   //   particlesList.add(new Particle(50,50,0,10,1,0.90, Color.WHITE,1,100));

       }

    public void draw(Canvas canvas) {
        for (int i = 0; i < particlesList.size(); i++) {
            Particle particle = particlesList.get(i);
            Paint paint = new Paint();
            paint.setColor(particle.Color);
            canvas.drawRect(particle.PosX*Game.pixelSize,particle.PosY*Game.pixelSize,particle.PosX*Game.pixelSize+Game.pixelSize,particle.PosY*Game.pixelSize+Game.pixelSize,paint);
        }
    }

    public void update() {
        for (int i = 0; i < particlesList.size(); i++) {
            Particle particle = particlesList.get(i);
            particle.PosX += particle.VelocityX;
            particle.PosY += particle.VelocityY;

            particle.VelocityX += particle.AccélérationX;
            particle.VelocityY += particle.AccélérationY;
            particle.LiveTime -=1;

            if(particle.LiveTime == 0){
                particlesList.remove(particle);
            }

            if(particle.ParticleID == 2 &&  (worldHandler.chunkhandler.getPixelData(particle.PosX, particle.PosY - 1) << 31) == 0){
                particlesList.remove(particle);
            }
           }


    }

    public void clearSimulation() {

    }
}
