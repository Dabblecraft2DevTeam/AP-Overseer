package net.cccm5.ApOverseer;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.entity.Player;
public final class OverseerMain extends JavaPlugin implements Listener {
    private FileConfiguration config = getConfig();
    private List<String> chatCommands,excludedCommands;
    private List<CommandSender> commandSpy=new ArrayList<CommandSender>();
    private boolean includeChat;
    @Override
    public void onEnable() {
        //////Configs//////
        config.addDefault("Include chat messages",false);
        //default list of messaging commands
        ArrayList<String> tempCommands = new ArrayList<String>();
        tempCommands.add("/r");
        tempCommands.add("/reply");
        tempCommands.add("/mail");
        tempCommands.add("/message");
        tempCommands.add("/m");
        tempCommands.add("/whisper");
        tempCommands.add("/ch qm");
        tempCommands.add("/helpop");
        config.addDefault("Chat message commands", tempCommands);
        //default list of excluded commands
        tempCommands=new ArrayList<String>();
        tempCommands.add("/help");
        tempCommands.add("/rules");
        config.addDefault("Excluded Commands", tempCommands);
        config.options().copyDefaults(true);
        this.saveConfig();
        //////Load Config//////
        includeChat = getConfig().getBoolean("Include chat messages");
        chatCommands=getConfig().getStringList("Chat message commands");
        excludedCommands=getConfig().getStringList("Excluded Commands");
        getServer().getPluginManager().registerEvents(this, this);
        //getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(cmd.getName().equalsIgnoreCase("commandSpy")){
            if(sender.hasPermission("ApOverseer.commandSpy")){
                if(!commandSpy.contains(sender))
                {
                    commandSpy.add(sender);
                    sender.sendMessage("Started observing player commands");
                    return true;
                }else{
                    commandSpy.remove(sender);
                    sender.sendMessage("stopped observing player commands");
                    return true;
                }
            }
            else
            {
                sender.sendMessage("You don't have permision for that!");
                return true;
            }
        }
        return false;
    }

    @EventHandler
    public void onPlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event){
        boolean testState=true;
        if(!includeChat)
        {
            for(String testString : chatCommands)
            {
                if(event.getMessage().toLowerCase().startsWith(testString+" "))
                    testState=false;
            }
        }
        for(String testString : excludedCommands)
        {
            if(event.getMessage().toLowerCase().startsWith(testString+" "))
                testState=false;
        }
        if(testState)
            for(CommandSender observer: commandSpy){	
                if(!(observer instanceof Player) && !(observer==event.getPlayer()))
                    observer.sendMessage(event.getPlayer().getDisplayName() + ": " + event.getMessage());	
            }
    }

    @EventHandler
    public void onServerCommandEvent(ServerCommandEvent event)
    {
        boolean testState=true;
        if(!includeChat)
        {
            for(String testString : chatCommands)
            {
                if(event.getCommand().toLowerCase().startsWith(testString+" "))
                    testState=false;
            }
        }
        for(String testString : excludedCommands)
        {
            if(event.getCommand().toLowerCase().startsWith(testString+" "))
                testState=false;
        }
        if(testState)
            for(CommandSender observer: commandSpy){
                if((observer instanceof Player))
                observer.sendMessage("console: " + event.getCommand());	
            }
    }
}

