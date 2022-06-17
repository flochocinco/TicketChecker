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

import java.io.IOException;
import java.util.List;

/* Class to demonstrate the use of Spreadsheet Get Values API */
public class GetValue {

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    public String spreadsheet_id="1B5Gfo_jq7jibpq_T6mFFr_AF8HrJe26zprFJz0IWmvo";
    public String spreadsheet_range="Sheet1!A2:E";
    public void readSpreadSheet(GoogleAccountCredential cred){
        final NetHttpTransport HTTP_TRANSPORT = new com.google.api.client.http.javanet.NetHttpTransport();
        Sheets sheets = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, cred).setApplicationName("Demat").build();
        try {
            List<List<Object>> values = sheets.spreadsheets().values().get(spreadsheet_id, spreadsheet_range).execute().getValues();
            for(List<Object> val : values){
                System.out.println(val);
                val.forEach(obj -> System.out.println(obj.toString()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
// [END sheets_get_values]
