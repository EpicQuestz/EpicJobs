package com.epicquestz.epicjobs.constants;

public enum SkullHeads {

    OAK_WOOD_ARROW_LEFT("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmQ2OWUwNmU1ZGFkZmQ4NGU1ZjNkMWMyMTA2M2YyNTUzYjJmYTk0NWVlMWQ0ZDcxNTJmZGM1NDI1YmMxMmE5In19fQ==");

    private final String base64;

    SkullHeads(final String base64) {
        this.base64 = base64;
    }

    public String getBase64() {
        return base64;
    }
}
