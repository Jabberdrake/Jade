package dev.jabberdrake.charter.handlers.renderers;

import dev.jabberdrake.charter.jade.players.PlayerManager;
import dev.jabberdrake.charter.utils.TextUtils;
import io.papermc.paper.chat.ChatRenderer;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.w3c.dom.Text;

public class RoleplayChatRenderer implements ChatRenderer {

    @Override
    public Component render(Player source, Component sourceDisplayName, Component message, Audience audience) {
        // There are two different formats for messages in the Roleplay channel:
        //   - 0OC (out-of-character) messages
        //   - IC (in-character) messages
        // A message is considered OOC when the first character of the whole message is #.
        String messageAsString = MiniMessage.miniMessage().serialize(message);
        if (messageAsString.startsWith("#")) {
            String treatedMessageAsString = messageAsString.substring(1);
            Component treatedMessage = Component.text(treatedMessageAsString, TextUtils.DARK_ZORBA);
            return Component.text()
                    .append(Component.text("[OOC] ", TextUtils.ZORBA))
                    .append(sourceDisplayName.color(TextUtils.ZORBA).append(Component.text(": ")))
                    .append(treatedMessage)
                    .build();
        }

        // IC messages vary wildly in format depending on exact player input. By default, if a player
        // types, for example, 'test', then the outgoing message should be:
        //      <roleplayName> says: "Test."
        // However, if they end their input with '?', for instance, then the outgoing message should be:
        //      <roleplayName> asks: "Test?"
        // There are a few set-in-stone cases like this. Ending your input with '!' will result in the
        // verb changing to 'exclaims', ending it with '~' will change it to 'flirts'.
        // More generally, if the first word starts with '*' and ends with ':', the verb can be entirely
        // customized:
        //    INPUT: '*mutters: I hate this place...'     -->     <roleplayname> mutters: "I hate this place."
        // Lastly, if the input starts with '*', but there is no subsequent ':', then the outgoing message
        // will be formatted similarly to the vanilla /me command:
        //    INPUT: '*kicks his feet and giggles.'       -->     <roleplayname> kicks his feet and giggles.
        String roleplayNameAsString = PlayerManager.fetchProfile(source.getUniqueId()).getRoleplayName();
        Component roleplayName = Component.text(roleplayNameAsString, TextUtils.LIGHT_BRASS);

        if (messageAsString.startsWith("*") && messageAsString.length() > 1) {
            // e.g.: '*mutters: I hate this place...'     -->     <roleplayName> mutters: "I hate this place."
            if (messageAsString.contains(":") && !messageAsString.endsWith(":")) {
                String[] parts = messageAsString.split(":");

                String verbAsString = " " + parts[0].trim().substring(1) + ": ";
                Component verb = Component.text(verbAsString, TextUtils.LIGHT_ZORBA);
                verb = verb.append(Component.text("\"", TextUtils.ZORBA));

                Component end = Component.text("\"", TextUtils.ZORBA);

                String treatedMessageAsString = parts[1].trim();
                Component treatedMessage = Component.text(treatedMessageAsString, TextUtils.ZORBA);

                return Component.text()
                        .append(roleplayName)
                        .append(verb)
                        .append(treatedMessage)
                        .append(end)
                        .build();
            }

            // e.g.: '*kicks his feet and giggles.'      -->     <roleplayName> kicks his feet and giggles."
            String treatedMessageAsString = " " + messageAsString.substring(1);
            Component treatedMessage = Component.text(treatedMessageAsString, TextUtils.LIGHT_ZORBA);

            return Component.text()
                    .append(roleplayName)
                    .append(treatedMessage)
                    .build();


        }
        Component verb;
        if (messageAsString.endsWith("!!!")) {
            verb = Component.text(" screams: ", TextUtils.LIGHT_ZORBA);
        } else if (messageAsString.endsWith("!!")) {
            verb = Component.text(" shouts: ", TextUtils.LIGHT_ZORBA);
        } else if (messageAsString.endsWith("!")) {
            verb = Component.text(" exclaims: ", TextUtils.LIGHT_ZORBA);
        } else if (messageAsString.endsWith("?")) {
            verb = Component.text(" asks: ", TextUtils.LIGHT_ZORBA);
        } else {
            verb = Component.text(" says: ", TextUtils.LIGHT_ZORBA);
        }

        verb = verb.append(Component.text("\"", TextUtils.ZORBA));
        Component end = Component.text("\"", TextUtils.ZORBA);

        return Component.text()
                .append(roleplayName)
                .append(verb)
                .append(message.color(TextUtils.ZORBA))
                .append(end)
                .build();
    }
}
