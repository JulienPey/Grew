package engine.pixel.grew;

import android.graphics.Matrix;

import java.util.Random;

public class ScreenShake {


    private float currentTranslationX;
    private float currentTranslationY;
    public Matrix matrix;
    public int y;
    public int x;
    public float lowering = 0.1F;

    public ScreenShake() {
        this.x =0;
        this.y = 0;
        this.currentTranslationX = 0;
        this.currentTranslationY = 0;
        this.matrix = new Matrix();
        this.matrix.postScale(Game.pixelSize, Game.pixelSize);

    }

    public void applyShakeEffect() {
        Random random = new Random();

        // Calculer la nouvelle translation aléatoire
        float randomX = -x + (x - (-x)) * random.nextFloat();
        float randomY = -y + (y - (-y)) * random.nextFloat();

        // Appliquer la translation
        this.matrix.postTranslate(randomX - currentTranslationX, randomY - currentTranslationY);

        // Mettre à jour les translations courantes
        this.currentTranslationX = randomX;
        this.currentTranslationY = randomY;

        // Réduire l'effet de secousse
        loweringShakeEffect();
    }

    private void loweringShakeEffect() {
        // Réduire la magnitude des secousses progressivement
        if (Math.abs(x) > 0.3) {
            x -= x > 0 ? lowering : -lowering;
        } else {
            x = 0;
        }

        if (Math.abs(y) > 0.3) {
            y -= y > 0 ? lowering : -lowering;
        } else {
            y = 0;
        }
    }
}
