package com.epicquestz.epicjobs.utils;

import com.epicquestz.epicjobs.constants.Palette;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemStackBuilder {

    private final ItemStack ITEM_STACK;

    public ItemStackBuilder(final Material material) {
        this.ITEM_STACK = new ItemStack(material);
    }

    public ItemStackBuilder(final ItemStack item) {
        this.ITEM_STACK = item;
    }

    public ItemStackBuilder withAmount(final int amount) {
        ITEM_STACK.setAmount(amount);
        return this;
    }

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    /**
     * Parses MiniMessage and disables the italic styling Minecraft applies to item names/lore
     * by default (unless the text explicitly requests italics). GUI text is an INFO surface, so
     * the palette tags {@code <em>} (white, bold) and {@code <muted>} are available.
     */
    private static Component deserialize(final String miniMessage) {
        return MINI_MESSAGE.deserialize(miniMessage, Palette.Role.INFO.tags()).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
    }

    public ItemStackBuilder withName(final String name) {
        final ItemMeta meta = ITEM_STACK.getItemMeta();
        meta.displayName(deserialize(name));
        ITEM_STACK.setItemMeta(meta);
        return this;
    }

    public ItemStackBuilder withLore(final String line) {
        final ItemMeta meta = ITEM_STACK.getItemMeta();
        final List<Component> lore = meta.lore() == null ? new ArrayList<>() : new ArrayList<>(meta.lore());
        lore.add(deserialize(line));
        meta.lore(lore);
        ITEM_STACK.setItemMeta(meta);
        return this;
    }

    public ItemStackBuilder withName(final Component name) {
        final ItemMeta meta = ITEM_STACK.getItemMeta();
        meta.displayName(name.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        ITEM_STACK.setItemMeta(meta);
        return this;
    }

    public ItemStackBuilder withLore(final Component line) {
        final ItemMeta meta = ITEM_STACK.getItemMeta();
        final List<Component> lore = meta.lore() == null ? new ArrayList<>() : new ArrayList<>(meta.lore());
        lore.add(line.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        meta.lore(lore);
        ITEM_STACK.setItemMeta(meta);
        return this;
    }

    public ItemStackBuilder withLineBreakLore(final TextColor color, final String text) {
        return withLineBreakLore(color, text, false);
    }

    public ItemStackBuilder withLineBreakLore(final TextColor color, final String text, final boolean italic) {
        final ItemMeta meta = ITEM_STACK.getItemMeta();
        final List<Component> lore = meta.lore() == null ? new ArrayList<>() : new ArrayList<>(meta.lore());

        final String[] words = text.split(" ");
        StringBuilder line = new StringBuilder(words[0]);
        if (words.length > 1) {
            for (int i = 1; i < words.length; i++) {
                if (line.length() <= 32) {
                    line.append(" ").append(words[i]);
                } else {
                    lore.add(Component.text(line.toString(), color).decoration(TextDecoration.ITALIC, italic));
                    line = new StringBuilder(words[i]);
                }
                if (i == words.length - 1) {
                    lore.add(Component.text(line.toString(), color).decoration(TextDecoration.ITALIC, italic));
                }
            }
        } else {
            lore.add(Component.text(line.toString(), color).decoration(TextDecoration.ITALIC, italic));
        }

        meta.lore(lore);
        ITEM_STACK.setItemMeta(meta);
        return this;
    }

    public ItemStackBuilder withDurability(final int durability) {
        final ItemMeta meta = ITEM_STACK.getItemMeta();
        final Damageable damageable = (Damageable) meta;
        damageable.setDamage(durability);
        ITEM_STACK.setItemMeta(meta);
        return this;
    }

    public ItemStackBuilder addFlags(final ItemFlag... flags) {
        final ItemMeta meta = ITEM_STACK.getItemMeta();
        meta.addItemFlags(flags);
        ITEM_STACK.setItemMeta(meta);
        return this;
    }

    @SuppressWarnings("deprecation")
    public ItemStackBuilder withSkullOwner(final OfflinePlayer player) {
        final Material type = ITEM_STACK.getType();
        if (type == Material.PLAYER_HEAD) {
            final ItemMeta meta = ITEM_STACK.getItemMeta();
            final SkullMeta skullMeta = (SkullMeta) meta;
            skullMeta.setOwner(player.getName());
            ITEM_STACK.setItemMeta(meta);
            return this;
        } else {
            throw new IllegalArgumentException("withSkullOwner is only applicable for skulls!");
        }
    }

    public ItemStackBuilder withModel(final int model) {
        final ItemMeta meta = ITEM_STACK.getItemMeta();
        meta.setCustomModelData(model);
        ITEM_STACK.setItemMeta(meta);
        return this;
    }


    public ItemStackBuilder withEnchantment(final Enchantment enchantment, final int level) {
        ITEM_STACK.addUnsafeEnchantment(enchantment, level);
        return this;
    }

    public ItemStackBuilder withEnchantment(final Enchantment enchantment) {
        ITEM_STACK.addUnsafeEnchantment(enchantment, 1);
        return this;
    }

    public ItemStackBuilder withType(final Material material) {
        ITEM_STACK.setType(material);
        return this;
    }

    public ItemStackBuilder clearLore() {
        final ItemMeta meta = ITEM_STACK.getItemMeta();
        meta.lore(new ArrayList<>());
        ITEM_STACK.setItemMeta(meta);
        return this;
    }

    public ItemStackBuilder clearEnchantments() {
        for (final Enchantment enchantment : ITEM_STACK.getEnchantments().keySet()) {
            ITEM_STACK.removeEnchantment(enchantment);
        }
        return this;
    }

    public ItemStackBuilder withColor(final Color color) {
        final Material type = ITEM_STACK.getType();
        if (type == Material.LEATHER_BOOTS || type == Material.LEATHER_CHESTPLATE || type == Material.LEATHER_HELMET || type == Material.LEATHER_LEGGINGS) {
            final LeatherArmorMeta meta = (LeatherArmorMeta) ITEM_STACK.getItemMeta();
            meta.setColor(color);
            ITEM_STACK.setItemMeta(meta);
            return this;
        } else {
            throw new IllegalArgumentException("withColor is only applicable for leather armor!");
        }
    }

    public ItemStack build() {
        return ITEM_STACK;
    }

}
