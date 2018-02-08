package com.battlegroundspvp.utils.enums;/* Created by GamerBah on 12/13/2017 */

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum StatPlaceholder {

    COINS("#COINS#"),
    SOULS("#SOULS#"),
    BATTLE_CRATES("#BATTLE_CRATES#"),
    KILLS("#KILLS#"),
    DEATHS("#DEATHS#");

    private String code;
}
