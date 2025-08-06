package com.altale.esperis.client.SkillKeyBinding;

import com.altale.esperis.client.packet.SkillKeyBindingPacketSender;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallbackI;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;

public class SkillKeyBinding {
    static boolean callbackRegistered=false;
    private static final Map<KeyBinding, String> KEYBINDING_MAP = new HashMap<>();
    private static final Map<KeyBinding, Integer> KEYBINDING_COOLTIME_MAP= new HashMap<>();
    private static final Deque<KeyBinding> pressedKeys = new ConcurrentLinkedDeque<>();
    private static int clientTick = 0;
    private static  int lastGlobalUseTick = 0;
    private static final int globalCooldown = 6;

    public static void register(){
        final KeyBinding KEYBINDING_1 = KeyBindingHelper.registerKeyBinding(
                new KeyBinding("skill_key_1",
                        InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_X,
                        "key.category.esperis")
        );
        final KeyBinding KEYBINDING_2 = KeyBindingHelper.registerKeyBinding(
                new KeyBinding("skill_key_2",
                        InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_C,
                        "key.category.esperis")
        );
        final KeyBinding KEYBINDING_3 = KeyBindingHelper.registerKeyBinding(
                new KeyBinding("skill_key_3",
                        InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_V,
                        "key.category.esperis")
        );
        final KeyBinding KEYBINDING_4 = KeyBindingHelper.registerKeyBinding(
                new KeyBinding("skill_key_4",
                        InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_G,
                        "key.category.esperis")
        );
        final KeyBinding KEYBINDING_5 = KeyBindingHelper.registerKeyBinding(
                new KeyBinding("skill_key_5",
                        InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_TAB,
                        "key.category.esperis")
        );
        final KeyBinding KEYBINDING_6 = KeyBindingHelper.registerKeyBinding(
                new KeyBinding("skill_key_6",
                        InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_LEFT_ALT,
                        "key.category.esperis")
        );
        final KeyBinding KEYBINDING_7 = KeyBindingHelper.registerKeyBinding(
                new KeyBinding("skill_key_7",
                        InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_R,
                        "key.category.esperis")
        );
        KEYBINDING_MAP.put(KEYBINDING_1, "skill_key_1");
        KEYBINDING_MAP.put(KEYBINDING_2, "skill_key_2");
        KEYBINDING_MAP.put(KEYBINDING_3, "skill_key_3");
        KEYBINDING_MAP.put(KEYBINDING_4, "skill_key_4");
        KEYBINDING_MAP.put(KEYBINDING_5, "skill_key_5");
        KEYBINDING_MAP.put(KEYBINDING_6, "skill_key_6");
        KEYBINDING_MAP.put(KEYBINDING_7, "skill_key_7");



        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!callbackRegistered && client.getWindow() != null) {
                long handle = client.getWindow().getHandle();

                // 1) 이전 콜백을 담을 배열(또는 길이가 1인 래퍼 객체)
                final GLFWKeyCallbackI[] original = new GLFWKeyCallbackI[1];

                // 2) 새 콜백을 등록하면서 이전 콜백도 리턴받아 저장
                original[0] = GLFW.glfwSetKeyCallback(handle, (window, key, scancode, action, mods) -> {
                    // ▶ 여러분 로직: 눌린 키를 순서대로 큐에 추가
                    if (action == GLFW.GLFW_PRESS) {
                        for (Map.Entry<KeyBinding, String> e : KEYBINDING_MAP.entrySet()) {
                            if (e.getKey().matchesKey(key, scancode)) {
                                pressedKeys.addLast(e.getKey());
                                break;
                            }
                        }
                    }
                    // ▶ 꼭! 이전 콜백도 호출해서 기본 입력 처리 유지
                    if (original[0] != null) {
                        original[0].invoke(window, key, scancode, action, mods);
                    }
                });

                callbackRegistered = true;
            }


            clientTick++;

            while(  pressedKeys.peekFirst() != null) {
                if(clientTick - lastGlobalUseTick < globalCooldown) break;
                KeyBinding key = pressedKeys.pollFirst();
                if(key.wasPressed()){
                    SkillKeyBindingPacketSender.sendSkillKeyBindingPacket(
                            KEYBINDING_MAP.get(key),
                            false
                    );
                    lastGlobalUseTick = clientTick;
                }else if(key.isPressed()){
                    SkillKeyBindingPacketSender.sendSkillKeyBindingPacket(
                            KEYBINDING_MAP.get(key),
                            true
                    );
                }
            }





//            for(Map.Entry<KeyBinding, String> entry : KEYBINDING_MAP.entrySet()){
//
//                    if(entry.getKey().wasPressed()){
//                        //눌렀다 때었을때의 패킷을 전송
//                        SkillKeyBindingPacketSender.sendSkillKeyBindingPacket(
//                                entry.getValue(),
//                                false
//                        );
//                    } else if(entry.getKey().isPressed()){
//                        //누르고 있을때의 패킷 전송
//                        SkillKeyBindingPacketSender.sendSkillKeyBindingPacket(
//                                entry.getValue(),
//                                true
//                        );
//                    }
//
//
//
//
//            }
        });
    }

}
