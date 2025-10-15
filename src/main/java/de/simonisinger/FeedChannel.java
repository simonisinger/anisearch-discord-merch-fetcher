package de.simonisinger;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FeedChannel implements Channel {

	final protected long channelId;
	final protected Locale language;
	final protected ProductType productType;
    final protected long creatorId;
	private boolean errorSent = false;

	public FeedChannel(
            @JsonProperty("channelId") long channelId,
            @JsonProperty("language") Locale language,
            @JsonProperty("productType") ProductType productType,
            @JsonProperty("creatorId") long creatorId
    ) {
		this.channelId = channelId;
		this.language = language;
		this.productType = productType;
        this.creatorId = creatorId;
    }

	@JsonIgnore
	@Override
	public void update(List<Product> newProducts, List<UpdatedProduct> updatedProducts, List<Product> releaseTodayProducts) {
		errorSent = false;
		if (!newProducts.isEmpty()) {
			generateMessageEmbeds(
					this::generateNewProductString,
					newProducts.stream().map(product -> (Object) product).toList(),
					":new: **New Releases**"
			);
		}

		if (!updatedProducts.isEmpty()) {
			generateMessageEmbeds(
					this::generateUpdatedProductString,
					updatedProducts.stream().map(product -> (Object) product).toList(),
					":date: **Updated Releases**"
			);
		}

		if (!releaseTodayProducts.isEmpty()) {
			generateMessageEmbeds(
					this::generateReleaseTodayProductString,
					releaseTodayProducts.stream().map(product -> (Object) product).toList(),
					":package: **Release today**"
			);
		}
	}

	protected void generateMessageEmbeds(Function<Object, String> generator, List<Object> products, String titleString) {
		List<String> productStrings = products.stream().map(generator).toList();
		StringBuilder currentString = new StringBuilder();
		for (String productString : productStrings) {
			if (currentString.length() + productString.length() > 2500) {
				sendEmbed(generateEmbed(titleString, currentString.toString()));
				currentString = new StringBuilder(productString);
			} else {
				currentString.append("\n").append(productString);
			}
		}
		if (!currentString.isEmpty()) {
			sendEmbed(generateEmbed(titleString, currentString.toString()));
		}
	}

	protected String formatDate(LocalDate date) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.GERMAN);
		return date.format(formatter);
	}

	protected String generateUpdatedProductString(Object object) {
		UpdatedProduct updatedProduct = (UpdatedProduct) object;
		return "** %s ** -> ** %s ** %s %s".formatted(
				formatDate(updatedProduct.oldProduct().getReleaseDate()),
				formatDate(updatedProduct.newProduct().getReleaseDate()),
				updatedProduct.newProduct().getTitle(),
				generateLinksString(
						updatedProduct.newProduct().getAnisearchId(),
						updatedProduct.newProduct().getShops()
				)
		);
	}

	protected String generateReleaseTodayProductString(Object product) {
		Product releaseTodayProduct = (Product) product;
		return "%s %s".formatted(releaseTodayProduct.getTitle(), generateLinksString(
					releaseTodayProduct.getAnisearchId(),
					releaseTodayProduct.getShops()
				)
		);
	}

	protected String generateNewProductString(Object product) {
		Product castedProduct = (Product) product;
		return "**" + formatDate(castedProduct.getReleaseDate()) + "** " +
				castedProduct.getTitle() + " " + generateLinksString(((Product) product).getAnisearchId(), castedProduct.getShops());
	}

	protected MessageEmbed generateEmbed(String title, String description) {
		return new EmbedBuilder()
				.setTitle(title)
				.setDescription(description)
				.build();
	}

	protected void sendEmbed(MessageEmbed embed) {
		JDA client = Main.getDiscordClient();
        GuildChannel channel = client.getGuildChannelById(channelId);
        try {
            switch (channel) {
                case ThreadChannel threadChannel -> threadChannel.sendMessageEmbeds(embed).queue();
                case TextChannel textChannel -> textChannel.sendMessageEmbeds(embed).queue();
                case null, default -> System.err.println("Channel with ID " + channelId + " not found or not supported");
            }
        } catch (InsufficientPermissionException e) {
            System.err.println("Could not send embed to channel with ID " + channelId + " because of permission '" + e.getPermission().getName() + "'");
			if (!errorSent) {
				errorSent = true;
				Objects.requireNonNull(client.getUserById(creatorId))
						.openPrivateChannel()
						.queue(privateChannel -> privateChannel.sendMessage("Could not send Merch updates to #" + channelId).queue());
			}
        }
	}

	@JsonIgnore
	protected String generateLinksString(int anisearchId, List<Shop> shops) {
		String shopsString = shops.stream()
				.map(shop -> "**[[%s]](%s)**".formatted(shop.name, shop.productUrl.toString()))
				.collect(Collectors.joining(" "));
		return  "**[[Details]](https://www.anisearch.de/article/%s)** %s".formatted(anisearchId, shopsString);
	}

	@JsonGetter("productType")
	public ProductType getProductType() {
		return productType;
	}

	@JsonGetter("language")
	public Locale getLanguage() {
		return language;
	}

	@JsonGetter("channelId")
	public long getChannelId() {
		return channelId;
	}

	@JsonGetter("creatorId")
	public long getCreatorId() {
		return creatorId;
	}
}
