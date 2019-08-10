package org.j3y.discordbot.listener;

import groovy.util.logging.Slf4j;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.j3y.discordbot.commands.Command
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service

import javax.annotation.Nonnull;

@Slf4j
@Component
class CommandListener extends ListenerAdapter {

    Map<String, Command> commandMap
    String commandIdentifier

    @Autowired
    CommandListener(@Qualifier("commandMap") Map<String, Command> commandMap,
                    @Qualifier("commandIdentifier") String commandIdentifier) {
        log.info("Registered Commands: {}", commandMap.keySet())
        log.info("Command Identifier: {}", commandIdentifier)
        this.commandMap = commandMap
        this.commandIdentifier = commandIdentifier
    }

    @Override
    void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return
        }

        String[] tokens = event.getMessage().getContentRaw().split()
        String command = tokens[0]

        if (command.startsWith(commandIdentifier)) {
            Command registeredCommand = commandMap.get(command)
            if (registeredCommand != null) {
                log.info "Received command: {}", command
                registeredCommand.execute(event, tokens)
            } else {
                log.info("Unrecognized Command: {}", command)
            }
        }

    }
}
