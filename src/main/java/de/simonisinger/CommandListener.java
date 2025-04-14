package de.simonisinger;

import de.simonisinger.commands.*;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CommandListener extends ListenerAdapter {
    //An array list of classes that implement the SlashCommand interface
    private final List<Command> commands = new ArrayList<>();

    public CommandListener() {
        commands.add(new AddChannelCommand());
        commands.add(new RemoveChannelCommand());
        commands.add(new ListFeedsCommand());
        commands.add(new InviteCommand());
        commands.add(new GithubCommand());
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getGuild() == null) return;
        Command command = commands.stream()
                .filter(tmpCommand -> Objects.equals(tmpCommand.getName(), event.getName()))
                .findAny().get();
        command.handle(event);
    }

    public void build(JDA client) {
        CommandListUpdateAction discordCommands = client.updateCommands();
        for (Command command : commands) {
            SlashCommandData commandRequest = command.build();
			//noinspection ResultOfMethodCallIgnored
			discordCommands.addCommands(commandRequest);
        }
        discordCommands.queue();
    }
}
