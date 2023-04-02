package fr.naulantiago.saeandroid.model;

import android.graphics.Bitmap;
import java.util.List;

public class PokemonData
{
    private final int id;
    private final List<Integer> evolutionId;
    private final String name;
    private final Bitmap sprite;
    // stats
    private final int hp;
    private final int attack;
    private final int defense;
    private final int special_attack;
    private final int special_defense;
    private final int speed;
    // types
    private final List<PokemonTypeData> types;

    private final List<PokemonTypeResistancesData> resistances;

    public PokemonData(final int id,
                       final List<Integer> evolutionId,
                       final String name,
                       final Bitmap sprite,
                       final int hp,
                       final int attack,
                       final int defense,
                       final int special_attack,
                       final int special_defense,
                       final int speed,
                       final List<PokemonTypeResistancesData> resistances,
                       final List<PokemonTypeData> types) {
        this.id = id;
        this.evolutionId = evolutionId;
        this.name = name;
        this.sprite = sprite;
        this.hp = hp;
        this.attack = attack;
        this.defense = defense;
        this.special_attack = special_attack;
        this.special_defense = special_defense;
        this.speed = speed;
        this.types = types;
        this.resistances = resistances;
    }

    public int getId() {
        return id;
    }

    public List<Integer> getEvolutions() {
        return evolutionId;
    }

    public String getName() {
        return name;
    }

    public Bitmap getSprite() {
        return sprite;
    }

    public int getHp() {
        return hp;
    }

    public int getAttack() {
        return attack;
    }

    public int getDefense() {
        return defense;
    }

    public int getSpecial_attack() {
        return special_attack;
    }

    public int getSpecial_defense() {
        return special_defense;
    }

    public int getSpeed() {
        return speed;
    }

    public List<PokemonTypeData> getTypes() {
        return types;
    }

    public List<PokemonTypeResistancesData> getResistances() {
        return resistances;
    }
}
