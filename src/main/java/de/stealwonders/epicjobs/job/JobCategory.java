package de.stealwonders.epicjobs.job;

public enum JobCategory {

    TERRAIN,
    INTERIOR,
    STRUCTURE,
    NATURE,
    DECORATION,
    REMOVAL,
    OTHER;

    public static JobCategory getJobCategoryByName(String name) {
        for (JobCategory jobCategory : JobCategory.values()) {
            if (jobCategory.toString().equalsIgnoreCase(name)) {
                return jobCategory;
            }
        }
        return null;
    }

}
