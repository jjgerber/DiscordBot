package org.j3y.discordbot.service

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import groovy.util.logging.Slf4j
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder

import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Slf4j
@Service
class DefaultEspnService implements EspnService {

    RestTemplate client = new RestTemplate()

    DateTimeFormatter FORMATTER_DATE = DateTimeFormatter.ofPattern('MM/dd')
    DateTimeFormatter FORMATTER_TIME = DateTimeFormatter.ofPattern('h:mm a z')

    @Override
    @Cacheable("cfbScoreboards")
    JsonNode getCfbScoreboard(int league, int week) {
        log.info "Updating CFB Cache."
        UriComponentsBuilder espnUriBuilder =
                UriComponentsBuilder.fromHttpUrl('https://site.api.espn.com/apis/site/v2/sports/football/college-football/scoreboard' +
                    '?lang=en&region=us&calendartype=blacklist&limit=300&dates=2019&seasontype=2')
                    .queryParam('week', week)

        if (league != 0) {
            espnUriBuilder.queryParam('groups', league)
        }

        return client.getForObject(espnUriBuilder.build().toUri(), JsonNode.class)
    }

    @Override
    @Cacheable("nflScoreboards")
    JsonNode getNflScoreboard(int week) {
        log.info "Updating NFL Cache."
        UriComponentsBuilder espnUriBuilder = UriComponentsBuilder.fromHttpUrl('https://site.api.espn.com/apis/site/v2/sports/football/nfl/scoreboard' +
                '?lang=en&region=us&calendartype=blacklist&limit=100&dates=2019&seasontype=2')
                .queryParam('week', week)

        return client.getForObject(espnUriBuilder.build().toUri(), JsonNode.class)
    }

    @CacheEvict(value = ['cfbScoreboards', 'nflScoreboards'], allEntries = true)
    @Scheduled(fixedRate = 60000l)
    void evictCaches() {
        log.debug "Evicted ESPN caches."
    }

    @Override
    String buildEventString(JsonNode apiData) {
        ArrayNode events = (ArrayNode) apiData.path("events")

        if (events.isEmpty()) {
            return "No games were found."
        }

        StringBuilder sb = new StringBuilder("```prolog")

        String currentDay = ''
        events.each { event ->
            String dateStr = event.path("date").asText()
            int period = event.path("status").path("period").asInt()
            ZonedDateTime date = ZonedDateTime.parse(dateStr).withZoneSameInstant(ZoneOffset.UTC)
            date = date.withZoneSameInstant(ZoneId.of("America/New_York"))
            ZonedDateTime centralDate = date.withZoneSameInstant(ZoneId.of("America/Chicago"))

            // Add 1 (go to EST timezone) when checking what day the event is cause ESPN gives
            // TBD events a date of midnight EST
            String eventDay = date.getDayOfWeek().toString()
            if (!currentDay.equals(eventDay)) {
                if (currentDay != '')
                    sb.append "\n"
                currentDay = eventDay
                sb.append "\n${eventDay} - ${date.format(FORMATTER_DATE)}"
                sb.append '\n-------------------------------------------------'
            }

            JsonNode competition = event.path("competitions").path(0)
            String network = competition.path("broadcasts").path(0)
                    .path("names").path(0).asText("TBD")

            JsonNode away = competition.path("competitors").path(1)
            String awayTeam = away.path("team").path("abbreviation").asText()
            boolean isAwayWinner = away.path("winner").asBoolean(false)
            int awayRank = away.path("curatedRank").path("current").asInt(99)
            if (isAwayWinner)
                awayTeam = "${awayTeam}*"
            if (awayRank <= 25)
                awayTeam = "${awayRank} ${awayTeam}"

            JsonNode home = competition.path("competitors").path(0)
            String homeTeam = home.path("team").path("abbreviation").asText()
            boolean isHomeWinner = home.path("winner").asBoolean(false)
            int homeRank = home.path("curatedRank").path("current").asInt(99)
            if (isHomeWinner)
                homeTeam = "*${homeTeam}"
            if (homeRank <= 25)
                homeTeam = "${homeTeam} ${homeRank}"

            String eventDate = date.format(FORMATTER_DATE)
            String status = "TBD"
            if (period > 0) {
                // Game is running -> Get scores.
                JsonNode competitors = event.path("competitions").path(0).path("competitors")
                String homeScore = competitors.path(0).path("score").asText()
                String awayScore = competitors.path(1).path("score").asText()
                String statusTxt = event.path("status").path("type").path("shortDetail").asText()

                status = String.format('%3s-%-4s%-12s', awayScore, homeScore, statusTxt)
            } else if (date.getHour() != 0) {
                status = String.format('%12s', centralDate.format(FORMATTER_TIME))
            }

            sb.append String.format('\n%-8s%10s @ %-10s %-7s', "[${network}]", awayTeam, homeTeam, status)
        }

        sb.append "\n```"
        return sb.toString()
    }
}
