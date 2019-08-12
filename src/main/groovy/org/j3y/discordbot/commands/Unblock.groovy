package org.j3y.discordbot.commands

import groovy.util.logging.Slf4j
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.springframework.stereotype.Component

import javax.transaction.Transactional

@Component
@Slf4j
class Unblock extends Command {

    @Override
    String getCommandKey() {
        return "unblock"
    }

    @Override
    boolean isAdminCommand() {
        return true
    }

    @Override
    @Transactional
    void execute(MessageReceivedEvent event, String... tokens) {
        if (tokens.size() != 2) {
            sendMessage(event.getChannel(), "Invalid arguments. Format: `${prefix}unblock [userId]`")
            return
        }

        String userId = tokens[1]

        try {
            userService.setUserBlocked(userId, false)
            sendMessage(event.getChannel(), "User `${userId}` was unblocked.")
        } catch (IllegalArgumentException iae) {
            sendMessage(event.getChannel(), iae.getMessage())
        }
    }

}
