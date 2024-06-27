package org.samo_lego.commandspy.mixin;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.world.CommandBlockExecutor;
import net.minecraft.world.World;
import org.apache.logging.log4j.core.lookup.StrSubstitutor;
import org.samo_lego.commandspy.CommandSpy;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;

import static org.samo_lego.commandspy.CommandSpy.MODID;


@Mixin(CommandBlockExecutor.class)
public abstract class MixinCommandBlockExecutor {

    @Shadow
    public abstract String getCommand();

    @Shadow
    public abstract ServerCommandSource getSource();

    // Injection for command block executing commands
    @Inject(method = "execute", at = @At(value = "RETURN"))
    private void execute(World world, CallbackInfoReturnable<Boolean> cir) {
        // Checking if mixin should be enabled todo
        boolean enabled = CommandSpy.config.logging.logCommandBlockCommands;
        String command = this.getCommand();

        if (enabled && CommandSpy.shouldLog(command)) {
            // Getting message style from config
            String message = CommandSpy.config.messages.commandBlockMessageStyle;

            // Getting other info
            String dimension = world.getDimension().effects().getNamespace() + ":" + world.getDimension().effects().getPath();
            int x = (int) (this.getSource().getPosition().x - 0.5);
            int y = (int) this.getSource().getPosition().y;
            int z = (int) (this.getSource().getPosition().z - 0.5);

            // Saving those to hashmap for fancy printing with logger
            Map<String, String> valuesMap = new HashMap<>();
            valuesMap.put("dimension", dimension);
            valuesMap.put("command", command);
            valuesMap.put("x", String.valueOf(x));
            valuesMap.put("y", String.valueOf(y));
            valuesMap.put("z", String.valueOf(z));
            StrSubstitutor sub = new StrSubstitutor(valuesMap);

            // Logging to console
            CommandSpy.logCommand(sub.replace(message), getSource(), MODID + ".log.command_blocks");
        }
    }
}