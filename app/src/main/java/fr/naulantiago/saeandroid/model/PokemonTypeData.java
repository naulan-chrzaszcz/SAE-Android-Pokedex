package fr.naulantiago.saeandroid.model;

import android.graphics.Bitmap;

public class PokemonTypeData
{
    private final int id;
    private final PokemonTypes name;
    private Bitmap img;

    public PokemonTypeData(final PokemonTypes name,
                           final int id) {
        this.name = name;
        this.id = id;
    }

    public PokemonTypes getName() {
        return name;
    }

    public Bitmap getImg() {
        return img;
    }
    public boolean hasImage() { return img == null; }
    public void setImage(Bitmap image) { this.img = image; }
}
