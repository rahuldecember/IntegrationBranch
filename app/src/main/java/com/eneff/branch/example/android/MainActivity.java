package com.eneff.branch.example.android;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.content.Intent;
import android.widget.Toast;


import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.Iterator;

import androidx.appcompat.app.AppCompatActivity;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.branch.referral.validators.IntegrationValidator;

public class MainActivity extends AppCompatActivity {

    View buttonShare;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("Main Activity");

        Log.d("onCreate","onCreate method called");
        buttonShare = findViewById(R.id.buttonTap);
        buttonShare.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                intent = new Intent(MainActivity.this, SecondActivity.class);
                UtilMethods.CreateBUO().generateShortUrl(MainActivity.this, UtilMethods.CreateLP(), new Branch.BranchLinkCreateListener() {
                    @Override
                    public void onLinkCreate(String url, BranchError error) {

                        if (error == null) {
                            Log.i("BRANCH SDK", "got my Branch link to share: " + url);

                            Toast.makeText(getApplicationContext(),
                                    "Your Message"+ url, Toast.LENGTH_LONG).show();
                            startActivity(intent);
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        IntegrationValidator.validate(MainActivity.this);
        Branch.sessionBuilder(this)
                .withCallback(branchReferralInitListener)
                .withData(getIntent() != null ? getIntent().getData() : null).init();
      // Branch.sessionBuilder(this).withCallback(branchReferralInitListener).init();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

        if (intent != null &&
                intent.hasExtra("branch_force_new_session") &&
                intent.getBooleanExtra("branch_force_new_session", false)
        ) {
            Log.i("XAPP", "Branch reInit called");
            Branch.sessionBuilder(this).withCallback(branchReferralInitListener).reInit();

        }
    }

    private Branch.BranchReferralInitListener branchReferralInitListener = new Branch.BranchReferralInitListener() {

        @Override
        public void onInitFinished(@Nullable JSONObject linkProperties, @Nullable BranchError error) {

            Log.i("XAPP", "Branch init session");
            if (error == null)
            {
                Log.d("XAPP", linkProperties.toString());

                if (linkProperties.has("deep_link_test")) {
                    //get Value of deep_link_test
                    String deep_link_test = linkProperties.optString("deep_link_test");
                    Log.d("deep_link_test value",deep_link_test);
                    if(deep_link_test.equals("other")){
                        // opti on 3: navigate to page
                        Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                        startActivity(intent);
                    }
                }

            }
            else
            {
                Log.e("XAPP", error.getMessage());
                finish();
            }


        }
    };
}