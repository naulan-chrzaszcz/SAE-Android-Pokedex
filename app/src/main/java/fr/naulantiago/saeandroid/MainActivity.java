package fr.naulantiago.saeandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TableLayout;

import java.util.List;

import fr.naulantiago.saeandroid.model.Database;
import fr.naulantiago.saeandroid.model.FetchPokemons;
import fr.naulantiago.saeandroid.model.PokemonData;

public class MainActivity extends AppCompatActivity {
    private Database db;
    private TableLayout mTableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.mTableLayout = findViewById(R.id.tablePokedex);
        this.db = new Database(this);
        System.out.println(this.db.query());

        
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}
