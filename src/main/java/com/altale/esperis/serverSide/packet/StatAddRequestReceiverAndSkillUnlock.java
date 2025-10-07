package com.altale.esperis.serverSide.packet;

import com.altale.esperis.items.SkillsTooltip.SkillTooltipItemRegister;
import com.altale.esperis.player_data.skill_data.PlayerSkillComponent;
import com.altale.esperis.player_data.skill_data.SkillsId;
import com.altale.esperis.player_data.stat_data.StatComponents.PlayerPointStatComponent;
import com.altale.esperis.player_data.stat_data.StatManager;
import com.altale.esperis.player_data.stat_data.StatType;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class StatAddRequestReceiverAndSkillUnlock {
    public static final Identifier ID2 = new Identifier("esperis", "stat_add_request");
    public static void register(){
        ServerPlayNetworking.registerGlobalReceiver(ID2, (server, player, handler, buf, responseSender) -> {
            UUID uuid = buf.readUuid();
            StatType statType =  buf.readEnumConstant(StatType.class);
            int value = buf.readInt();
            server.execute(() -> {
                ServerPlayerEntity playerTarget = server.getPlayerManager().getPlayer(uuid);
            //FIXME 스킬 해금 로직 반드시 분리하기!!!!!!!!!!!!!!!!
                if (playerTarget != null) {
                    PlayerPointStatComponent playerPointStatComponent = PlayerPointStatComponent.KEY.get(playerTarget);
                    double beforeValue = playerPointStatComponent.getPointStat(statType);
                    PlayerSkillComponent playerSkillComponent = PlayerSkillComponent.KEY.get(playerTarget);
                    playerPointStatComponent.useSP(statType, value);//미사용 sp감소 , addSp
                    StatManager.statUpdate(playerTarget);
                    double afterValue = playerPointStatComponent.getPointStat(statType);

                    int jumpSkillUnlockIndex =0;
                    int jumpSkillIndex =0;
                    if(beforeValue < 5){
                        if(afterValue>=5){
                            jumpSkillUnlockIndex=1;
                        }
                    }
                    else{
                        jumpSkillIndex =1;
                    }//FIXME logic ㅄ 같은데 군대 입대 전이라서 임시로 때움(2025/10/8)
                    int beforeSkillUnlockedNum = Math.min(8,(int) (beforeValue/25)  + jumpSkillIndex);
                    int afterSkillUnlockedNum = Math.min(8,(int) (afterValue/25) + jumpSkillUnlockIndex + jumpSkillIndex);
                    for(int i =0 ; i< (afterSkillUnlockedNum-beforeSkillUnlockedNum);i++){
                        SkillsId[] list = SkillsId.getStatTypeSkillsId(statType);
                        String unlockedSkill = list[beforeSkillUnlockedNum + i].getSkillName();
                        Item item = SkillTooltipItemRegister.getStatTypeSkillTooltipItems(statType).get(beforeSkillUnlockedNum + i);
                        Text msg = Text.empty().append("스킬 해금: ").append(item.getDefaultStack().toHoverableText().copy());
//                        player.sendMessage(Text.literal(String.format("스킬 %s 해금", unlockedSkill)),false);
                        player.sendMessage((msg),false);

                    }
                    playerSkillComponent.setUnlockedSkill();

                }
            });
        });
    }
}
