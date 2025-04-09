package de.simonisinger.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public interface Command {

    String getName();

    SlashCommandData build();

    void handle(SlashCommandInteractionEvent event);
}
