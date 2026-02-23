package com.altale.esperis.items.SkillsTooltip;

import com.altale.esperis.player_data.skill_data.SkillsId;
import com.altale.esperis.player_data.stat_data.StatType;
import com.altale.esperis.skills.statSkills.dexStatSkill.*;
import com.altale.esperis.skills.statSkills.durSkill.*;
import com.altale.esperis.skills.statSkills.lukStatSkill.*;
import com.altale.esperis.skills.statSkills.strSkill.*;
import com.google.common.collect.Maps;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.*;

public class SkillTooltipItemRegister {
    public static final String MODID = "esperis";
    public static final float emptyValue = 0f;
    public static final Map<StatType, Float> emptyMap = Collections.emptyMap();
    public static final Map<String, Item> MAP = new LinkedHashMap<String, Item>();

    public static final Item STR_1 = register("str1", new SkillTooltipItem(
            new FabricItemSettings(),
            "액티브", SkillsId.STR_1.getSkillName(),
                    StatType.STR,SkillsId.STR_1.getSkillRequiredStatAmount(),
            "전방으로 도약하고 3초동안 방어력이 _barrierFlag_ 증가한다.",
            emptyValue, emptyMap,
            5,Map.of(StatType.DEF, 0.08f),
            emptyValue,emptyMap,
            StrJump.cooltime,0,0,
            ""
    ));
    public static final Item STR_25 = register("str25", new SkillTooltipItem(
            new FabricItemSettings(),
            "액티브", SkillsId.STR_25.getSkillName(),
                    StatType.STR,SkillsId.STR_25.getSkillRequiredStatAmount(),
            "전방으로 검을 휘둘러 범위 내의 모든 적에게 _damageFlag_ 의 피해를 입히고," +
                    "_lineBreak_적중시킨 대상 하나당 체력을 _healFlag_ 만큼 회복한다.",
            HorizenSweep.baseDamage, Map.of(StatType.ATK, HorizenSweep.atkCoeffi),
            emptyValue,emptyMap,
            HorizenSweep.baseHealAmount,Map.of(StatType.ATK, HorizenSweep.healAtkCoeffi),
            HorizenSweep.cooltime,HorizenSweep.cooltimeReduceCoeffi, 0,
            "공격 속도에 비례하여 시전 속도와 쿨타임이 최대 50%까지 감소한다."
    ));
    public static final Item STR_50 = register("str50", new SkillTooltipItem(
            new FabricItemSettings(),
            "패시브", SkillsId.STR_50.getSkillName(),
                    StatType.STR,SkillsId.STR_50.getSkillRequiredStatAmount(),
            "적에게 피해를 입히면 8초 동안 공격력이 _damageFlag_ (최소 0.1) 증가한다." +
                    "_lineBreak_이 효과는 최대 20회 중첩되며 최대 중첩 시 입히는 피해량의 13%를 회복한다.",
            0, Map.of(StatType.ATK, 0.01f),
            emptyValue,emptyMap,
            emptyValue,emptyMap,
            -1,0,0,
            "중첩시 기본 공격력의 1% 와 0.1 중 더 큰 값이 적용된다."
    ));
    public static final Item STR_75 = register("str75", new SkillTooltipItem(
            new FabricItemSettings(),
            "액티브", SkillsId.STR_75.getSkillName(),
            StatType.STR,SkillsId.STR_75.getSkillRequiredStatAmount(),
            "검기를 일으키는 수평베기를 시전한다. _lineBreak_" +
                    "검기에 적중한 대상에게  _damageFlag_  의 피해를 입히고 _lineBreak_" +
                    "대상을 1초동안 공중에 띄운다.",
            6, Map.of(StatType.ATK, WindSlash.atkCoeffi,StatType.MAX_HEALTH, WindSlash.HpCoeffi),
            emptyValue,emptyMap,
            emptyValue,emptyMap,
            WindSlash.cooltime,WindSlash.cooltimeReduceCoeffi,0,
            "공격 속도에 비례하여 쿨타임이 최대 50%까지 감소한다."
    ));
    public static final Item STR_100 = register("str100", new SkillTooltipItem(
            new FabricItemSettings(),
            "패시브", SkillsId.STR_100.getSkillName(),
            StatType.STR,SkillsId.STR_100.getSkillRequiredStatAmount(),
            "체력이 30% 이하가 되는 피해를 입을 시 _lineBreak_" +
                    "5초 동안 _barrierFlag_ 의 보호막을 얻으며 _lineBreak_" +
                    "5초에 걸쳐 체력을 _healFlag_ 를 회복한다.",
            emptyValue,emptyMap,
            0,Map.of(StatType.MAX_HEALTH, 0.25f),
            0,Map.of(StatType.MAX_HEALTH, 0.1f),
            1200,0,0,
            ""
    ));
    public static final Item STR_125 = register("str125", new SkillTooltipItem(
            new FabricItemSettings(),
            "액티브", SkillsId.STR_125.getSkillName(),
            StatType.STR,SkillsId.STR_125.getSkillRequiredStatAmount(),
            "사용 즉시 공중으로 도약하고 0.6초후에 공중에 고정된다. _lineBreak_" +
                    "3초 동안 스킬을 재사용 가능하며, 지면을 향하여 재사용시 그 방향으로 빠르게 낙하하여_lineBreak_" +
                    "_barrierFlag_ 의 보호막과 10%의 공격력 증가를 3초동안 얻고 _lineBreak_" +
                    "범위 내의 모든 대상을 0.5 ~ 2.5초동안 공중에 띄우고 _lineBreak_" +
                    "_healFlag_ ~ _damageFlag_ 의 피해를 입힌다. _lineBreak_" +
                    "착지지점과 피격대상의 거리가 멀 수록 공중에 뜸 지속시간이 증가하고 _lineBreak_" +
                    "거리가 가까울 수록 피해량이 증가한다.",
            GrandFallLandingHandler.baseDamage,Map.of(StatType.ATK, GrandFallLandingHandler.atkCoeffi ,StatType.MAX_HEALTH, GrandFallLandingHandler.hpCoeffi),
            10,Map.of(StatType.ATK, GrandStarfall.barrierAtkCoeffi),
            GrandFallLandingHandler.baseDamage/3,Map.of(StatType.ATK, GrandFallLandingHandler.atkCoeffi/3 ,StatType.MAX_HEALTH, GrandFallLandingHandler.hpCoeffi/3),
            GrandStarfall.cooltime,0,0,
            "아래쪽이 아닌 위쪽을 바라보고 사용시 제자리에 낙하합니다."
    ));
    public static final Item STR_150 = register("str150", new SkillTooltipItem(
            new FabricItemSettings(),
            "패시브", SkillsId.STR_150.getSkillName(),
            StatType.STR,SkillsId.STR_150.getSkillRequiredStatAmount(),
            "피해를 입을 시 해당 피해의 50%를 4초에 걸쳐 나누어 입으며 _lineBreak_" +
                    "4초동안 공격속도, 이동속도가 2.5% 증가하고 방어력이 2.5 증가한다.(최대 6중첩) _lineBreak_" +
                    "대상을 처치시 3초에 걸쳐 체력을 _healFlag_ 만큼 회복한다.",
            emptyValue,emptyMap,
            emptyValue,emptyMap,
            0,Map.of(StatType.ATK, 0.6f),
            0,0,0,
            "어떤 게임과는 다르게 유예된 피해량은 반드시 돌아옵니다."
    ));
    public static final Item STR_175 = register("str175", new SkillTooltipItem(
            new FabricItemSettings(),
            "액티브", SkillsId.STR_175.getSkillName(),
            StatType.STR,SkillsId.STR_175.getSkillRequiredStatAmount(),
            "공중에 뜬 대상에게만 사용 가능. _lineBreak_" +
                    "사용시 6초동안 20% 의 방어력관통을 얻고 _lineBreak_" +
                    "대상과 대상 주변 2칸의 모든 대상을 공중에 2초 더 붙잡아 두고 대상의 뒤로 순간이동한다. _lineBreak_" +
                    "0.5초마다 _damageFlag_ 의 피해를 주고 _barrierFlag_ 의 보호막을 얻는다. _lineBreak_",
            0,Map.of(StatType.ATK, 0.18f),
            0,Map.of(StatType.ATK, 0.24f),
            emptyValue,emptyMap,
            LastBreath.cooltime,0,0,
            "최대 피해량 72%⚔, 최대 보호막 96%⚔"
    ));
    public static final Item DEX_1 = register("dex1", new SkillTooltipItem(
            new FabricItemSettings(),
            "액티브", SkillsId.DEX_1.getSkillName(),
            StatType.DEX,SkillsId.DEX_1.getSkillRequiredStatAmount(),
            "전방으로 도약한다. _lineBreak_" +
                    "4칸 안에 바라보는 대상이 있다면 그 대상을 0.2초 공중에 띄워 올리고 자신은 뒤로 도약한다.",
            emptyValue, emptyMap,
            emptyValue, emptyMap,
            emptyValue,emptyMap,
            DexJump.cooltime,0,0,
            "적을 밀어내고 뒤로 도약할때 쿨타임이 2배로 적용됩니다."
    ));
    public static final Item DEX_25 = register("dex25", new SkillTooltipItem(
            new FabricItemSettings(),
            "액티브", SkillsId.DEX_25.getSkillName(),
            StatType.DEX,SkillsId.DEX_25.getSkillRequiredStatAmount(),
                    "바라보는 방향으로 활을 3번 쏜다. _lineBreak_" +
                    "화살 한 발당 피해량: _damageFlag_"+
                    "화살 적중시 이 스킬의 쿨타임이 0.75초 감소한다.",
            3.2f, Map.of(StatType.ATK, 0.2f, StatType.DEX,0.04f),
            emptyValue,emptyMap,
            emptyValue,emptyMap,
            TripleShot.Cooltime,HorizenSweep.cooltimeReduceCoeffi, 0,
            ""
    ));
    public static final Item DEX_50 = register("dex50", new SkillTooltipItem(
            new FabricItemSettings(),
            "패시브", SkillsId.DEX_50.getSkillName(),
            StatType.DEX,SkillsId.DEX_50.getSkillRequiredStatAmount(),
            "활 적중시 1스택을 얻고 4스택일때 모든 스택을 소모하고 _lineBreak_" +
                    "3 + 대상 최대체력의 5% 의 추가 피해를 입힌다.",
            emptyValue,emptyMap,
            emptyValue,emptyMap,
            emptyValue,emptyMap,
            -1,0,0,
            ""
    ));
    public static final Item DEX_75 = register("dex75", new SkillTooltipItem(
            new FabricItemSettings(),
            "액티브", SkillsId.DEX_75.getSkillName(),
            StatType.DEX,SkillsId.DEX_75.getSkillRequiredStatAmount(),
            "사용시 60초 동안 이동속도가 2.5%, 명중률이 2%, 크리티컬 확률이 4% 증가한다. (최대 3회 중첩) _lineBreak_" +
                    "적중시 이 스킬의 쿨타임이 3초 감소한다.",
            emptyValue,emptyMap,
            emptyValue,emptyMap,
            emptyValue,emptyMap,
            FastAccurateAdvanced.cooltime,0,0,
            ""
    ));
    public static final Item DEX_100 = register("dex100", new SkillTooltipItem(
            new FabricItemSettings(),
            "패시브", SkillsId.DEX_100.getSkillName(),
            StatType.DEX,SkillsId.DEX_100.getSkillRequiredStatAmount(),
            "활 적중시 12초동안 공격속도가 5% 증가한다. (최대 15회 중첩) _lineBreak_",
            emptyValue,emptyMap,
            emptyValue,emptyMap,
            emptyValue,emptyMap,
            -1,0,0,
            ""
    ));
    public static final Item DEX_125 = register("dex125", new SkillTooltipItem(
            (Item.Settings) new FabricItemSettings(),
            "액티브", SkillsId.DEX_125.getSkillName(),
            StatType.DEX,SkillsId.DEX_125.getSkillRequiredStatAmount(),
            "사용 즉시 최대 6초동안 그 자리에 고정되고 3초동안 차징을 시작한다. _lineBreak_" +
                    "6초안에 스킬을 재사용하면 충전 시간에 비례하여 최대 3배까지 증가하는 저격탄을 발사한다. _lineBreak_" +
                    "저격탄 최소 피해량: _healFlag__lineBreak_"+
                    "저격탄 최대 피해량: _damageFlag_",
            (float) Snipe.maxBaseDamage,Map.of(StatType.ATK, (float) Snipe.maxAtkCoeffi),
            emptyValue,emptyMap,
            (float) Snipe.baseDamage,Map.of(StatType.ATK, (float) Snipe.atkCoeffi),
            Snipe.cooltime,0,0,
            "저격탄 사정거리: 100 (피해감소 거리:50)"
    ));
    public static final Item DEX_150 = register("dex150", new SkillTooltipItem(
            new FabricItemSettings(),
            "패시브", SkillsId.DEX_150.getSkillName(),
            StatType.DEX,SkillsId.DEX_150.getSkillRequiredStatAmount(),
            "\"패시브: 거인학살자\" 에 2초동안 2 + 대상 최대 체력의 3%의 피해를 입히는 출혈 효과를 부여한다. _lineBreak_" +
                    "출혈효과는 남은 피해+ 새로운 피해로 무한히 중첩된다.",
            emptyValue,emptyMap,
            emptyValue,emptyMap,
            emptyValue,emptyMap,
            -1,0,0,
            ""
    ));
    public static final Item DEX_175 = register("dex175", new SkillTooltipItem(
            new FabricItemSettings(),
            "액티브", SkillsId.DEX_175.getSkillName(),
            StatType.DEX,SkillsId.DEX_175.getSkillRequiredStatAmount(),
            "이동속도가 60% 감소하고  _lineBreak_" +
                    "_damageFlag_ 의 피해를 입히는 화살을 _barrierFlag_ 만큼 발사한다. _lineBreak_" +
                    "화살을 발사할 때 마다 _healFlag_ 만큼 체력을 회복한다. _lineBreak_" +
                    "적중시 이 스킬의 쿨타임이 1.5초 감소한다.",
            StormsPoem.baseDamage,Map.of(StatType.ATK, StormsPoem.atkCoeffi),
            1,Map.of(StatType.ATTACK_SPEED, 10f),
            StormsPoem.baseHitHeal,Map.of(StatType.ATK, StormsPoem.hitHealAtkCoefficient),
            StormsPoem.cooltime,0,0,
            ""
    ));
    public static final Item LUK_1 = register("luk1", new SkillTooltipItem(
            new FabricItemSettings(),
            "액티브", SkillsId.LUK_1.getSkillName(),
            StatType.LUK,SkillsId.LUK_1.getSkillRequiredStatAmount(),
            "전방으로 2번 도약한다.",
            emptyValue, emptyMap,
            emptyValue, emptyMap,
            emptyValue,emptyMap,
            TripleJump.cooltime,0,0,
            ""
    ));
    public static final Item LUK_25 = register("luk25", new SkillTooltipItem(
            new FabricItemSettings(),
            "액티브", SkillsId.LUK_25.getSkillName(),
            StatType.LUK,SkillsId.LUK_25.getSkillRequiredStatAmount(),
            "전방으로 단검을 3번 찌른다. 각 단검은 2.5초동안 출혈 상태를 부여한다. _lineBreak_" +
                    "단검 명중 피해량: _damageFlag_ _lineBreak_" +
                    "출혈 피해량: _barrierFlag_ _lineBreak_"+
                    "단검 적중시 \"그림자 이동\"의 쿨타임이 1초 감소한다.",
            DoubleStep.baseDamage, Map.of(StatType.ATK, DoubleStep.atkCoeffi),
            DoubleStep.baseDotDamage, Map.of(StatType.ATK, DoubleStep.dotAtkCoeffi),
            emptyValue,emptyMap,
            DoubleStep.cooltime,0, 0,
            ""
    ));
    public static final Item LUK_50 = register("luk50", new SkillTooltipItem(
            new FabricItemSettings(),
            "패시브", SkillsId.LUK_50.getSkillName(),
            StatType.LUK,SkillsId.LUK_50.getSkillRequiredStatAmount(),
            "치명타 피해의 15% 만큼 체력을 회복한다. " ,
            emptyValue,emptyMap,
            emptyValue,emptyMap,
            emptyValue,emptyMap,
            -1,0,0,
            ""
    ));
    public static final Item LUK_75 = register("luk75", new SkillTooltipItem(
            new FabricItemSettings(),
            "액티브", SkillsId.LUK_75.getSkillName(),
            StatType.LUK,SkillsId.LUK_75.getSkillRequiredStatAmount(),
            "사용시 대상 뒤로 순간이동하고 _healFlag_ 만큼 체력을 회복하고 _lineBreak_잠시 투명화 상태가 되며 대상에게 실명 효과를 부여한다. _lineBreak_" +
                    "대상이 출혈상태일 때 남은 출혈피해를 즉시 입힌 후 _lineBreak_이 스킬의 쿨타임이 초기화되는 대신 트리플 스탭의 쿨타임이 0.75초 증가한다. ",
            emptyValue,emptyMap,
            emptyValue,emptyMap,
            ShadowTeleport.baseHeal,Map.of(StatType.ATK,ShadowTeleport.healAtkCoeffi),
            ShadowTeleport.cooltime,0,0,
            "바라보는 대상이 없이 사용시 쿨타임이 60% 감소합니다."
    ));
    public static final Item LUK_100 = register("luk100", new SkillTooltipItem(
            new FabricItemSettings(),
            "패시브", SkillsId.LUK_100.getSkillName(),
            StatType.LUK,SkillsId.LUK_100.getSkillRequiredStatAmount(),
            "\"그림자 이동\" 으로 출혈 피해를 즉시 입힐 때 _lineBreak_ 7초 동안 공격력을 _damageFlag_ 얻고 (최대 7중첩) _lineBreak_주변 대상들에게 해당 피해량의 30%의 피해를 입힌다._lineBreak_" +
                    "최대 중첩 공격력: _barrierFlag_",
            emptyValue,Map.of(StatType.ATK, 0.027f),
            emptyValue,Map.of(StatType.ATK, 0.189f),
            emptyValue,emptyMap,
            -1,0,0,
            ""
    ));
    public static final Item LUK_125 = register("luk125", new SkillTooltipItem(
            new FabricItemSettings(),
            "액티브", SkillsId.LUK_125.getSkillName(),
            StatType.LUK,SkillsId.LUK_125.getSkillRequiredStatAmount(),
            "전방 최대 6칸 앞으로 순간이동하면서 그 사이의 모든 대상에게 _lineBreak_3초동안 _damageFlag_ 피해를 주는 출혈효과를 부여하고 _lineBreak_" +
                    "\"그림자 이동\"의 쿨타임을 4초 감소시킨다. _lineBreak_",
            FatalBlitz.baseDotDamage,Map.of(StatType.ATK, FatalBlitz.dotDamageAtkCoeffi),
            emptyValue,emptyMap,
            emptyValue,emptyMap,
            FatalBlitz.cooltime,0,0,
            "이 스킬로 벽을 통과할 수 없습니다."
    ));
    public static final Item LUK_150 = register("luk150", new SkillTooltipItem(
            new FabricItemSettings(),
            "패시브", SkillsId.LUK_150.getSkillName(),
            StatType.LUK,SkillsId.LUK_150.getSkillRequiredStatAmount(),
            "체력이 30% 이하로 내려가면 주변에 연막을 흩뿌리고 4초동안 투명화 효과를 얻으며 _lineBreak_" +
                    "2.5초동안 이동속도가 50%, 회피율이 100% 증가하고 _lineBreak_\"그림자 이동\"의 쿨타임을 초기화한다.",
            emptyValue,emptyMap,
            emptyValue,Map.of(StatType.AVD, 1f),
            emptyValue,emptyMap,
            1200,0,0,
            ""
    ));
    public static final Item LUK_175 = register("luk175", new SkillTooltipItem(
            new FabricItemSettings(),
            "액티브", SkillsId.LUK_175.getSkillName(),
            StatType.LUK,SkillsId.LUK_175.getSkillRequiredStatAmount(),
            "15초동안 버프 적용 이전의 회피율을 0%으로 만들고  _lineBreak_" +
                    "감소하는 수치인 _barrierFlag_ % 만큼 최종데미지, 크리티컬확률, 방어력관통이 증가한다. _lineBreak_",
            emptyValue,emptyMap,
            emptyValue,Map.of(StatType.AVD, 1f),
            emptyValue,emptyMap,
            ReadyToDie.cooltime,0,0,
            ""
    ));
    public static final Item DUR_1 = register("dur1", new SkillTooltipItem(
            new FabricItemSettings(),
            "액티브", SkillsId.DUR_1.getSkillName(),
            StatType.DUR,SkillsId.DUR_1.getSkillRequiredStatAmount(),
            "전방으로 도약하고 1초동안 _barrierFlag_ 의 보호막을 얻는다. _lineBreak_" +
                    "총공세: 1초동안 _damageFlag_ 의 보호막을 얻는다.",
            DurJump.baseBarrier, Map.of(StatType.ATK, DurJump.barrierAlloutAtkCoeffi),
            DurJump.baseBarrier, Map.of(StatType.MAX_HEALTH, 0.03f),
            emptyValue,emptyMap,
            DurJump.cooltime,0,0,
            "총공세 상태일때 이 스킬의 쿨타임이 40% 감소합니다."
    ));
    public static final Item DUR_25 = register("dur25", new SkillTooltipItem(
            new FabricItemSettings(),
            "액티브", SkillsId.DUR_25.getSkillName(),
            StatType.DUR,SkillsId.DUR_25.getSkillRequiredStatAmount(),
            "주변 대상들에게 _damageFlag_ 의 피해를 입히고 0.6초동안 공중으로 띄워올린다. _lineBreak_" +
                    "총공세: 더 넓은 범위의 대상들에게 _barrierFlag_ 의 피해를 입힌다.",
            GroundSlam.baseDamage, Map.of(StatType.MAX_HEALTH, GroundSlam.hpCoeffi, StatType.DEF, GroundSlam.defCoeffi),
            GroundSlam.baseDamage, Map.of(StatType.ATK, GroundSlam.allOutAttackAtkCoeffi),
            emptyValue,emptyMap,
            GroundSlam.cooltime,0, 0,
            "총공세 상태일때 이 스킬의 쿨타임이 30% 감소합니다."
    ));
    public static final Item DUR_50 = register("dur50", new SkillTooltipItem(
            new FabricItemSettings(),
            "패시브", SkillsId.DUR_50.getSkillName(),
            StatType.DUR,SkillsId.DUR_50.getSkillRequiredStatAmount(),
            "1초마다 _healFlag_ 만큼 체력을 회복한다. _lineBreak_" +
                    "회복량은 체력이 낮을 수록 증가하며, 체력이 20% 이하에서 효과가 100% 까지 증가한다." ,
            emptyValue,emptyMap,
            emptyValue,emptyMap,
            0.4f,Map.of(StatType.MAX_HEALTH, 0.001f, StatType.ATK, 0.06f),
            -1,0,0,
            ""
    ));
    public static final Item DUR_75 = register("dur75", new SkillTooltipItem(
            new FabricItemSettings(),
            "액티브", SkillsId.DUR_75.getSkillName(),
            StatType.DUR,SkillsId.DUR_75.getSkillRequiredStatAmount(),
            "바라보는 방향으로 이동한다. _lineBreak_" +
                    "이동 경로 주변의 대상들에게 _damageFlag_ 의 피해를 입히고 2.5초동안 공중에 띄워 올린다. _lineBreak_" +
                    "총공세: 스킬이 더 빠르게 종료되고 더 이상 경로 주변의 대상들을 공중에 띄워올리지 않는 대신 체력을 회복한다. _lineBreak_" +
                    "총공세 상태 피해량: _barrierFlag_ , 적중 대상당 _healFlag_ 만큼 체력을 회복",
            PathMaker.baseDamage, Map.of(StatType.MAX_HEALTH, PathMaker.hpCoeffi, StatType.DEF, PathMaker.defCoeffi),
            PathMaker.baseDamage, Map.of(StatType.ATK, PathMaker.allOutAtkCoeffi),
            PathMaker.baseDamage * PathMaker.alloutHealPercent , Map.of(StatType.ATK, PathMaker.allOutAtkCoeffi * PathMaker.alloutHealPercent),
            PathMaker.cooltime,0,0,
            "총공세 상태일때 이 스킬의 쿨타임이 40% 감소합니다."
    ));
    public static final Item DUR_100 = register("dur100", new SkillTooltipItem(
            new FabricItemSettings(),
            "패시브", SkillsId.DUR_100.getSkillName(),
            StatType.DUR,SkillsId.DUR_100.getSkillRequiredStatAmount(),
            "피해를 입기 전 1.5초동안 _barrierFlag_ 에 해당하는 보호막을 얻는다.",
            emptyValue,emptyMap,
            emptyValue, Map.of(StatType.MAX_HEALTH, 0.025f, StatType.ATK, 0.85f),
            emptyValue,emptyMap,
            200,0,0,
            ""
    ));
    public static final Item DUR_125 = register("dur125", new SkillTooltipItem(
            new FabricItemSettings(),
            "액티브", SkillsId.DUR_125.getSkillName(),
            StatType.DUR,SkillsId.DUR_125.getSkillRequiredStatAmount(),
            "사용시 즉시 넓은 범위의 대상들에게 동상 효과를 부여하고 최대 5초동안 기운을 모은다. _lineBreak_" +
                    "기운을 모으는 동안 해당 스킬의 범위가 증가하고 _lineBreak_" +
                    "1초마다 6초간 지속되는 _barrierFlag_ 의 보호막을 얻는다._lineBreak_" +
                    "스킬 재사용시 기운을 모은 시간에 비례한 피해를 입힌다._lineBreak_" +
                    "최소 피해량: _healFlag__lineBreak_" +
                    "최대 피해량: _damageFlag_",
            emptyValue,Map.of(StatType.MAX_HEALTH, ArcticSlam.maxDamageHpCoeffi,StatType.DEF, ArcticSlam.maxDamageDefCoeffi,StatType.ATK, ArcticSlam.maxDamageAtkCoeffi),
            emptyValue,Map.of(StatType.MAX_HEALTH, ArcticSlam.barrierHpcoeffi, StatType.ATK, ArcticSlam.barrierAtkcoeffi),
            emptyValue,Map.of(StatType.MAX_HEALTH, ArcticSlam.damageHpPerSecondCoeffi,StatType.DEF, ArcticSlam.damageDefPerSecondCoeffi,StatType.ATK, ArcticSlam.damageAtkPerSecondCoeffi),
            ArcticSlam.cooltime,0,0,
            "동상: 총 이동속도가 50% 이상 감소하고 주기적으로 2의 피해를 입힌다."
    ));
    public static final Item DUR_150 = register("dur150", new SkillTooltipItem(
            new FabricItemSettings(),
            "패시브", SkillsId.DUR_150.getSkillName(),
            StatType.DUR,SkillsId.DUR_150.getSkillRequiredStatAmount(),
            "보호막을 얻을 시 _damageFlag_ + 보호막 수치의 30% 의 피해를 주변에 입히고 _lineBreak_" +
                    "1.5초동안 이동속도를 15% 감소시킨다.",
            emptyValue,Map.of(StatType.ATK, 0.8f),
            emptyValue,emptyMap,
            emptyValue,emptyMap,
            -1,0,0,
            ""
    ));
    public static final Item DUR_175 = register("dur175", new SkillTooltipItem(
            new FabricItemSettings(),
            "액티브", SkillsId.DUR_175.getSkillName(),
            StatType.DUR,SkillsId.DUR_175.getSkillRequiredStatAmount(),
            "사용시 이 스킬을 제외한 모든 스킬의 쿨타임을 10초 감소시키고 다음과 같은 효과를 얻는다.  _lineBreak_" +
                    "20초 동안 체력이 _healFlag_, 방어력이 _barrierFlag_ 감소하고 _lineBreak_" +
                    "감소한 총 수치의 5%인 _damageFlag_ 만큼 공격력을 얻으며 _lineBreak_" +
                    "이동속도, 공격속도가 50%, 방어력관통 20%, 입힌 피해 회복 25%을 얻고 스킬의 효과가 변경된다.",
            emptyValue,Map.of(StatType.MAX_HEALTH, 0.0185f, StatType.DEF, 0.03f),
            emptyValue,Map.of(StatType.DEF, 0.6f),
            emptyValue,Map.of(StatType.MAX_HEALTH, 0.35f),
            AllOutAttack.cooltime,0,0,
            ""
    ));
    public static final List<Item> strList = new ArrayList<>(Arrays.asList(STR_1, STR_25, STR_50, STR_75,STR_100, STR_125, STR_150, STR_175));
    public static final List<Item> dexList = new ArrayList<>(Arrays.asList(DEX_1, DEX_25, DEX_50, DEX_75, DEX_100, DEX_125, DEX_150, DEX_175));
    public static final List<Item> lukList = new ArrayList<>(Arrays.asList(LUK_1, LUK_25, LUK_50, LUK_75, LUK_100, LUK_125, LUK_150, LUK_175));
    public static final List<Item> durList = new ArrayList<>(Arrays.asList(DUR_1, DUR_25, DUR_50, DUR_75,DUR_100, DUR_125, DUR_150, DUR_175));
    public static List<Item> getStatTypeSkillTooltipItems(StatType statType) {
        switch (statType) {
            case STR -> {
                return strList;
            }
            case DEX -> {
                return dexList;
            }
            case LUK -> {
                return lukList;
            }
            case DUR -> {
                return durList;
            }
            default -> throw new IllegalStateException("Unexpected value: " + statType);
        }
    }

    public static Item register(String name, Item item){
        MAP.put(name, item);
        return item;
    }
    public static void registerAll(){
        MAP.forEach((name, item) -> Registry.register(Registries.ITEM, new Identifier(MODID, name), item));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(entries -> MAP.values().forEach(item -> entries.add(item.getDefaultStack())));
    }

}
