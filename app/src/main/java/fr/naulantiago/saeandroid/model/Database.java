package fr.naulantiago.saeandroid.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Database extends SQLiteOpenHelper implements StatusCallback {
    private List<PokemonData> pokemonDatas;
    private Set<PokemonTypeData> pokemonTypes;
    private FetchPokemons pokemonAPI;
    private StatusCallback callBack;
    private Context context;

    private static final int DATABASE_VERSION = 5;
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


    public Database(Context context, StatusCallback statusCallback) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.callBack = statusCallback;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.pokemonAPI = new FetchPokemons();

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

    public void initInsertIfNewDB() {
        SQLiteDatabase db = this.getWritableDatabase();
        this.pokemonAPI.waitFetchFinish();
        this.pokemonDatas = this.pokemonAPI.getPokemonDatas();
        this.pokemonTypes = this.pokemonAPI.getPkmTypes();

        insertTypes(db);

        for (int pkmId = this.pokemonDatas.size() - 1; pkmId >= 0; pkmId--) {
            insertPokemons(this.pokemonDatas.get(pkmId), db);
        }
        callBack.statusChange(1);
    }

    private void clearDatabase(SQLiteDatabase db) {
        db.execSQL("DELETE FROM " + TABLE_RESISTANCE );
        db.execSQL("DELETE FROM " + TABLE_POKEMON);
        db.execSQL("DELETE FROM " + TABLE_TYPE);
        db.execSQL("DELETE FROM " + TABLE_EVOLUTIONS);
    }

    private void insertTypes(SQLiteDatabase db) {
        for (PokemonTypeData pokemonType : this.pokemonTypes) {
            byte[] byteArray = null;
            if (pokemonType.hasImage())
                byteArray = getByteArray(pokemonType.getImg());
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
            initInsertIfNewDB();
        } else if (status == -1) {

        }
    }

    public ArrayList<MinimalPokemonInfo> getMinimalPokemonInfos() {
        ArrayList<MinimalPokemonInfo> minimalPokemonInfos = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_POKEMON, new String[] {COLUMN_POKEMON_ID,COLUMN_POKEMON_NAME,COLUMN_POKEMON_SPRITE},null, null, null, null, null);
        if (cursor != null){
            while (cursor.moveToNext()) {
                minimalPokemonInfos.add(new MinimalPokemonInfo(
                        cursor.getInt(0),
                        cursor.getString(1),
                        getBitmap(cursor.getBlob(2))
                ));
            }
            return minimalPokemonInfos;
        }
        return null;
    }

    public byte[] getByteArray(Bitmap bm) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    public Bitmap getBitmap(byte[] bytes) {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public int getNumbersOfPokemons() {
        SQLiteDatabase db = this.getReadableDatabase();
        int result;
        Cursor cursor = db.query(TABLE_POKEMON, new String[] {COLUMN_POKEMON_ID},null, null, null, null, null);
        result = (cursor.moveToFirst()) ? cursor.getCount() : -1;
        cursor.close();
        return result;
    }

    public PokemonData getPokemonData(int pokemonId) {
        SQLiteDatabase db = this.getReadableDatabase();

        PokemonData returnedPokemon = null;
        List<Integer> pokemonEvolutions = new ArrayList<>();
        List<PokemonTypeData> pokemonTypes = new ArrayList<>();
        List<PokemonTypeResistancesData> pokemonResistances = new ArrayList<>();

        String queryPokemon = "SELECT p.*, t1." + COLUMN_TYPE_NAME + " AS type1, t1." + COLUMN_TYPE_IMAGE + " AS type1Img, t1." + COLUMN_TYPE_ID + " AS type1ID, t2."+COLUMN_TYPE_NAME+" AS type2,  t2."+COLUMN_TYPE_IMAGE+" AS type2Img , t2." + COLUMN_TYPE_ID + " AS type2ID \n" +
                "FROM "+TABLE_POKEMON+" p\n" +
                "LEFT JOIN "+TABLE_TYPE+" t1 ON p."+COLUMN_POKEMON_TYPE1+" = t1."+COLUMN_TYPE_ID+"\n" +
                "LEFT JOIN "+TABLE_TYPE+" t2 ON p."+COLUMN_POKEMON_TYPE2+" = t2."+COLUMN_TYPE_ID+"\n" +
                "WHERE p."+COLUMN_POKEMON_ID+" = ?\n";

        String queryResistances = "SELECT t."+COLUMN_TYPE_NAME+" , t."+COLUMN_TYPE_ID+" , r."+COLUMN_RESISTANCE_DAMAGE_MULTIPLIER+ " " +
        "FROM " + TABLE_POKEMON + " p " +
        "LEFT JOIN " + TABLE_RESISTANCE + " r ON p." + COLUMN_POKEMON_ID + " = r." + COLUMN_RESISTANCE_POKEMON_ID + " " +
        "LEFT JOIN " + TABLE_TYPE + " t ON r." + COLUMN_RESISTANCE_TYPE_ID + " = t." + COLUMN_TYPE_ID + " " +
        "WHERE p." + COLUMN_POKEMON_ID + " = ?";

        String queryEvolutions = "SELECT " + COLUMN_EVOLUTIONS_PKM_EVOLUTION +
                " FROM " + TABLE_EVOLUTIONS +
                " WHERE " + COLUMN_EVOLUTIONS_PKM_BASE + " = ?";

        // getting the evolutions

        final Cursor cursorEvolution = db.rawQuery(queryEvolutions, new String[]{String.valueOf(pokemonId)});
        while (cursorEvolution.moveToNext()) {
            pokemonEvolutions.add(cursorEvolution.getInt(0));
        }
        cursorEvolution.close();

        // getting the resistances :
        final Cursor cursorResistances = db.rawQuery(queryResistances, new String[]{String.valueOf(pokemonId)});
        while (cursorResistances.moveToNext()) {
            pokemonResistances.add(new PokemonTypeResistancesData(
                    new PokemonTypeData(PokemonTypes.valueOf(cursorResistances.getString(0)), cursorResistances.getInt(1)),
                    cursorResistances.getDouble(2)
            ));
        }
        cursorResistances.close();

        // getting the pokemon and creating it
        final Cursor cursorPokemon = db.rawQuery(queryPokemon, new String[]{String.valueOf(pokemonId)});
        if (cursorPokemon.moveToFirst()) {
            returnedPokemon = new PokemonData(
                    cursorPokemon.getInt(0),
                    pokemonEvolutions,
                    cursorPokemon.getString(1),
                    getBitmap(cursorPokemon.getBlob(2)),
                    cursorPokemon.getInt(3),
                    cursorPokemon.getInt(4),
                    cursorPokemon.getInt(5),
                    cursorPokemon.getInt(6),
                    cursorPokemon.getInt(7),
                    cursorPokemon.getInt(8), // speed
                    pokemonResistances,
                    new ArrayList<PokemonTypeData>() {{
                        add(new PokemonTypeData(PokemonTypes.valueOf(cursorPokemon.getString(11)),getBitmap(cursorPokemon.getBlob(12)),cursorPokemon.getInt(13)));
                        if (cursorPokemon.getString(14) != null)
                            add(new PokemonTypeData(PokemonTypes.valueOf(cursorPokemon.getString(14)),getBitmap(cursorPokemon.getBlob(15)),cursorPokemon.getInt(16)));
                    }}
            );
        }
        cursorPokemon.close();
        return returnedPokemon;
    }
}