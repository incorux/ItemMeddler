package com.company;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ChampionStatMapper {
    ChampionStat parameter();
}