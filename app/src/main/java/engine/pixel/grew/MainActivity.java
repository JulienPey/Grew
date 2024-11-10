package engine.pixel.grew;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private GameLoop gameLoop;

    @Override
    public void onCreate(Bundle sis){
        super.onCreate(sis);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getWindow().setNavigationBarColor(Color.BLACK);
        setContentView(R.layout.activity_main);

        Button startGameButton = findViewById(R.id.startGameButton);

        //Si on appuis sur le bouton, on lance la Simulation
        startGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameLoop = new GameLoop(MainActivity.this);
                setContentView(gameLoop);
            }
        });
    }

    }
