package org.j3y.discordbot.commands

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder

@Component
class GuildRank implements Command {

    RestTemplate client
    ObjectMapper mapper

    GuildRank() {
        client = new RestTemplate()
        mapper = new ObjectMapper()
    }

    @Override
    String getCommandKey() {
        return "guildrank"
    }

    @Override
    void execute(MessageReceivedEvent event, String... tokens) {
        if (tokens.size() < 3) {
            event.getChannel().sendMessage("Invalid arguments. Format: `!guildrank [server] [guildname]`").queue()
            return
        }

        String server = tokens[1]
        String guild = tokens.drop(2).join('+')

        URI url = UriComponentsBuilder
                .fromHttpUrl("https://www.wowprogress.com/guild/us/{realm}/{guild}/json_rank")
                .buildAndExpand(server, guild)
                .toUri()

        String response = client.getForObject(url, String.class)
        JsonNode responseJson = mapper.readTree(response)

        def areaRank = responseJson.path('area_rank').asInt()
        def worldRank = responseJson.path('world_rank').asInt()
        def realmRank = responseJson.path('realm_rank').asInt()

        String botResponse = "**Realm Rank:** `${realmRank}`\n**US Rank:** `${areaRank}`\n**World Rank:** `${worldRank}`"

        event.getChannel().sendMessage(botResponse).queue()
    }

}
