package com.altale.esperis.player_data.skill_data;

import com.altale.esperis.player_data.stat_data.StatComponents.PlayerPointStatComponent;
import com.altale.esperis.player_data.stat_data.StatType;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;

import java.util.*;

public class PlayerSkillComponentImp implements PlayerSkillComponent, AutoSyncedComponent {
    private final PlayerEntity player;
    private final Map<String , SkillsId> keybindingMap;
    private final Set<SkillsId> playerUnlockedSkillSet;// 빠른 검색용도
    private final Map<StatType, List<SkillsId>> playerUnlockedSkillMap;
    private final Set<SkillsId> playerPassiveSkillSet;


    public PlayerSkillComponentImp(PlayerEntity player) {
        this.player = player;
        this.keybindingMap = new HashMap<String, SkillsId>();
        this.playerUnlockedSkillSet = new HashSet<SkillsId>();
        this.playerUnlockedSkillMap  = new HashMap<>();
        this.playerPassiveSkillSet = new HashSet<SkillsId>();

    }

    @Override
    public void setKeyBinding(String keyId, SkillsId skillId) {
        if(isPassiveSkill(skillId)){
            player.sendMessage(Text.literal(String.format("패시브 스킬 (%s) 은 등록이 불가능 합니다.",skillId.getSkillName())), false);
            return;
        }
        if(isUnlockedSkill(skillId)){
            //TODO 나중에 시간 남으면 이미 할당된 키 있으면 서로 바꾸는거 넣기
            keybindingMap.put(keyId, skillId);
            PlayerSkillComponent.KEY.sync(this.player);
            player.sendMessage(Text.literal(String.format(
                    "%s 에 %s 이 등록되었습니다.",keyId, skillId.getSkillName()
            )));
        }else{
            player.sendMessage(Text.literal("해금되지 않은 스킬: "+ skillId));
        }
    }

    @Override
    public SkillsId getKeyBoundSkill(String keyId) {
        return keybindingMap.get(keyId);
    }

    @Override
    public String getSkillKey(SkillsId skillId) {
        for(Map.Entry<StatType, List<SkillsId>> entry : playerUnlockedSkillMap.entrySet()){
            if(entry.getValue().contains(skillId)){
                return entry.getKey().toString();
            }
        }
        return "";
    }

    @Override
    public Map<String, SkillsId> getKeyBindSkills() {
        return keybindingMap;
    }

    @Override
    public void setUnlockedSkill() {
        playerUnlockedSkillSet.clear();//구조상 스탯 초기화하고 다시 찍을때 스킬이 그대로 사용되는거 방지용도
        playerUnlockedSkillMap.clear();
        playerPassiveSkillSet.clear();
        PlayerPointStatComponent playerPointStatComponent = PlayerPointStatComponent.KEY.get(player);
        Map<StatType, Double> playerStatMap = playerPointStatComponent.getAllPointStat();
        for(Map.Entry<StatType, Double> entry : playerStatMap.entrySet()){
            StatType currentStat = entry.getKey();
            List<SkillsId> tempSkillList= new ArrayList<>();

            for(SkillsId skillId : SkillsId.getStatTypeSkillsId(currentStat)){
                if(playerStatMap.get(currentStat) >= skillId.getSkillRequiredLevel()){
                    playerUnlockedSkillSet.add(skillId);
                    tempSkillList.add(skillId);
                    if(isPassiveSkill(skillId)){
                        playerPassiveSkillSet.add(skillId);
                        System.out.println("패시브 해금: " + skillId.getSkillName());
                    }


                }else {
                    System.out.println("패시브 내용: "+ playerPassiveSkillSet);
                    System.out.println("액티브 내용: "+ playerUnlockedSkillSet);
                    break;// 반복으로 자원 낭비 없이 이하이면 바로 break 걸어버리기
                }
            }
            playerUnlockedSkillMap.put(currentStat, tempSkillList);
        }
        PlayerSkillComponent.KEY.sync(this.player);

    }

    @Override
    public Set<SkillsId> getUnlockedSkillsSet() {
        return playerUnlockedSkillSet;
    }
    @Override
    public Map<StatType, List<SkillsId>> getUnlockedStatSkillsMap(){
        return playerUnlockedSkillMap;
    }
    @Override
    public Set<SkillsId> getPassiveSkillSet() {
        return playerPassiveSkillSet;
    }
    @Override
    public boolean hasPassiveSkill(SkillsId skillId){
        return playerPassiveSkillSet.contains(skillId);
    }

    @Override
    public boolean isUnlockedSkill(SkillsId skillId) {
        if(playerUnlockedSkillSet.contains(skillId)||hasPassiveSkill(skillId)) {
            return true;
        }else{
            return false;
        }
    }
    @Override
    public boolean isPassiveSkill(SkillsId skillId) {
        Set<SkillsId> passiveSkillSet= SkillsId.getPassiveSkills();
        return passiveSkillSet.contains(skillId);
    }
    @Override
    public boolean isKeydownSkill(SkillsId skillId) {
        Set<SkillsId> keydownSkillSet= SkillsId.getKeydownSkills();
        return keydownSkillSet.contains(skillId);
    }

    @Override
    public void writeToNbt(NbtCompound nbt) {
        // 1) KeyBinding 저장
        NbtList kbList = new NbtList();
        for (Map.Entry<String, SkillsId> entry : keybindingMap.entrySet()) {
            NbtCompound tag = new NbtCompound();
            tag.putString("Key", entry.getKey());
            tag.putString("Skill", entry.getValue().getSkillName());
            kbList.add(tag);
        }
        nbt.put("KeyBindings", kbList);
    }

    @Override
    public void readFromNbt(NbtCompound nbt) {
        // 1) KeyBinding 복원
        keybindingMap.clear();
        if (nbt.contains("KeyBindings", NbtElement.LIST_TYPE)) {
            NbtList kbList = nbt.getList("KeyBindings", NbtElement.COMPOUND_TYPE);
            for (int i = 0; i < kbList.size(); i++) {
                NbtCompound tag = kbList.getCompound(i);
                String key = tag.getString("Key");
                System.out.println("키바인드 : "+key);
//                if(SkillsId.getAllSkills().contains(key)){
//                if(SkillsId.getActiveSkills().contains(keybindingMap.get(key))){
                    SkillsId skill = SkillsId.getSkillIdByName(tag.getString("Skill"));
                    System.out.println("PlayerSkillComponentImp: "+skill);
                    if(skill == null){
                        continue;
                    }
//
//                    SkillsId skill = SkillsId.valueOf(tag.getString("Skill"));

                    keybindingMap.put(key, skill);
//                }

//                }

            }
        }

        // 2) 언락 스킬 재계산
        //    내부적으로 playerUnlockedSkillSet 과 playerUnlockedSkillMap 을 채워줍니다.
        setUnlockedSkill();

        // 3) 동기화
        PlayerSkillComponent.KEY.sync(this.player);
    }
}
