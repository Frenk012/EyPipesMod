# EyPipes Mod Configuration Guide

## Overview
The EyPipes mod now includes a comprehensive configuration system that allows you to customize various aspects of the mod's behavior without modifying the source code.

## Configuration File Location
The configuration file is automatically created at:
```
<minecraft_directory>/config/eypipes_config.json
```

## How to Use

### 1. Initial Setup
- When you first run the mod, it will automatically create a default configuration file
- The file contains all available settings with their default values and descriptions

### 2. Editing the Configuration
- Open the `eypipes_config.json` file in any text editor
- Modify the values you want to change
- Save the file

### 3. Reloading Configuration In-Game
After editing the configuration file, you can reload it without restarting Minecraft:

**Command:** `/eypipes reload`

**Requirements:**
- You must be an operator (OP level 2 or higher)
- The command can be used in both single-player and multiplayer

**Usage:**
1. Edit the config file
2. Save your changes
3. Run `/eypipes reload` in-game
4. Changes take effect immediately!

## Configuration Categories

### Crop Settings
- `growth_chance`: How likely crops are to grow each tick (0.0-1.0)
- `max_age`: Maximum age before crops are harvestable
- `grow_upper_age`: Age when crops start growing the upper part
- `requires_support`: Whether crops need solid blocks below

### Drying Rack Settings
- `drying_time_ticks`: Time for items to dry (6000 = 5 minutes)
- `max_items`: Maximum items per drying rack
- `requires_sunlight`: Whether drying needs sunlight

### Loot Settings
- `erbapipa_drop_rolls`: Base rolls for erbapipa/seeds drop pool (0.0-1.0)
- `seeds_bonus_rolls`: Additional rolls for bonus seeds when crop is mature (0.0-1.0)
- `seeds_fortune_probability`: Probability modifier for Fortune enchantment on seed drops (0.0-1.0)
- `fortune_affects_drops`: Whether Fortune enchantment affects drop rates
- `erbapipa_drop_percentage`: Percentage chance to drop erbapipa when crop is mature (0.0-100.0)
- `seeds_drop_percentage`: Percentage chance to drop seeds when harvesting (0.0-100.0)

**Note**: Loot table changes now work with runtime reloading! Use `/eypipes reload` to apply changes immediately.

### Recipe Settings
- `cutting_output_count`: Number of items produced when cutting
- `enable_knife_tag_support`: Use conventional knife tags

### Compost Settings
- `erbapipa_seeds_compost_chance`: Compost level increase chance
- `erbapipa_compost_chance`: Compost level increase chance

### Gameplay Settings
- `enable_tall_crop_mechanics`: Enable 2-block tall crops
- `breaking_upper_breaks_lower`: Breaking behavior
- `fertilizer_effectiveness`: Bone meal effectiveness multiplier

### Performance Settings
- `tick_rate_multiplier`: Processing speed multiplier
- `max_drying_racks_per_chunk`: Limit drying racks per chunk

### Integration Settings
- `jei_integration`: Show recipes in JEI
- `trinkets_integration`: Enable Trinkets mod support for head slot equipment

### Debug Settings
- `enable_debug_logging`: Detailed logging
- `show_growth_particles`: Visual growth effects

## Tips

1. **Backup your config**: Keep a backup of your customized configuration
2. **Test changes**: Use `/eypipes reload` to test changes quickly
3. **Valid values**: Make sure to use valid JSON syntax and appropriate value ranges
4. **Comments**: The `_comment` and `_description` fields are for documentation only

## Troubleshooting

### Config not loading
- Check that the JSON syntax is valid
- Ensure file permissions allow reading
- Check the game logs for error messages

### Command not working
- Make sure you have operator permissions
- Verify the command syntax: `/eypipes reload`
- Check that the mod is properly installed

### Changes not taking effect
- Some changes may require a world reload
- Ensure you saved the config file before reloading
- Check the console for any error messages

## Example Configuration Changes

### Faster Crop Growth
```json
"crop_settings": {
    "growth_chance": 0.5,
    "fertilizer_effectiveness": 2.0
}
```

### Quicker Drying
```json
"drying_rack_settings": {
    "drying_time_ticks": 3000
}
```

### More Generous Drops
```json
"loot_settings": {
    "erbapipa_drop_chance": 0.8,
    "seeds_drop_chance": 0.6
}
```

Remember to run `/eypipes reload` after making any changes!