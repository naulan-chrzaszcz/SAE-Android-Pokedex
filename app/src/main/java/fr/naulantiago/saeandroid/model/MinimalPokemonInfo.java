package fr.naulantiago.saeandroid.model;

import android.graphics.Bitmap;

public class MinimalPokemonInfo {
    private final int id;
    private final String name;
    private final Bitmap sprite;

    public MinimalPokemonInfo(final int id,
                              final String name,
                              final Bitmap sprite){
        this.id = id;
        this.name = name;
        this.sprite = sprite;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Bitmap getSprite() {
        return sprite;
    }
}
