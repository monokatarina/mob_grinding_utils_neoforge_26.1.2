package mob_grinding_utils.network;

import mob_grinding_utils.MobGrindingUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.chicken.Chicken;
import net.minecraft.world.level.Level;

public final class MGUClientPackets {

    private MGUClientPackets() {
    }

    public static void handleChickenSync(ChickenSyncPacket message) {
        Minecraft minecraft = Minecraft.getInstance();
        Level level = minecraft.level;

        if (level == null || !level.isClientSide()) {
            return;
        }

        Entity entity =
                level.getEntity(message.chickenID());

        if (!(entity instanceof Chicken chicken)) {
            return;
        }

        CompoundTag messageTag = message.nbt();
        CompoundTag chickenTag =
                chicken.getPersistentData();

        boolean shouldExplode =
                messageTag.getBoolean(
                        "shouldExplode"
                ).orElse(false);

        int countDown =
                messageTag.getInt(
                        "countDown"
                ).orElse(0);

        chickenTag.putBoolean(
                "shouldExplode",
                shouldExplode
        );

        chickenTag.putInt(
                "countDown",
                countDown
        );

        if (countDown < 20) {
            return;
        }

        for (int index = 0; index < 20; index++) {
            double xSpeed =
                    level.getRandom().nextGaussian()
                            * 0.02D;

            double ySpeed =
                    level.getRandom().nextGaussian()
                            * 0.02D;

            double zSpeed =
                    level.getRandom().nextGaussian()
                            * 0.02D;

            double particleX =
                    chicken.getX()
                            + level.getRandom().nextFloat()
                            * chicken.getBbWidth()
                            * 2.0F
                            - chicken.getBbWidth();

            double particleY =
                    chicken.getY()
                            + level.getRandom().nextFloat()
                            * chicken.getBbHeight();

            double particleZ =
                    chicken.getZ()
                            + level.getRandom().nextFloat()
                            * chicken.getBbWidth()
                            * 2.0F
                            - chicken.getBbWidth();

            level.addParticle(
                    ParticleTypes.EXPLOSION,
                    particleX,
                    particleY,
                    particleZ,
                    xSpeed,
                    ySpeed,
                    zSpeed
            );

            level.addParticle(
                    ParticleTypes.LAVA,
                    particleX,
                    particleY,
                    particleZ,
                    xSpeed,
                    ySpeed,
                    zSpeed
            );
        }
    }

    public static void spawnGlitterParticles(
            double x,
            double y,
            double z,
            double velocityX,
            double velocityY,
            double velocityZ
    ) {
        Level level =
                Minecraft.getInstance().level;

        if (level == null) {
            return;
        }

        level.addParticle(
                MobGrindingUtils.PARTICLE_FLUID_XP.get(),
                x,
                y,
                z,
                velocityX,
                velocityY,
                velocityZ
        );
    }

    public static void handleFlagSyncPacket(
            FlagSyncPacket packet
    ) {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null) {
            return;
        }

        CompoundTag tag =
                minecraft.player.getPersistentData();

        tag.putBoolean(
                "MGU_WitherMuffle",
                packet.wither()
        );

        tag.putBoolean(
                "MGU_DragonMuffle",
                packet.dragon()
        );
    }
}

