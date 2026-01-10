package frenk.eypipes;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.block.ComposterBlock;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import frenk.eypipes.item.EyPipesItems;
import frenk.eypipes.config.EyPipesConfig;
import frenk.eypipes.block.EyPipesBlocks;
import frenk.eypipes.command.ReloadConfigCommand;
import frenk.eypipes.sound.EyPipesSound;
import frenk.eypipes.particles.EyPipesParticleTypes;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.block.CropBlock;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.BlockStatePropertyLootCondition;
import net.minecraft.loot.entry.AlternativeEntry;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.ApplyBonusLootFunction;
import net.minecraft.loot.function.ExplosionDecayLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.predicate.StatePredicate;

public class EyPipes implements ModInitializer {
	public static final String MOD_ID = "eypipes";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final ItemGroup EY_PIPES_GROUP = FabricItemGroupBuilder.create(
		new Identifier(EyPipes.MOD_ID, "general"))
		.icon(() -> new ItemStack(Items.WHEAT_SEEDS))
		.build();

	@Override
	public void onInitialize() {
		LOGGER.info("Hello from EyPipes!");
		
		// Load configuration first
		EyPipesConfig.loadConfig();
		
		EyPipesItems.init();
        EyPipesSound.registerModSounds();
        EyPipesParticleTypes.registerParticleType();
        PipesEntities.init();
        PipesEntities.registerBlocks();
        PipesEntities.registerBlockEntities();
		
		// Register compostables with config values
		registerCompostables();
		
		// Register commands
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			ReloadConfigCommand.register(dispatcher);
		});
		
		// Register runtime loot table modifications
		registerLootTableModifications();
		
		// Register server tick event for pipe durability
		registerPipeDurabilityTick();
	}

	public static void registerCompostables() {
		ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.put(EyPipesItems.ERBAPIPA_SEEDS, EyPipesConfig.ERBAPIPA_SEEDS_COMPOST_CHANCE);
		ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.put(EyPipesItems.ERBAPIPA, EyPipesConfig.ERBAPIPA_COMPOST_CHANCE);
	}
	
	public static void registerLootTableModifications() {
		LootTableEvents.REPLACE.register((resourceManager, lootManager, id, original, source) -> {
			// Override erbapipa crop loot table with config values
			if (id.equals(new Identifier(MOD_ID, "blocks/erbapipa_crop"))) {
				// Create mature condition
				BlockStatePropertyLootCondition.Builder matureCondition = BlockStatePropertyLootCondition.builder(EyPipesBlocks.ERBAPIPA_CROP)
						.properties(StatePredicate.Builder.create().exactMatch(CropBlock.AGE, 7));
				
				// Main drop pool - either erbapipa (if mature) or seeds
				LootPool.Builder mainPool = LootPool.builder()
						.rolls(ConstantLootNumberProvider.create((float) EyPipesConfig.ERBAPIPA_DROP_ROLLS))
						.with(AlternativeEntry.builder(
								ItemEntry.builder(EyPipesItems.ERBAPIPA)
										.conditionally(matureCondition),
								ItemEntry.builder(EyPipesItems.ERBAPIPA_SEEDS)
						))
						.apply(ExplosionDecayLootFunction.builder());
				
				// Bonus seeds pool - only when mature
				LootPool.Builder bonusPool = LootPool.builder()
						.rolls(ConstantLootNumberProvider.create((float) EyPipesConfig.SEEDS_BONUS_ROLLS))
						.conditionally(matureCondition)
						.with(ItemEntry.builder(EyPipesItems.ERBAPIPA_SEEDS)
								.apply(ApplyBonusLootFunction.binomialWithBonusCount(
										Enchantments.FORTUNE,
										(float) EyPipesConfig.SEEDS_FORTUNE_PROBABILITY,
										2
								))
						);
				
				// Create and return new loot table
				return LootTable.builder()
						.pool(mainPool)
						.pool(bonusPool)
						.build();
			}
			return null; // Use original table for other blocks
		});
	}
	
	// Pipe durability tick system temporarily disabled due to Trinkets API compatibility issues
	// TODO: Re-implement when Cardinal Components API dependency is resolved
	private static void registerPipeDurabilityTick() {
		// Durability system disabled - requires Cardinal Components API
	}
}