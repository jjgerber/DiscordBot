package org.j3y.discordbot.commands

import com.fasterxml.jackson.databind.JsonNode
import groovy.util.logging.Slf4j
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.j3y.discordbot.service.EspnService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.transaction.Transactional
import java.time.LocalDateTime

@Component
@Slf4j
class NflSched extends Command {

    @Autowired
    EspnService espnService

    List<LocalDateTime> weeks = [
            LocalDateTime.parse('2019-01-01T00:00:00'),
            LocalDateTime.parse('2019-09-10T00:00:00'),
            LocalDateTime.parse('2019-09-17T00:00:00'),
            LocalDateTime.parse('2019-09-24T00:00:00'),
            LocalDateTime.parse('2019-10-01T00:00:00'),
            LocalDateTime.parse('2019-10-08T00:00:00'),
            LocalDateTime.parse('2019-10-15T00:00:00'),
            LocalDateTime.parse('2019-10-22T00:00:00'),
            LocalDateTime.parse('2019-10-29T00:00:00'),
            LocalDateTime.parse('2019-11-05T00:00:00'),
            LocalDateTime.parse('2019-11-12T00:00:00'),
            LocalDateTime.parse('2019-11-19T00:00:00'),
            LocalDateTime.parse('2019-11-26T00:00:00'),
            LocalDateTime.parse('2019-12-03T00:00:00'),
            LocalDateTime.parse('2019-12-10T00:00:00'),
            LocalDateTime.parse('2019-12-17T00:00:00'),
            LocalDateTime.parse('2019-12-24T00:00:00')
    ]

    @Override
    String getCommandKey() {
        return "nflsched"
    }

    @Override
    boolean isAdminCommand() {
        return false
    }

    @Override
    @Transactional
    synchronized void execute(MessageReceivedEvent event, String... tokens) {
        if (tokens.size() > 2) {
            sendMessage(event.getChannel(), "Invalid arguments. Format: `${prefix}nflsched [week # (optional)]`")
            return
        }

        String week
        if (tokens.size() == 1) {
            week = getCurrentWeek()
        } else {
            week = tokens[1]
        }

        if (!week.isInteger()) {
            sendMessage(event.getChannel(), "Week argument must be a number.")
            return
        }

        JsonNode apiJson = espnService.getNflScoreboard(week as int)
        sendMessage event.getChannel(), espnService.buildEventString(apiJson)
    }


    private int getCurrentWeek() {
        LocalDateTime curTime = LocalDateTime.now()

        for (int week = weeks.size(); week > 0; week--) {
            LocalDateTime cfbWeek = weeks[week - 1] // subtract 1 because 0-based idx
            if (curTime.isAfter(cfbWeek))
                return week
        }

        return weeks.size()
    }

}
