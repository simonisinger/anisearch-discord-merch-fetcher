package de.simonisinger.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class GithubCommand implements Command {
	@Override
	public String getName() {
		return "github";
	}

	@Override
	public SlashCommandData build() {
		return Commands.slash(this.getName(), "Link to the Github repository");
	}

	@Override
	public void handle(SlashCommandInteractionEvent event) {
		event.reply("https://github.com/simonisinger/anisearch-discord-merch-fetcher").setEphemeral(true).queue();
	}
}
