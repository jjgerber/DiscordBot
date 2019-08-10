package org.j3y.discordbot.config

import groovy.util.logging.Slf4j
import net.dv8tion.jda.api.AccountType
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import org.j3y.discordbot.listener.CommandListener
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@Slf4j
class DiscordConfig {

    @Bean
    JDA getDiscordClient(
            @Value('${discord.token}') String token,
            CommandListener commandListener
    ) {
        return new JDABuilder(AccountType.BOT)
                .setToken(token)
                .addEventListeners(commandListener)
                .build()
    }

}
