package fr.naulantiago.saeandroid.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Set;

public class Database extends SQLiteOpenHelper implements StatusCallback {
    private List<PokemonData> pokemonDatas;
    private Set<PokemonTypeData> pokemonTypes;
    private FetchPokemons pokemonAPI;

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "pokedex";
    private static final String TABLE_POKEMON = "table_pokemon";
    private static final String TABLE_TYPE = "table_type";
    private static final String TABLE_RESISTANCE = "table_resistance";
    private static final String TABLE_EVOLUTIONS = "table_evolution";
    private static final String COLUMN_TYPE_ID = "type_id";
    private static final String COLUMN_TYPE_NAME = "type_name";
    private static final String COLUMN_TYPE_IMAGE = "type_image";
    private static final String COLUMN_POKEMON_ID = "pokemon_id";
    private static final String COLUMN_POKEMON_NAME = "pokemon_name";
    private static final String COLUMN_POKEMON_SPRITE = "pokemon_sprite";
    private static final String COLUMN_POKEMON_HP = "pokemon_hp";
    private static final String COLUMN_POKEMON_ATTACK = "pokemon_attack";
    private static final String COLUMN_POKEMON_DEFENSE = "pokemon_defense";
    private static final String COLUMN_POKEMON_SPE_ATTACK = "pokemon_spe_attack";
    private static final String COLUMN_POKEMON_SPE_DEFENSE = "pokemon_spe_defense";
    private static final String COLUMN_POKEMON_SPEED = "pokemon_speed";
    private static final String COLUMN_POKEMON_TYPE1 = "pokemon_type_1";
    private static final String COLUMN_POKEMON_TYPE2 = "pokemon_type2";
    private static final String COLUMN_RESISTANCE_POKEMON_ID = "resistance_pokemon_id";
    private static final String COLUMN_RESISTANCE_TYPE_ID = "resistance_type_id";
    private static final String COLUMN_RESISTANCE_DAMAGE_MULTIPLIER = "resistance_damage_multiplier";
    private static final String COLUMN_EVOLUTIONS_PKM_BASE = "pokemon_id_pkm_base";
    private static final String COLUMN_EVOLUTIONS_PKM_EVOLUTION = "pokemon_id_pkm_evolution";


    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        pokemonAPI = new FetchPokemons(this);

        String script_type = "CREATE TABLE IF NOT EXISTS " + TABLE_TYPE + "(" +
                COLUMN_TYPE_ID + " INTEGER PRIMARY KEY," +
                COLUMN_TYPE_NAME + " VARCHAR(20)," +
                COLUMN_TYPE_IMAGE + " BLOB )";

        String script_pokemon = "CREATE TABLE IF NOT EXISTS " + TABLE_POKEMON + "(" +
                COLUMN_POKEMON_ID + " INTEGER PRIMARY KEY," +
                COLUMN_POKEMON_NAME + " VARCHAR(100)," +
                COLUMN_POKEMON_SPRITE + " BLOB," +
                COLUMN_POKEMON_HP + " INTEGER," +
                COLUMN_POKEMON_ATTACK + " INTEGER," +
                COLUMN_POKEMON_DEFENSE + " INTEGER," +
                COLUMN_POKEMON_SPE_ATTACK + " INTEGER," +
                COLUMN_POKEMON_SPE_DEFENSE + " INTEGER," +
                COLUMN_POKEMON_SPEED + " INTEGER," +
                COLUMN_POKEMON_TYPE1 + " INTEGER," +
                COLUMN_POKEMON_TYPE2 + " INTEGER," +
                " FOREIGN KEY (" + COLUMN_POKEMON_TYPE1 + ") REFERENCES " + TABLE_TYPE + "(" + COLUMN_TYPE_ID + ")," +
                " FOREIGN KEY (" + COLUMN_POKEMON_TYPE2 + ") REFERENCES " + TABLE_TYPE + "(" + COLUMN_TYPE_ID + ")" +
                ")";



        String script_resistances = "CREATE TABLE IF NOT EXISTS " + TABLE_RESISTANCE + "(" +
                COLUMN_RESISTANCE_POKEMON_ID + " INTEGER," +
                COLUMN_RESISTANCE_TYPE_ID + " INTEGER, " +
                COLUMN_RESISTANCE_DAMAGE_MULTIPLIER + " REAL NOT NULL ," +
                " PRIMARY KEY (" + COLUMN_RESISTANCE_POKEMON_ID + "," + COLUMN_RESISTANCE_TYPE_ID + ")," +
                " FOREIGN KEY ("  + COLUMN_RESISTANCE_POKEMON_ID + ") REFERENCES " + TABLE_POKEMON +'(' + COLUMN_POKEMON_ID + ")," +
                " FOREIGN KEY ("  + COLUMN_RESISTANCE_TYPE_ID + ") REFERENCES " + TABLE_TYPE +'(' + COLUMN_TYPE_ID + "))";

        String script_evolutions = "CREATE TABLE IF NOT EXISTS " + TABLE_EVOLUTIONS + "(" +
                COLUMN_EVOLUTIONS_PKM_BASE + " INTEGER ," +
                COLUMN_EVOLUTIONS_PKM_EVOLUTION + " INTEGER ," +
                " PRIMARY KEY (" + COLUMN_EVOLUTIONS_PKM_BASE + "," + COLUMN_EVOLUTIONS_PKM_EVOLUTION + ")," +
                " FOREIGN KEY (" + COLUMN_EVOLUTIONS_PKM_BASE + ") REFERENCES " + TABLE_POKEMON +'(' + COLUMN_POKEMON_ID + ")," +
                " FOREIGN KEY (" + COLUMN_EVOLUTIONS_PKM_EVOLUTION + ") REFERENCES " + TABLE_POKEMON +'(' + COLUMN_POKEMON_ID + "))";

        db.execSQL(script_type);
        db.execSQL(script_pokemon);
        db.execSQL(script_resistances);
        db.execSQL(script_evolutions);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESISTANCE );
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_POKEMON);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TYPE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVOLUTIONS);

        onCreate(db);
    }

    private void initInsert() {
        SQLiteDatabase db = this.getWritableDatabase();
        this.pokemonDatas = this.pokemonAPI.getPokemonDatas();
        this.pokemonTypes = this.pokemonAPI.getPkmTypes();

        insertTypes(db);

        for (int pkmId = this.pokemonDatas.size() - 1; pkmId >= 0; pkmId--) {
            insertPokemons(this.pokemonDatas.get(pkmId), db);
        }

        db.close();
    }

    private void insertTypes(SQLiteDatabase db) {
        for (PokemonTypeData pokemonType : this.pokemonTypes) {

            byte[] byteArray = getByteArray(pokemonType.getImg());
            ContentValues values = new ContentValues();
            values.put(COLUMN_TYPE_ID, pokemonType.getId());
            values.put(COLUMN_TYPE_IMAGE, byteArray);
            values.put(COLUMN_TYPE_NAME, pokemonType.getType().name());

            db.insert(TABLE_TYPE, null, values);
        }
    }

    private void insertPokemons(PokemonData pkmData, SQLiteDatabase db) {

        byte[] byteArray = getByteArray(pkmData.getSprite());

        ContentValues values = new ContentValues();
        values.put(COLUMN_POKEMON_ID, pkmData.getId());
        values.put(COLUMN_POKEMON_ATTACK, pkmData.getAttack());
        values.put(COLUMN_POKEMON_HP, pkmData.getHp());
        values.put(COLUMN_POKEMON_DEFENSE, pkmData.getDefense());
        values.put(COLUMN_POKEMON_NAME, pkmData.getName());
        values.put(COLUMN_POKEMON_SPE_ATTACK, pkmData.getSpecial_attack());
        values.put(COLUMN_POKEMON_SPE_DEFENSE, pkmData.getSpecial_defense());
        values.put(COLUMN_POKEMON_SPRITE, byteArray);
        values.put(COLUMN_POKEMON_SPEED, pkmData.getSpeed());
        values.put(COLUMN_POKEMON_TYPE1, pkmData.getTypes().get(0).getId());
        values.put(COLUMN_POKEMON_TYPE2, pkmData.getTypes().size() > 1 ? pkmData.getTypes().get(1).getId() : null);

        db.insert(TABLE_POKEMON, null, values);
        insertEvolutions(pkmData, db);
        insertResistances(pkmData,db);
    }
    private void insertEvolutions(PokemonData pkmData, SQLiteDatabase db) {
        for (Integer evolutionId : pkmData.getEvolutions()) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_EVOLUTIONS_PKM_BASE, pkmData.getId());
            values.put(COLUMN_EVOLUTIONS_PKM_EVOLUTION, evolutionId);

            db.insert(TABLE_EVOLUTIONS, null, values);
        }
    }

    private void insertResistances(PokemonData pkmData, SQLiteDatabase db) {
        for (PokemonTypeResistancesData resistance : pkmData.getResistances()) {
            ContentValues values = new ContentValues();

            values.put(COLUMN_RESISTANCE_POKEMON_ID,pkmData.getId());
            values.put(COLUMN_RESISTANCE_TYPE_ID,resistance.getTypes().getId());
            values.put(COLUMN_RESISTANCE_DAMAGE_MULTIPLIER,resistance.getDamageMultiplier());

            db.insert(TABLE_RESISTANCE,null,values);
        }
    }

    @Override
    public void statusChange(int status) {
        if (status == 1 ) {
            initInsert();
        } else if (status == -1) {
            System.out.println("bruh moment");
        }
    }

    public String query() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_POKEMON, new String[] {COLUMN_POKEMON_NAME},null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            return cursor.getString(1);
        }
        return null;
    }

    public byte[] getByteArray(Bitmap bm) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }
}
