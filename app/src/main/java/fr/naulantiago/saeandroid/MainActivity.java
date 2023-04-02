package fr.naulantiago.saeandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TableLayout;

import java.util.List;

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

        FetchPokemons toast = new FetchPokemons();
        this.pokemonData = toast.getPokemonDatas();
        System.out.println(this.pokemonData);
    }

    @Override
    protected void onStart() {
        super.onStart();

        this.addPokemonsToTable();
    }

    private void addPokemonsToTable() {

    }
}
