package com.thinkaboutit;

// Copyright 2022 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.


// [START sheets_get_values]

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* Class to demonstrate the use of Spreadsheet Get Values API */
public class GetValue {

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    public String spreadsheet_id="";
    public String spreadsheet_range="";
    final NetHttpTransport HTTP_TRANSPORT = new com.google.api.client.http.javanet.NetHttpTransport();

    public List<Object> readSpreadSheet(GoogleAccountCredential cred){
        List<Object> ids = new ArrayList<>();
        Sheets sheets = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, cred).setApplicationName("Demat").build();
        try {
            List<List<Object>> values = sheets.spreadsheets().values().get(spreadsheet_id, spreadsheet_range).execute().getValues();
            values.stream().filter(line -> !line.isEmpty()).forEach(line -> ids.add(line.get(0)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ids;
    }

    public void writeData(GoogleAccountCredential cred, List<Object> data){
        Sheets sheets = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, cred).setApplicationName("Demat").build();
        try {
            ValueRange vr = new ValueRange().setValues(Collections.singletonList(data));
            AppendValuesResponse execute = sheets.spreadsheets().values().append(spreadsheet_id, spreadsheet_range, vr)
                    .setValueInputOption("RAW")
                    .setInsertDataOption("INSERT_ROWS")
                    .execute();
            execute.values();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
// [END sheets_get_values]
