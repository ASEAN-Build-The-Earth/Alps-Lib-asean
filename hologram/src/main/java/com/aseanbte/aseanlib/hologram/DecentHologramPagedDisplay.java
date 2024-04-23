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

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.logging.Level;

public abstract class DecentHologramPagedDisplay extends DecentHologramDisplay {
    protected String sortByPage;
    public BukkitTask changePageTask = null;
    private int pageCount = 1;
    private int changeState = 0;
    private long changeDelay = 0;
    private final Plugin plugin;
    private static final String contentSeparator = "§7---------------";
    protected boolean automaticallySkipPage = true;

    public DecentHologramPagedDisplay(@NotNull String id, Location position, boolean isEnabled, @NotNull Plugin plugin) {
        super(id, position, isEnabled);
        this.plugin = plugin;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    @Override
    public void create(Player player) {
        if (getPages() != null && getPages().size() > 0) sortByPage = getPages().get(0);
        if(!super.isEnabled()) return;
        if (this.hasViewPermission(player.getUniqueId())) {
            if (this.holograms.containsKey(player.getUniqueId())) {
                this.reload(player.getUniqueId());
            } else {
                Bukkit.getLogger().log(Level.INFO, "[DHAPI] Created display ID: " + super.getId() + " For player: " + player.getUniqueId());
                Hologram hologram = DHAPI.createHologram(player.getUniqueId() + "-" + super.getId(), super.getLocation());
                for(int i = 1; i <= pageCount; i++) hologram.addPage();

                // Allow only player to see
                hologram.setDefaultVisibleState(false);
                hologram.setShowPlayer(player);
                this.holograms.put(player.getUniqueId(), hologram);
                this.reload(player.getUniqueId());
            }
        }
    }

    @Override
    public void reload(UUID playerUUID) {
        if (!holograms.containsKey(playerUUID)) return;

        List<List<DataLine<?>>> header = getPagedHeader(playerUUID);
        List<List<DataLine<?>>> content = getPagedContent(playerUUID);
        List<List<DataLine<?>>> footer = getPagedFooter(playerUUID);

        for(int i = 0; i < pageCount; i++) {

            List<DataLine<?>> dataLines = new ArrayList<>();

            if (header != null && header.size() > 1) dataLines.addAll(header.get(i));
            else if (header != null && header.size() == 1) dataLines.addAll(header.get(0));

            if (content != null && content.size() > 1) dataLines.addAll(content.get(i));
            else if (content != null && content.size() == 1) dataLines.addAll(content.get(0));

            if (footer != null && footer.size() > 1) dataLines.addAll(footer.get(i));
            else if (footer != null && footer.size() == 1) dataLines.addAll(footer.get(0));

            updateDataLines(holograms.get(playerUUID).getPage(i), 0, dataLines);
        }

    }

    private void startChangePageTask() {
        final long interval = getInterval();
        changeState = 0;
        changeDelay = interval / contentSeparator.length();

        if (changePageTask != null) changePageTask.cancel();
        changePageTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (changeState == 0) getHolograms().keySet().forEach(uuid -> reload(uuid));
                if (interval == 0) return;
                if (changeState >= changeDelay) {
                    if (automaticallySkipPage) nextPage();
                    else changePageTask.cancel();
                } else {
                    changeState++;
                    // getHolograms().forEach((uuid, holo) -> updateDataLines(holo,holo.getPage(0).getLines().size() - 1, getFooter(uuid)));
                }
            }
        }.runTaskTimer(plugin, 0, changeDelay);
    }


    public List<DataLine<?>> getFooter(UUID playerUUID) {
        int footerLength = contentSeparator.length();
        int highlightCount = (int) (((float) changeState / changeDelay) * footerLength);

        StringBuilder highlighted = new StringBuilder();
        for (int i = 0; i < highlightCount; i++) {
            highlighted.append("-");
        }
        StringBuilder notH = new StringBuilder();
        for (int i = 0; i < footerLength - highlightCount; i++) {
            notH.append("-");
        }

        return Collections.singletonList(new TextLine("§e" + highlighted + "§7" + notH));
    }

    @Override
    public void remove(UUID playerUUID) {
        super.remove(playerUUID);
    }

    public abstract long getInterval();

    public abstract List<String> getPages();
    public List<List<DataLine<?>>> getPagedHeader(UUID playerUUID) { return new ArrayList<>(Collections.singletonList(getHeader(playerUUID))); }
    public List<List<DataLine<?>>> getPagedContent(UUID playerUUID) { return new ArrayList<>(Collections.singletonList(getContent(playerUUID))); }
    public List<List<DataLine<?>>> getPagedFooter(UUID playerUUID) { return new ArrayList<>(Collections.singletonList(getFooter(playerUUID))); }

    public void nextPage() {
        String next = getNextListItem(getPages(), sortByPage);
        sortByPage = next == null ? getPages().get(0) : next;
        startChangePageTask();
    }

    // TODO: UNTESTED
    public void previousPage() {
        int index = getPages().indexOf(sortByPage);
        if (index == 0) {
            sortByPage = getPages().get(getPages().size() - 1); // Wrap around to the last page
        } else {
            sortByPage = getPages().get(index - 1); // Go to the previous page
        }
        startChangePageTask();
    }

    private static <T> T getNextListItem(List<T> haystack, T needle) {
        if(!haystack.contains(needle) || haystack.indexOf(needle) + 1 >= haystack.size()) {
            return null;
        }
        return haystack.get(haystack.indexOf(needle) + 1);
    }
}
