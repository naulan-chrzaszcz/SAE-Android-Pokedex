package fr.naulantiago.saeandroid;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

public class CameraActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private Camera camera;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private Button captureButton;

    private final Camera.PictureCallback pictureCallback = (data, camera) -> {
        saveImage(data);
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        surfaceView = findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);

        // Ajoutez votre code pour afficher la superposition d'image ici
        ImageView overlayImage = new ImageView(this);
        overlayImage.setImageResource(R.drawable.image_overlay);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        );
        overlayImage.setLayoutParams(params);
        ((FrameLayout)findViewById(R.id.camera_preview)).addView(overlayImage);

        captureButton = findViewById(R.id.capture_button);
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                camera.takePicture(null, null, pictureCallback);
            }
        });
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        camera = Camera.open();
        try {
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        if (camera != null) {
            camera.stopPreview();
            try {
                camera.setPreviewDisplay(surfaceHolder);
                camera.startPreview();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        camera.stopPreview();
        camera.release();
        camera = null;
    }

    private void saveImage(byte[] data) {
        try {
            File pictureFile = getOutputMediaFile();
            if (pictureFile == null) {
                Log.d("CameraActivity", "Error creating media file, check storage permissions");
                return;
            }

            // Charger l'image de superposition dans un bitmap
            Bitmap overlayBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.image_overlay);

            // Redimensionner l'image de superposition
            int maxWidth = 300;
            int maxHeight = 300;
            float widthScale = ((float) maxWidth) / overlayBitmap.getWidth();
            float heightScale = ((float) maxHeight) / overlayBitmap.getHeight();
            float scale = Math.min(widthScale, heightScale);
            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale);
            Bitmap resizedOverlayBitmap = Bitmap.createBitmap(overlayBitmap, 0, 0, overlayBitmap.getWidth(), overlayBitmap.getHeight(), matrix, false);


            // Convertir les données de l'image capturée en bitmap
            Bitmap capturedBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

            // Créer un nouveau bitmap pour la combinaison de l'image capturée et de l'image de superposition
            Bitmap combinedBitmap = Bitmap.createBitmap(capturedBitmap.getWidth(), capturedBitmap.getHeight(), capturedBitmap.getConfig());

            // Dessiner l'image capturée sur un nouveau Canvas
            Canvas canvas = new Canvas(combinedBitmap);
            canvas.drawBitmap(capturedBitmap, 0, 0, null);

            // Dessiner l'image de superposition sur le même Canvas
            canvas.drawBitmap(resizedOverlayBitmap, 0, 0, null);

            // Enregistrer le nouveau bitmap dans un fichier
            FileOutputStream fos = new FileOutputStream(pictureFile);
            combinedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.close();
            Log.d("CameraActivity", "Image saved: " + pictureFile.getAbsolutePath());
        } catch (FileNotFoundException e) {
            Log.d("CameraActivity", "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d("CameraActivity", "Error accessing file: " + e.getMessage());
        }
    }

    private File getOutputMediaFile() {
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "CameraApp");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        return mediaFile;
    }
}
