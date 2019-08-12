package org.j3y.discordbot.commands


import groovy.util.logging.Slf4j
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpStatusCodeException
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder

@Component
@Slf4j
class OsrsStats extends Command {

    RestTemplate client = new RestTemplate()

    List skills = ['Overall', 'Attack', 'Defence', 'Strength', 'HP', 'Ranged' , 'Prayer', 'Magic', 'Cooking',
                   'Woodcutting', 'Fletching', 'Fishing' , 'Firemaking', 'Crafting', 'Smithing', 'Mining',
                   'Herblore', 'Agility', 'Thieving', 'Slayer', 'Farming', 'Runecrafting', 'Hunter', 'Construction']

    @Override
    String getCommandKey() {
        return "osrs"
    }

    @Override
    boolean isAdminCommand() {
        return false
    }

    @Override
    void execute(MessageReceivedEvent event, String... tokens) {
        if (tokens.size() < 2) {
            sendMessage(event.getChannel(), "Invalid arguments. Format: `${prefix}osrs [player]`")
            return
        }

        String player = tokens.drop(1).join('+')

        URI url = UriComponentsBuilder
                .fromHttpUrl("https://secure.runescape.com/m=hiscore_oldschool/index_lite.ws")
                .queryParam("player", player)
                .build().toUri()

        String response
        try {
             response = client.getForObject(url, String.class)
        } catch (HttpStatusCodeException hsce) {
            sendMessage(event.getChannel(), "That player's high scores were not found.")
            return
        }

        String[] respTokens = response.split()
        String botResponse = '```prolog\n'

        botResponse += String.format("%18s%16s%11s\n", "Lvl", "XP", "Rank")

        skills.eachWithIndex{ def skill, int i ->
            String[] stats = respTokens[i].split(',')
            String skillLine = String.format("%12s:  %-5s%,14d%,11d\n", skill, stats[1], stats[2] as long, stats[0] as long)
            botResponse += skillLine
        }

        botResponse += '\n```'

        sendMessage(event.getChannel(), botResponse)
    }

}
