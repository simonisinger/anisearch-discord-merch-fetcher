package de.simonisinger;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

public class Product
{
	private final int anisearchId;
	private final String title;
	private final List<ProductType> types;
	private final LocalDate releaseDate;
	private final Locale language;
	private final List<Shop> shops;

	@JsonCreator
	Product(
			@JsonProperty("anisearchId") int anisearchId,
			@JsonProperty("title") String name,
			@JsonProperty("types") List<ProductType> types,
			@JsonProperty("releaseDate") LocalDate releaseDate,
			@JsonProperty("language") Locale language,
			@JsonProperty("shops") List<Shop> shops
	) {
		this.anisearchId = anisearchId;
		this.title = name;
		this.types = types;
		this.releaseDate = releaseDate;
		this.language = language;
		this.shops = shops;
	}

	@JsonGetter("anisearchId")
	public int getAnisearchId() {
		return anisearchId;
	}

	@JsonGetter("title")
	public String getTitle() {
		return title;
	}

	@JsonGetter("types")
	public List<ProductType> getTypes() {
		return types;
	}

	@JsonGetter("releaseDate")
	public LocalDate getReleaseDate() {
		return releaseDate;
	}

	@JsonGetter("language")
	public Locale getLanguage() {
		return language;
	}

	@JsonGetter("shops")
	public List<Shop> getShops() {
		return shops;
	}
}
