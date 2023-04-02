package fr.naulantiago.saeandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TableLayout;

import fr.naulantiago.saeandroid.model.Database;

public class MainActivity extends AppCompatActivity {
    private Database db;
    private TableLayout mTableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        this.mTableLayout = findViewById(R.id.tablePokedex);

        this.db = new Database(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        this.addPokemonsToTable();
    }

    private void addPokemonsToTable() {

    }
}
