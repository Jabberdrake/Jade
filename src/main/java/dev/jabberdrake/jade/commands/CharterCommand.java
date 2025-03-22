package dev.jabberdrake.jade.commands;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

public class CharterCommand {

    // COMMAND TREE
    // /charter
    //  ┣━━ help
    //  ┣━┳ admin
    //  ┃ ┣━━ sanitycheck
    //  ┃ ┗━━ ...
    //  ┣━┳ town
    //  ┃ ┣━━ create
    //  ┃ ┣━━ delete
    //  ┃ ┣━━ invite
    //  ┃ ┣━━ kick
    //  ┃ ┣━━ manage
    //  ┃ ┗━━ lookup
    //  ┗━┳ nation
    //    ┗━━ ...

    public static LiteralCommandNode<CommandSourceStack> buildCommand(final String label) {
        return Commands.literal(label)
                    .executes(CharterHelpCommand::runCommand)
                .then(AdminCommand.buildCommand("admin"))
                .then(SettlementCommand.buildCommand("settlement"))
                .then(CharterHelpCommand.buildCommand("help"))
                .build();
    }
}
