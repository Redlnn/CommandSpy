package org.samo_lego.commandspy.mixin;

import net.minecraft.entity.EntityPose;
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.apache.logging.log4j.core.lookup.StrSubstitutor;
import org.samo_lego.commandspy.CommandSpy;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

import static org.samo_lego.commandspy.CommandSpy.MODID;
import static org.samo_lego.commandspy.CommandSpy.config;


// Mojang: ServerGamePacketListenerImpl
// Yarn: ServerPlayNetworkHandler
@Mixin(ServerPlayNetworkHandler.class)
public abstract class MixinServerPlayNetworkHandler {

    @Unique
    private final ServerPlayNetworkHandler self = (ServerPlayNetworkHandler) (Object) this;


    // Injection for player chatting
    // Mojang: handleChatCommand
    // Yarn: onCommandExecution
    @Inject(
            method = "onCommandExecution",
            at = @At(value = "RETURN")
    )
    private void onCommandExecution(CommandExecutionC2SPacket packet, CallbackInfo ci) {
        boolean enabled = config.logging.logPlayerCommands;
        String command = packet.command();

        if (enabled && CommandSpy.shouldLog(command)) {
            // Message style from config
            String message = config.messages.playerMessage;
            ServerPlayerEntity player = self.getPlayer();

            // Other info, later optionally appended to message
            // Mojang: getScoreboardName
            // Yarn: getNameForScoreboard
            String playername = player.getNameForScoreboard();
            String uuid = player.getUuidAsString();

            // Saving those to hashmap for fancy printing with logger
            Map<String, String> valuesMap = new HashMap<>();
            valuesMap.put("playername", playername);
            valuesMap.put("uuid", uuid);
            valuesMap.put("command", command);
            valuesMap.put("dimension", player.getBaseDimensions(EntityPose.STANDING).toString());

            StrSubstitutor sub = new StrSubstitutor(valuesMap);
            // Logging to console
            CommandSpy.logCommand(sub.replace(message), player.getCommandSource(), MODID + ".log.players");
        }
    }
}
