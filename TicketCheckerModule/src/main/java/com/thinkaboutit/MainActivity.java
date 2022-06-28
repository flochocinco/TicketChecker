package com.thinkaboutit;

import android.accounts.Account;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.sheets.v4.SheetsScopes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.leo.produce.ZxingView;

public class MainActivity extends AppCompatActivity {

    Set<String> listItems=new HashSet<>();
    ArrayAdapter<String> adapter;

    protected boolean debounced = false;
    private ActivityResultLauncher<Intent> launcher;
    protected Intent signInIntent;
    ExecutorService mExecutor = Executors.newSingleThreadExecutor();
    GoogleAccountCredential cred;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // GetContent creates an ActivityResultLauncher<String> to allow you to pass
        // in the mime type you'd like to allow the user to select
        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == RESULT_CANCELED){
                        Toast.makeText(this, "Unable to log in", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    //Toast.makeText(this, "logged in !!!", Toast.LENGTH_SHORT).show();
                    cred = GoogleAccountCredential.usingOAuth2(MainActivity.this, Collections.singletonList(SheetsScopes.SPREADSHEETS))
                            .setBackOff(new ExponentialBackOff());

                    try {
                        Account account = Objects.requireNonNull(GoogleSignIn.getLastSignedInAccount(this)).getAccount();
                        cred.setSelectedAccount(account);
                    }catch (NullPointerException e){
                        Toast.makeText(this, "Unexpected error while logging in", Toast.LENGTH_SHORT).show();
                    }

                    Runnable runnable = () -> {
                        List<Object> usedIds = new GetValue().readSpreadSheet(cred);
                        usedIds.forEach(this::addId);
                    };
                    //Runnable runnable = () -> new GetValue().writeData(cred);
                    mExecutor.execute(runnable);
                });

        //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.MANAGE_EXTERNAL_STORAGE}, 1);

        ZxingView zxingView = findViewById(R.id.zxingView);
        zxingView.bind(this)
            .subscribe(result -> {
                if(debounced){
                    return;
                }
                boolean isUnique = addId(result);
                if(isUnique){
                    List<Object> data = Arrays.asList(result,"","",((TextView)findViewById(R.id.PartnerName)).getText().toString());
                    Runnable runnable = () -> new GetValue().writeData(cred, data);
                    mExecutor.execute(runnable);
                }
                debounced = true;
                final Handler handler = new Handler();
                handler.postDelayed(() -> debounced = false, 2000);
            });

        registerCredentials();

        adapter=new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                new ArrayList<>(listItems)){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                int color;
                String item = this.getItem(position);
                if(item != null && item.contains(getString(R.string.already_used))){
                    color = getResources().getColor(android.R.color.holo_red_dark, MainActivity.this.getTheme());

                }else{
                    color = getResources().getColor(android.R.color.holo_green_dark, MainActivity.this.getTheme());
                }
                view.setBackgroundColor(color);
                return view;
            }
        };
        ((ListView)findViewById(R.id.listview)).setAdapter(adapter);

    }

    protected boolean addId(Object result) {
        boolean isUnique = listItems.add(result.toString());
        String res = isUnique ? "OK: " : getString(R.string.already_used);
        res += result;
        String finalRes = res;
        runOnUiThread( () -> adapter.insert(finalRes, 0));
        return isUnique;
    }

    @Override
    protected void onDestroy() {
        mExecutor.shutdown();
        super.onDestroy();
    }

    public void registerCredentials(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if(!Environment.isExternalStorageManager()){
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", this.getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        }
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(SheetsScopes.SPREADSHEETS))
                .requestEmail()
                .build();

        signInIntent = GoogleSignIn.getClient(this, options).getSignInIntent();
        launcher.launch(signInIntent);
    }
}
