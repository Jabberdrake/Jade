package dev.jabberdrake.jade.utils;

import dev.jabberdrake.jade.items.Rarity;
import io.papermc.paper.datacomponent.item.attribute.AttributeModifierDisplay;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.w3c.dom.Attr;

public class AttributeUtils {

    public static final double BASE_ATTACK_DAMAGE = 1.0;
    public static final double BASE_ATTACK_SPEED = 4.0;

    public static class ItemAttributeEntry {
        private Attribute attribute;
        private AttributeModifier modifier;
        private EquipmentSlotGroup equipmentSlotGroup;
        private AttributeModifierDisplay modifierDisplay;

        public ItemAttributeEntry(Attribute attribute, AttributeModifier modifier, AttributeModifierDisplay modifierDisplay) {
            this.attribute = attribute;
            this.modifier = modifier;
            this.equipmentSlotGroup = null;
            this.modifierDisplay = modifierDisplay;
        }

        public ItemAttributeEntry(Attribute attribute, AttributeModifier modifier, EquipmentSlotGroup equipmentSlotGroup, AttributeModifierDisplay modifierDisplay) {
            this.attribute = attribute;
            this.modifier = modifier;
            this.equipmentSlotGroup = equipmentSlotGroup;
            this.modifierDisplay = modifierDisplay;
        }

        public Attribute getAttribute() {
            return this.attribute;
        }

        public AttributeModifier getModifier() {
            return this.modifier;
        }

        public EquipmentSlotGroup getSlotGroup() {
            return this.equipmentSlotGroup;
        }

        public AttributeModifierDisplay getDisplay() {
            return this.modifierDisplay;
        }
    }

    public enum BaseAttributes {
        ARMOR("minecraft:armor", 0.0),
        ARMOR_TOUGHNESS("minecraft:armor_toughness", 0.0),
        ATTACK_DAMAGE("minecraft:attack_damage", 1.0),
        ATTACK_KNOCKBACK("minecraft:attack_knockback", 0.0),
        ATTACK_SPEED("minecraft:attack_speed", 4.0),
        BLOCK_BREAK_SPEED("minecraft:block_break_speed", 1.0),
        BLOCK_INTERACTION_RANGE("minecraft:block_interaction_range", 4.5),
        ENTITY_INTERACTION_RANGE("minecraft:entity_interaction_range", 3.0),
        FALL_DAMAGE_MULTIPLIER("minecraft:fall_damage_multiplier", 1.0),
        GRAVITY("minecraft:gravity", 0.08),
        JUMP_STRENGTH("minecraft:jump_strength", 0.42),
        KNOCKBACK_RESISTANCE("minecraft:knockback_resistance", 0.0),
        LUCK("minecraft:luck", 0.0),
        MAX_ABSORPTION("minecraft:max_absorption", 0.0),
        MAX_HEALTH("minecraft:max_health", 0.0),
        MINING_EFFICIENCY("minecraft:mining_efficiency", 0.0),
        MOVEMENT_EFFICIENCY("minecraft:movement_efficiency", 0.0),
        MOVEMENT_SPEED("minecraft:movement_speed", 0.1),
        OXYGEN_BONUS("minecraft:oxygen_bonus", 0.0),
        SAFE_FALL_DISTANCE("minecraft:safe_fall_distance", 3.0),
        SCALE("minecraft:scale", 1.0),
        SNEAKING_SPEED("minecraft:sneaking_speed", 1.0),
        SUBMERGED_MINING_SPEED("minecraft:submerged_mining_speed", 1.0),
        SWEEPING_DAMAGE_RATIO("minecraft:sweeping_damage_ratio", 1.0),
        WATER_MOVEMENT_EFFICIENCY("minecraft:water_movement_efficiency", 0.0);

        private String key;
        private double value;

        BaseAttributes(String key, double value) {
            this.key = key;
            this.value = value;
        }

        public String getKeyAsString() {
            return this.key;
        }

        public double getValue() {
            return this.value;
        }

        public static BaseAttributes fromVanillaAttribute(Attribute vanillaAttribute) {
            for (BaseAttributes attribute : BaseAttributes.values()) {
                if (attribute.getKeyAsString().equalsIgnoreCase(vanillaAttribute.toString())) {
                    return attribute;
                }
            }

            return null;
        }
    }
}
