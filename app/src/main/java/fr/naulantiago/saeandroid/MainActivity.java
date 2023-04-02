package fr.naulantiago.saeandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TableLayout;

import java.util.List;

import fr.naulantiago.saeandroid.model.Database;
import fr.naulantiago.saeandroid.model.FetchPokemons;
import fr.naulantiago.saeandroid.model.PokemonData;

public class MainActivity extends AppCompatActivity {
    private TableLayout mTableLayout;
    private List<PokemonData> pokemonData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        this.mTableLayout = findViewById(R.id.tablePokedex);
        Database db = new Database(this);
        System.out.println(db.query());
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}
