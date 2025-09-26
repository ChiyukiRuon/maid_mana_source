# 女仆魔源

> [!NOTE]  
> 这是 1.21.1 neoforge 版本的文档，1.20.1 forge 的文档请看[这里](https://github.com/ChiyukiRuon/maid_mana_source/blob/main/README.md)

简体中文 | [English](README_EN.md)

这是一个 [车万女仆(TLM)](https://github.com/TartaricAcid/TouhouLittleMaid) 与 [新生魔艺](https://github.com/baileyholl/Ars-Nouveau) 的联动模组，它为女仆添加了名为 "魔源" 的任务，让你的女仆可以给你的魔源罐充能。

---

## 游戏内设置

在选择了魔源作为女仆的任务后，可以在任务配置中进行以下配置

### 充能模式

- **单独**(默认)：女仆一次仅对一个魔源罐进行充能，充能后进入冷却
- **批量**：女仆一次对工作范围内所有可充能的魔源罐进行充能，但魔力会根据充能的数量平均分配

### 充能策略

> [!NOTE]
> 该配置仅在充能模式为 `单独` 时有效

- **轮询**(默认)：女仆对每个魔源罐轮流充能
- **顺序**：仅当前一个魔源罐充满后才对下一个魔源罐充能

## 配置文件

| 配置项                          | 类型        | 默认值     | 说明                                 |
|------------------------------|-----------|---------|------------------------------------|
| `coolingTime`                | `int`     | `200`   | 每次充能后的基础冷却时间（单位：tick）              |
| `maxPerCharge`               | `int`     | `200`   | 每次充能最多添加到魔源罐的魔力值                   |
| `scanInterval`               | `int`     | `200`   | 魔女仆自动搜索魔源罐的间隔时间（单位：tick）           |
| `enableFavorEffect`          | `boolean` | `false` | 是否启用好感度影响充能效率与冷却时间                 |
| `favorChargeBonus`           | `int`     | `100`   | 每级好感度增加的额外充能量（仅在启用好感度功能时生效）        |
| `favorCooldownReduction`     | `int`     | `20`    | 每级好感度减少的冷却时间（单位：tick，仅在启用好感度功能时生效） |
| `enableHighlightPenetration` | `boolean` | `true`  | 高亮与提示文本是否穿透方块渲染                    |
| `listOverlayYOffset`         | `double`  | `1.3`   | 提示文本的Y轴偏移量                         |
| `numbersOnly`                | `boolean` | `false` | 提示文本只显示数字                          |
| `chargeParticleCount`        | `int`     | `20`    | 充能时的粒子播放数量                         |
| `chargeParticleRadius`       | `double`  | `0.5`   | 充能时的粒子播放半径                         |
| `maidTaskSound`              | `boolean` | `true`  | 女仆开始/结束充能时播放语音                     |
| `chargingCompletedSound`     | `boolean` | `true`  | 充满每个魔源罐时播放音效                       |

## 鸣谢
- [TartaricAcid](https://github.com/TartaricAcid)
- [zxy19](https://github.com/zxy19)
- ChatGPT

## 许可
[MIT](LICENSE)