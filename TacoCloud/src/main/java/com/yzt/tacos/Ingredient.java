package com.yzt.tacos;

import com.sun.scenario.effect.Effect;
import lombok.Data;
import lombok.RequiredArgsConstructor;
// lombok 并不是 Spring 库
// lombok 提供 @Data

@Data
@RequiredArgsConstructor
public class Ingredient {

    private final String id;
    private final String name;
    private final Type type;

    public static enum Type {
        WRAP, PROTEIN, VEGGIES, CHESS, SAUCE
    }
}