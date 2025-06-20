package com.altale.esperis.client.packet;
import com.altale.esperis.client.cache.AbsorptionCache;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

public class AbsorptionSyncReceiver {
    public static final Identifier ID = new Identifier("esperis", "absorption_sync");
    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(ID, (client, handler, buf, responseSender) -> {
            int entityId = buf.readInt();
            float absorption = buf.readFloat();

            client.execute(() -> {
                Entity entity = client.world.getEntityById(entityId);
                if (entity instanceof LivingEntity living) {
                    // 임시 맵에 저장하거나 UI에 즉시 반영
                    AbsorptionCache.setAbsorption(living, absorption);
                }
            });
        });
    }
}
