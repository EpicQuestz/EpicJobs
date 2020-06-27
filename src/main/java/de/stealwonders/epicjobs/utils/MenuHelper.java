package de.stealwonders.epicjobs.utils;

import com.github.stefvanschie.inventoryframework.Gui;
import com.github.stefvanschie.inventoryframework.GuiItem;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class MenuHelper {

    public static Gui getStaticSelectionGui(final String name, final  GuiItem... guiItems) {
        final Gui gui = new Gui(1, name);
        int startPos = guiItems.length > 5 ? 0 : 5 - guiItems.length;
        final int incrementAmount = guiItems.length > 5 ? 1 : 2;
        for (final GuiItem guiItem : guiItems) {
            final StaticPane staticPane = new StaticPane(startPos, 0, 1, 1);
            staticPane.addItem(guiItem, 0, 0);
            gui.addPane(staticPane);
            startPos += incrementAmount;
        }
        return gui;
    }

    public static Gui getPaginatedSelectionGui(final String name, final List<GuiItem> guiItems) {
        final Gui gui = new Gui(3, name);
        final PaginatedPane pagination = new PaginatedPane(0, 0, 9, 3);
        pagination.populateWithGuiItems(guiItems);
        gui.addPane(pagination);
        return gui;
    }

    public static Gui getPaginatedGui(final String name, final List<GuiItem> guiItems, final GuiItem mainMenuItem, final ItemStack infoBook) {

        final Gui gui = new Gui(6, name);
        final PaginatedPane pagination = new PaginatedPane(0, 0, 9, 5);
        pagination.populateWithGuiItems(guiItems);

        if (pagination.getPages() > 1) {
            gui.setTitle(name + " (1/" + pagination.getPages() + ")");
        }

        final StaticPane mainMenu = new StaticPane(0, 5, 1, 1);
        final StaticPane back = new StaticPane(2, 5, 1, 1);
        final StaticPane info = new StaticPane(4, 5, 1, 1);
        final StaticPane forward = new StaticPane(6, 5, 1, 1);

        mainMenu.addItem(mainMenuItem, 0, 0);

        back.addItem(new GuiItem(new ItemStackBuilder(Material.ARROW).withName("Previous Page").build(), inventoryClickEvent -> {
            inventoryClickEvent.setResult(Event.Result.DENY);
            pagination.setPage(pagination.getPage() - 1);

            if (pagination.getPage() == 0) {
                back.setVisible(false);
            }
            forward.setVisible(true);

            if (pagination.getPages() > 1) {
                gui.setTitle(name + " (" + (pagination.getPage() + 1) + "/" + pagination.getPages() + ")");
            }

            gui.update();
        }), 0, 0);

        info.addItem(new GuiItem(infoBook, inventoryClickEvent -> inventoryClickEvent.setResult(Event.Result.DENY)), 0, 0);

        forward.addItem(new GuiItem(new ItemStackBuilder(Material.ARROW).withName("Next Page").build(), inventoryClickEvent -> {
            inventoryClickEvent.setResult(Event.Result.DENY);
            pagination.setPage(pagination.getPage() + 1);

            if (pagination.getPage() == pagination.getPages() - 1) {
                forward.setVisible(false);
            }
            back.setVisible(true);

            if (pagination.getPages() > 1) {
                gui.setTitle(name + " (" + (pagination.getPage() + 1) + "/" + pagination.getPages() + ")");
            }

            gui.update();
        }), 0, 0);

        back.setVisible(false);
        forward.setVisible(pagination.getPages() > 1);

        gui.addPane(pagination);
        gui.addPane(mainMenu);
        gui.addPane(back);
        gui.addPane(info);
        gui.addPane(forward);

        return gui;
    }
}
