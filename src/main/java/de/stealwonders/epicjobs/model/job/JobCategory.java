package de.stealwonders.epicjobs.model.job;

import org.bukkit.Material;
import org.checkerframework.checker.nullness.qual.Nullable;

public enum JobCategory {

    TERRAIN("Terrain", "Hills, rivers, other terraforming, etc", Material.GRASS_BLOCK),
    VEGETATION("Vegetation", "Gardens, forests, agriculture & general nature", Material.SUNFLOWER),
    PATHWAY("Pathway", "Roads, paths, trails, etc", Material.DIRT_PATH),
    ATMOSPHERE("Pathway", "Mainly for towns: street furniture & ambience", Material.CAMPFIRE),
    EXTERIOR_STRUCTURE("Exterior Structure", "Exterior structure, including walls & windows", Material.SCAFFOLDING),
    INTERIOR_STRUCTURE("Interior Structure", "Internal structure layout - walls, doors & floors", Material.OAK_DOOR),
    INTERIOR("Interior Decoration", "Furniture, internal ambience & decoration", Material.CANDLE),
    REMOVAL("Removal", "Thing go kaboom", Material.BARRIER),
    OTHER("Other", "Anything uncategorisable", Material.DRIED_KELP);

    private final String name;
    private final String description;
    private final Material material;

    JobCategory(final String name, final String description, final Material material) {
        this.name = name;
        this.description = description;
        this.material = material;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Material getMaterial() {
        return material;
    }

    public static @Nullable JobCategory getJobCategoryByName(final String name) {
        for (final JobCategory jobCategory : JobCategory.values()) {
            if (jobCategory.toString().equalsIgnoreCase(name)) {
                return jobCategory;
            }
        }
        return null;
    }

}
