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

import com.directmediatips.klout.Influence;
import com.directmediatips.klout.Klout;
import com.directmediatips.klout.Network;
import com.directmediatips.klout.Topic;
import com.directmediatips.klout.User;
import com.directmediatips.klout.UserId;

public class KloutConnect {

	public static void main(String[] args) throws IOException {
		Properties properties = new Properties();
		properties.load(new FileInputStream("klout/klout.properties"));
		Klout klout = new Klout(properties.getProperty("apiKey"));
		UserId id = klout.getUserIdFromTwitterScreenName("bruno1970");
		System.out.println(id);
		id = klout.getUserId(id, Network.TWITTER);
		System.out.println(id);
		id = klout.getUserId(id);
		System.out.println(id);
		System.out.println();
		System.out.println(klout.getUser(id));
		System.out.println();
		System.out.println("Influencers:");
		Influence influence = klout.getInfluence(id);
		for (User user : influence.getMyInfluencers()) {
			System.out.println(String.format("%s: %s", user.getNick(), user.getScore()));
		}
		System.out.println();
		System.out.println("Influencees:");
		for (User user : influence.getMyInfluencees()) {
			System.out.println(String.format("%s: %s", user.getNick(), user.getScore()));
		}
		System.out.println();
		System.out.println("Topics:");
		for (Topic topic: klout.getTopics(id)) {
			System.out.println(topic.getDisplayName());
		}
	}
}
