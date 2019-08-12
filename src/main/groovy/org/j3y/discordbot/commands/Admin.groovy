package org.j3y.discordbot.commands

import groovy.util.logging.Slf4j
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.springframework.stereotype.Component

import javax.transaction.Transactional

@Component
@Slf4j
class Admin extends Command {

    @Override
    String getCommandKey() {
        return "admin"
    }

    @Override
    boolean isAdminCommand() {
        return true
    }

    @Override
    @Transactional
    void execute(MessageReceivedEvent event, String... tokens) {
        if (tokens.size() != 2) {
            sendMessage(event.getChannel(), "Invalid arguments. Format: `${prefix}admin [userId]`")
            return
        }

        String userId = tokens[1]

        try {
            userService.setUserAdmin(userId, true)
            sendMessage(event.getChannel(), "User `${userId}` was set as an Admin.")
        } catch (IllegalArgumentException iae) {
            event.getChannel().sendMessage(iae.getMessage()).queue()
        }

    }

}
