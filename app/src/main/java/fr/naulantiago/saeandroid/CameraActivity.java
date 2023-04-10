package fr.naulantiago.saeandroid;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.PictureResult;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import fr.naulantiago.saeandroid.model.Database;

public class CameraActivity extends AppCompatActivity {
    private CameraView camera;
    private Button takePictureBtn;
    private Button switchCameraBtn;
    private Button quitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        camera = findViewById(R.id.camera);
        takePictureBtn = findViewById(R.id.btn_take_picture);
        switchCameraBtn = findViewById(R.id.btn_switch_camera);
        quitButton = findViewById(R.id.button_quit_camera);

        camera.setLifecycleOwner(this);

        takePictureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camera.takePicture();
            }
        });

        switchCameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camera.toggleFacing();
            }
        });

        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        camera.addCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(PictureResult result) {
                byte[] jpeg = result.getData();

                new AlertDialog.Builder(CameraActivity.this)
                        .setTitle("Save picture")
                        .setMessage("Do you want to keep this picture?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                savePicture(jpeg);
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
    }

    private void savePicture(byte[] jpeg) {
        String fileName = UUID.randomUUID().toString() + ".jpg";
        Bitmap baseImg = Database.getBitmap(jpeg);
        Bitmap combinedBitmap = Bitmap.createBitmap(baseImg.getWidth(), baseImg.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(combinedBitmap);
        canvas.drawBitmap(baseImg, 0, 0, null);
        Bitmap overlayBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.image_overlay);
        overlayBitmap = Bitmap.createScaledBitmap(overlayBitmap,500,500,true);
        canvas.drawBitmap(overlayBitmap, 0, 0, null);

        File file = new File(getExternalFilesDir(null), fileName);

        try (OutputStream os = new FileOutputStream(file)) {
            combinedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
        } catch (IOException e) {
            System.out.println("Un problème est survenu veuillez reessayer");
            return;
        }
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "My Image");
        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
        values.put(MediaStore.Images.Media.DESCRIPTION, "Image captured with CameraView");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());

        ContentResolver contentResolver = getContentResolver();
        Uri uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(uri);
        sendBroadcast(mediaScanIntent);
    }
}
