package org.j3y.discordbot.config

import groovy.util.logging.Slf4j
import org.j3y.discordbot.commands.Command
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@Slf4j
class CommandConfig {

    @Bean("commandMap")
    Map<String, Command> getCommandMap(
            List<Command> allCommands,
            @Qualifier("commandIdentifier") String commandIdentifier
    ) {
        Map<String, Command> commandMap = new HashMap<>()

        allCommands.each { command ->
            String commandKey = commandIdentifier + command.getCommandKey()
            commandMap.put(commandKey, command)
        }

        return commandMap
    }

    @Bean("commandIdentifier")
    String getCommandIdentifier(@Value('${bot.command-identifier:@}') String commandIdentifier) {
        return commandIdentifier
    }

}
