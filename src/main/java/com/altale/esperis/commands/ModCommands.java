package com.altale.esperis.commands;

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

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;


public class ModCommands {
    public static void register(){
        CommandRegistrationCallback.EVENT.register(ModCommands::registerCommands);
        CommandRegistrationCallback.EVENT.register(ModCommands::registerCommandsDoubleStep);
        CommandRegistrationCallback.EVENT.register(ModCommands::registerCommandsJump);
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

//    private static void registerCommandsJump(CommandDispatcher<ServerCommandSource> serverCommandSourceCommandDispatcher,CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
//        serverCommandSourceCommandDispatcher.register(
//
//                literal("sa")
//                        .then(argument("triple_jump", StringArgumentType.word())
//                                .executes(context -> {
//                                    ServerPlayerEntity player = context.getSource().getPlayer();
//                                    ServerWorld world = context.getSource().getWorld();
//                                    TripleJump.tripleJump(player, world);
//                                    String skillName = StringArgumentType.getString(context, "triple_jump");
//                                    context.getSource().sendFeedback(() -> Text.literal("TripleJump: " + skillName), false);
//                                    return 1;
//                                })
//                        )
//                        .then(argument("dex_jump", StringArgumentType.word())
//                                .executes(context -> {
//                                    ServerPlayerEntity player = context.getSource().getPlayer();
//                                    ServerWorld world = context.getSource().getWorld();
//                                    DexJump.dexJump(player, world);
//                                    String skillName = StringArgumentType.getString(context, "dex_jump");
//                                    context.getSource().sendFeedback(() -> Text.literal("DexJump: " + skillName), false);
//                                    return 1;
//                                })
//                        )
//        );
//    }

}

