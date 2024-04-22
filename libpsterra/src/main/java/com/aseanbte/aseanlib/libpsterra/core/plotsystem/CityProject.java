package com.aseanbte.aseanlib.libpsterra.core.plotsystem;

import com.aseanbte.aseanlib.utils.head.AlpsHeadUtils;
import com.aseanbte.aseanlib.utils.item.LegacyItemBuilder;
import com.aseanbte.aseanlib.utils.item.LegacyLoreBuilder;
import org.bukkit.inventory.ItemStack;

public class CityProject {

    public final int id;
    public final String name;
    public final int country_id;
    
    //use Connection.getCityProject(ID) instead of this constructor
    public CityProject(int id, int country_id, String name) {
        this.id = id;
        this.country_id = country_id;
        this.name = name;
      
    }



    public ItemStack getItem(String countryHeadID) {
        return new LegacyItemBuilder(AlpsHeadUtils.getCustomHead(countryHeadID))
                .setName("§b§l" + name)
                .setLore(new LegacyLoreBuilder()
                        .addLine("§bID: §7" + id)
                        .build())
                .build();
    }
}
