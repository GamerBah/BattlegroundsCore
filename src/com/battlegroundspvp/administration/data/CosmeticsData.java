package com.battlegroundspvp.administration.data;
/* Created by GamerBah on 8/22/2016 */

import com.battlegroundspvp.BattlegroundsCore;
import com.battlegroundspvp.administration.data.sql.CosmeticsEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hibernate.Session;

import java.util.ArrayList;

@AllArgsConstructor
public class CosmeticsData {

    @Getter
    private final CosmeticsEntity entity;
    @Getter
    private final int id;
    @Getter
    private ArrayList<Integer> lobbyCosmetics = new ArrayList<>();
    @Getter
    private ArrayList<Integer> kitPvpCosmetics = new ArrayList<>();

    CosmeticsData(CosmeticsEntity entity) {
        this.entity = entity;
        this.id = entity.getId();
        String lobby = entity.getLobby().replace("[", "").replace("]", "").replace(" ", "");
        ArrayList<Integer> lobbyCosmetics = new ArrayList<>();
        if (lobby.equals(""))
            this.kitPvpCosmetics = lobbyCosmetics;
        else {
            for (String id : lobby.split(","))
                lobbyCosmetics.add(Integer.parseInt(id));
            this.lobbyCosmetics = lobbyCosmetics;
        }

        String kitPvp = entity.getKitPvp().replace("[", "").replace("]", "").replace(" ", "");
        ArrayList<Integer> kitPvpCosmetics = new ArrayList<>();
        if (kitPvp.equals(""))
            this.kitPvpCosmetics = kitPvpCosmetics;
        else {
            for (String id : kitPvp.split(","))
                kitPvpCosmetics.add(Integer.parseInt(id));
            this.kitPvpCosmetics = kitPvpCosmetics;
        }
    }

    void sync() {
        Session session = BattlegroundsCore.getSessionFactory().openSession();
        session.beginTransaction();
        entity.setLobby(this.lobbyCosmetics.toString());
        entity.setKitPvp(this.kitPvpCosmetics.toString());
        session.getTransaction().commit();
        session.close();
    }
}
