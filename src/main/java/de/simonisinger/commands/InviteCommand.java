package de.simonisinger.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class InviteCommand implements Command {
	@Override
	public String getName() {
		return "invite";
	}

	@Override
	public SlashCommandData build() {
		return Commands.slash(
				this.getName(),
				"Post the invitation link for this bot"
		);
	}

	@Override
	public void handle(SlashCommandInteractionEvent event) {
		event.reply(
				"https://discord.com/oauth2/authorize?client_id=%s&scope=bot".formatted(
						event.getJDA().getSelfUser().getIdLong())
				)
				.setEphemeral(true)
				.queue();
	}
}
