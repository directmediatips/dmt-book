package com.directmediatips.book.chapter01;

/*
 * Copyright 2017, Bruno Lowagie, Wil-Low BVBA
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the  * specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.CellData;
import com.google.api.services.sheets.v4.model.ExtendedValue;
import com.google.api.services.sheets.v4.model.GridCoordinate;
import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.RowData;
import com.google.api.services.sheets.v4.model.UpdateCellsRequest;
import com.google.api.services.sheets.v4.model.ValueRange;

/**
 * Example class that connects to Google, asks for authorization to access
 * your Google Sheets account, then reads and writes some data from and
 * to a Google Sheets document.
 */
public class GoogleConnect {

	/** Provides a low-level JSON implementation. */
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	/** Provides a thread-safe HTTP implementation. */
	private static HttpTransport HTTP_TRANSPORT;
	/** Defines where to store serialized objects (such as credentials). */
	private static final File DATA_STORE_DIR = new File("google");
	/** Provides a thread-safe file implementation to store serialized objects. */
	private static FileDataStoreFactory DATA_STORE_FACTORY;
    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }
    /** We'll limit the scope to sheets. */
	private static final List<String> SCOPES = Arrays.asList(SheetsScopes.SPREADSHEETS);
	
	/**
	 * Creates a Credential instance, either by opening a browser window so that
	 * you can allow the application, or by reading credentials for a file.
	 * @return credentials that will allow access to your Google Sheets account
	 * @throws IOException
	 */
	public Credential getCredential() throws IOException {
        InputStream in = new FileInputStream("google/client_secret.json");
        GoogleClientSecrets clientSecrets =
            GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
        GoogleAuthorizationCodeFlow flow =
            new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(DATA_STORE_FACTORY)
                .setAccessType("offline")
                .build();
        return new AuthorizationCodeInstalledApp(
                flow, new LocalServerReceiver()).authorize("testuser");
	}
    
    /**
     * Creates an instance of the Sheets class, which is a service class that
     * allows you to read from and write to sheets.
     * @param credential	the credentials that give you access to your sheets
     * @param application	the name of an application
     * @return	a Sheets service class
     * @throws IOException
     */
    public Sheets getSheets(Credential credential, String application) throws IOException {
        return new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(application)
                .build();
    }
	
    /**
     * Gets the cell content of a single cell (assuming that you pass the correct parameters).
     * @param sheets	the Sheets service
     * @param sheetId	a valid ID of a Google Sheets document
     * @param cell	the "coordinate" of a cell, e.g. "A1"
     * @return	an object that represents the value stored in that cell
     * @throws IOException
     */
    public Object getCellContent(Sheets sheets, String sheetId, String cell) throws IOException {
		ValueRange response = sheets
				.spreadsheets()
				.values()
				.get(sheetId, cell)
				.execute();
		List<List<Object>> values = response.getValues();
		return values.get(0).get(0);
    }
    
    /**
     * Sets the cell content of a cell in a Google sheets document.
     * @param sheets	the Sheets service
     * @param sheetId	a valid ID of a Google sheets document
     * @param value		a String value that will be added to a cell
     * @param row	the row number of the cell we'll update
     * @param column	the column number of the cell we'll update
     * @throws IOException
     */
    public void setCellContent(Sheets sheets,
    		String sheetId, String value, int row, int column) throws IOException {
		List<CellData> values = new ArrayList<CellData>();
		values.add(new CellData()
				.setUserEnteredValue(new ExtendedValue().setStringValue(value)));
  	  	List<Request> requests = new ArrayList<Request>();
  	  	UpdateCellsRequest updateCellRequest = new UpdateCellsRequest()
  	  			.setStart(new GridCoordinate()
  	  					.setSheetId(0)
  	  					.setRowIndex(0)
  	  					.setColumnIndex(1))
  	  			.setRows(Arrays.asList(new RowData().setValues(values)))
  	  			.setFields("*");
  	  	requests.add(new Request().setUpdateCells(updateCellRequest));
  	  	BatchUpdateSpreadsheetRequest batchUpdateRequest =
  	  			new BatchUpdateSpreadsheetRequest().setRequests(requests);
  	  	sheets.spreadsheets().batchUpdate(sheetId, batchUpdateRequest).execute();
    }

	/** The main method of this application. */
	public static void main(String[] args) throws IOException {
		GoogleConnect app = new GoogleConnect();
		Credential credential = app.getCredential();
		Sheets sheets = app.getSheets(credential, "Test application");
		System.out.println(app.getCellContent(sheets,
				"1IP-ALTVvAIWSgMtZtCcRlr268TJjuWLeZVKY0baoDEE", "A1"));
		app.setCellContent(sheets,
				"1IP-ALTVvAIWSgMtZtCcRlr268TJjuWLeZVKY0baoDEE", "Lowagie", 0, 1);
	}
}
