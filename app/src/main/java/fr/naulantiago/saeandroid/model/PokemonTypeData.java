package fr.naulantiago.saeandroid.model;

import java.sql.Blob;

public class PokemonTypeData
{
    private final PokemonTypes name;
    private final Blob img;

    public PokemonTypeData(final PokemonTypes name,
                           final Blob img) {
        this.name = name;
        this.img = img;
    }

    public PokemonTypes getName() {
        return name;
    }

    public Blob getImg() {
        return img;
    }
}
