package fr.naulantiago.saeandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TableLayout;

import fr.naulantiago.saeandroid.model.FetchPokemons;

public class MainActivity extends AppCompatActivity {
    private TableLayout mTableLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTableLayout = findViewById(R.id.tablePokedex);
        FetchPokemons toast = new FetchPokemons();
        System.out.println(toast.getPokemonDatas());
    }

    private void addPokemonsToTable() {

    }
}