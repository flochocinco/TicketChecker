package com.thinkaboutit;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class GoogleHandler {
    private Sheets service;
    private Exception mLastError = null;

    public GoogleHandler(GoogleAccountCredential credential){
        NetHttpTransport HTTP_TRANSPORT = new com.google.api.client.http.javanet.NetHttpTransport();
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
        this.service = new Sheets.Builder(HTTP_TRANSPORT,jsonFactory,credential)
                .setApplicationName("Cezam_Demat")
                .build();
    }

    public void readCurrentWarehouse(){
        Thread thread = new Thread(){
            public void run(){
                String spreadsheetId = "";
                try{
                    Spreadsheet spreadsheet = service.spreadsheets().get(spreadsheetId).execute();
                    Sheet lastSheet = spreadsheet.getSheets().get(spreadsheet.getSheets().size()-1);
                    String range = lastSheet.getProperties().getTitle();
                    List<String> results = new ArrayList<String>();
                    ValueRange response = service.spreadsheets().values().get(spreadsheetId,range).execute();
                    List<List<Object>> values = response.getValues();
                    if (values!=null){
                        for (List row : values){
                            results.add(row.get(0).toString());
                        }
                    }
                    System.out.println(results.size());

                }catch (IOException e){
                    System.out.println("-------------------------------");
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }
}

public class SheetActivity extends AppCompatActivity {
    private void handleSignInResult(Task<GoogleSignInAccount> task) {
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            GoogleSignIn.requestPermissions(this, 2, account, new Scope(SheetsScopes.SPREADSHEETS));
            if (GoogleSignIn.hasPermissions(
                    GoogleSignIn.getLastSignedInAccount(this), new Scope(SheetsScopes.SPREADSHEETS)
            )) {
                String accountName = account.getEmail();
                GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(this, Arrays.asList(SheetsScopes.SPREADSHEETS)).setBackOff(new ExponentialBackOff());
                credential.setSelectedAccountName(accountName);
                GoogleHandler googleHandler = new GoogleHandler(credential);
                googleHandler.readCurrentWarehouse();
                /*Intent mainMenuIntent = new Intent(getBaseContext(),SheetActivity.class);
                mainMenuIntent.putParcelableArrayListExtra("warehouse",warehouse.getWarehouse());
                startActivity(mainMenuIntent);*/
            }


        } catch (ApiException e) {
            System.out.println("tag" + "signInResult:failed code=" + e.getStatusCode());
            System.out.println("tag2" + "signInResult:failed description = " + e.getCause());
            //updateUI(null)
        }
    }
}
