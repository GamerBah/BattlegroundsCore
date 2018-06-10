package com.battlegroundspvp.administration.data;
/* Created by GamerBah on 1/3/2017 */

import com.battlegroundspvp.administration.data.sql.SettingsEntity;
import com.battlegroundspvp.util.enums.ParticleQuality;
import lombok.Getter;
import lombok.Setter;

public class PlayerSettings {

    @Getter
    private final SettingsEntity entity;
    @Getter
    private final int id;
    @Getter
    @Setter
    private boolean teamRequests, privateMessaging, stealthyJoin;
    @Getter
    @Setter
    private ParticleQuality particleQuality;

    PlayerSettings(SettingsEntity entity) {
        this.entity = entity;
        this.id = entity.getId();
        this.teamRequests = entity.isTeamRequests();
        this.privateMessaging = entity.isPrivateMessaging();
        this.stealthyJoin = entity.isStealthyJoin();
        this.particleQuality = ParticleQuality.fromString(entity.getParticleQuality());
    }

    void sync() {
        entity.setTeamRequests(this.teamRequests);
        entity.setPrivateMessaging(this.privateMessaging);
        entity.setStealthyJoin(this.stealthyJoin);
        entity.setParticleQuality(this.particleQuality.toString());
    }
}
