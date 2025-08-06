package com.altale.esperis.player_data.skill_data.skillKeybind;

import com.altale.esperis.player_data.skill_data.PlayerSkillComponent;
import com.altale.esperis.player_data.skill_data.SkillManager;
import com.altale.esperis.player_data.skill_data.SkillsId;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class SkillKeyBindingPacketReceiver {
    public static final Identifier ID= new Identifier("esperis","skill_keybinding");
    public static void register(){
        ServerPlayNetworking.registerGlobalReceiver(ID, (server, player, handler, buf, responseSender) -> {


            String key = buf.readString();
            boolean isHolding = buf.readBoolean();
            server.execute(() -> {

                //플레이어가 키 누름 -> 패킷 보냄 -> 서버에서 받음 -> key에 저장된 스킬이 있는지 확인
                //-> 저장된 스킬이 있으면 해금된 스킬인지 확인-> 스킬 수행



                PlayerSkillComponent playerSkillComponent = PlayerSkillComponent.KEY.get(player);
                SkillsId keyMatchesSkillId =  playerSkillComponent.getKeyBoundSkill(key);

                if(keyMatchesSkillId == null){
                    player.sendMessage(Text.literal("key: " + key), false);
                    player.sendMessage(Text.literal("keyMatchesSkillId가 null임"),false);
                    return;
                }
                if( playerSkillComponent.isUnlockedSkill(keyMatchesSkillId) ){//스킬이 해금되어있는지 검사

                    if(isHolding && playerSkillComponent.isKeydownSkill(keyMatchesSkillId)) {
                        //키다운 스킬이 키 다운 중일때
                    }
                    else if(!isHolding && !playerSkillComponent.isKeydownSkill(keyMatchesSkillId)) {
                        //홀딩 중이 아니고 키다운 스킬이 아닐때
                        SkillManager.excuteSkill(player, keyMatchesSkillId);
                    }
                    else{
                        player.sendMessage(Text.literal(String.format("스킬ID: %s, Key: %s | 키가 저장이 되어있지 않거나 해금이 되지 않은 스킬입니다.", keyMatchesSkillId.toString(), key)), false);
                    }
                }
            });

        });
    }
}
