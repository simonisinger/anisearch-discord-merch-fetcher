package de.simonisinger;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.File;
import java.util.List;
import java.util.Locale;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;


class ProductCacheTest {
	ProductCache cache;
	WireMockServer wireMockServer = new WireMockServer(options().port(8089));

	@BeforeEach
	void init() {
		wireMockServer.start();
		new File("db/feeds.json").delete();
		cache = new ProductCache();
		cache.addFeed(
				new FeedChannel(
						1,
						Locale.forLanguageTag("de"),
						ProductType.BOOK
				)
		);
	}

	@AfterEach
	void cleanup() {
		new File("db/feeds.json").delete();
		wireMockServer.stop();
	}

	@org.junit.jupiter.api.Test
	void addFeed() {
		cache.addFeed(
				new FeedChannel(
						1,
						Locale.forLanguageTag("de"),
						ProductType.DVD
				)
		);
		Assertions.assertThat(cache.productFeeds).hasSize(2);
	}

	@org.junit.jupiter.api.Test
	void removeFeed() {
		Assertions.assertThat(cache.productFeeds).hasSize(1);
		cache.removeFeed(cache.productFeeds.getFirst());
		Assertions.assertThat(cache.productFeeds).isEmpty();
	}

	@org.junit.jupiter.api.Test
	void getFeedsFromChannelId() {
		List<FeedChannel> feeds = cache.getFeedsFromChannelId(1);
		Assertions.assertThat(feeds).hasSize(1);
		Assertions.assertThat(feeds.getFirst()).isEqualTo(cache.productFeeds.getFirst());
		Assertions.assertThat(cache.getFeedsFromChannelId(2)).isEmpty();

	}

	@org.junit.jupiter.api.Test
	void requestData() {
	}

	@org.junit.jupiter.api.Test
	void cleanupProducts() {
	}
}