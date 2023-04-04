package fr.naulantiago.saeandroid.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FetchPokemons {
    private StatusCallback callback;
    private static final String API = "https://pokebuildapi.fr/api/v1/pokemon/generation/1";

    private final List<PokemonData> pkmDatas;

    private int status;
    private final Set<PokemonTypeData> pkmTypes;
    private final OkHttpClient client;

    public FetchPokemons(StatusCallback callback) {
        this.callback = callback;
        this.pkmDatas = new ArrayList<>();
        this.pkmTypes = new HashSet<>();
        this.client = new OkHttpClient();
        this.status = 0;

        this.client.newCall(new Request.Builder()
                .url(API)
                .build()).enqueue(new Callback() {
            public void onFailure(Call call, IOException e) {
                callback.statusChange(-1);
            }

            public void onResponse(Call call, Response response) throws IOException {
                if (response.body() != null) {
                    JsonNode data = new ObjectMapper().readTree(response.body().string());
                    initTypes(data.get(0).get("apiResistances"));

                    for (int i = 0; i < data.size(); i++)
                        storeData(data.get(i));
                    callback.statusChange(1);
                }
            }
        });
    }

    /** @param pkm Pokemon JSON data */
    private void storeData(JsonNode pkm) {
        List<PokemonTypeData> pkmTypes = new ArrayList<>();
        pkm.get("apiTypes").forEach(type -> {
            PokemonTypeData typeData = getType(type.get("name").asText());
            if (!typeData.hasImage())
                typeData.setImage(getImage(type.get("image").asText()));
            pkmTypes.add(typeData);
        });

        List<PokemonTypeResistancesData> pkmTypeResistances = new ArrayList<>();
        pkm.get("apiResistances").forEach(r -> {
            PokemonTypeData typeData = getType(r.get("name").asText());
            double dmgMultiplier = r.get("damage_multiplier").asDouble();
            pkmTypeResistances.add(new PokemonTypeResistancesData(typeData, dmgMultiplier));
        });

        List<Integer> pkmEvolutions = new ArrayList<>();
        pkm.get("apiEvolutions").forEach(e -> pkmEvolutions.add(e.get("pokedexId").asInt()));

        JsonNode stats = pkm.get("stats");
        this.pkmDatas.add(new PokemonData(pkm.get("pokedexId").asInt(),
                                          pkmEvolutions,
                                          pkm.get("name").asText(),
                                          getImage(pkm.get("sprite").asText()),
                                          stats.get("HP").asInt(),
                                          stats.get("attack").asInt(),
                                          stats.get("defense").asInt(),
                                          stats.get("special_attack").asInt(),
                                          stats.get("special_defense").asInt(),
                                          stats.get("speed").asInt(),
                                          pkmTypeResistances,
                                          pkmTypes));
    }

    private Bitmap getImage(String link) {
        System.out.println(link);
        Request request = new Request.Builder().url(link).build();
        System.out.println(link);
        try (Response response = this.client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            InputStream inputStream = response.body().byteStream();
            return BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            return null;
        }
    }

    private void initTypes(JsonNode types) {
        int id = 1;
        for (JsonNode type : types) {
            String name = type.get("name").asText();
            this.pkmTypes.add(new PokemonTypeData(PokemonTypes.valueOf(name), id));
            id++;
        }
    }

    private PokemonTypeData getType(String name) {
        // faire quelque chose avec l'optional s'il ne trouve rien idk quoi alors je laisse la .get pour l'instant
        return pkmTypes.stream().filter(el -> el.getType().name().equals(name)).findFirst().get();
    }

    public List<PokemonData> getPokemonDatas() {
        return this.pkmDatas;
    }

    public Set<PokemonTypeData> getPkmTypes() {
        return pkmTypes;
    }
}
