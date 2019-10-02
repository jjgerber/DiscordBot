package org.j3y.discordbot.service

import com.fasterxml.jackson.databind.JsonNode

interface EspnService {
    JsonNode getCfbScoreboard(int league, int week)
    JsonNode getNflScoreboard(int week)
    JsonNode getNhlScoreboard(int days)

    String buildEventString(JsonNode apiData)
}
