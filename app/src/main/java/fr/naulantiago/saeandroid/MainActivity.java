package fr.naulantiago.saeandroid;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import fr.naulantiago.saeandroid.model.Database;
import fr.naulantiago.saeandroid.model.MinimalPokemonInfo;
import fr.naulantiago.saeandroid.model.StatusCallback;

public class MainActivity extends AppCompatActivity implements StatusCallback {
    private ArrayList<MinimalPokemonInfo> minimalPokemonInfos;
    public static Database db;
    private TableLayout mTableLayout;
    private final String LAST_POKEMON_INFOS = "LAST_POKEMON_INFOS";
    private final String LAST_POKEMON_INFOS_ID = "LAST_POKEMON_INFOS_ID";
    private final int PERMISSION_NOTIFICATION_ID = 546;
    private final int QUIT_NOTIFICATION_ID = 546;
    private final String NOTIFICATION_PKM_ID = "PKM_ID";
    public MainActivity() {
        db = new Database(this, this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.mTableLayout = findViewById(R.id.tablePokedex);
        if (db.getNumbersOfPokemons() < 151)
            db.initInsertIfNewDB();
        else addPokemon();

        if (getIntent().getExtras() != null) {
            int pkmId = getIntent().getExtras().getInt(NOTIFICATION_PKM_ID);
            startDetailActivity(pkmId);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isFinishing()) {
            sendNotification();
        }
    }

    @Override
    public void statusChange(int status) {
        if (status == 1) {
            addPokemon();
        } else {
            // faire un truc qui handle l'error je suppose
        }
    }

    public void addPokemon() {
        if (minimalPokemonInfos == null)
            minimalPokemonInfos = db.getMinimalPokemonInfos();
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;

        TableRow tr = null;
        for (int index = 0; index < minimalPokemonInfos.size(); index++) {
            if ( index % 2 == 0) {
                tr = new TableRow(this);
                this.mTableLayout.addView(tr);
            }
            // Create a new ImageView to display the bitmap
            ImageView imageView = new ImageView(this);
            imageView.setImageBitmap(minimalPokemonInfos.get(index).getSprite());
            imageView.setLayoutParams(new LinearLayout.LayoutParams(screenWidth / 2,screenHeight / 5));

            // Create a new TextView to display the text beneath the bitmap
            TextView textView = new TextView(this);
            textView.setText(minimalPokemonInfos.get(index).getName());
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            // Add the ImageView and TextView to a new nested LinearLayout
            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.addView(imageView);
            linearLayout.addView(textView);
            linearLayout.setOnClickListener(createOnclickListener(minimalPokemonInfos.get(index).getId()));
            // Add the nested LinearLayout to the TableRow
            tr.addView(linearLayout);
        }
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        int pkmId = intent.getExtras().getInt(NOTIFICATION_PKM_ID);
        startDetailActivity(pkmId);
    }
    private View.OnClickListener createOnclickListener(int id) {
        return v -> {
            startDetailActivity(id);
        };
    }
    private void startDetailActivity(int id) {
        newLastPokemonSeen(id);
        Intent pkmDescIntent = new Intent(MainActivity.this, PokemonDescribeActivity.class);
        pkmDescIntent.putExtra("id", id);
        startActivity(pkmDescIntent);
    }

    private void newLastPokemonSeen(int id) {
        getSharedPreferences(LAST_POKEMON_INFOS, MODE_PRIVATE)
                .edit()
                .putInt(LAST_POKEMON_INFOS_ID, id)
                .apply();
    }

    private int getLastPokemonSeen() {
        return getSharedPreferences(LAST_POKEMON_INFOS, MODE_PRIVATE).getInt(LAST_POKEMON_INFOS_ID, -1);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_NOTIFICATION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendNotification();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void sendNotification() {
        int id = getLastPokemonSeen();
        if (id > 0 && id < 150) {
            List<MinimalPokemonInfo> notificationPokemons = minimalPokemonInfos.stream().filter(el -> el.getId() == id || el.getId() == id + 1).collect(Collectors.toList());
            String pokemon1 = notificationPokemons.get(0).getName();
            String pokemon2 = notificationPokemons.get(1).getName();
            String outputString = "Vous avez regardé " + pokemon1 + "vous aprécirez peut-être aussi " + pokemon2 + " clickez sur la notification pour plus de détails";

            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(NOTIFICATION_PKM_ID,notificationPokemons.get(1).getId());
            PendingIntent pendingIntent = PendingIntent.getActivity(this, QUIT_NOTIFICATION_ID, intent, PendingIntent.FLAG_MUTABLE);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NotificationApp.CHANNEL_PKM)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle("Pokemon App")
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(outputString))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setAutoCancel(true);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[] { android.Manifest.permission.POST_NOTIFICATIONS }, PERMISSION_NOTIFICATION_ID);
            } else {
                notificationManager.notify(QUIT_NOTIFICATION_ID, builder.build());
            }
        }
    }
}
