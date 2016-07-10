package net.cccm5.ApOverseer;


import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class OverseerMain extends JavaPlugin {
    private FileConfiguration config = getConfig();
    private ArrayList<String> chatCommands,excludedCommands;
    private ArrayList<CommandSender> playerSpy,consoleSpy;
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
        saveDefaultConfig();
        //////Load Config//////
        //includeChat = getConfig().getBoolean("Include chat messages");
        //chatCommands.addAll(getConfig().getStringList("Chat message commands"));
        //excludedCommands.addAll(getConfig().getStringList("Excluded Commands"));

        //getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(cmd.getName().equalsIgnoreCase("playerSpy")){
            if(sender.hasPermission("ApOverseer.playerSpy")){
                if(!playerSpy.contains(sender))
                {
                    playerSpy.add(sender);
                    sender.sendMessage("Started observing player commands");
                    return true;
                }else{
                    playerSpy.remove(sender);
                    sender.sendMessage("stopped observing player commands");
                    return true;
                }
            }
            else
            {
                sender.sendMessage("You don't have permision for that!");
                return false;
            }
        }
        return false;
    }

    @EventHandler
    public void onPlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event){
    	//testing
    	Bukkit.broadcastMessage(event.getMessage());
    	//
        boolean testState=true;
        if(!includeChat)
        {
            for(String testString : chatCommands)
            {
                if(event.getMessage().toLowerCase().startsWith(testString))
                    testState=false;
            }
        }
        for(String testString : excludedCommands)
        {
            if(event.getMessage().toLowerCase().startsWith(testString))
                testState=false;
        }
        if(testState)
            for(CommandSender observer: playerSpy){	
                observer.sendMessage(event.getPlayer().getDisplayName() + ": " + event.getMessage());	
            }
    }
}

