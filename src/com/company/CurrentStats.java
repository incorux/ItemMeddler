package com.company;

import net.rithms.riot.api.endpoints.static_data.dto.Champion;
import net.rithms.riot.api.endpoints.static_data.dto.InventoryDataStats;
import net.rithms.riot.api.endpoints.static_data.dto.Item;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class CurrentStats {
    private Champion _currentChampion;
    private Map<ChampionStat, Double> _params;
    public CurrentStats(Champion currentChampion, int level, List<Item> items) {
        _currentChampion = currentChampion;
        calculateBaseParams(level);
        if(items != null)
            addItemsStats(items);
    }

    public Double get(ChampionStat param){
        return _params.get(param);
    }

    public void calculateBaseParams(int level){
        _params = new HashMap<>();
        Arrays.stream(ChampionStat.values()).forEach(championStat -> _params.put(championStat, getValueAtLevel(championStat, level)));
    }

    private Double getValueAtLevel(ChampionStat championStat, int level){
        int actualLevel = level - 1;
        Objects.requireNonNull(_currentChampion.getStats(), "Stats are null");
        switch (championStat){
            case Attack_Damage: return _currentChampion.getStats().getAttackDamage() + _currentChampion.getStats().getAttackDamagePerLevel() * actualLevel;
            case Armor: return _currentChampion.getStats().getArmor() + _currentChampion.getStats().getArmorPerLevel() * actualLevel;
            case Magic_Resist: return _currentChampion.getStats().getSpellBlock() + _currentChampion.getStats().getSpellBlockPerLevel() * actualLevel;
            case Attack_Speed: return _currentChampion.getStats().getBaseAttackSpeed() + _currentChampion.getStats().getAttackSpeedPerLevel() * actualLevel;
            case Hit_Points: return _currentChampion.getStats().getHp() + _currentChampion.getStats().getHpPerLevel() * actualLevel;
            default: return 0.00;
        }
    }

    private void addItemsStats(Iterable<Item> items){
        for(Item item : items){
            InventoryDataStats stats = item.getStats();
            for (Method method : InventoryDataStats.class.getDeclaredMethods()) {
                ChampionStatMapper[] annotations = method.getDeclaredAnnotationsByType(ChampionStatMapper.class);
                if(annotations.length == 0) continue;
                ChampionStatMapper annotation = annotations[0];
                ChampionStat championStat = annotation.parameter();
                try {
                    Double aDoubleValue = _params.get(championStat) + (Double) method.invoke(stats);
                    _params.remove(championStat);
                    _params.put(championStat, aDoubleValue);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
