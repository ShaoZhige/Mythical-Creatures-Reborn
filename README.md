# Mythical Creatures Reborn

《小马宝莉》主题的《神话生物》重制版 —— 面向 Minecraft **1.20.1 / Forge** 的重制移植。

原版《神话生物》停留在 1.12.2，本重制版按 1.20.1 的机制与 API 重新设计：生物、装备、投掷物、
套装效果与配置系统都是重新实现的，并非逐行搬运。当前版本 **0.11-beta-hotfix**。

> 状态：Beta。数值仍在调整，更新前请备份存档。

---

## 内容规模

| 内容 | 数量 |
| --- | --- |
| 实体注册（生物 + 投掷物） | 52 |
| 物品 | 195 |
| 方块 | 17 |
| 状态效果 | 2（流血、修复） |
| 语言条目（`zh_cn`） | 472 |

生物涵盖紫悦、云宝、碧琪、苹果嘉儿、柔柔、珍奇等主角小马，以及雪魔、九头蛇、利维坦、
蝎尾狮、乌萨、鸡蛇、帝王蝎、彩虹蜈蚣等敌对生物。多数生物为 GeckoLib 骨骼动画，
模型工程见 [`blockbench/`](blockbench)。

---

## 环境要求

| 项目 | 要求 |
| --- | --- |
| Minecraft | 1.20.1 |
| Forge | 47.x（开发环境 47.2.0） |
| Java | 17（客户端与服务端都必须） |
| GeckoLib | 4.4.9（**强烈建议**，缺失时生物无法播放动画） |
| Curios API | 5.x（可选，仅「可爱标志」饰品相关功能需要） |

GeckoLib 在 `mods.toml` 里声明为**可选**依赖且不限制版本，以便同时兼容原生 Forge 版
与经 Sinytra Connector 运行的 Fabric 版（两者公开 API 一致）。是否真的可用由启动期自检把关，
缺失时会在日志和聊天栏给出中英文提示。

---

## 安装

1. 安装 Minecraft 1.20.1 + 对应 Forge 47.x。
2. 把 `mythical_creatures_reborn-<版本>.jar` 放进 `mods/`。
3. 一并放入 `geckolib-forge-1.20.1-4.4.9.jar`（可选但建议）；需要饰品功能再加 Curios。
4. **联机时客户端与服务端都要装**，服务端同样需要 Java 17 与 GeckoLib。

---

## 操作

| 按键 | 功能 | 说明 |
| --- | --- | --- |
| `左 Shift` | 技能键 | 武器第二技能、工具特殊交互 |
| `V` | 坐骑下降 | 飞行坐骑下降 |
| `Ctrl + H` | 配置编辑器 | 需 OP 权限等级 2；单人存档默认满足 |

三个键都可以在「选项 → 控制」里改绑。

---

## 配置

- **服务端配置**（`config/mythical_creatures_reborn-common.toml`）
  `defaults` 段为默认数值，`overrides` 段为实际生效的覆盖值，两边分离，升级时默认值可以安全更新。
- **可视化编辑器**：`Ctrl + H` 打开，按生物编辑生命值、攻击力、护甲、移速、刷新权重等，
  保存后同步给所有玩家；也提供 `/mythical_creatures_reborn config_edit` 命令入口。
- **客户端配置**（`config/mythical_creatures_reborn-client.toml`）
  可爱标志渲染等纯客户端选项，支持热重载，不影响服务端。

配置保存需要 OP 权限；编辑器里的「恢复默认」是把它从 `overrides` 里移除，而不是写入默认值。

---

## 从源码构建

```bash
./gradlew build
```

产物：`build/libs/mythical_creatures_reborn-<mod_version>.jar`。
版本号只在 `gradle.properties` 的 `mod_version` 一处定义，`build.gradle` 与 `mods.toml` 通过占位符引用。

依赖（ForgeGradle、GeckoLib、Curios）需联网拉取一次；本地 `gradle.properties` 里的
`org.gradle.java.installations.paths` 是**本机路径**，克隆后请按自己环境改掉或删除这一行。

---

## 仓库结构

```
src/main/java/com/shao/mythical_creatures_reborn/
  block/    方块与方块实体
  client/   渲染、模型、界面、键位、客户端配置
  command/  命令
  config/   服务端配置与生物数值管理
  effect/   状态效果
  entity/   生物与投掷物（基类 ModThrowableProjectile）
  event/    事件处理（套装效果、工具交互等）
  item/     物品、装备、套装加成
  mixin/    ItemStack 相关 mixin
  network/  配置编辑、坐骑操控等网络包
  sound/    音效注册
  util/     工具类
src/main/resources/   资源：模型、动画、贴图、配方、战利品表、语言文件
blockbench/           Blockbench 模型工程（entity/ 生物，items/ 物品）
```

`blockbench/` 里是可直接用 Blockbench 打开的 `.bbmodel` 工程文件，用于修改模型后重新导出
`.geo.json` 与 `.animation.json`。注意该目录含高分辨率贴图，体积较大。

---

## 已知问题

- Beta 阶段数值不平衡属正常现象，欢迎反馈具体生物与场景。
- 与 **Chloride** + 新版 **JEI** 同时安装时，客户端可能在进入世界后崩溃
  （`InvalidMixinException: configButton`），这是 Chloride 的 JEI 兼容 mixin 与新 JEI 不匹配，
  与本模组无关；把 JEI 降级或在 Chloride 适配前先移除即可。
- 服务端必须使用 Java 17；用 Java 21+ 运行 Forge 47.2.0 出现的崩溃不受支持。

---

## 致谢

- 原作者与原《神话生物》模组团队：`fffqu`、`miao888`、`Holy Light Radiance`
- 重制与维护：`Shao_Zhige`
- GeckoLib：`software.bernie.geckolib`

## 许可

MIT License，见 [LICENSE](LICENSE)。使用或分发时请保留版权声明；二次修改与整合包收录无需另行申请。
