package engine.pixel.grew;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {
    private GameLoop gameLoop;

    @Override
    public void onCreate(Bundle sis){
        super.onCreate(sis);

        // Masquer la barre de statut (en haut de l'écran)
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        /*
        // Masquer la barre de navigation (en bas de l'écran)
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

         */
        getWindow().setNavigationBarColor(Color.BLACK);

        gameLoop = new GameLoop(MainActivity.this);//Initialize the gameLoop instance
        setContentView(gameLoop);//setContentView to the game surfaceview
    }
}