package de.simonisinger.commands;

import de.simonisinger.FeedChannel;
import de.simonisinger.Main;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.List;
import java.util.Objects;

public class RemoveChannelCommand implements Command {
	@Override
	public String getName() {
		return "remove_feed";
	}

	@Override
	public SlashCommandData build() {
		return Commands
				.slash(this.getName(), "Remove a feed from a channel")
				.setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MODERATE_MEMBERS))
				.addOptions(
						new OptionData(OptionType.INTEGER, "index", "Index of the Feed from the channel", true)
				);
	}

	@Override
	public void handle(SlashCommandInteractionEvent event) {
		long channelId = event.getChannelIdLong();
		List<FeedChannel> feeds = Main.cache.getFeedsFromChannelId(channelId);
		int index;
		try {
			index = Objects.requireNonNull(event.getOption("index")).getAsInt();
		} catch (Exception e) {
			return;
		}
		// retrieving and removing the product
		if (feeds.size() > index - 1) {
			Main.cache.removeFeed(feeds.get(index - 1));
		}

		event.reply("Removed Feed").setEphemeral(true).queue();
	}
}
