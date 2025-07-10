package com.altale.esperis.commands;

import com.altale.esperis.player_data.money_data.PlayerMoneyComponent;
import com.altale.esperis.skills.dexStatSkill.DexJump;
import com.altale.esperis.skills.lukStatSkill.DoubleStep;
import com.altale.esperis.skills.lukStatSkill.ShadowTeleport;
import com.altale.esperis.skills.lukStatSkill.TripleJump;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

import java.util.Objects;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;


public class ModCommands {
    public static void register(){
        CommandRegistrationCallback.EVENT.register(ModCommands::registerCommands);
        CommandRegistrationCallback.EVENT.register(ModCommands::registerCommandsDoubleStep);
        CommandRegistrationCallback.EVENT.register(ModCommands::registerCommandsJump);
        CommandRegistrationCallback.EVENT.register(ModCommands::registerMoneyData);
    }

    private static void registerCommands(CommandDispatcher<ServerCommandSource> serverCommandSourceCommandDispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        serverCommandSourceCommandDispatcher.register(
                literal("hello")
                        .executes(context -> {
                            context.getSource().sendFeedback(() -> Text.literal("안녕하세요!"), false);
                            return 1;
                        })
        );
    }
    private static void registerCommandsDoubleStep(CommandDispatcher<ServerCommandSource> serverCommandSourceCommandDispatcher,CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        serverCommandSourceCommandDispatcher.register(
                literal("skill_activation")
                        .then(literal("double_step")
                                .executes(context -> {
                                    ServerPlayerEntity player = context.getSource().getPlayer();
                                    ServerWorld world =  context.getSource().getWorld();
                                    DoubleStep.doubleStepCommand(player, world);
                                    return 1;
                                }))
                        .then(literal("shadow_teleport")
                                .executes(context -> {
                                    ServerPlayerEntity player = context.getSource().getPlayer();
                                    ServerWorld world =  context.getSource().getWorld();
                                    ShadowTeleport.doShadowTeleportPlayer(player,world);
                                    return 1;
                                }))

        );
    }
    private static void registerCommandsJump(
            CommandDispatcher<ServerCommandSource> dispatcher,
            CommandRegistryAccess access,
            CommandManager.RegistrationEnvironment env) {

        dispatcher.register(
                literal("sa")
                        .then(literal("triple_jump")
                                .executes(ctx -> {
                                    ServerPlayerEntity player = ctx.getSource().getPlayer();
                                    ServerWorld world = ctx.getSource().getWorld();
                                    TripleJump.tripleJump(player, world);
                                    ctx.getSource().sendFeedback(() -> Text.literal("TripleJump activated!"), false);
                                    return 1;
                                })
                        )
                        .then(literal("dex_jump")
                                .executes(ctx -> {
                                    ServerPlayerEntity player = ctx.getSource().getPlayer();
                                    ServerWorld world = ctx.getSource().getWorld();
                                    DexJump.dexJump(player, world);
                                    ctx.getSource().sendFeedback(() -> Text.literal("DexJump activated!"), false);
                                    return 1;
                                })
                        )
        );
    }
    private static void registerMoneyData(
            CommandDispatcher<ServerCommandSource> dispatcher,
            CommandRegistryAccess access,
            CommandManager.RegistrationEnvironment env) {

        dispatcher.register(
                literal("입금")
                        .then(argument("amount",integer(1))
                                .executes(ctx -> {
                                    ServerPlayerEntity player = ctx.getSource().getPlayer();
                                    PlayerMoneyComponent  component = PlayerMoneyComponent.KEY.get(Objects.requireNonNull(player));
                                    System.out.println(component);
                                    int[] a= component.deposit(ctx.getArgument("amount", Integer.class));
                                    String text= String.format("%d esp 입금 완료, 현재 잔고: %d esp",a[1],a[0]);
                                    player.sendMessage(Text.literal(text), false);
                                    ctx.getSource().sendFeedback(() -> Text.literal(text), false);
                                    return 1;
                                })
                        )
        );
        dispatcher.register(
                literal("출금")
                        .then(argument("amount",integer(1))
                                .executes(ctx -> {
                                    ServerPlayerEntity player = ctx.getSource().getPlayer();
                                    PlayerMoneyComponent  component = PlayerMoneyComponent.KEY.get(Objects.requireNonNull(player));
                                    int[] a= component.withdraw(ctx.getArgument("amount", Integer.class));
                                    String text= String.format("%d esp 출금 완료, 현재 잔고: %d esp",a[1],a[0]);
                                    player.sendMessage(Text.literal(text), false);
                                    ctx.getSource().sendFeedback(() -> Text.literal(text), false);
                                    return 1;
                                })
                        )
        );
    }


}

