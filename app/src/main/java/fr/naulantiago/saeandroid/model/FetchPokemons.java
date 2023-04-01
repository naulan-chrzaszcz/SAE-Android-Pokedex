package fr.naulantiago.saeandroid.model;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FetchPokemons {

    private List<PokemonData> pokemonData;
    Set<PokemonTypeData> pokemonTypes;
    private int status;
    private OkHttpClient client;

    public FetchPokemons(){
        pokemonData = new ArrayList<>();
        pokemonTypes = new HashSet<>();
        client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://pokebuildapi.fr/api/v1/pokemon/generation/1")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                status = -1;
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                status = 1;
                String responseBody = response.body().string();
                ObjectMapper objectMapper = new ObjectMapper();
                ArrayList<LinkedHashMap<String,Object>> data = objectMapper.readValue(responseBody, ArrayList.class);
                initTypes((ArrayList<LinkedHashMap<String, Object>>) data.get(0).get("apiResistances"));

                for (int i = 0; i < data.size(); i++) {
                    storeData(data.get(i));
                }
            }
        });
    }

    private void storeData(LinkedHashMap<String,Object> pokemon) {

        LinkedHashMap<String,Integer> stats = (LinkedHashMap<String, Integer>) pokemon.get("stats");
        ArrayList<LinkedHashMap<String,String>> typesMap = (ArrayList<LinkedHashMap<String, String>>) pokemon.get("apiTypes");
        ArrayList<LinkedHashMap<String, Object>> resistancesMap =  (ArrayList<LinkedHashMap<String, Object>>) pokemon.get("apiResistances");
        ArrayList<LinkedHashMap<String, Object>> evolutionMap =  (ArrayList<LinkedHashMap<String, Object>>) pokemon.get("apiEvolutions");


        ArrayList<PokemonTypeData> pokemonType = new ArrayList<>();
        ArrayList<PokemonTypeResistancesData> pokemonTypeResistances = new ArrayList<>();
        ArrayList<Integer> pokemonEvolutions = new ArrayList<>();

        int id = (int) pokemon.get("pokedexId");
        String name = (String) pokemon.get("name");
        Bitmap sprite = getImage((String) pokemon.get("sprite"));
        int hp = stats.get("HP");
        int attack = stats.get("attack");
        int defense = stats.get("defense");
        int special_attack = stats.get("special_attack");
        int special_defense = stats.get("special_defense");
        int speed = stats.get("speed");

        // evolution aussi
        typesMap.forEach(type -> {
            String typeName = type.get("name");

            PokemonTypeData typeData = getType(typeName);
            if (!typeData.hasImage())
                typeData.setImage(getImage(type.get("image")));
            pokemonType.add(typeData);
        });

        resistancesMap.forEach(resistance -> {
            String typeName = (String) resistance.get("name");
            double damage_multiplier;
            if (resistance.get("damage_multiplier") instanceof Integer)
                damage_multiplier = ((Integer) resistance.get("damage_multiplier")).doubleValue();
            else
                damage_multiplier = (double) resistance.get("damage_multiplier");
            PokemonTypeData typeData = getType(typeName);
            pokemonTypeResistances.add(new PokemonTypeResistancesData(typeData,damage_multiplier));
        });

        evolutionMap.forEach(evolution -> {
            int evolutionId = (int) evolution.get("pokedexId");
            pokemonEvolutions.add(evolutionId);
        });

        this.pokemonData.add(new PokemonData(id,pokemonEvolutions,name,sprite,hp,attack,defense,special_attack,special_defense,speed,pokemonTypeResistances,pokemonType));
    }

    private Bitmap getImage(String link) {
        Request request = new Request.Builder().url(link).build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            InputStream inputStream = response.body().byteStream();
            return BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            return null;
        }
    }

    private void initTypes(ArrayList<LinkedHashMap<String,Object>> types) {
        int id = 1;
        for (LinkedHashMap<String, Object> type : types) {
            String name = (String) type.get("name");
            this.pokemonTypes.add(new PokemonTypeData(PokemonTypes.valueOf(name),id));
            id ++;
        }
    }

    private PokemonTypeData getType(String name) {
        // faire quelque chose avec l'optional s'il ne trouve rien idk quoi alors je laisse la .get pour l'instant
        return this.pokemonTypes.stream().filter(el -> el.getName().name().equals(name)).findFirst().get();
    }

    public List<PokemonData> getPokemonData() { return  this.pokemonData; }
}
