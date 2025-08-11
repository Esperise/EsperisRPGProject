package com.altale.esperis.commands;

import com.altale.esperis.items.ModItems;
import com.altale.esperis.player_data.level_data.PlayerLevelComponent;
import com.altale.esperis.player_data.money_data.PlayerMoneyComponent;
import com.altale.esperis.player_data.skill_data.PlayerSkillComponent;
import com.altale.esperis.player_data.skill_data.SkillsId;
import com.altale.esperis.player_data.stat_data.StatComponents.PlayerFinalStatComponent;
import com.altale.esperis.player_data.stat_data.StatComponents.PlayerPointStatComponent;
import com.altale.esperis.player_data.stat_data.StatManager;
import com.altale.esperis.player_data.stat_data.StatPointType;
import com.altale.esperis.player_data.stat_data.StatType;
import com.altale.esperis.skills.statSkills.dexStatSkill.DexJump;
import com.altale.esperis.skills.statSkills.lukStatSkill.TripleJump;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Objects;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;


public class ModCommands {
    public static void register(){
        CommandRegistrationCallback.EVENT.register(ModCommands::registerCommands);
        CommandRegistrationCallback.EVENT.register(ModCommands::registerCommandsJump);
        CommandRegistrationCallback.EVENT.register(ModCommands::registerMoneyData);
        CommandRegistrationCallback.EVENT.register(ModCommands::setSkillKeyBinding);

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
                                        ItemStack moneyStack =new ItemStack(ModItems.MONEY);
                                        moneyStack.getOrCreateNbt().putInt("amount", a[1]);
                                        String text= String.format("%d esp 출금 완료, 현재 잔고: %d esp",a[1],a[0]);
                                        moneyStack.setCustomName(
                                                Text.literal(a[1]+" esp").formatted(Formatting.AQUA)
                                        );
                                        player.giveItemStack(moneyStack);
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
                                    PlayerPointStatComponent pointStatComponent = PlayerPointStatComponent.KEY.get(player);
                                    component.setLevel(1);
                                    component.setCurrentExp(0);
                                    component.setMaxExp(50);
                                    pointStatComponent.setSP(StatPointType.UnusedSP,0);
                                    pointStatComponent.setSP(StatPointType.TotalSP,0);
                                    pointStatComponent.setSP(StatPointType.UnusedSP,0);
                                    StatManager.statUpdate(player);
                                    String text= String.format("초기화 완료");
                                    ctx.getSource().sendFeedback(() -> Text.literal(text), false);
                                    return 1;
                                })

        );
        dispatcher.register(
                literal("clearAll")//FIXME 디버깅용임
                        .executes(ctx -> {
                                    ServerPlayerEntity player = ctx.getSource().getPlayer();
                                    PlayerLevelComponent component = PlayerLevelComponent.KEY.get(Objects.requireNonNull(player));
                                    PlayerPointStatComponent pointStatComponent = PlayerPointStatComponent.KEY.get(player);
                                    PlayerFinalStatComponent finalStatComponent = PlayerFinalStatComponent.KEY.get(player);
                                    component.setLevel(1);
                                    component.setCurrentExp(0);
                                    component.setMaxExp(50);
                                    pointStatComponent.setSP(StatPointType.UnusedSP,5);
                                    pointStatComponent.setSP(StatPointType.TotalSP,0);
                                    pointStatComponent.setSP(StatPointType.UsedSP,0);
                                    for(StatType statType : StatType.getNormalStatType()){
                                        pointStatComponent.setPointStat(statType,0);
                            }
                                    finalStatComponent.setFinalStat(StatType.ATK, 1.0);
                                    finalStatComponent.setFinalStat(StatType.DEF,1.0);
                                    finalStatComponent.setFinalStat(StatType.ACC,0.0);
                                    finalStatComponent.setFinalStat(StatType.AVD,0.0);
                                    finalStatComponent.setFinalStat(StatType.CRIT,0.05);
                                    finalStatComponent.setFinalStat(StatType.CRIT_DAMAGE,1.75);
                                    finalStatComponent.setFinalStat(StatType.SPD,1);

                                    StatManager.statUpdate(player);
                                    String text= String.format("초기화 완료");
                                    ctx.getSource().sendFeedback(() -> Text.literal(text), false);
                                    return 1;
                                })

        );
    }
    private static void setSkillKeyBinding(
            CommandDispatcher<ServerCommandSource> dispatcher,
            CommandRegistryAccess access,
            CommandManager.RegistrationEnvironment env){
        dispatcher.register(
                literal("키설정")
                        .then(literal("현재키")
                                        .executes(ctx -> {
                                            ServerPlayerEntity player = ctx.getSource().getPlayer();
                                            PlayerSkillComponent skillComponent = PlayerSkillComponent.KEY.get(player);
                                            var map= skillComponent.getKeyBindSkills();
                                            for(var entry: map.entrySet()){
                                                player.sendMessage(Text.literal(String.format("%s : %s", entry.getKey(), entry.getValue())), false);
                                            }
                                            return 1;
                                        })

                        )
                        .then(literal("스킬키1")
                                .then(argument("skillName",  StringArgumentType.greedyString())
                                        .executes(ctx -> {
                                    ServerPlayerEntity player = ctx.getSource().getPlayer();
                                    PlayerSkillComponent skillComponent = PlayerSkillComponent.KEY.get(player);
                                            String skillName = StringArgumentType.getString(ctx, "skillName");
                                    skillComponent.setKeyBinding("skill_key_1",SkillsId.getSkillIdByName(skillName));
                                    return 1;
                                }))

                        )
                        .then(literal("스킬키2")
                                .then(argument("skillName",  StringArgumentType.greedyString())
                                        .executes(ctx -> {
                                    ServerPlayerEntity player = ctx.getSource().getPlayer();
                                    PlayerSkillComponent skillComponent = PlayerSkillComponent.KEY.get(player);
                                            String skillName = StringArgumentType.getString(ctx, "skillName");
                                    skillComponent.setKeyBinding("skill_key_2",SkillsId.getSkillIdByName(skillName));
                                    return 1;
                                }))

                        )
                        .then(literal("스킬키3")
                                .then(argument("skillName",  StringArgumentType.greedyString())
                                        .executes(ctx -> {
                                    ServerPlayerEntity player = ctx.getSource().getPlayer();
                                    PlayerSkillComponent skillComponent = PlayerSkillComponent.KEY.get(player);
                                            String skillName = StringArgumentType.getString(ctx, "skillName");
                                    skillComponent.setKeyBinding("skill_key_3",SkillsId.getSkillIdByName(skillName));
                                    return 1;
                                }))

                        )
                        .then(literal("스킬키4")
                                .then(argument("skillName",  StringArgumentType.greedyString())
                                        .executes(ctx -> {
                                    ServerPlayerEntity player = ctx.getSource().getPlayer();
                                    PlayerSkillComponent skillComponent = PlayerSkillComponent.KEY.get(player);
                                            String skillName = StringArgumentType.getString(ctx, "skillName");
                                    skillComponent.setKeyBinding("skill_key_4",SkillsId.getSkillIdByName(skillName));
                                    return 1;
                                }))

                        )
                        .then(literal("스킬키5")
                                .then(argument("skillName",  StringArgumentType.greedyString())
                                        .executes(ctx -> {
                                    ServerPlayerEntity player = ctx.getSource().getPlayer();
                                    PlayerSkillComponent skillComponent = PlayerSkillComponent.KEY.get(player);
                                            String skillName = StringArgumentType.getString(ctx, "skillName");
                                    skillComponent.setKeyBinding("skill_key_5",SkillsId.getSkillIdByName(skillName));
                                    return 1;
                                }))

                        )
                        .then(literal("스킬키6")
                                .then(argument("skillName",  StringArgumentType.greedyString())
                                        .executes(ctx -> {
                                    ServerPlayerEntity player = ctx.getSource().getPlayer();
                                    PlayerSkillComponent skillComponent = PlayerSkillComponent.KEY.get(player);
                                            String skillName = StringArgumentType.getString(ctx, "skillName");
                                    skillComponent.setKeyBinding("skill_key_6",SkillsId.getSkillIdByName(skillName));
                                    return 1;
                                }))

                        )
                        .then(literal("스킬키7")
                                .then(argument("skillName",  StringArgumentType.greedyString())
                                        .executes(ctx -> {
                                    ServerPlayerEntity player = ctx.getSource().getPlayer();
                                    PlayerSkillComponent skillComponent = PlayerSkillComponent.KEY.get(player);
                                            String skillName = StringArgumentType.getString(ctx, "skillName");
                                    skillComponent.setKeyBinding("skill_key_7",SkillsId.getSkillIdByName(skillName));
                                    return 1;
                                }))

                        )

        );

    }


}

