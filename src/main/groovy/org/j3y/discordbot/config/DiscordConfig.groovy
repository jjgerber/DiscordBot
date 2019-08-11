package org.j3y.discordbot.config

import groovy.util.logging.Slf4j
import net.dv8tion.jda.api.AccountType
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import org.j3y.discordbot.commands.Command
import org.j3y.discordbot.listener.DefaultAdminListener
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@Slf4j
class DiscordConfig {

    @Bean
    JDA getDiscordClient(
            @Value('${discord.token}') String token,
            Command[] commands,
            DefaultAdminListener defaultAdminListener
    ) {
        return new JDABuilder(AccountType.BOT)
                .setToken(token)
                .addEventListeners(commands)
                .addEventListeners(defaultAdminListener)
                .build()
    }

}
