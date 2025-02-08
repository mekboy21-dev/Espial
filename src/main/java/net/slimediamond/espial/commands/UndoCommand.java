package net.slimediamond.espial.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.slimediamond.espial.Espial;
import net.slimediamond.espial.EspialTransaction;
import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.entity.living.player.Player;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ListIterator;

public class UndoCommand implements CommandExecutor {
    @Override
    public CommandResult execute(CommandContext context) throws CommandException {
        if (!Espial.transactions.containsKey(context.cause().root())) {
            context.sendMessage(Espial.prefix.append(Component.text("There is nothing to undo.").color(NamedTextColor.WHITE)));
            return CommandResult.success();
        } else {
            ArrayList<EspialTransaction> transactions = Espial.transactions.get(context.cause().root());

            if (transactions.isEmpty()) {
                System.out.println("transactions empty");
                context.sendMessage(Espial.prefix.append(Component.text("There is nothing to undo.").color(NamedTextColor.WHITE)));
                return CommandResult.success();
            }

            ListIterator<EspialTransaction> iterator = transactions.listIterator(transactions.size());
            while (iterator.hasPrevious()) {
                EspialTransaction transaction = iterator.previous();
                // we should be going back in the tree for non-undone changes only
                if (transaction.isUndone()) continue;

                // now we have it
                try {
                    transaction.undo();
                } catch (SQLException e) {
                    context.sendMessage(Espial.prefix.append(Component.text("SQLException occurred. This is very very bad. Please verify blocks are actually being logged and if not complain to someone immediately.")));
                    break;
                }

                context.sendMessage(Espial.prefix.append(Component.text(transaction.getIds().size()).append(Component.text(" action(s) have been undone.")).color(NamedTextColor.WHITE)));
                return CommandResult.success();
            }

            context.sendMessage(Espial.prefix.append(Component.text("There is nothing to undo.").color(NamedTextColor.WHITE)));
        }

        return CommandResult.success();
    }
}
