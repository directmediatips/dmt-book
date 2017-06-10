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

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

import com.github.scribejava.apis.LinkedInApi20;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;


/**
 * Example class that connects to LinkedIn, asks for authorization to
 * get access to your LinkedIn data, and displays some information about
 * your account in the console window.
 */
public class LinkedInConnect {
	
	/**
	 * Creates an OAuth 2.0 service object.
	 * @param application	the application for which we want to get the client ID and Secret.
	 * @return	an OAuth 2.0 service object
	 * @throws IOException
	 */
	public OAuth20Service getService(String application) throws IOException {
		Properties properties = new Properties();
		properties.load(new FileInputStream(String.format("linkedin/%s.properties", application)));
		return new ServiceBuilder()
                .apiKey(properties.getProperty("ClientID"))
                .apiSecret(properties.getProperty("ClientSecret"))
                .scope(properties.getProperty("Permissions"))
                .callback(properties.getProperty("Redirect_URL1"))
                .build(LinkedInApi20.instance());
	}

	/**
	 * Gets an access token for accessing LinkedIn information.
	 * @param service an OAuth 2.0 service object
	 * @return an OAuth 2.0 access token
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	@SuppressWarnings("resource")
	public OAuth2AccessToken getAccessToken(OAuth20Service service)
		throws IOException, InterruptedException, ExecutionException {
		Scanner in = new Scanner(System.in);
        String authorizationUrl = service.getAuthorizationUrl();
        System.out.println("Go to the authorization URL:");
        System.out.println(authorizationUrl);
        System.out.println("Paste the resulting code here:");
        String code = in.nextLine();
        return service.getAccessToken(code);
	}
	
	/**
	 * Executes a LinkedIn query using an OAuth 2.0 service and access token.
	 * @param service	the OAuth 2.0 service
	 * @param accessToken	an OAuth 2.0 service
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws IOException
	 */
	public void executeQuery(OAuth20Service service, OAuth2AccessToken accessToken)
			throws InterruptedException, ExecutionException, IOException {
		String query = "https://api.linkedin.com/v1/people/~:"
				+ "(id,first-name,last-name,headline,location,email-address)";
		OAuthRequest req = new OAuthRequest(Verb.GET, query);
        req.addHeader("x-li-format", "xml");
        req.addHeader("Accept-Language", "en");
        service.signRequest(accessToken, req);
        Response resp = service.execute(req);
        System.out.println(resp.getBody());
	}

	/** The main method of this application. */
	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException, ExecutionException {
		LinkedInConnect app = new LinkedInConnect();
		OAuth20Service service = app.getService("directmediatips");
		OAuth2AccessToken accessToken = app.getAccessToken(service);
		app.executeQuery(service, accessToken);
	}
}
