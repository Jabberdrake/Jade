package dev.jabberdrake.jade.utils.message;

import dev.jabberdrake.jade.utils.JadeTextColor;
import dev.jabberdrake.jade.utils.TextUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

public interface JadeMessageStrategy {

    default Component process(String message, TagResolver... resolvers) {
        return Component.text().color(normalColor())
                .append(prefix())
                .append(MiniMessage.miniMessage().deserialize(
                        TextUtils.preprocess(message),

                        Placeholder.parsed("normal", "<reset>" + JadeTextColor.toMessageTag(normalColor())),
                        Placeholder.styling("highlight", highlightColor()),
                        JadeTextColor.asAdventureTags(),
                        TagResolver.resolver(resolvers)))
                .build();
    }
    TextColor normalColor();
    TextColor highlightColor();

    Component prefix();
}
