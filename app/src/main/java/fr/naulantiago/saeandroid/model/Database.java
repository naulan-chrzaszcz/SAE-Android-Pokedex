package fr.naulantiago.saeandroid.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "pokedex";
    private static final String TABLE_POKEMON = "table_pokemon";
    private static final String TABLE_TYPE = "table_type";
    private static final String TABLE_RESISTANCE = "table_type";

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


    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String script_type = "CREATE TABLE " + TABLE_TYPE + "(" +
                COLUMN_TYPE_ID + " INTEGER PRIMARY KEY," +
                COLUMN_TYPE_NAME + " VARCHAR(20)," +
                COLUMN_TYPE_IMAGE + " BLOB )";

        String script_pokemon = "CREATE TABLE " + TABLE_POKEMON + "(" +
                COLUMN_POKEMON_ID + " INTEGER PRIMARY KEY," +
                COLUMN_POKEMON_NAME + " VARCHAR(100)," +
                COLUMN_POKEMON_SPRITE + " BLOB," +
                COLUMN_POKEMON_HP + " INTEGER," +
                COLUMN_POKEMON_ATTACK + " INTEGER," +
                COLUMN_POKEMON_DEFENSE + " INTEGER," +
                COLUMN_POKEMON_SPE_ATTACK + " INTEGER," +
                COLUMN_POKEMON_SPE_DEFENSE + " INTEGER," +
                COLUMN_POKEMON_SPEED + "INTEGER," +
                "FOREIGN KEY(" + COLUMN_POKEMON_TYPE1 + ") REFERENCES " + TABLE_TYPE + "(" + COLUMN_TYPE_ID + ")," +
                "FOREIGN KEY(" + COLUMN_POKEMON_TYPE2 + ") REFERENCES " + TABLE_TYPE + "(" + COLUMN_TYPE_ID + "))";

        String script_resistances = "CREATE TABLE " + TABLE_RESISTANCE + "(" +
                COLUMN_RESISTANCE_POKEMON_ID + " INTEGER PRIMARY KEY REFERENCES " + TABLE_POKEMON + "(" + COLUMN_POKEMON_ID + ")," +
                COLUMN_RESISTANCE_TYPE_ID + " INTEGER PRIMARY KEY REFERENCES " + TABLE_RESISTANCE + "(" + COLUMN_TYPE_ID + ")," +
                COLUMN_RESISTANCE_DAMAGE_MULTIPLIER + " REAL NOT NULL" + ")";

        db.execSQL(script_type);
        db.execSQL(script_pokemon);
        db.execSQL(script_resistances);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("");

        onCreate(db);
    }
}
