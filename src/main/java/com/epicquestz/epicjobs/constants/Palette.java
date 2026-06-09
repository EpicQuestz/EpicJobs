package com.epicquestz.epicjobs.constants;

import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

/**
 * Central colour palette for all user-facing text, built on tonal pairs: every message has one
 * semantic {@link Role} whose body colour covers the whole line, and emphasised values inside it
 * (names, ids, commands) use a lighter tone of the <i>same</i> hue plus bold. A line therefore
 * never mixes more than two tones, and both always belong to one colour family.
 *
 * <p>Templates in {@link Messages} use the role's tags:
 * <ul>
 *   <li>{@code <em>} — the role's accent tone, bold; for the values that should stand out.</li>
 *   <li>{@code <muted>} — {@link #MUTED}; for secondary hint lines.</li>
 * </ul>
 *
 * <p>Components assembled in Java should pick a role and use {@link Role#body()} /
 * {@link Role#accent()} instead of raw colours.
 */
public final class Palette {

    /** Secondary text: hints, separators, de-emphasised detail. */
    public static final TextColor MUTED = TextColor.color(0x8A94A0);

    /**
     * Semantic message roles. Each pairs a body colour with a lighter accent tone of the same hue.
     */
    public enum Role {

        /** Positive outcomes: an action completed successfully. Green body, mint accent. */
        SUCCESS(0x57E389, 0xC2F6D4),
        /** Hard failures: invalid input, missing data, errored operations. Red body, rose accent. */
        ERROR(0xFF6B6B, 0xFFC4C4),
        /** No-op notices, empty states, reversals. Amber body, gold accent. */
        WARNING(0xFFB454, 0xFFE093),
        /** Neutral system feedback: teleports, counts, assignments. Slate body, white accent. */
        INFO(0xAEBBC7, 0xFFFFFF);

        private final TextColor body;
        private final TextColor accent;
        private final TagResolver tags;

        Role(final int body, final int accent) {
            this.body = TextColor.color(body);
            this.accent = TextColor.color(accent);
            this.tags = TagResolver.resolver(
                TagResolver.resolver("em", Tag.styling(this.accent, TextDecoration.BOLD)),
                TagResolver.resolver("muted", Tag.styling(MUTED))
            );
        }

        /** The default colour for the message body. */
        public TextColor body() {
            return body;
        }

        /** The lighter same-hue tone for emphasised values; pair with bold. */
        public TextColor accent() {
            return accent;
        }

        /** MiniMessage tags ({@code <em>}, {@code <muted>}) bound to this role. */
        public TagResolver tags() {
            return tags;
        }

    }

    private Palette() {
    }

}
