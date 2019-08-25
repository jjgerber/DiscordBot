package org.j3y.discordbot.service

import com.fasterxml.jackson.databind.JsonNode

interface EspnService {

    JsonNode getCfbScoreboard(int league, int week)
    JsonNode getNflScoreboard(int week);

    String buildEventString(JsonNode apiData)

}
