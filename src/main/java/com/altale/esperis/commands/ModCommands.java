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
import com.altale.esperis.shop.ShopItemManager;
import com.altale.esperis.skills.statSkills.dexStatSkill.DexJump;
import com.altale.esperis.skills.statSkills.lukStatSkill.TripleJump;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;


public class ModCommands {
    private static final Map<String, List<String>> SKILLS_BY_STAT = Map.of(
            "STR", List.of(SkillsId.getStrSkillNames()),
            "DEX", List.of(SkillsId.getDexSkillNames()),
            "LUK",   List.of(SkillsId.getLukSkillNames()),
            "DUR", List.of(SkillsId.getDurSkillNames())
    );
    private static final String[] STATS = {"STR", "DEX", "LUK", "DUR"};
    public static void register(){
        CommandRegistrationCallback.EVENT.register(ModCommands::registerMoneyData);
        CommandRegistrationCallback.EVENT.register(ModCommands::setSkillKeyBinding);
        CommandRegistrationCallback.EVENT.register(ModCommands::registerSetShopItemInfo);
        CommandRegistrationCallback.EVENT.register(ModCommands::registerShowItemInfo);

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

    private static void registerMoneyData(
            CommandDispatcher<ServerCommandSource> dispatcher,
            CommandRegistryAccess access,
            CommandManager.RegistrationEnvironment env) {

        dispatcher.register(
                literal("입금").requires(ctx -> ctx.hasPermissionLevel(1))
                                .executes(ctx -> {
                                    ServerPlayerEntity player = ctx.getSource().getPlayer();
                                    PlayerMoneyComponent  component = PlayerMoneyComponent.KEY.get(Objects.requireNonNull(player));
                                    int[] a= component.deposit(ctx.getArgument("amount", Integer.class));
                                    String text= String.format("%d esp 입금 완료, 현재 잔고: %d esp",a[1],a[0]);
                                    ctx.getSource().sendFeedback(() -> Text.literal(text), false);
                                    return 1;
                                })

        );
        dispatcher.register(
                literal("출금").requires(ctx -> ctx.hasPermissionLevel(1))
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
                literal("경험치초기화").requires(ctx -> ctx.hasPermissionLevel(4))//FIXME 디버깅용임
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
                literal("clearAll").requires(ctx -> ctx.hasPermissionLevel(2))//FIXME 디버깅용임
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
                literal("키").requires(ctx -> ctx.hasPermissionLevel(0))
                        .then(literal("현재키")
                                        .executes(ctx -> {
                                            ServerPlayerEntity player = ctx.getSource().getPlayer();
                                            PlayerSkillComponent skillComponent = PlayerSkillComponent.KEY.get(player);
                                            var map= skillComponent.getKeyBindSkills();
                                            for(var entry: map.entrySet()){
                                                player.sendMessage(Text.literal(String.format("%s : %s", entry.getKey(), entry.getValue().getSkillName())), false);
                                            }
                                            return 1;
                                        })

                        )
                        .then(literal("스킬키1")
                                .then(argument("StatType", StringArgumentType.word())
                                        .suggests((c,b)-> CommandSource.suggestMatching(STATS,b))
                                        .then(argument("skillName",  StringArgumentType.greedyString())
                                        .suggests((c,b)->{
                                            String stat = StringArgumentType.getString(c, "StatType"); // 또는 c.getArgument("StatType", String.class)
                                            if (c.getSource().getPlayer() != null) {
                                                PlayerSkillComponent playerSkillComponent = PlayerSkillComponent.KEY.get(c.getSource().getPlayer());
                                                StatType statType = StatType.valueOf(stat);
                                                List<SkillsId> list =playerSkillComponent.getUnlockedStatSkillsMap().get(statType);
                                                String[] unlockedActiveSkillsList = list.stream().filter(SkillsId::isActiveSkill).map(SkillsId::getSkillName).toArray(String[]::new);
                                                return CommandSource.suggestMatching(unlockedActiveSkillsList,b);
                                            }
                                            var list = SKILLS_BY_STAT.getOrDefault(stat, List.of());
                                            return CommandSource.suggestMatching(list, b);
                                        })
                                        .executes(ctx -> {
                                    ServerPlayerEntity player = ctx.getSource().getPlayer();
                                    PlayerSkillComponent skillComponent = PlayerSkillComponent.KEY.get(player);
                                            String skillName = StringArgumentType.getString(ctx, "skillName");
                                    skillComponent.setKeyBinding("skill_key_1",SkillsId.getSkillIdByName(skillName));
                                    return 1;
                                }))

                        ))
                        .then(literal("스킬키2")
                                .then(argument("StatType", StringArgumentType.word())
                                        .suggests((c,b)-> CommandSource.suggestMatching(STATS,b))
                                        .then(argument("skillName",  StringArgumentType.greedyString())
                                                .suggests((c,b)->{
                                                    String stat = StringArgumentType.getString(c, "StatType"); // 또는 c.getArgument("StatType", String.class)
                                                    if (c.getSource().getPlayer() != null) {
                                                        PlayerSkillComponent playerSkillComponent = PlayerSkillComponent.KEY.get(c.getSource().getPlayer());
                                                        StatType statType = StatType.valueOf(stat);
                                                        List<SkillsId> list =playerSkillComponent.getUnlockedStatSkillsMap().get(statType);
                                                        String[] unlockedActiveSkillsList = list.stream().filter(SkillsId::isActiveSkill).map(SkillsId::getSkillName).toArray(String[]::new);
                                                        return CommandSource.suggestMatching(unlockedActiveSkillsList,b);
                                                    }
                                                    var list = SKILLS_BY_STAT.getOrDefault(stat, List.of());
                                                    return CommandSource.suggestMatching(list, b);
                                                })
                                                .executes(ctx -> {
                                    ServerPlayerEntity player = ctx.getSource().getPlayer();
                                    PlayerSkillComponent skillComponent = PlayerSkillComponent.KEY.get(player);
                                            String skillName = StringArgumentType.getString(ctx, "skillName");
                                    skillComponent.setKeyBinding("skill_key_2",SkillsId.getSkillIdByName(skillName));
                                    return 1;
                                })))

                        )
                        .then(literal("스킬키3")
                                .then(argument("StatType", StringArgumentType.word())
                                        .suggests((c,b)-> CommandSource.suggestMatching(STATS,b))
                                        .then(argument("skillName",  StringArgumentType.greedyString())
                                                .suggests((c,b)->{
                                                    String stat = StringArgumentType.getString(c, "StatType"); // 또는 c.getArgument("StatType", String.class)
                                                    if (c.getSource().getPlayer() != null) {
                                                        PlayerSkillComponent playerSkillComponent = PlayerSkillComponent.KEY.get(c.getSource().getPlayer());
                                                        StatType statType = StatType.valueOf(stat);
                                                        List<SkillsId> list =playerSkillComponent.getUnlockedStatSkillsMap().get(statType);
                                                        String[] unlockedActiveSkillsList = list.stream().filter(SkillsId::isActiveSkill).map(SkillsId::getSkillName).toArray(String[]::new);
                                                        return CommandSource.suggestMatching(unlockedActiveSkillsList,b);
                                                    }
                                                    var list = SKILLS_BY_STAT.getOrDefault(stat, List.of());
                                                    return CommandSource.suggestMatching(list, b);
                                                })
                                                .executes(ctx -> {
                                    ServerPlayerEntity player = ctx.getSource().getPlayer();
                                    PlayerSkillComponent skillComponent = PlayerSkillComponent.KEY.get(player);
                                            String skillName = StringArgumentType.getString(ctx, "skillName");
                                    skillComponent.setKeyBinding("skill_key_3",SkillsId.getSkillIdByName(skillName));
                                    return 1;
                                })))

                        )
                        .then(literal("스킬키4")
                                .then(argument("StatType", StringArgumentType.word())
                                        .suggests((c,b)-> CommandSource.suggestMatching(STATS,b))
                                        .then(argument("skillName",  StringArgumentType.greedyString())
                                                .suggests((c,b)->{
                                                    String stat = StringArgumentType.getString(c, "StatType"); // 또는 c.getArgument("StatType", String.class)
                                                    if (c.getSource().getPlayer() != null) {
                                                        PlayerSkillComponent playerSkillComponent = PlayerSkillComponent.KEY.get(c.getSource().getPlayer());
                                                        StatType statType = StatType.valueOf(stat);
                                                        List<SkillsId> list =playerSkillComponent.getUnlockedStatSkillsMap().get(statType);
                                                        String[] unlockedActiveSkillsList = list.stream().filter(SkillsId::isActiveSkill).map(SkillsId::getSkillName).toArray(String[]::new);
                                                        return CommandSource.suggestMatching(unlockedActiveSkillsList,b);
                                                    }
                                                    var list = SKILLS_BY_STAT.getOrDefault(stat, List.of());
                                                    return CommandSource.suggestMatching(list, b);
                                                })
                                                .executes(ctx -> {
                                    ServerPlayerEntity player = ctx.getSource().getPlayer();
                                    PlayerSkillComponent skillComponent = PlayerSkillComponent.KEY.get(player);
                                            String skillName = StringArgumentType.getString(ctx, "skillName");
                                    skillComponent.setKeyBinding("skill_key_4",SkillsId.getSkillIdByName(skillName));
                                    return 1;
                                })))

                        )
                        .then(literal("스킬키5")
                                .then(argument("StatType", StringArgumentType.word())
                                        .suggests((c,b)-> CommandSource.suggestMatching(STATS,b))
                                        .then(argument("skillName",  StringArgumentType.greedyString())
                                                .suggests((c,b)->{
                                                    String stat = StringArgumentType.getString(c, "StatType"); // 또는 c.getArgument("StatType", String.class)
                                                    if (c.getSource().getPlayer() != null) {
                                                        PlayerSkillComponent playerSkillComponent = PlayerSkillComponent.KEY.get(c.getSource().getPlayer());
                                                        StatType statType = StatType.valueOf(stat);
                                                        List<SkillsId> list =playerSkillComponent.getUnlockedStatSkillsMap().get(statType);
                                                        String[] unlockedActiveSkillsList = list.stream().filter(SkillsId::isActiveSkill).map(SkillsId::getSkillName).toArray(String[]::new);
                                                        return CommandSource.suggestMatching(unlockedActiveSkillsList,b);
                                                    }
                                                    var list = SKILLS_BY_STAT.getOrDefault(stat, List.of());
                                                    return CommandSource.suggestMatching(list, b);
                                                })
                                        .executes(ctx -> {
                                    ServerPlayerEntity player = ctx.getSource().getPlayer();
                                    PlayerSkillComponent skillComponent = PlayerSkillComponent.KEY.get(player);
                                            String skillName = StringArgumentType.getString(ctx, "skillName");
                                    skillComponent.setKeyBinding("skill_key_5",SkillsId.getSkillIdByName(skillName));
                                    return 1;
                                })))

                        )
                        .then(literal("스킬키6")
                                .then(argument("StatType", StringArgumentType.word())
                                        .suggests((c,b)-> CommandSource.suggestMatching(STATS,b))
                                        .then(argument("skillName",  StringArgumentType.greedyString())
                                                .suggests((c,b)->{
                                                    String stat = StringArgumentType.getString(c, "StatType"); // 또는 c.getArgument("StatType", String.class)
                                                    if (c.getSource().getPlayer() != null) {
                                                        PlayerSkillComponent playerSkillComponent = PlayerSkillComponent.KEY.get(c.getSource().getPlayer());
                                                        StatType statType = StatType.valueOf(stat);
                                                        List<SkillsId> list =playerSkillComponent.getUnlockedStatSkillsMap().get(statType);
                                                        String[] unlockedActiveSkillsList = list.stream().filter(SkillsId::isActiveSkill).map(SkillsId::getSkillName).toArray(String[]::new);
                                                        return CommandSource.suggestMatching(unlockedActiveSkillsList,b);
                                                    }
                                                    var list = SKILLS_BY_STAT.getOrDefault(stat, List.of());
                                                    return CommandSource.suggestMatching(list, b);
                                                })
                                        .executes(ctx -> {
                                    ServerPlayerEntity player = ctx.getSource().getPlayer();
                                    PlayerSkillComponent skillComponent = PlayerSkillComponent.KEY.get(player);
                                            String skillName = StringArgumentType.getString(ctx, "skillName");
                                    skillComponent.setKeyBinding("skill_key_6",SkillsId.getSkillIdByName(skillName));
                                    return 1;
                                })))

                        )
                        .then(literal("스킬키7")
                                .then(argument("StatType", StringArgumentType.word())
                                        .suggests((c,b)-> CommandSource.suggestMatching(STATS,b))
                                        .then(argument("skillName",  StringArgumentType.greedyString())
                                                .suggests((c,b)->{
                                                    String stat = StringArgumentType.getString(c, "StatType"); // 또는 c.getArgument("StatType", String.class)
                                                    if (c.getSource().getPlayer() != null) {
                                                        PlayerSkillComponent playerSkillComponent = PlayerSkillComponent.KEY.get(c.getSource().getPlayer());
                                                        StatType statType = StatType.valueOf(stat);
                                                        List<SkillsId> list =playerSkillComponent.getUnlockedStatSkillsMap().get(statType);
                                                        String[] unlockedActiveSkillsList = list.stream().filter(SkillsId::isActiveSkill).map(SkillsId::getSkillName).toArray(String[]::new);
                                                        return CommandSource.suggestMatching(unlockedActiveSkillsList,b);
                                                    }
                                                    var list = SKILLS_BY_STAT.getOrDefault(stat, List.of());
                                                    return CommandSource.suggestMatching(list, b);
                                                })
                                        .executes(ctx -> {
                                    ServerPlayerEntity player = ctx.getSource().getPlayer();
                                    PlayerSkillComponent skillComponent = PlayerSkillComponent.KEY.get(player);
                                            String skillName = StringArgumentType.getString(ctx, "skillName");
                                    skillComponent.setKeyBinding("skill_key_7",SkillsId.getSkillIdByName(skillName));
                                    return 1;
                                })))

                        )

        );

    }
    private static void registerSetShopItemInfo(
            CommandDispatcher<ServerCommandSource> dispatcher,
            CommandRegistryAccess access,
            CommandManager.RegistrationEnvironment env) {
        dispatcher.register(
                literal("SetItemPrice").requires(ctx -> ctx.hasPermissionLevel(1))
                        .then(argument("PurchasePrice", integer(-1, Integer.MAX_VALUE))
                            .then(argument("SalesPrice", integer(-1, Integer.MAX_VALUE))
                                .executes(ctx->{
                                    PlayerEntity player = ctx.getSource().getPlayer();
                                    if(player ==null) return 0;
                                    if(player.getMainHandStack().isEmpty()) {
                                        player.sendMessage(Text.literal("아이템을 들고 있지 않습니다."));
                                    }else{
                                        ItemStack stack = ShopItemManager.makeShopItem(player, ctx.getArgument("PurchasePrice", Integer.class), ctx.getArgument("SalesPrice", Integer.class));
                                        player.sendMessage(Text.literal("상점용 아이템 생성에 성공하였습니다. "));
                                    }
                                    //들고있는(mainHand) 아이템의 상점용 nbt추가 전달 인자: (player, purchasePrice, salesPrice)
                                    //purchasePrice 와 salesPrice 값이 -1이면 구매 혹은 판매 [불가] 임

                                    return 1;
                                })

                        )
        )
                        .then(literal("change").executes(ctx->{
                            PlayerEntity player = ctx.getSource().getPlayer();
                            if(player ==null) return 0;
                            if(player.getMainHandStack().isEmpty()) {
                                player.sendMessage(Text.literal("아이템을 들고 있지 않습니다."));
                            }else{
                                ItemStack item = player.getMainHandStack();
                                if(ShopItemManager.hasShopInfo(item)){
                                    ItemStack stack = ShopItemManager.salesPriceFluctuation(item);
                                    player.sendMessage(Text.literal("상점용 아이템 가격 변동에 성공하였습니다. "));
                                }

                            }
                            return 1;
                        })
                        )
        );

    }
    private static void registerShowItemInfo(
            CommandDispatcher<ServerCommandSource> dispatcher,
            CommandRegistryAccess access,
            CommandManager.RegistrationEnvironment env) {
        dispatcher.register(
                literal("비틱").requires(ctx -> ctx.hasPermissionLevel(0)).executes(ctx->{
                    PlayerEntity player = ctx.getSource().getPlayer();
                    ItemStack stack = player.getMainHandStack();
                    if(stack == null || stack.isEmpty()){
                        player.sendMessage(Text.literal("들고 있는 아이템이 없습니다.").formatted(Formatting.RED));
                        return 1;
                    }else{
                        Text msg = Text.empty()
                                .append(player.getDisplayName().copy().formatted(Formatting.GRAY, Formatting.BOLD))
                                .append(Text.literal(" 이/가 비틱을 하였습니다.  ").formatted(Formatting.GOLD))
                                .append(stack.toHoverableText().copy().formatted(Formatting.ITALIC, Formatting.AQUA)); // ← 마우스 올리면 아이템 툴팁이 뜸

                        ctx.getSource().getServer().getPlayerManager().broadcast(msg, false);
                        return 1;
                    }
                })
        );

    }


}


