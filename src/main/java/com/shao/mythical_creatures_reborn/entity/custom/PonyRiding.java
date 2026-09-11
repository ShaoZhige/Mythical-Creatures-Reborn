package com.shao.mythical_creatures_reborn.entity.custom;

import com.shao.mythical_creatures_reborn.config.MythicalConfig;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

/**
 * 小马骑乘组件：骑乘调参缓存 + 骑手定位。
 *
 * 从 PonyEntity 抽出。骑乘调参字段（speedFactor / vertical* / horizontalFactor / inertiaDecay /
 * jumpHeight）在此持有，包级可见，供同包的 FlightRideAPI / GroundRideAPI 直接读取（原本是
 * readSpeedFactor 等实体字段，改为 owner.riding.speedFactor）。
 *
 * 原骑乘 API 每 tick 都调 MythicalConfig.DATA.entityAttr(...)（字符串拼接 + 多次 HashMap 查找），
 * 是项目中最热的路径。这里在实体创建/入场时按 entityId 缓存一次，骑乘 API 之后直接读字段。
 * 重载配置后现存实体的缓存不变（与 max_health 等属性重载语义一致：仅新建实体生效）。
 *
 * Pony riding component: ride-tuning cache + rider seat placement, extracted from PonyEntity.
 * The ride-tuning fields live here (package-visible) so FlightRideAPI / GroundRideAPI read
 * owner.riding.speedFactor instead of touching entity fields directly.
 */
public final class PonyRiding {

    private final PonyEntity owner;

    // 每实体骑乘调参缓存（默认值即"无覆盖"手感，始终被 cacheRideTuning 覆盖）
    // Per-entity ride-tuning cache (defaults match the no-override feel; cacheRideTuning overwrites).
    double speedFactor      = 1.0;
    double verticalUp       = 0.0;
    double verticalDown     = 0.0;
    double verticalHover    = 0.0;
    double horizontalFactor = 1.0;
    double inertiaDecay     = 0.9;
    double jumpHeight       = 0.0;

    public PonyRiding(PonyEntity owner) {
        this.owner = owner;
    }

    /** 从配置读入骑乘调参并缓存（实体创建/入场 + 可视化编辑器改配置后调用）。 */
    public void cacheRideTuning(String entityId) {
        this.speedFactor      = MythicalConfig.DATA.entityAttr(entityId, "ridden_speed_factor");
        this.verticalUp       = MythicalConfig.DATA.entityAttr(entityId, "vertical_up");
        this.verticalDown     = MythicalConfig.DATA.entityAttr(entityId, "vertical_down");
        this.verticalHover    = MythicalConfig.DATA.entityAttr(entityId, "vertical_hover");
        this.horizontalFactor = MythicalConfig.DATA.entityAttr(entityId, "horizontal_factor");
        this.inertiaDecay     = MythicalConfig.DATA.entityAttr(entityId, "inertia_decay");
        this.jumpHeight       = MythicalConfig.DATA.entityAttr(entityId, "jump_height");
    }

    /**
     * 骑手定位：把乘客摆到实体背后 + 垂直偏移处，并同步朝向。
     * 由 PonyEntity.positionRider 在调完 super.positionRider 后委托进来（父类实现无法从外部调用）。
     */
    public void placeRider(@NotNull Entity passenger) {
        if (owner.hasPassenger(passenger)) {
            owner.yBodyRot = owner.getYRot();
            owner.setYHeadRot(passenger.getYHeadRot());
            owner.setYBodyRot(passenger.getYRot());
        }
        Vec3 back = owner.getLookAngle().scale(-owner.getRiderBackOffset());
        float riderY = owner.getRiderVerticalOffset();
        passenger.setPos(owner.getX() + back.x, owner.getY() + riderY, owner.getZ() + back.z);
    }

    /** 骑乘控制者：仅认已驯服的玩家主人。 */
    public Player getControllingPassenger() {
        for (Entity p : owner.getPassengers())
            if (p instanceof Player player && owner.isOwnedBy(player))
                return player;
        return null;
    }
}
