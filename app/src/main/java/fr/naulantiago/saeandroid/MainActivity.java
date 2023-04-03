package fr.naulantiago.saeandroid;

import android.os.Bundle;
import android.widget.TableLayout;

import androidx.appcompat.app.AppCompatActivity;

import fr.naulantiago.saeandroid.model.Database;
import fr.naulantiago.saeandroid.model.StatusCallback;

public class MainActivity extends AppCompatActivity implements StatusCallback {
    private Database db;
    private TableLayout mTableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.mTableLayout = findViewById(R.id.tablePokedex);
        this.db = new Database(this, this);
        System.out.println(this.db.query());
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void statusChange(int status) {
        if (status == 1) {
            System.out.println(this.db.query());
        } else {
            // faire un truc qui handle l'error je suppose
        }
    }
}
