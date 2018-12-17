package net.schueller.peertube.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import net.schueller.peertube.R;

public class UserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        init();
    }

    private void init() {
        // try to get user data
        if (!getUserData()) {
            Intent intent = new Intent(this, LoginActivity.class);
            this.startActivity(intent);
        }
    }

    private boolean getUserData() {

        // TODO

        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();

        init();

    }
}
