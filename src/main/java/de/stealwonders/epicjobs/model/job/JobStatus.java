package de.stealwonders.epicjobs.model.job;

public enum JobStatus {

    OPEN (4),
    TAKEN (1),
    DONE (2),
    COMPLETE (3);

    private final int weight;

    JobStatus(final int weight) {
        this.weight = weight;
    }

    public int getWeight() {
        return weight;
    }

}
