package org.j3y.discordbot.commands


import groovy.util.logging.Slf4j
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.springframework.stereotype.Component

import javax.transaction.Transactional

@Component
@Slf4j
class Block extends Command {

    @Override
    String getCommandKey() {
        return "block"
    }

    @Override
    boolean isAdminCommand() {
        return true
    }

    @Override
    @Transactional
    void execute(MessageReceivedEvent event, String... tokens) {
        if (tokens.size() != 2) {
            event.getChannel().sendMessage("Invalid arguments. Format: `!block [userId]`").queue()
            return
        }

        String userId = tokens[1]

        try {
            userService.setUserBlocked(userId, true)
            event.getChannel().sendMessage("User `" + userId + "` was blocked.").queue()
        } catch (IllegalArgumentException iae) {
            event.getChannel().sendMessage(iae.getMessage()).queue()
        }

    }

}
