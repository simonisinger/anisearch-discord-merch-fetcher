package de.simonisinger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

import static java.lang.Integer.parseInt;

public class ProductCache extends TimerTask {
	protected final ArrayList<Product> products = new ArrayList<>();
	protected final ArrayList<FeedChannel> productFeeds = new ArrayList<>();


	// inits the products and productsfeed cache
	ProductCache() {
		initProducts();
		initFeeds();
	}

	public void addFeed(FeedChannel feed) {
		productFeeds.add(feed);
		saveFeeds();
	}

	public void removeFeed(FeedChannel feed) {
		productFeeds.remove(feed);
		saveFeeds();
	}

	public List<FeedChannel> getFeedsFromChannelId(long channelId) {
		return productFeeds.stream().filter(feed -> feed.channelId == channelId).toList();
	}

	String requestData(URI url) throws IOException {
		HttpURLConnection connection = (HttpURLConnection) url.toURL().openConnection();
		connection.setRequestMethod("GET");
		connection.setDoOutput(true);
		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String contentLine;
		StringBuilder contentBuilder = new StringBuilder();
		while ((contentLine = reader.readLine()) != null) {
			contentBuilder.append(contentLine);
		}
		reader.close();
		connection.disconnect();
		return contentBuilder.toString();
	}

	void update() {
		JSONObject json;
		try {
			URI apiUrl = URI.create(System.getenv("API_URL"));
			String content = requestData(apiUrl);
			json = new JSONObject(content);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		Set<String> languages = json.keySet();
		Map<Locale, ArrayList<Product>> newProducts = new HashMap<>();
		Map<Locale, ArrayList<UpdatedProduct>> updatedProducts = new HashMap<>();
		Map<Locale, ArrayList<Product>> releaseTodayProducts = new HashMap<>();
		// This variable is needed that we remove obsolete products
		List<Product> currentProductsList = new ArrayList<>();
		for (String language : languages) {
			Locale locale = Locale.forLanguageTag(language);
			newProducts.put(locale, new ArrayList<>());
			updatedProducts.put(locale, new ArrayList<>());
			releaseTodayProducts.put(locale, new ArrayList<>());
		}

		LocalDate now = LocalDate.now();

		for (String language : json.keySet()) {
			JSONObject languageProducts = json.getJSONObject(language);
			for (String productIdString : languageProducts.keySet()) {
				int productId = parseInt(productIdString);
				Optional<Product> product = products
						.stream()
						.filter(tmpProduct -> tmpProduct.getAnisearchId() == productId)
						.findFirst();
				JSONObject productJson = languageProducts.getJSONObject(productIdString);

				try {
					if (product.isEmpty()) {
						Product newProduct = createProduct(productId, productJson);
						// Prevent to be readded if the timer is less than a day
						if (now.isEqual(newProduct.getReleaseDate())) {
							continue;
						}
						newProducts.get(Locale.forLanguageTag(language)).add(newProduct);
						products.add(newProduct);
						currentProductsList.add(newProduct);
					} else {
						LocalDate newDate = LocalDate.parse(productJson.getString("date"));
						if (newDate.isAfter(product.get().getReleaseDate())) {
							Product newProduct = createProduct(productId, productJson);
							updatedProducts.get(Locale.forLanguageTag(language))
									.add(new UpdatedProduct(product.get(), newProduct));
							products.remove(product.get());
							products.add(newProduct);
							currentProductsList.add(newProduct);
						} else if (newDate.isEqual(now)) {
							releaseTodayProducts.get(Locale.forLanguageTag(language)).add(product.get());
							products.remove(product.get());
						} else {
							currentProductsList.add(product.get());
						}
					}
				} catch (DateTimeParseException e) {
					// nothing to do
				}
			}
		}

		for (FeedChannel feed : this.productFeeds) {
			Locale feedLanguage = feed.language;
			List<UpdatedProduct> filteredUpdatedProduct = updatedProducts.get(feedLanguage)
					.stream()
					.filter(updatedProduct -> updatedProduct.oldProduct().getTypes().contains(feed.productType))
					.toList();
			List<Product> filteredNewProduct = newProducts.get(feedLanguage)
					.stream()
					.filter(product -> product.getTypes().contains(feed.productType))
					.toList();
			List<Product> releaseTodayProduct = releaseTodayProducts.get(feedLanguage)
					.stream()
					.filter(product -> product.getTypes().contains(feed.productType))
					.toList();
			feed.update(filteredNewProduct, filteredUpdatedProduct, releaseTodayProduct);
		}
		cleanupProducts(currentProductsList);
		saveProducts();
	}

	protected void cleanupProducts(List<Product> currentProductsList) {
		products.removeIf(
				product -> currentProductsList
						.stream()
						.noneMatch(tmpProduct -> tmpProduct.getAnisearchId() == product.getAnisearchId())
		);
	}

	private void initFeeds() {
		File productFeedsFile = new File("db/feeds.json");
		try {
			if (!productFeedsFile.exists()) {
				productFeedsFile.createNewFile();
				FileWriter fileWriter = new FileWriter(productFeedsFile);
				fileWriter.write("[]");
				fileWriter.close();
			} else {
				ObjectMapper mapper = getMapper();
				List<FeedChannel> productFeeds = Arrays.stream(mapper.readValue(productFeedsFile, FeedChannel[].class)).toList();

				this.productFeeds.clear();
				this.productFeeds.addAll(productFeeds);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void initProducts() {
		File productsFile = new File("db/products.json");
		try {
			if (!productsFile.exists()) {
				productsFile.createNewFile();
				FileWriter fileWriter = new FileWriter(productsFile);
				fileWriter.write("[]");
				fileWriter.close();
			} else {
				ObjectMapper mapper = getMapper();
				List<Product> products = Arrays.stream(mapper.readValue(productsFile, Product[].class)).toList();

				this.products.clear();
				this.products.addAll(products);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private ObjectMapper getMapper() {
		return new ObjectMapper().registerModule(new JavaTimeModule());
	}

	private void saveProducts() {
		try {
			ObjectMapper objectMapper = getMapper();
			objectMapper.writeValue(new File("db/products.json"), products);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void saveFeeds() {
		try {
			ObjectMapper objectMapper = getMapper();
			objectMapper.writeValue(new File("db/feeds.json"), productFeeds);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private Product createProduct(int anisearchId, JSONObject productJson) {
		List<ProductType> mediumTypes = productJson.getJSONArray("medium")
				.toList()
				.stream()
				.map(id -> (int) id)
				.toList()
				.stream().map(ProductType::fromMediumId).toList();

		List<Shop> shops = new ArrayList<>();
		if (productJson.get("shops").getClass() == JSONObject.class) {
			shops = productJson.getJSONObject("shops")
					.toMap()
					.entrySet()
					.stream()
					.map(entry -> new Shop(entry.getKey(), URI.create((String) entry.getValue())))
					.toList();
		}

		return new Product(
				anisearchId,
				productJson.getString("title"),
				mediumTypes,
				LocalDate.parse(productJson.getString("date")),
				Locale.forLanguageTag("language"),
				shops
		);
	}

	@Override
	public void run() {
		update();
	}
}
