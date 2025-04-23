package de.simonisinger.commands;

import de.simonisinger.channels.DiscordFeedChannel;
import de.simonisinger.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.List;

public class ListFeedsCommand implements Command {
	@Override
	public String getName() {
		return "list_feeds";
	}

	@Override
	public SlashCommandData build() {
		return Commands
				.slash(this.getName(), "Lists all feeds for this channel")
				.setContexts(InteractionContextType.GUILD);
	}

	@Override
	public void handle(SlashCommandInteractionEvent event) {
		List<DiscordFeedChannel> feeds = Main.cache.getFeedsFromChannelId(event.getChannelIdLong());
		StringBuilder embedContent = new StringBuilder();
		for (int i = 0; i < feeds.size(); i++) {
			DiscordFeedChannel feed = feeds.get(i);
			embedContent.append(i+1)
					.append(") Medium: ")
					.append(feed.getProductType().name())
					.append(" Language: ")
					.append(feed.getLanguage().getDisplayName())
					.append('\n');
		}
		event.replyEmbeds(
				new EmbedBuilder().setTitle("List of feeds")
						.setDescription(embedContent.toString())
						.build()
		).setEphemeral(true).queue();
	}
}
