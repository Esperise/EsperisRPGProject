package com.altale.esperis.skills.coolTime;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

public class CoolTimeTickManager {
    public static void register(){
        ServerTickEvents.END_SERVER_TICK.register(server->{
            CoolTimeManager.tick();
        });
    }
}
