package de.simonisinger;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import java.util.Timer;

public class Main {
	private static JDA discordClient;

	public static JDA getDiscordClient() {
		return discordClient;
	}

	public static final ProductCache cache = new ProductCache();

	public static void main(String[] args) {
        discordClient = JDABuilder.createLight(System.getenv("DISCORD_TOKEN")).build();
		discordClient.addEventListener(new CommandListener());
		try {
			discordClient.awaitReady();
		} catch (InterruptedException e) {
			System.exit(2);
		}

		Timer timer = new Timer();
		timer.scheduleAtFixedRate(cache, 1000, 1000 * 60 * 60 * 24);
		//timer.scheduleAtFixedRate(cache, 1000, 1000 * 60);

		CommandListener commandListener = new CommandListener();
		commandListener.build(discordClient);
    }
}