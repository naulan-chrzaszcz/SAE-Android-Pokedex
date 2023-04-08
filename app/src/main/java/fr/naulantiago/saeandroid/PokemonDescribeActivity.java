package fr.naulantiago.saeandroid;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import fr.naulantiago.saeandroid.model.PokemonData;

public class PokemonDescribeActivity extends AppCompatActivity
{
    private int pokemonId;
    private PokemonData pokemonData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon_describe);
        this.pokemonId = getIntent().getExtras().getInt("id");
        pokemonData = MainActivity.db.getPokemonData(this.pokemonId);
    }
}
