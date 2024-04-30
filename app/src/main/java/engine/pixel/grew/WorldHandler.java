package engine.pixel.grew;

import android.content.Context;
import android.graphics.Canvas;

public class WorldHandler {

    public final ChunkHandler chunkhandler;
    public final Game game;

    public WorldHandler(Context context, GameLoop gameLoop,Game game){

        this.game = game;
        this.chunkhandler = new ChunkHandler(context,gameLoop,this);

    }

    public void draw(Canvas canvas) {

        chunkhandler.draw(canvas);
    }

    public void update() {
        chunkhandler.update();
    }
}
