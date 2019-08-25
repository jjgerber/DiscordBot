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

    DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern('MM/dd hh:mm a z')

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

        events.each { event ->
            String name = event.path("shortName").asText()
            String dateStr = event.path("date").asText()
            int period = event.path("status").path("period").asInt()
            ZonedDateTime date = ZonedDateTime.parse(dateStr).withZoneSameInstant(ZoneOffset.UTC)
            date = date.withZoneSameInstant(ZoneId.of("America/Chicago"))

            sb.append String.format('\n%-20s%s', name, date.format(FORMATTER))

            if (period > 0) {
                // Game is running -> Get scores.
                JsonNode competitors = event.path("competitions").path(0).path("competitors")
                String homeScore = competitors.path(0).path("score").asText()
                String awayScore = competitors.path(1).path("score").asText()
                String status = event.path("status").path("type").path("shortDetail").asText()

                sb.append String.format('%18s%5s-%-10s', "[${status}]", awayScore, homeScore)
            }
        }

        sb.append "\n```"
        return sb.toString()
    }
}
