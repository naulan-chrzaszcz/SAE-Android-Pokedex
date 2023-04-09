package fr.naulantiago.saeandroid;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import fr.naulantiago.saeandroid.model.PokemonData;
import fr.naulantiago.saeandroid.model.PokemonTypeData;
import fr.naulantiago.saeandroid.model.PokemonTypeResistancesData;

public class PokemonDescribeActivity extends AppCompatActivity
{
    private int pokemonId;
    private PokemonData pokemonData;

    private ImageView sprite;
    private TextView id;
    private TextView name;
    private ImageView type_0;
    private ImageView type_1;
    private TextView hp;
    private TextView attack;
    private TextView defense;
    private TextView special_attack;
    private TextView special_defense;
    private TextView speed;
    private TextView resistancePoison;
    private TextView resistanceNormal;
    private TextView resistanceCombat;
    private TextView resistanceVol;
    private TextView resistanceSol;
    private TextView resistanceRoche;
    private TextView resistanceInsecte;
    private TextView resistanceSpectre;
    private TextView resistanceAcier;
    private TextView resistanceFeu;
    private TextView resistanceEau;
    private TextView resistancePlante;
    private TextView resistanceElectrik;
    private TextView resistancePsy;
    private TextView resistanceGlace;
    private TextView resistanceDragon;
    private TextView resistanceTenebres;
    private TextView resistanceFee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon_describe);
        this.pokemonId = getIntent().getExtras().getInt("id");
        this.pokemonData = MainActivity.db.getPokemonData(this.pokemonId);

        this.id = findViewById(R.id.id);
        this.id.setText(String.valueOf(this.pokemonData.getId()));
        this.sprite = findViewById(R.id.sprite);
        this.sprite.setImageBitmap(this.pokemonData.getSprite());
        this.name = findViewById(R.id.name);
        this.name.setText(this.pokemonData.getName());
        this.type_0 = findViewById(R.id.type_0);
        List<PokemonTypeData> types = this.pokemonData.getTypes();
        this.type_0.setImageBitmap(types.get(0).getImg());
        this.type_1 = findViewById(R.id.type_1);
        if (types.size() > 1)
            this.type_1.setImageBitmap(types.get(1).getImg());
        this.hp = findViewById(R.id.hp);
        this.hp.setText(String.valueOf(this.pokemonData.getHp()));
        this.attack = findViewById(R.id.attack);
        this.attack.setText(String.valueOf(this.pokemonData.getAttack()));
        this.defense = findViewById(R.id.defense);
        this.defense.setText(String.valueOf(this.pokemonData.getDefense()));
        this.special_attack = findViewById(R.id.special_attack);
        this.special_attack.setText(String.valueOf(this.pokemonData.getSpecial_attack()));
        this.special_defense = findViewById(R.id.special_defense);
        this.special_defense.setText(String.valueOf(this.pokemonData.getSpecial_defense()));
        this.speed = findViewById(R.id.speed);
        this.speed.setText(String.valueOf(this.pokemonData.getSpeed()));
        this.resistancePoison = findViewById(R.id.resistancePoison);
        this.resistanceNormal = findViewById(R.id.resistanceNormal);
        this.resistanceCombat = findViewById(R.id.resistanceCombat);
        this.resistanceVol = findViewById(R.id.resistanceVol);
        this.resistanceSol = findViewById(R.id.resistanceSol);
        this.resistanceRoche = findViewById(R.id.resistanceRoche);
        this.resistanceInsecte = findViewById(R.id.resistanceInsecte);
        this.resistanceSpectre = findViewById(R.id.resistanceSpectre);
        this.resistanceAcier = findViewById(R.id.resistanceAcier);
        this.resistanceFeu = findViewById(R.id.resistanceFeu);
        this.resistanceEau = findViewById(R.id.resistanceEau);
        this.resistancePlante = findViewById(R.id.resistancePlante);
        this.resistanceElectrik = findViewById(R.id.resistanceElectrik);
        this.resistancePsy = findViewById(R.id.resistancePsy);
        this.resistanceGlace = findViewById(R.id.resistanceGlace);
        this.resistanceDragon = findViewById(R.id.resistanceDragon);
        this.resistanceTenebres = findViewById(R.id.resistanceTenebres);
        this.resistanceFee = findViewById(R.id.resistanceFee);

        for (PokemonTypeResistancesData pkmResistanceData : this.pokemonData.getResistances()) {
            switch (pkmResistanceData.getTypes().getType().name()) {
                case "Poison":
                    this.resistancePoison.setText(pkmResistanceData.getDamageRelation().name());
                    break;
                case "Normal":
                    this.resistanceNormal.setText(pkmResistanceData.getDamageRelation().name());
                    break;
                case "Combat":
                    this.resistanceCombat.setText(pkmResistanceData.getDamageRelation().name());
                    break;
                case "Vol":
                    this.resistanceVol.setText(pkmResistanceData.getDamageRelation().name());
                    break;
                case "Sol":
                    this.resistanceSol.setText(pkmResistanceData.getDamageRelation().name());
                    break;
                case "Roche":
                    this.resistanceRoche.setText(pkmResistanceData.getDamageRelation().name());
                    break;
                case "Insecte":
                    this.resistanceInsecte.setText(pkmResistanceData.getDamageRelation().name());
                    break;
                case "Spectre":
                    this.resistanceSpectre.setText(pkmResistanceData.getDamageRelation().name());
                    break;
                case "Acier":
                    this.resistanceAcier.setText(pkmResistanceData.getDamageRelation().name());
                    break;
                case "Feu":
                    this.resistanceFeu.setText(pkmResistanceData.getDamageRelation().name());
                    break;
                case "Eau":
                    this.resistanceEau.setText(pkmResistanceData.getDamageRelation().name());
                    break;
                case "Plante":
                    this.resistancePlante.setText(pkmResistanceData.getDamageRelation().name());
                    break;
                case "Électrik":
                    this.resistanceElectrik.setText(pkmResistanceData.getDamageRelation().name());
                    break;
                case "Psy":
                    this.resistancePsy.setText(pkmResistanceData.getDamageRelation().name());
                    break;
                case "Glace":
                    this.resistanceGlace.setText(pkmResistanceData.getDamageRelation().name());
                    break;
                case "Dragon":
                    this.resistanceDragon.setText(pkmResistanceData.getDamageRelation().name());
                    break;
                case "Ténèbres":
                    this.resistanceTenebres.setText(pkmResistanceData.getDamageRelation().name());
                    break;
                case "Fée":
                    this.resistanceFee.setText(pkmResistanceData.getDamageRelation().name());
                    break;
                default:
                    break;
            }
        }
    }
}
