package net.slimediamond.espial.listeners;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.slimediamond.espial.Espial;
import net.slimediamond.espial.api.action.BlockAction;
import net.slimediamond.espial.api.action.type.ActionTypes;
import net.slimediamond.espial.api.nbt.NBTApplier;
import net.slimediamond.espial.api.nbt.json.JsonNBTData;
import net.slimediamond.espial.api.nbt.json.JsonSignData;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.entity.ChangeSignEvent;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class SignInteractEvent {
    @Listener(order = Order.LATE)
    public void onSignChangeEvent(ChangeSignEvent event) throws SQLException {
        if (event.cause().root() instanceof Living source) {
            Optional<BlockAction> block = Espial.getInstance().getDatabase().insertAction(ActionTypes.MODIFY, source, event.sign().serverLocation().world().key().formatted(), null, event.sign().serverLocation().createSnapshot());

            if (block.isPresent()) {
                List<Component> newText = event.text().get();
                List<Component> currentFront = event.sign().frontText().lines().get();
                List<Component> currentBack = event.sign().backText().lines().get();

                List<String> frontSerialized;
                List<String> backSerialized;

                if (event.isFrontSide()) {
                    frontSerialized = newText.stream()
                            .map(component -> GsonComponentSerializer.gson().serialize(component))
                            .toList();
                    backSerialized = currentBack.stream()
                            .map(component -> GsonComponentSerializer.gson().serialize(component))
                            .toList();
                } else {
                    frontSerialized = currentFront.stream()
                            .map(component -> GsonComponentSerializer.gson().serialize(component))
                            .toList();
                    backSerialized = newText.stream()
                            .map(component -> GsonComponentSerializer.gson().serialize(component))
                            .toList();
                }

                JsonNBTData nbtData = new JsonNBTData();
                nbtData.setSignData(new JsonSignData(frontSerialized, backSerialized));
                // process other elements
                NBTApplier.applyData(nbtData, event.sign().block(), block.get());
            } else {
                Espial.getInstance().getLogger().error("Could not insert a sign modification event because Database#insertAction returned Optional.empty(). (is the database down?)");
            }
        }
    }
}
