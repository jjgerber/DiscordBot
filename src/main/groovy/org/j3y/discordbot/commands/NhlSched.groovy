package org.j3y.discordbot.commands

import com.fasterxml.jackson.databind.JsonNode
import groovy.util.logging.Slf4j
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.j3y.discordbot.service.EspnService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.transaction.Transactional

@Component
@Slf4j
class NhlSched extends Command {

    @Autowired
    EspnService espnService

    @Override
    String getCommandKey() {
        return "nhlsched"
    }

    @Override
    boolean isAdminCommand() {
        return false
    }

    @Override
    @Transactional
    synchronized void execute(MessageReceivedEvent event, String... tokens) {
        if (tokens.size() > 2 || tokens.contains("help")) {
            sendMessage(event.getChannel(), "Invalid arguments. Format: `${prefix}nhlsched [# days from today]`")
            return
        }

        int days
        if (tokens.size() <= 1 || !tokens[1].isInteger()) {
            days = 0
        } else {
            days = tokens[1] as int
        }

        JsonNode apiJson = espnService.getNhlScoreboard(days)
        sendMessage event.getChannel(), espnService.buildEventString(apiJson)
    }

}
