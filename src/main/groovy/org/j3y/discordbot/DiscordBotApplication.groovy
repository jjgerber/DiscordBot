package org.j3y.discordbot

import groovy.util.logging.Slf4j
import net.dv8tion.jda.api.AccountType
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import org.j3y.discordbot.listener.CommandListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
@Slf4j
class DiscordBotApplication {
	static void main(String[] args) {
		SpringApplication.run(DiscordBotApplication, args)
	}
}
