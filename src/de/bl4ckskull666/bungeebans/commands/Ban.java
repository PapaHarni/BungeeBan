/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.bungeebans.commands;

import com.google.common.collect.ObjectArrays;
import de.bl4ckskull666.bungeebans.BungeeBans;
import de.bl4ckskull666.bungeebans.Tasks;
import de.bl4ckskull666.bungeebans.classes.PlayerBan;
import de.bl4ckskull666.bungeebans.database.MySQL;
import de.bl4ckskull666.mu1ti1ingu41.Language;
import java.util.UUID;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/**
 *
 * @author PapaHarni
 */
public class Ban extends Command {
    
    public Ban() {
        super("ban", "bungeebans.ban");
    }

    @Override
    public void execute(CommandSender s, String[] a) {
        UUID uuid_by_sender = BungeeBans.getPlayer(s.getName()) == null?UUID.fromString("00000000-0000-0000-0000-000000000000"):BungeeBans.getPlayer(s.getName()).getUniqueId();
        if(a.length <= 2) {
            Language.sendMessage(BungeeBans.getPlugin(), s, "command.ban.wrongformat", "Please add a Username and a Reason.");
            return;
        }
        String name = a[0];
        String nick = a[0];
        ProxiedPlayer pp = null;
        if(ProxyServer.getInstance().getPlayer(nick) != null) {
            pp = ProxyServer.getInstance().getPlayer(nick);
            name = pp.getUniqueId().toString();
            nick = pp.getName();
        } else {
            UUID u = BungeeBans.getUUIDByName(nick);
            if(u != null) {
                name = u.toString();
            }
        }
        
        String msg = "";
        for(int i = 1; i < a.length; i++)
            msg += (msg.isEmpty()?"":" ") + a[i];
        
        if(msg.isEmpty()) {
            Language.sendMessage(BungeeBans.getPlugin(), s, "command.ban.needreason", "Please add a Reason to ban %name% successful.", new String[] {"%name%"}, new String[] {nick});
            return;
        }
        
        if(PlayerBan.isBanned(name) || PlayerBan.isBanned(nick)) {
            Language.sendMessage(BungeeBans.getPlugin(), s, "command.ban.is-banned", "%name% is already banned.", new String[] {"%name%"}, new String[] {nick});
            return;
        }
        
        PlayerBan pb = new PlayerBan(name, nick, msg, 0L, "ban");
        MySQL.addBan(pb, uuid_by_sender.toString());
        
        if(pp != null)
            pp.disconnect(new TextComponent[] {Language.getText(BungeeBans.getPlugin(), pp.getUniqueId(), "command.ban.kick", "You have been just the handcuffs applied by %by% because %message%", new String[] {"%message%", "%by%"}, new String[] {msg, s.getName()}), Language.getText(BungeeBans.getPlugin(), pp.getUniqueId(), "objection", "")});
        BungeeBans.TeamInform("command.ban.message", "Player %name% was banned by %by% while %message%.", new String[] {"%message%", "%name%", "%by%"}, new String[] {msg, nick, s.getName()});
        Tasks.restartBanTask();
    }
}
