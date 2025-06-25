package dev.jabberdrake.jade.chat;

import dev.jabberdrake.jade.JadeConfig;
import dev.jabberdrake.jade.players.JadePlayer;
import dev.jabberdrake.jade.players.PlayerManager;
import dev.jabberdrake.jade.players.PlayerSettings;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import static dev.jabberdrake.jade.utils.TextUtils.info;

public class RandomAdviceTask extends BukkitRunnable {

    private static int lastAdviceIndex = 0;
    private static final String PREFIX = "<highlight>Did you know?</highlight> ";
    private static final String[] ADVICE = {
            "<livingmetal>Food</livingmetal> is used as a currency to claim chunks for settlements, at a rate of <highlight>" + JadeConfig.chunkCost + "</highlight> food per chunk!",
            "There is absolutely no upkeep on realms! <livingmetal>Food</livingmetal> is only used to purchase new claims, not to upkeep already claimed ones!",
            "There is no limit on <dark_mana>settlement</dark_mana> and <dark_brass>nation</dark_brass> membership! You can be part of as many settlements and nations as you'd like!",
            "Elements like <dark_mana>settlements</dark_mana>, <dark_brass>nations</dark_brass> and <highlight>titles</highlight> have two types of names: <highlight>reference names</highlight> and <gradient:red:blue>display names</gradient>!",
            "Display names are generated using the <highlight>MiniMessage</highlight> format! To preview how your display name is going to look, you can use <coral><click:open_url:https://webui.advntr.dev><hover:show_text:'<zorba>Click to open the web editor!'>this site</hover></click></coral>!",
            "System messages displayed in <coral>this shade of orange</coral> usually do interesting things when you <hover:show_text:'<red>Boo!'><coral>hover</coral></hover> over them or <click:open_url:https://www.youtube.com/watch?v=dQw4w9WgXcQ><coral>click</coral></click> on them!",
            "There are four different options you can use when claiming chunks for your settlement: default, <coral><click:suggest_command:'/settlement claim square 1'><hover:show_text:'<zorba>Click to see the command!'>square</hover></click></coral>, <coral><click:suggest_command:'/settlement claim fill'><hover:show_text:'<zorba>Click to see the command!'>fill</hover></click></coral> and <coral><click:suggest_command:'/settlement claim auto'><hover:show_text:'<zorba>Click to see the command!'>auto</hover></click></coral>!",
            "<bold><livingmetal>Jade</livingmetal></bold> adds a bunch of <salmon>new</salmon> <rosemetal>custom</rosemetal> <tyrian_purple>colors</tyrian_purple>, which you can use in <highlight>display names</highlight> for many different game elements! You can even make <gradient:wine_red:audalad_cyan>gradients</gradient> with them!",
            "Tired of having only <red>boring</red>, <blue>solid</blue> colors in your display text? Try <gradient:red:blue>gradients</gradient>! Gradients can be easily declared with a tag: the one you see in this message corresponds to the tag <chrome><tag_open>gradient:red:blue<tag_close></chrome>!",
            "Got <i>way</i> too much <highlight>Rotten Flesh</highlight> collecting dust in your chests? Try treating it into <highlight>Leather</highlight> by shoving it all in a furnace!",
            "If you die over the void or in an obstructed location, the game will spawn a <highlight>virtual grave</highlight> for you, which can be opened via <coral><click:suggest_command:'/grave list'><hover:show_text:'<zorba>Click to see the command!'>this menu</hover></click></coral>!",
            "Graves are <red>NOT</red> protected against other players! If someone else kills you, they can loot your grave!",
            "You can quickly switch to a different <highlight>chat channel</highlight> by typing its associated channel tag! For example, if you want to start speaking in the <light_rosemetal>Roleplay</light_rosemetal> channel, type \"<coral><click:suggest_command:'rp:'><hover:show_text:'<zorba>Click to copy this to the chat!'><insert:'rp:'>rp:</hover></click></coral>\"!",
            "You can swap in and out of <light_rosemetal>Roleplay</light_rosemetal> chat by typing the <coral><click:suggest_command:'/toggleroleplay'><hover:show_text:'<zorba>Click to see the command!'>toggleroleplay</hover></click></coral> command!",
            "While in <light_rosemetal>Roleplay</light_rosemetal> chat, you can customize your character's action by adding an <highlight>asterisk</highlight> before your message, like so: \"<white>*waves: Good morning!</white>\"",
            "Walking on <highlight>roads</highlight> can boost your movement speed several times over!",
            "Ever wanted to subdivide your towns and cities into neat districts and zones, each with their own set of <highlight>trusted members</highlight>? Try using <coral><click:suggest_command:'/settlement areas'><hover:show_text:'<zorba>Click to see the command!'>areas</hover></click></coral>!"
    };

    @Override
    public void run() {
        if (!JadeConfig.sayRandomAdvice) return;

        int newIndex = generateNewIndex();
        String advice = ADVICE[newIndex];

        for (Player player : Bukkit.getOnlinePlayers()) {
            JadePlayer jadePlayer = PlayerManager.asJadePlayer(player.getUniqueId());
            if (!jadePlayer.getSetting(PlayerSettings.MUTE_RANDOM_ADVICE)) {
                player.sendMessage(info(PREFIX + advice));
            }
        }
        lastAdviceIndex = newIndex;
    }

    public int generateNewIndex() {
        int index;
        do {
            index = (int) (Math.random() * ADVICE.length);
        } while (index == lastAdviceIndex);

        return index;
    }
}
