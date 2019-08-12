package org.j3y.discordbot.commands

import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.springframework.stereotype.Component

@Component
class Roll extends Command {

    @Override
    String getCommandKey() {
        return "roll"
    }

    @Override
    boolean isAdminCommand() {
        return false
    }

    @Override
    void execute(MessageReceivedEvent event, String... tokens) {
        int maxRoll = 100

        if (tokens.size() > 2) {
            sendMessage(event.getChannel(), "Invalid arguments. Format: `${prefix}roll [maxNumber (optional)]`")
            return
        }

        if (tokens.size() == 2 && !tokens[1].isNumber()) {
            sendMessage(event.getChannel(), "Max number argument must be a number.")
            return
        } else if (tokens.size() == 2) {
            maxRoll = tokens[1] as int
        }

        int rand = new Random().nextInt(maxRoll)
        sendMessage(event.getChannel(), "${event.getAuthor().getName()} rolled (1-${maxRoll}): ${(rand + 1)}")
    }

}
