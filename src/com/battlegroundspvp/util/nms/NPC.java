package com.battlegroundspvp.util.nms;
/* Created by GamerBah on 11/2/2017 */

import com.comphenix.packetwrapper.WrapperPlayServerNamedEntitySpawn;
import com.comphenix.packetwrapper.WrapperPlayServerPlayerInfo;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import lombok.Getter;
import org.bukkit.Location;

import java.util.List;
import java.util.UUID;

public class NPC {

    private static final int ENTITY_ID = 7000;

    @Getter
    private int id = ENTITY_ID + 1;
    @Getter
    private Location location;
    @Getter
    private UUID skinAccount;
    @Getter
    private Hologram hologram;

    public NPC(Location location, UUID skinAccount, String... lines) {
        this.location = location;
        this.skinAccount = skinAccount;
        this.hologram = new Hologram(location, false, lines);

        WrappedDataWatcher.Serializer stringSerializer = WrappedDataWatcher.Registry.get(String.class);
        WrappedDataWatcher.Serializer byteSerializer = WrappedDataWatcher.Registry.get(Byte.class);

        WrapperPlayServerPlayerInfo playerInfo = new WrapperPlayServerPlayerInfo();
        List<PlayerInfoData> playerInfoData = playerInfo.getData();
        WrappedDataWatcher dataWatcher = new WrappedDataWatcher();
        //dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject())

        WrapperPlayServerNamedEntitySpawn namedEntitySpawn = new WrapperPlayServerNamedEntitySpawn();
        namedEntitySpawn.setEntityID(id);
        namedEntitySpawn.setPlayerUUID(skinAccount);
        namedEntitySpawn.setX(location.getBlockX());
        namedEntitySpawn.setY(location.getBlockY());
        namedEntitySpawn.setZ(location.getBlockZ());
        namedEntitySpawn.setPitch(location.getPitch());
        namedEntitySpawn.setYaw(location.getYaw());
    }
}
