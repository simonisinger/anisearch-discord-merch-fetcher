package de.simonisinger;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

class FeedChannelTest {

	static FeedChannel feed;

	@BeforeAll
	public static void setup() {
		feed = new FeedChannel(
				56565464,
				Locale.forLanguageTag("ger"),
				ProductType.DVD
		);
	}

	@Test
	void generateMessageEmbeds() {
	}

	@Test
	void formatDate() {
		LocalDate date = LocalDate.of(2025, 4, 1);
		Assertions.assertThat(feed.formatDate(date)).isEqualTo("01.04.2025");
	}

	@Test
	void generateUpdatedProductString() {
	}

	@Test
	void generateReleaseTodayProductString() {
	}

	@Test
	void generateNewProductString() {
	}

	@Test
	void generateEmbed() {
	}

	@Test
	void sendEmbed() {
	}

	@Test
	void generateShopsString() {
		List<URI> uris = new ArrayList<>();
		uris.add(URI.create("https://amazon.com/dp/xxxxxx"));
		uris.add(URI.create("https://amazon.co.uk/dp/xxxxxx"));
		List<Shop> shops = uris.stream().map(uri -> new Shop(uri.getHost(), uri)).toList();
		String expected = "**[[Details]](https://www.anisearch.de/article/50)** **[[amazon.com]](https://amazon.com/dp/xxxxxx)** **[[amazon.co.uk]](https://amazon.co.uk/dp/xxxxxx)**";
		Assertions.assertThat(feed.generateLinksString(50, shops)).isEqualTo(expected);
	}

	@Test
	void getProductType() {
		Assertions.assertThat(feed.getProductType()).isEqualTo(ProductType.DVD);
	}

	@Test
	void getLanguage() {

	}

	@Test
	void getChannelId() {
		Assertions.assertThat(feed.getChannelId()).isEqualTo(56565464);
	}
}