package com.altale.esperis.commands;

import com.altale.esperis.player_data.level_data.PlayerLevelComponent;
import com.altale.esperis.player_data.money_data.PlayerMoneyComponent;
import com.altale.esperis.player_data.stat_data.StatManager;
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
                literal("입금")//지폐 아이템 구현-> 사용 구현-> 기능 옮기고 삭제하기
                        .then(argument("amount",integer(1))
                                .executes(ctx -> {
                                    ServerPlayerEntity player = ctx.getSource().getPlayer();
                                    PlayerMoneyComponent  component = PlayerMoneyComponent.KEY.get(Objects.requireNonNull(player));
                                    int[] a= component.deposit(ctx.getArgument("amount", Integer.class));
                                    String text= String.format("%d esp 입금 완료, 현재 잔고: %d esp",a[1],a[0]);
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
                                    if(component.canWithdraw(ctx.getArgument("amount", Integer.class))
                                    && player.getInventory().getEmptySlot()!=-1){
                                        int[] a= component.withdraw(ctx.getArgument("amount", Integer.class));
                                        //아이템 구현-> 해당 명령어 치면 아이템 지급
                                        String text= String.format("%d esp 출금 완료, 현재 잔고: %d esp",a[1],a[0]);
                                        ctx.getSource().sendFeedback(() -> Text.literal(text), false);
                                        return 1;
                                    } else if(!component.canWithdraw(ctx.getArgument("amount", Integer.class))){
                                        int currentBalance = component.getBalance();
                                        String text = String.format("출금 불가능: 현재 잔고:%d esp", currentBalance);
                                        ctx.getSource().sendFeedback(() -> Text.literal(text), false);
                                        return 0;
                                    } else if(player.getInventory().getEmptySlot()==-1){
                                        ctx.getSource().sendFeedback(() -> Text.literal("인벤토리에 빈 슬롯이 없습니다."), false);
                                        return 0;
                                    }
                                    return 0;
                                }
                                )
                        )
        );
        dispatcher.register(
                literal("경험치초기화")//FIXME 디버깅용임
                        .executes(ctx -> {
                                    ServerPlayerEntity player = ctx.getSource().getPlayer();
                                    PlayerLevelComponent component = PlayerLevelComponent.KEY.get(Objects.requireNonNull(player));
                                    component.setLevel(1);
                                    component.setCurrentExp(0);
                                    component.setMaxExp(50);
                                    StatManager.statUpdate(player);
                                    String text= String.format("초기화 완료");
                                    ctx.getSource().sendFeedback(() -> Text.literal(text), false);
                                    return 1;
                                })

        );
    }


}

