package de.stealwonders.epicjobs.model.job;

import org.bukkit.Material;

public enum JobCategory {

    TERRAIN("Terrain", Material.GRASS_BLOCK),
    INTERIOR("Interior", Material.PAINTING),
    STRUCTURE("Structure", Material.SCAFFOLDING),
    NATURE("Nature", Material.SUNFLOWER),
    DECORATION("Decoration", Material.FLOWER_POT),
    REMOVAL("Removal", Material.BARRIER),
    OTHER("Other", Material.DRIED_KELP);

    private final String name;
    private final Material material;

    JobCategory(final String name, final Material material) {
        this.name = name;
        this.material = material;
    }

    public String getName() {
        return name;
    }

    public Material getMaterial() {
        return material;
    }

    public static JobCategory getJobCategoryByName(final String name) {
        for (final JobCategory jobCategory : JobCategory.values()) {
            if (jobCategory.toString().equalsIgnoreCase(name)) {
                return jobCategory;
            }
        }
        return null;
    }

}
