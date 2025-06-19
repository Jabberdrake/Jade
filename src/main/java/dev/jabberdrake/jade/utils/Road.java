package dev.jabberdrake.jade.utils;

import org.bukkit.Material;

public record Road(double speedModifier, Material topMaterial, Material middleMaterial, Material bottomMaterial) {

    private static final double DEFAULT_SPEED_MODIFIER = 5;
    private static final Material DEFAULT_TOP_MATERIAL = null;

    public Road(Material middleMaterial, Material bottomMaterial) {
        this(DEFAULT_SPEED_MODIFIER, DEFAULT_TOP_MATERIAL, middleMaterial, bottomMaterial);
    }

    public Road(Material topMaterial, Material middleMaterial, Material bottomMaterial) {
        this(DEFAULT_SPEED_MODIFIER, topMaterial, middleMaterial, bottomMaterial);
    }

    public boolean match(Material topMaterial, Material middleMaterial, Material bottomMaterial) {
        return matchTopMaterial(topMaterial) && matchMiddleMaterial(middleMaterial) && matchBottomMaterial(bottomMaterial);
    }

    public boolean matchTopMaterial(Material topMaterial) {
        if (this.topMaterial() == null) {
            return true;
        } else {
            return (this.topMaterial().equals(topMaterial));
        }
    }

    public boolean matchMiddleMaterial(Material middleMaterial) {
        if (this.middleMaterial() == null) {
            return true;
        } else {
            return (this.middleMaterial().equals(middleMaterial));
        }
    }

    public boolean matchBottomMaterial(Material bottomMaterial) {
        if (this.bottomMaterial() == null) {
            return true;
        } else {
            return (this.bottomMaterial().equals(bottomMaterial));
        }
    }

}
