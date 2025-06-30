# Maid Mana Source

[简体中文](README.md) | English

This is a crossover mod between [Touhou Little Maid (TLM)](https://github.com/TartaricAcid/TouhouLittleMaid) and [Ars Nouveau](https://github.com/baileyholl/Ars-Nouveau). It adds a new maid task called "Mana Source", allowing your maid to charge Source Jars for you.

---

## In-Game Settings

After selecting Mana Source as the maid's task, the following settings are available in the task configuration:

### Charge Mode

- **Single**(default): The maid charges one source jar at a time and enters cooldown after each charge
- **Multi**: The maid charges all available source jars within range simultaneously, but the total mana is evenly split among them

### Charge Strategy

> [!NOTE]
> This option is only effective when Charge Mode is set to `Single`

- **Polling**(default)：The maid charges source jars in a round-robin manner
- **Sequential**：The maid only charges the next jar after the current one is full

## Configuration

| Config                   | Type    | Default | Description                                                       |
|--------------------------|---------|---------|-------------------------------------------------------------------|
| `coolingTime`            | int     | `200`   | Base cooldown time (in ticks) after each mana charging action     |
| `maxPerCharge`           | int     | `200`   | Maximum mana amount added to a source jar per charge              |
| `scanInterval`           | int     | `200`   | Interval (in ticks) between maid scanning for nearby source jars  |
| `enableFavorEffect`      | boolean | `false` | Whether maid's favorability affects charging amount and cooldown  |
| `favorChargeBonus`       | int     | `100`   | Extra mana added per favorability level (requires favor effect)   |
| `favorCooldownReduction` | int     | `20`    | Cooldown reduction per favorability level (requires favor effect) |

## Botania Support

By using [Ars Botania](https://github.com/zxy19/ars_botania), you can enable mana charging for Botania's Mana Pools.

## Thanks
- [TartaricAcid](https://github.com/TartaricAcid)
- [zxy19](https://github.com/zxy19)
- ChatGPT

## License
[MIT](LICENSE)