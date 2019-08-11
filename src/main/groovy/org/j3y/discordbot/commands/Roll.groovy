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
            event.getChannel().sendMessage("Invalid arguments. Format: `!roll [maxNumber (optional)]`").queue()
            return
        }

        if (tokens.size() == 2 && !tokens[1].isNumber()) {
            event.getChannel().sendMessage("Max number argument must be a number.").queue()
            return
        } else if (tokens.size() == 2) {
            maxRoll = tokens[1] as int
        }

        int rand = new Random().nextInt(maxRoll)
        event.getChannel().sendMessage(event.getAuthor().getName() + " rolled (1-" + maxRoll + "): " + (rand + 1)).queue()
    }

}
