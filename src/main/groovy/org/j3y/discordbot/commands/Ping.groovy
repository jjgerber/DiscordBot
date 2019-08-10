package org.j3y.discordbot.commands

import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.springframework.stereotype.Component

@Component
class Ping implements Command {

    @Override
    String getCommandKey() {
        return "ping"
    }

    @Override
    void execute(MessageReceivedEvent event, String... tokens) {
        event.getChannel().sendMessage(event.getAuthor().getName() + " - Pong!").queue()
    }

}
