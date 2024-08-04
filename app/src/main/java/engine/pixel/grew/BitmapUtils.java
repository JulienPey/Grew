package engine.pixel.grew;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.SparseArray;

public class BitmapUtils {

    private static SparseArray<Bitmap> bitmapCache = new SparseArray<>();
    private static Bitmap defaultBitmap;

    public static void preloadBitmaps(Context context, int maxIndex) {
        defaultBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.defaut);

        for (int i = 0; i <= maxIndex; i++) {
            int resId = context.getResources().getIdentifier("icone_" + i, "drawable", context.getPackageName());

            if (resId != 0) {
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId);
                bitmapCache.put(i, bitmap);
            }
        }
    }

    public static Bitmap getBitmapForIndex(int i) {
        Bitmap bitmap = bitmapCache.get(i);
        if (bitmap == null) {
            return defaultBitmap;
        }
        return bitmap;
    }
}