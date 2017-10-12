package com.battlegroundspvp.utils;
/* Created by GamerBah on 10/11/2017 */

import com.battlegroundspvp.BattlegroundsCore;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class DiscordBot extends ListenerAdapter {

    private BattlegroundsCore plugin;

    public static TextChannel staffChannel;
    public static TextChannel punishmentsChannel;
    public static TextChannel casualChannel;
    public static TextChannel vipCasualChannel;
    public static TextChannel announcementsChannel;
    public static TextChannel supportChannel;
    public static TextChannel errorLoggingChannel;

    public DiscordBot(BattlegroundsCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
    }

}
