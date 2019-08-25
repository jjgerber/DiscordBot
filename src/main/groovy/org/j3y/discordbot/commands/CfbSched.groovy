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
class CfbSched extends Command {

    @Autowired
    EspnService espnService

    List<LocalDateTime> weeks = [
            LocalDateTime.parse('2019-01-01T00:00:00'),
            LocalDateTime.parse('2019-09-01T00:00:00'),
            LocalDateTime.parse('2019-09-08T00:00:00'),
            LocalDateTime.parse('2019-09-15T00:00:00'),
            LocalDateTime.parse('2019-09-22T00:00:00'),
            LocalDateTime.parse('2019-09-29T00:00:00'),
            LocalDateTime.parse('2019-10-06T00:00:00'),
            LocalDateTime.parse('2019-10-13T00:00:00'),
            LocalDateTime.parse('2019-10-20T00:00:00'),
            LocalDateTime.parse('2019-10-27T00:00:00'),
            LocalDateTime.parse('2019-11-03T00:00:00'),
            LocalDateTime.parse('2019-11-10T00:00:00'),
            LocalDateTime.parse('2019-11-17T00:00:00'),
            LocalDateTime.parse('2019-11-24T00:00:00'),
            LocalDateTime.parse('2019-12-01T00:00:00')
    ]

    Map<String, Integer> leagueMap = [
            'top25': 0,
            'acc': 1,
            'american': 151,
            'big12': 4,
            'b12': 4,
            'big10': 5,
            'b10': 5,
            'sec': 8,
            'pac12': 9,
            'p12': 9,
            'mac': 15,
            'independent': 18
    ]

    @Override
    String getCommandKey() {
        return "cfbsched"
    }

    @Override
    boolean isAdminCommand() {
        return false
    }

    @Override
    @Transactional
    synchronized void execute(MessageReceivedEvent event, String... tokens) {
        if (tokens.size() > 3) {
            sendMessage(event.getChannel(), "Invalid arguments. Format: `${prefix}cfbsched [league] [week # (optional)]`")
            return
        }

        String leagueStr = tokens.size() > 1 ? tokens[1].toLowerCase().trim() : 'top25'
        int league = leagueMap.getOrDefault(leagueStr, -1)
        if (league == -1) {
            sendMessage(event.getChannel(), "That league was not recognized.")
            return
        }

        String week
        if (tokens.size() <= 2) {
            week = getCurrentWeek()
        } else {
            week = tokens[2]
        }

        if (!week.isInteger()) {
            sendMessage(event.getChannel(), "Week argument must be a number.")
            return
        }

        JsonNode apiJson = espnService.getCfbScoreboard(league, week as int)
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
