package de.simonisinger.commands;

import de.simonisinger.FeedChannel;
import de.simonisinger.Main;
import de.simonisinger.ProductCache;
import de.simonisinger.ProductType;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;


public class AddChannelCommand implements Command {
	@Override
	public String getName() {
		return "add_feed";
	}

	@Override
	public SlashCommandData build() {
		return Commands.slash(this.getName(), "Add a channel to the channel list")
				.setContexts(InteractionContextType.GUILD)
				.addOptions(
						new OptionData(
								OptionType.STRING,
								"language",
								"The language filter for the feed",
								true
						),

						new OptionData(
								OptionType.STRING,
								"medium",
								"The medium filter for the feed",
								true
						).addChoices(
								Arrays.stream(ProductType.values())
										.map(type -> new net.dv8tion.jda.api.interactions.commands.Command.Choice(type.name(), type.name()))
										.toList()
						)
				);
	}

	@Override
	public void handle(SlashCommandInteractionEvent event) {
		String language = Objects.requireNonNull(event.getOption("language")).getAsString();
		String medium = Objects.requireNonNull(event.getOption("medium")).getAsString();

		long channelId = event.getChannelIdLong();

		// add feed to productcache
		Main.cache.addFeed(
				new FeedChannel(
						channelId,
						Locale.forLanguageTag(language),
						ProductType.valueOf(medium)
				)
		);

		event.reply("Feed created").setEphemeral(true).queue();
	}
}
