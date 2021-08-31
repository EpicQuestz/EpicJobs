package de.stealwonders.epicjobs.model.job;

public enum JobDifficulty {

    EASY("★"),
    MEDIUM("★★"),
    HARD("★★★");

    private final String textDecor;

    JobDifficulty(final String textDecor) {
        this.textDecor = textDecor;
    }

    public String getTextDecor() {
        return textDecor;
    }

}
