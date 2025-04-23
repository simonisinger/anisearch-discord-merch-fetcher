package de.simonisinger.channels;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class FeedChannels {
	final ArrayList<Channel> productFeeds;
	public FeedChannels() {

	}

	public void addFeed(DiscordFeedChannel feed) {
		productFeeds.add(feed);
		saveFeeds();
	}

	public void removeFeed(DiscordFeedChannel feed) {
		productFeeds.remove(feed);
		saveFeeds();
	}


	private void saveFeeds() {
		try {
			ObjectMapper objectMapper = getMapper();
			objectMapper.writeValue(new File("db/feeds.json"), productFeeds);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private ObjectMapper getMapper() {
		return new ObjectMapper().registerModule(new JavaTimeModule());
	}
}
