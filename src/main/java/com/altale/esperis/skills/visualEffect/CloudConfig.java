package com.altale.esperis.skills.visualEffect;

import net.minecraft.particle.DustParticleEffect;

public class CloudConfig {
    final double distance;
    final float radius;
    final int durationTicks;
    final DustParticleEffect particle;

    CloudConfig(double distance, double radius, int durationTicks, DustParticleEffect particle) {
        this.distance = distance;
        this.radius = (float) radius;
        this.durationTicks = durationTicks;
        this.particle = particle;
    }
}
