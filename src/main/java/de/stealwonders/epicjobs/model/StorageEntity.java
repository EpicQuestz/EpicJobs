package de.stealwonders.epicjobs.model;

public abstract class StorageEntity {

    private final int id;
    private final long creationTime;
    private long updateTime;

    public StorageEntity(int id, long creationTime, long updateTime) {
        this.id = id;
        this.creationTime = creationTime;
        this.updateTime = updateTime;
    }

    public int getId() {
        return id;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

}
