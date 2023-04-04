package fr.naulantiago.saeandroid;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import fr.naulantiago.saeandroid.model.Database;
import fr.naulantiago.saeandroid.model.MinimalPokemonInfo;
import fr.naulantiago.saeandroid.model.PokemonData;
import fr.naulantiago.saeandroid.model.StatusCallback;

public class MainActivity extends AppCompatActivity implements StatusCallback {
    private Database db;
    private TableLayout mTableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.mTableLayout = findViewById(R.id.tablePokedex);
        this.db = new Database(this, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
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
        System.out.println("beozcjebaocbzeaibeozbvoaibeovbaoze");
        ArrayList<MinimalPokemonInfo> minimalPokemonInfos = this.db.getMinimalPokemonInfos();
        TableRow tr = new TableRow(this);
        for (int index = 0; index < minimalPokemonInfos.size(); index++) {
            if (index != 0 && index % 2 == 0) {
                tr = new TableRow(this);
                this.mTableLayout.addView(tr);
            }
            // Create a new ImageView to display the bitmap
            ImageView imageView = new ImageView(this);
            imageView.setImageBitmap(minimalPokemonInfos.get(index).getSprite());

            // Create a new TextView to display the text beneath the bitmap
            TextView textView = new TextView(this);
            textView.setText(minimalPokemonInfos.get(index).getName());

            // Add the ImageView and TextView to a new nested LinearLayout
            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.addView(imageView);
            linearLayout.addView(textView);

            // Add the nested LinearLayout to the TableRow
            tr.addView(linearLayout);
        }
    }
}
