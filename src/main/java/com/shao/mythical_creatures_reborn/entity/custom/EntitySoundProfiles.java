package com.shao.mythical_creatures_reborn.entity.custom;

import com.shao.mythical_creatures_reborn.sound.ModSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 全模组生物语音总表 | The single source of truth for entity voices.
 *
 * <p>映射方案尽量照抄重制模组 {@code LegacySoundHelper}：</p>
 * <ul>
 *   <li>有专属台词的只有少数主角（云宝 / 阿杰环境音）；</li>
 *   <li>猛兽共用 beastroar / bosshurt / growl 等共享吼叫；</li>
 *   <li>一大半生物直接用原版音效（牛→野牛麋鹿、蠹虫→蜈蚣、烈焰人→蜘蛛、
 *       铁傀儡→雪魔蝎子鳄鱼、末影龙→九头蛇利维坦、鸡→凤凰、木头断裂→木狼……）；</li>
 *   <li>参考模组没有专属处理的生物保持静默或原版默认，不强行配音。</li>
 * </ul>
 *
 * <p>音量沿用参考模组的逐生物数值，但整体 × {@link #VOLUME_SCALE} 并封顶
 * {@link #VOLUME_CAP}（玩家反馈参考模组原值如 7.5 / 5.65 太响会被吓到）。
 * 音高保持参考模组原值不变。</p>
 */
public final class EntitySoundProfiles {

    /** 全局音量缩放：参考模组音量整体减半（被吓到好几次的反馈）。想再调就改这一个数。 */
    private static final float VOLUME_SCALE = 0.5F;
    /** 音量封顶：参考模组的极端值（木狼 7.5、大熊 5.65）收敛到这里。 */
    private static final float VOLUME_CAP = 2.0F;

    /** 一条语音配置 */
    public record Profile(Supplier<SoundEvent> ambient,
                          Supplier<SoundEvent> hurt,
                          Supplier<SoundEvent> death,
                          float volume, float pitch) {

        public SoundEvent ambientEvent() { return ambient == null ? null : ambient.get(); }
        public SoundEvent hurtEvent()    { return hurt    == null ? null : hurt.get(); }
        public SoundEvent deathEvent()   { return death   == null ? null : death.get(); }
    }

    private static final Map<String, Profile> MAP = new HashMap<>();

    private EntitySoundProfiles() {}

    /** 模组注册的音效（延迟解析，避免注册完成前取值） */
    private static Supplier<SoundEvent> mod(RegistryObject<SoundEvent> r) { return r == null ? null : r::get; }
    /** 原版音效（SoundEvents 静态常量，随时可用） */
    private static Supplier<SoundEvent> mc(SoundEvent s) { return () -> s; }

    /** refVol 是参考模组的原始音量，落表时统一缩放并封顶。 */
    private static void put(String id, Supplier<SoundEvent> amb, Supplier<SoundEvent> hurt,
                            Supplier<SoundEvent> death, float refVol, float pitch) {
        MAP.put(id, new Profile(amb, hurt, death, Math.min(refVol * VOLUME_SCALE, VOLUME_CAP), pitch));
    }

    static {
        /* ── 主角小马（参考：云宝/阿杰有专属环境音，紫悦无环境音；受伤/死亡 = 玩家受伤音）── */
        put("mythical_creatures_reborn:twilight_sparkle",
                null, mc(SoundEvents.PLAYER_HURT), mc(SoundEvents.PLAYER_HURT), 0.5F, 1.0F);
        put("mythical_creatures_reborn:rainbow_dash",
                mod(ModSounds.RAINBOW_DASH_AMBIENT), mc(SoundEvents.PLAYER_HURT),
                mc(SoundEvents.GENERIC_EXPLODE), 0.5F, 1.0F); // 参考用 rainboom.ogg，我们没有，爆炸音替代
        put("mythical_creatures_reborn:applejack",
                mod(ModSounds.APPLEJACK_AMBIENT), mc(SoundEvents.PLAYER_HURT), mc(SoundEvents.PLAYER_HURT), 0.5F, 1.0F);
        // 柔柔/碧琪/珍奇/圣光：参考模组无条目，按其余小马同款处理
        for (String id : new String[]{"mythical_creatures_reborn:fluttershy", "mythical_creatures_reborn:pinkie_pie",
                                      "mythical_creatures_reborn:rarity", "mythical_creatures_reborn:holy_light_radiance"}) {
            put(id, null, mc(SoundEvents.PLAYER_HURT), mc(SoundEvents.PLAYER_HURT), 0.5F, 1.0F);
        }
        // 紫悦之杖召唤的魔法团：存在时间短、数量多，保持静默
        put("mythical_creatures_reborn:twilight_magic", null, null, null, 0.8F, 1.2F);

        /* ── 小型生物 ────────────────────────────────────────────── */
        put("mythical_creatures_reborn:parasprite",
                null, mc(SoundEvents.PLAYER_HURT), mc(SoundEvents.PLAYER_HURT), 1.25F, 2.0F);
        put("mythical_creatures_reborn:mavis",
                null, mc(SoundEvents.BAT_HURT), mc(SoundEvents.BAT_DEATH), 1.0F, 2.0F);
        put("mythical_creatures_reborn:black_widow",
                null, mc(SoundEvents.BLAZE_HURT), mc(SoundEvents.BLAZE_DEATH), 2.5F, 4.0F);
        put("mythical_creatures_reborn:centipede",
                mc(SoundEvents.SILVERFISH_AMBIENT), mc(SoundEvents.SILVERFISH_HURT), mc(SoundEvents.SILVERFISH_DEATH), 2.5F, 1.0F);
        put("mythical_creatures_reborn:rainbow_centipede",
                null, mc(SoundEvents.IRON_GOLEM_HURT), mc(SoundEvents.IRON_GOLEM_HURT), 2.5F, 0.2F);

        /* ── 中型生物 ────────────────────────────────────────────── */
        put("mythical_creatures_reborn:bear",
                null, mod(ModSounds.SHARED_BEASTROAR), mod(ModSounds.SHARED_BEASTROAR), 1.2F, 0.8F);
        put("mythical_creatures_reborn:cockatrice",
                mod(ModSounds.SHARED_CRAGADILE_AMBIENT), mod(ModSounds.SHARED_BEASTROAR), mod(ModSounds.SHARED_BEASTROAR), 1.0F, 2.0F);
        put("mythical_creatures_reborn:buffalo",
                null, mc(SoundEvents.COW_HURT), mc(SoundEvents.COW_HURT), 1.5F, 1.0F);
        put("mythical_creatures_reborn:baby_moose",
                null, mc(SoundEvents.COW_HURT), mc(SoundEvents.COW_HURT), 0.9F, 3.5F);
        put("mythical_creatures_reborn:adult_moose",
                null, mc(SoundEvents.COW_HURT), mc(SoundEvents.COW_HURT), 1.5F, 0.6F);
        put("mythical_creatures_reborn:arctic_scorpion",
                null, mc(SoundEvents.IRON_GOLEM_HURT), mc(SoundEvents.IRON_GOLEM_HURT), 2.5F, 0.2F);
        put("mythical_creatures_reborn:timber_wolf",
                null, mc(SoundEvents.WOOD_BREAK), mc(SoundEvents.WOOD_BREAK), 7.5F, 1.0F); // 参考原值 7.5，已被封顶
        put("mythical_creatures_reborn:rhinoceros",
                null, mod(ModSounds.SHARED_GROWL), mod(ModSounds.SHARED_GROWL), 3.5F, 1.0F);
        put("mythical_creatures_reborn:cragadile",
                null, mc(SoundEvents.IRON_GOLEM_HURT), mc(SoundEvents.IRON_GOLEM_HURT), 2.5F, 0.3F);
        put("mythical_creatures_reborn:iron_will",
                null, mod(ModSounds.SHARED_BEASTROAR), mod(ModSounds.SHARED_BEASTROAR), 1.8F, 1.0F);
        put("mythical_creatures_reborn:tough_guy",
                mc(SoundEvents.IRON_GOLEM_STEP), mod(ModSounds.SHARED_TOUGHPAIN), mod(ModSounds.SHARED_TOUGHDEATH), 1.5F, 1.0F);
        put("mythical_creatures_reborn:skull_of_doom",
                null, mc(SoundEvents.SKELETON_DEATH), mc(SoundEvents.SKELETON_DEATH), 1.5F, 0.5F);
        put("mythical_creatures_reborn:robot_sombra",
                null, mc(SoundEvents.IRON_GOLEM_HURT), mc(SoundEvents.IRON_GOLEM_DEATH), 1.5F, 0.8F);

        /* ── 精英 ────────────────────────────────────────────────── */
        put("mythical_creatures_reborn:manticore",
                mod(ModSounds.SHARED_CRAGADILE_AMBIENT), mod(ModSounds.SHARED_BEASTROAR), mod(ModSounds.SHARED_BEASTROAR), 1.8F, 1.0F);
        put("mythical_creatures_reborn:garble", // 对应参考模组的 FLAME_DRAGON
                null, mod(ModSounds.SHARED_BOSSHURT), mod(ModSounds.SHARED_BOSSHURT), 2.75F, 1.0F);
        put("mythical_creatures_reborn:phoenix",
                mc(SoundEvents.CHICKEN_AMBIENT), mc(SoundEvents.CHICKEN_HURT), mc(SoundEvents.CHICKEN_HURT), 2.0F, 0.5F);
        put("mythical_creatures_reborn:kingbowser_9000",
                mod(ModSounds.SHARED_BEASTROAR), mc(SoundEvents.WITHER_HURT), mc(SoundEvents.WITHER_HURT), 2.0F, 0.5F);
        put("mythical_creatures_reborn:leviathan",
                mc(SoundEvents.ENDER_DRAGON_GROWL), mc(SoundEvents.ENDER_DRAGON_HURT), mc(SoundEvents.ENDER_DRAGON_HURT), 1.75F, 4.0F);
        put("mythical_creatures_reborn:chief_thunderhooves",
                null, mc(SoundEvents.COW_HURT), mc(SoundEvents.COW_HURT), 4.5F, 0.2F);
        put("mythical_creatures_reborn:prince_rutherford",
                null, mc(SoundEvents.COW_HURT), mc(SoundEvents.COW_HURT), 4.0F, 0.1F);

        /* ── Boss ────────────────────────────────────────────────── */
        put("mythical_creatures_reborn:hydra",
                mc(SoundEvents.ENDER_DRAGON_GROWL), mod(ModSounds.SHARED_BOSSHURT), mc(SoundEvents.ENDER_DRAGON_DEATH), 2.65F, 0.4F);
        put("mythical_creatures_reborn:crabzilla",
                null, mod(ModSounds.SHARED_BOSSHURT), mod(ModSounds.SHARED_BOSSHURT), 1.5F, 1.4F);
        put("mythical_creatures_reborn:ursa_major",
                mod(ModSounds.SHARED_URSAROAR), mc(SoundEvents.ANVIL_LAND), mod(ModSounds.SHARED_URSAROAR), 5.65F, 1.0F);
        put("mythical_creatures_reborn:windigo",
                mod(ModSounds.WINDIGO_AMBIENT), mc(SoundEvents.IRON_GOLEM_HURT), mod(ModSounds.SHARED_LIGHTNINGDEATH), 2.35F, 0.2F);
        put("mythical_creatures_reborn:spikezilla",
                mod(ModSounds.SHARED_BOSSHURT), mc(SoundEvents.ENDER_DRAGON_GROWL), mod(ModSounds.SHARED_FANFARE), 2.75F, 0.5F);
    }

    /** 兜底：表里没登记的生物用原版默认受伤/死亡音（与 vanilla Mob 行为一致），绝不返回 null。 */
    private static final Profile FALLBACK =
            new Profile(null, mc(SoundEvents.GENERIC_HURT), mc(SoundEvents.GENERIC_DEATH), 1.0F, 1.0F);

    /** 取某生物的语音配置（缺失时返回兜底，绝不返回 null）。 */
    public static Profile of(String entityId) {
        Profile p = MAP.get(entityId);
        return p != null ? p : FALLBACK;
    }
}
