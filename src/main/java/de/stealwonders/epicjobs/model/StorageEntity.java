package de.stealwonders.epicjobs.model;

public abstract class StorageEntity {

    private final long id;
    private final long creationTime;
    private long updateTime;

    public StorageEntity(final long id, final long creationTime, final long updateTime) {
        this.id = id;
        this.creationTime = creationTime;
        this.updateTime = updateTime;
    }

    public long getId() {
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
