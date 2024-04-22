/*
 * The MIT License (MIT)
 *
 *  Copyright © 2023, Alps BTE <bte.atchli@gmail.com>
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package com.aseanbte.aseanlib.hologram;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.DecentHolograms;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import eu.decentsoftware.holograms.api.DecentHologramsAPI;
import eu.decentsoftware.holograms.api.holograms.HologramLine;
import eu.decentsoftware.holograms.api.holograms.HologramPage;
import eu.decentsoftware.holograms.event.HologramClickEvent;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class DecentHologramDisplay implements DecentHologramContent {
    public static List<DecentHologramDisplay> activeDisplays = new ArrayList<>();
    public static DecentHolograms decentHolograms;
    public static String contentSeparator = "§7---------------";
    public static final String EMPTY_TAG = "&f";
    private final String id;
    private Location position;
    private boolean isEnabled;
    protected final HashMap<UUID, Hologram> holograms = new HashMap<>();
    private ClickAction clickListener;

    /**
     * This action is executed when the player clicks on the 'mark as read' text on the hologram.
     */
    @FunctionalInterface
    public interface ClickAction {
        void onClick(@NotNull HologramClickEvent clickEvent);
    }

    public static void registerPlugin(Plugin plugin) {
        decentHolograms = DecentHologramsAPI.get();
        plugin.getServer().getPluginManager().registerEvents(new DecentHologramListener(), plugin);
    }

    public DecentHologramDisplay(@NotNull String id, Location position, boolean isEnabled) {
        this.id = id;
        this.position = position;
        this.isEnabled = isEnabled;
        activeDisplays.add(this);
    }

    public void create(Player player) {
        if(!isEnabled) return;
        if (this.hasViewPermission(player.getUniqueId())) {
            if (this.holograms.containsKey(player.getUniqueId())) {
                this.reload(player.getUniqueId());
            } else {
                Bukkit.getLogger().log(Level.INFO, "[DHAPI] Created display ID: " + id + " For player: " + player.getUniqueId());
                Hologram hologram = DHAPI.createHologram(player.getUniqueId() + "-" + id, position);
                // Allow only player to see
                hologram.setDefaultVisibleState(false);
                hologram.setShowPlayer(player);

                this.holograms.put(player.getUniqueId(), hologram);
                this.reload(player.getUniqueId());
            }
        }
    }

    public abstract boolean hasViewPermission(UUID var1);

    public boolean isVisible(UUID playerUUID) {
        return this.holograms.containsKey(playerUUID);
    }

    public List<DataLine<?>> getHeader(UUID playerUUID) {
        return Arrays.asList(new ItemLine(this.getItem()), new TextLine(this.getTitle(playerUUID)), new TextLine(contentSeparator));
    }

    public List<DataLine<?>> getFooter(UUID playerUUID) {
        return Collections.singletonList(new TextLine(contentSeparator));
    }

    public void reload(UUID playerUUID) {
        if (!holograms.containsKey(playerUUID)) return;
        List<DataLine<?>> dataLines = new ArrayList<>();

        List<DataLine<?>> header = getHeader(playerUUID);
        if (header != null) dataLines.addAll(header);

        List<DataLine<?>> content = getContent(playerUUID);
        if (content != null) dataLines.addAll(content);

        List<DataLine<?>> footer = getFooter(playerUUID);
        if (footer != null) dataLines.addAll(footer);

        updateDataLines(holograms.get(playerUUID).getPage(0), 0, dataLines);
    }

    public void reloadAll() {
        for (UUID playerUUID : holograms.keySet()) reload(playerUUID);
    }

    public void remove(UUID playerUUID) {
        if (this.holograms.containsKey(playerUUID)) {
            DHAPI.removeHologram(playerUUID + "-" + id);
            this.holograms.get(playerUUID).delete();
        }

        this.holograms.remove(playerUUID);
    }

    public void removeAll() {
        List<UUID> playerUUIDs = new ArrayList<>(holograms.keySet());
        for (UUID playerUUID : playerUUIDs) remove(playerUUID);
    }

    public void delete() {
        this.removeAll();
        this.holograms.clear();
        activeDisplays.remove(this);
    }

    public String getId() {
        return this.id;
    }

    public Location getLocation() {
        return this.position;
    }

    public void setLocation(Location newPosition) {
        this.position = newPosition;
        for (UUID playerUUID : holograms.keySet()) holograms.get(playerUUID).setLocation(newPosition);

    }

    public boolean isEnabled() {
        return this.isEnabled;
    }
    public void setEnabled(boolean isEnabled) { this.isEnabled = isEnabled; }

    public Hologram getHologram(UUID playerUUID) {
        return this.holograms.get(playerUUID);
    }

    public HashMap<UUID, Hologram> getHolograms() {
        return this.holograms;
    }

    /**
     * Write a hologram page by any DataLine of type
     * @param page The hologram page to write
     * @param startIndex Start line to update
     * @param dataLines The data item to be write on the page
     */
    protected static void updateDataLines(HologramPage page, int startIndex, List<DataLine<?>> dataLines) {
        int index = startIndex;
        if (index == 0 && page.getLines().size() > dataLines.size()) {
            int removeCount = page.getLines().size() - dataLines.size();

            for(int i = 0; i < removeCount; ++i) {
                int lineIndex = page.getLines().size() - 1;
                if (lineIndex >= 0) {
                    page.getLines().remove(lineIndex);
                }
            }
        }

        for (DataLine<?> data : dataLines) {
            if (data instanceof TextLine) replaceLine(page, index, ((TextLine) data).getLine());
            else if (data instanceof ItemLine) replaceLine(page, index, ((ItemLine) data).getLine());
            index++;
        }

    }

    /**
     * Write an index line of a hologram page
     * @param page The hologram page to write
     * @param line The index line to write to
     * @param item any minecraft item as an ItemStack to be inserted
     */
    protected static void replaceLine(HologramPage page, int line, ItemStack item) {
        if (page.getLines().size() < line + 1) {
            DHAPI.addHologramLine(page, item);
        } else {
            HologramLine hologramLine = page.getLines().get(line);
            if (hologramLine != null) {

                DHAPI.insertHologramLine(page, line, item);
                DHAPI.removeHologramLine(page, line + 1);
            } else {
                DHAPI.setHologramLine(page, line,  item);

            }
        }
    }

    /**
     * Write an index line of a hologram page
     * @param page The hologram page to write
     * @param line The index line to write to
     * @param text The text to be written on
     */
    protected static void replaceLine(HologramPage page, int line, String text) {
        if (page.getLines().size() < line + 1) {
            DHAPI.addHologramLine(page, text);
        } else {
            HologramLine hologramLine = page.getLines().get(line);
            if (hologramLine != null) {
                DHAPI.insertHologramLine(page, line, text);
                DHAPI.removeHologramLine(page, line + 1);
            } else {
                DHAPI.setHologramLine(page, line,  text);
            }
        }

    }

    public void setClickListener(@Nullable ClickAction clickListener) {
        this.clickListener = clickListener;
    }

    public @Nullable ClickAction getClickListener() {
        return this.clickListener;
    }


    public static DecentHologramDisplay getById(String id) {
        return (DecentHologramDisplay) activeDisplays.stream().filter((holo) -> {
            return holo.getId().equals(id);
        }).findFirst().orElse((DecentHologramDisplay)null);
    }

    public interface DataLine<T> {
        T getLine();
    }

    public static class ItemLine implements DataLine<ItemStack> {
        private final ItemStack line;

        public ItemLine(ItemStack line) {
            this.line = line;
        }

        public ItemStack getLine() {
            return this.line;
        }
    }

    public static class TextLine implements DataLine<String> {
        private final String line;

        public TextLine(String line) {
            this.line = line;
        }

        public String getLine() {
            return this.line;
        }
    }
}