/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.bungeebans.commands;

import de.bl4ckskull666.bungeebans.BungeeBans;
import de.bl4ckskull666.bungeebans.Tasks;
import de.bl4ckskull666.bungeebans.classes.PlayerBan;
import de.bl4ckskull666.bungeebans.database.MySQL;
import de.bl4ckskull666.mu1ti1ingu41.Language;
import java.util.UUID;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

/**
 *
 * @author PapaHarni
 */
public class IpTempMute extends Command {
    
    public IpTempMute() {
        super("iptempmute", "bungeebans.iptempmute");
    }

    @Override
    public void execute(CommandSender s, String[] a) {
        UUID uuid_by_sender = BungeeBans.getPlayer(s.getName()) == null?UUID.fromString("00000000-0000-0000-0000-000000000000"):BungeeBans.getPlayer(s.getName()).getUniqueId();
        if(a.length <= 2) {
            //Wrong format
            Language.sendMessage(BungeeBans.getPlugin(), s, "command.iptempmute.wrongformat", "Please add a IP,a Reason and a Time.");
            return;
        }
        String ip = a[0];
        
        String msg = "";
        long time = 0L;
        for(int i = 1; i < a.length; i++) {
            long tadd = BungeeBans.getTimeIsTime(a[i]);
            if(tadd > 0L) {
                time += tadd;
                continue;
            }
            msg += (msg.isEmpty()?"":" ") + a[i];
        }
        
        if(msg.isEmpty()) {
            Language.sendMessage(BungeeBans.getPlugin(), s, "command.iptempmute.needreason", "Please add a Reason to mute %ip% for a time.", new String[] {"%ip%"}, new String[] {ip});
            return;
        }
        
        if(time == 0L) {
            Language.sendMessage(BungeeBans.getPlugin(), s, "command.iptempmute.needtime", "Please add a time to mute %ip% successful.", new String[] {"%ip%"}, new String[] {ip});
            return;
        }
        
        if(PlayerBan.isMuted(ip)) {
            Language.sendMessage(BungeeBans.getPlugin(), s, "command.iptempmute.is-muted", "IP %ip% is already muted.", new String[] {"%ip%"}, new String[] {ip});
            return;
        }

        PlayerBan pb = new PlayerBan(BungeeBans.getTheIP(ip, true), BungeeBans.getTheIP(ip, true), msg, time, "mute");
        MySQL.addBan(pb, uuid_by_sender.toString());
        
        BungeeBans.MessagePlayerByIP(BungeeBans.getTheIP(ip, false), "command.iptempmute.muted", "Your address is muted by %by% because of %message% until %date% on %time%.", new String[] {"%message%", "%by%", "%date%", "%time%"}, new String[] {msg, s.getName(), BungeeBans.getDateByCalendar(pb.getEnding()), BungeeBans.getTimeByCalendar(pb.getEnding())});
        
        BungeeBans.TeamInform("command.iptempmute.message", "%ip% was tempmuted by %by% while %message% until %date% on %time%.", new String[] {"%message%", "%ip%", "%by%", "%date%", "%time%"}, new String[] {msg, BungeeBans.getTheIP(ip, true), s.getName(), BungeeBans.getDateByCalendar(pb.getEnding()), BungeeBans.getTimeByCalendar(pb.getEnding())});
        Tasks.restartMuteTask();
    }
}
