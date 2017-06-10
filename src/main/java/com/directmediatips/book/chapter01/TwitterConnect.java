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

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Example class that connects to Twitter, and displays some information about
 * our account in the console window.
 */
public class TwitterConnect {
	
	/** The twitter4j instance. */
	protected Twitter twitter;
	
	/**
	 * Initializes the Twitter4J Twitter object for an account.
	 * @param	account	the screen name of an account
	 */
	public void initTwitter(String account) throws IOException {
		Properties properties = new Properties();
		properties.load(new FileInputStream(String.format("twitter/%s.properties", account)));
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled("true".equals(properties.getProperty("debug")))
		  .setOAuthConsumerKey(properties.getProperty("oauth.consumerKey"))
		  .setOAuthConsumerSecret(properties.getProperty("oauth.consumerSecret"))
		  .setOAuthAccessToken(properties.getProperty("oauth.accessToken"))
		  .setOAuthAccessTokenSecret(properties.getProperty("oauth.accessTokenSecret"));
		TwitterFactory tf = new TwitterFactory(cb.build());
		twitter = tf.getInstance();
	}
	
	/** Shows the ID, screen name, and description of the current user. */
	public void showCurrentUser() throws TwitterException {
		long id = twitter.getId();
		System.out.println(String.format("Current id: %s", id));
		User user = twitter.showUser(id);
		System.out.println(String.format("%s: %s",
			user.getScreenName(), user.getDescription()));
	}

	/** The main method of this application. */
	public static void main(String[] args) throws IOException, TwitterException {
		TwitterConnect app = new TwitterConnect();
		app.initTwitter("directmediatips");
		app.showCurrentUser();
	}
}
