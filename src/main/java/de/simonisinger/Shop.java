package de.simonisinger;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.net.URI;

public class Shop {
	public String name;
	public URI productUrl;

	Shop(@JsonProperty("name") String name, @JsonProperty("productUrl") URI productUrl) {
		this.name = name;
		this.productUrl = productUrl;
	}
}
