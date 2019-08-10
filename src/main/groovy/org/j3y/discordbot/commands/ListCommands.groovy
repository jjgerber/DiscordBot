package org.j3y.discordbot.commands

import groovy.util.logging.Slf4j
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

@Component
@Slf4j
class ListCommands implements Command {

    List<Command> allCommands
    String commandIdentifier

    @Autowired
    ListCommands(List<Command> allCommands, @Qualifier("commandIdentifier") String commandIdentifier) {
        this.allCommands = allCommands
        this.commandIdentifier = commandIdentifier
    }

    @Override
    String getCommandKey() {
        return "commands"
    }

    @Override
    void execute(MessageReceivedEvent event, String... tokens) {
        String message = "Available Commands: "
        allCommands.eachWithIndex { cmd, idx ->
            message += "`${commandIdentifier}${cmd.getCommandKey()}`"
            if (idx < allCommands.size() - 1)
                message += ', '
        }

        event.getChannel().sendMessage(message).queue()
    }

}
