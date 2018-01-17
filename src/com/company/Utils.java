package com.company;

import net.rithms.riot.api.endpoints.static_data.dto.InventoryDataStats;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

public class Utils {
    public static String itemStatsToTooltip(InventoryDataStats itemStats){
        StringBuilder tooltipLabel = new StringBuilder("<html>");
        ArrayList<Method> methods = new ArrayList<>(Arrays.asList(itemStats.getClass().getDeclaredMethods()));
        methods.removeIf((method -> method.getAnnotation(ChampionStatMapper.class) == null));
        for (Method method : methods) {
            try {
                Double value = (Double) method.invoke(itemStats);
                if(value > 0.00){
                    tooltipLabel
                            .append("<p>")
                            .append(method.getAnnotation(ChampionStatMapper.class).parameter().toString().replaceAll("_", " "))
                            .append("     ")
                            .append(value.intValue())
                            .append("</p>");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        tooltipLabel.append("</html>");
        return tooltipLabel.toString();
    }
}
