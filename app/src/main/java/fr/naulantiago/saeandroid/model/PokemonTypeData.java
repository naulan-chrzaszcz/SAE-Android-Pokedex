package fr.naulantiago.saeandroid.model;

import android.graphics.Bitmap;

public class PokemonTypeData
{
    private final int id;
    private final PokemonTypes name;
    private Bitmap img;

    public PokemonTypeData(final PokemonTypes name,
                           final int id) {
        this.img = null;
        this.name = name;
        this.id = id;
    }
    public PokemonTypeData(final PokemonTypes name,
                           final Bitmap img,
                           final int id) {
        this.img = img;
        this.name = name;
        this.id = id;
    }

    public PokemonTypes getType() {
        return name;
    }

    public Bitmap getImg() {
        return img;
    }
    public boolean hasImage() { return img != null; }
    public void setImage(Bitmap image) { this.img = image; }
    public int getId() { return this.id; }

    @Override
    public String toString() {
        return "PokemonTypeData{" +
                "id=" + id +
                ", name=" + name +
                ", img=" + img +
                '}';
    }
}
