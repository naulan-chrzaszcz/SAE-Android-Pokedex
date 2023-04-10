package fr.naulantiago.saeandroid;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.icu.text.SimpleDateFormat;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class CameraActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private Camera camera;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private Button captureButton;

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
        ((FrameLayout) findViewById(R.id.camera_preview)).addView(overlayImage);

        captureButton = findViewById(R.id.capture_button);
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                captureImage();
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

    private void captureImage() {
        try {
            CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
            String cameraId = cameraManager.getCameraIdList()[0];
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }

            cameraManager.openCamera(cameraId, new CameraDevice.StateCallback() {
                @Override
                public void onOpened(CameraDevice camera) {
                    try {
                        SurfaceTexture surfaceTexture = new SurfaceTexture(0);
                        surfaceTexture.setDefaultBufferSize(1080, 1920);

                        // Créer un objet ImageReader pour capturer l'image
                        ImageReader imageReader = ImageReader.newInstance(1080, 1920, ImageFormat.JPEG, 1);
                        List<Surface> outputSurfaces = new ArrayList<>(2);
                        outputSurfaces.add(imageReader.getSurface());
                        outputSurfaces.add(new Surface(surfaceTexture));

                        CaptureRequest.Builder captureRequestBuilder = camera.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
                        captureRequestBuilder.addTarget(imageReader.getSurface());
                        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);

                        camera.createCaptureSession(outputSurfaces, new CameraCaptureSession.StateCallback() {
                            @Override
                            public void onConfigured(CameraCaptureSession session) {
                                try {
                                    session.capture(captureRequestBuilder.build(), new CameraCaptureSession.CaptureCallback() {
                                        @Override
                                        public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
                                            super.onCaptureCompleted(session, request, result);

                                            // Convertir l'image en tableau de bytes
                                            Image image = imageReader.acquireLatestImage();
                                            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                                            byte[] data = new byte[buffer.remaining()];
                                            buffer.get(data);
                                            image.close();

                                            saveImage(data);
                                        }
                                    }, null);
                                } catch (CameraAccessException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onConfigureFailed(CameraCaptureSession session) {
                                // La configuration de la session a échoué
                            }
                        }, null);

                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onDisconnected(CameraDevice camera) {
                    camera.close();
                }

                @Override
                public void onError(CameraDevice camera, int error) {
                    camera.close();
                }
            }, null);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
}
