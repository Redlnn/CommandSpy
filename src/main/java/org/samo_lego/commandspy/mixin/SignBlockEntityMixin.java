package org.samo_lego.commandspy.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.logging.log4j.core.lookup.StrSubstitutor;
import org.jetbrains.annotations.Nullable;
import org.samo_lego.commandspy.CommandSpy;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.HashMap;
import java.util.Map;

import static org.samo_lego.commandspy.CommandSpy.MODID;
import static org.samo_lego.commandspy.CommandSpy.config;

@Mixin(SignBlockEntity.class)
public abstract class SignBlockEntityMixin {
    @Unique
    private final SignBlockEntity self = (SignBlockEntity) (Object) this;

    @Shadow
    private static ServerCommandSource createCommandSource(@Nullable PlayerEntity player, World world, BlockPos pos) {
        throw new AssertionError();
    }

    @Inject(
            method = "runCommandClickEvent",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/command/CommandManager;executeWithPrefix(Lnet/minecraft/server/command/ServerCommandSource;Ljava/lang/String;)V"
            ),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void catchSignCommand(PlayerEntity player, World world, BlockPos pos, boolean front, CallbackInfoReturnable<Boolean> cir, @Local ClickEvent clickEvent) {
        if (config.logging.logSignCommands) {

            // Getting message style from config
            String message = CommandSpy.config.messages.signMessageStyle;

            // Getting other info
            String dimension = world.getDimension().effects().getNamespace() + ":" + world.getDimension().effects().getPath();
            int x = pos.getX();
            int y = pos.getY();
            int z = pos.getZ();

            // Saving those to hashmap for fancy printing with logger
            Map<String, String> valuesMap = new HashMap<>();
            valuesMap.put("dimension", dimension);
            valuesMap.put("command", clickEvent.getValue());
            valuesMap.put("x", String.valueOf(x));
            valuesMap.put("y", String.valueOf(y));
            valuesMap.put("z", String.valueOf(z));
            StrSubstitutor sub = new StrSubstitutor(valuesMap);

            // Logging to console
            CommandSpy.logCommand(sub.replace(message), createCommandSource(player, world, pos), MODID + ".log.signs");
        }
    }
}
