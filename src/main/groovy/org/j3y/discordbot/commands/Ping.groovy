package org.j3y.discordbot.commands

import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.springframework.stereotype.Component

@Component
class Ping extends Command {

    @Override
    String getCommandKey() {
        return "ping"
    }

    @Override
    boolean isAdminCommand() {
        return false
    }

    @Override
    void execute(MessageReceivedEvent event, String... tokens) {
        sendMessage(event.getChannel(), "${event.getAuthor().getName()} - Pong!")
    }

}
