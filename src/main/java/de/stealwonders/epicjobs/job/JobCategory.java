package de.stealwonders.epicjobs.job;

public enum JobCategory {

    TERRAIN,
    INTERIOR,
    STRUCTURE,
    NATURE,
    DECORATION,
    REMOVAL,
    OTHER;

    public static JobCategory getJobCategoryByName(final String name) {
        for (final JobCategory jobCategory : JobCategory.values()) {
            if (jobCategory.toString().equalsIgnoreCase(name)) {
                return jobCategory;
            }
        }
        return null;
    }

}
