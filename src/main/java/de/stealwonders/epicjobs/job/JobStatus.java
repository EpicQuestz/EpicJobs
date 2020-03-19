package de.stealwonders.epicjobs.job;

public enum JobStatus {

    OPEN (5),
    TAKEN (1),
    DONE (2),
    REOPENED (4),
    COMPLETE (3);

    private int weight;

    JobStatus(int weight) {
        this.weight = weight;
    }

    public int getWeight() {
        return weight;
    }
}
