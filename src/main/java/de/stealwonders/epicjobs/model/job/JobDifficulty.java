package de.stealwonders.epicjobs.model.job;

public enum JobDifficulty {

	EASY("★"),
	MEDIUM("★★"),
	HARD("★★★");

	private final String textDecoration;

	JobDifficulty(final String textDecoration) {
		this.textDecoration = textDecoration;
	}

	public String getTextDecoration() {
		return textDecoration;
	}

}