package com.directmediatips.book.chapter02;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.directmediatips.database.DatabaseConnection;

public class ShowQuotes {

	public static void main(String[] args) throws IOException, SQLException {
		DatabaseConnection connection = new DatabaseConnection();
		ResultSet rs = connection.execute("SELECT quote, author FROM directmediatips_quotes");
		while (rs.next()) {
			System.out.println(String.format("\"%s\" - %s", rs.getString("quote"), rs.getString("author")));
		}
		connection.close();
	}
}
