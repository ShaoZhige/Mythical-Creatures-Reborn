# Mythical Creatures Reborn

《小马宝莉》主题的《神话生物》重制版 —— 面向 Minecraft **1.20.1 / Forge** 的重制移植。

原版《神话生物》停留在 1.12.2，本重制版按 1.20.1 的机制与 API 重新设计：生物、装备、投掷物、
套装效果与配置系统都是重新实现，并非逐行搬运。当前版本 **0.11-beta-hotfix**。

> 状态：Beta。仍在调整，更新前请备份存档。

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
| GeckoLib | 4.4.9（必须安装，缺失时模组会在启动期直接报错并中止加载） |
| Curios API | 5.x（可选，仅「可爱标志」饰品相关功能需要） |

GeckoLib 在 `mods.toml` 里刻意声明为可选且不限版本，只为兼容原生 Forge 版与经 Sinytra Connector
运行的 Fabric 版（两者公开 API 一致）；实际仍是必装，缺失时加载阶段会抛出可读的中英文错误并中止，
避免出现随机 `NoClassDefFoundError`。

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

## 致谢

- 原作者：`HurricaneSHLBHP`
- 重制与维护：`Shao_Zhige`、`fffqu`、`miao888`、`Holy Light Radiance`
- GeckoLib：`software.bernie.geckolib`

## 许可

MIT License，见 [LICENSE](LICENSE)。使用或分发时请保留版权声明；二次修改与整合包收录无需另行申请。
