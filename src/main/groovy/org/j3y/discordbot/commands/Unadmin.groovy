package org.j3y.discordbot.commands

import groovy.util.logging.Slf4j
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.springframework.stereotype.Component

import javax.transaction.Transactional

@Component
@Slf4j
class Unadmin extends Command {

    @Override
    String getCommandKey() {
        return "unadmin"
    }

    @Override
    boolean isAdminCommand() {
        return true
    }

    @Override
    @Transactional
    void execute(MessageReceivedEvent event, String... tokens) {
        if (tokens.size() != 2) {
            event.getChannel().sendMessage("Invalid arguments. Format: `!unadmin [userId]`").queue()
            return
        }

        String userId = tokens[1]

        try {
            userService.setUserAdmin(userId, false)
            event.getChannel().sendMessage("User `" + userId + "` was removed as an Admin.").queue()
        } catch (IllegalArgumentException iae) {
            event.getChannel().sendMessage(iae.getMessage()).queue()
        }

    }

}
