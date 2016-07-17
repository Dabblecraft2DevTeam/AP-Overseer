package net.cccm5.ApOverseer;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

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

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.layout.PatternLayout;

public final class OverseerMain extends JavaPlugin implements Listener {
    private FileConfiguration config = getConfig();
    private List<String> chatCommands,excludedCommands;
    private List<CommandSender> commandSpy=new ArrayList<CommandSender>(),socialSpy=new ArrayList<CommandSender>();
    private HashMap<String, String> nameColor = new HashMap<String, String>();
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
        tempCommands.add("/tell");
        tempCommands.add("/msg");
        config.addDefault("Chat message commands", tempCommands);
        //default list of excluded commands
        tempCommands=new ArrayList<String>();
        tempCommands.add("/help");
        tempCommands.add("/rules");
        tempCommands.add("/ping");
        config.addDefault("Excluded Commands", tempCommands);
        config.options().copyDefaults(true);
        this.saveConfig();
        //////Load Config//////
        includeChat = getConfig().getBoolean("Include chat messages");
        chatCommands=getConfig().getStringList("Chat message commands");
        excludedCommands=getConfig().getStringList("Excluded Commands");
        getServer().getPluginManager().registerEvents(this, this);
        //getServer().getPluginManager().registerEvents(this, this);
        //set up logger

        //disabled for now

        //Logger log = (Logger) LogManager.getRootLogger();
        //log.addAppender(new Log4JAppender());
    }

    @Override
    public void onDisable() {
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(cmd.getName().equalsIgnoreCase("overseer")){
            //check if player has permision for main command
            if(!sender.hasPermission("ApOverseer.main"))
            {
                sender.sendMessage("§cError:§4 You don't have permission to do that");
                return true;
            }
            //check if player is using help or if theres no arguments
            if(args.length==0 || args[0].equalsIgnoreCase("help"))
            {
                sender.sendMessage("§a-----§bAP Overseer§a-----");
                sender.sendMessage("§a/overseer help:    §bview this message");
                sender.sendMessage("§a/overseer commandSpy: §bsend command inputs to you");
                sender.sendMessage("§a/overseer color <color>:   §bset the output color of commands");
                if(!includeChat)
                    sender.sendMessage("§a/overseer socialSpy: §bsend social command inputs to you");
                return true;
            }
            //executes the commandSpy sub-command
            if(args[0].equalsIgnoreCase("commandSpy")||args[0].equalsIgnoreCase("cspy")){
                if(sender.hasPermission("ApOverseer.commandSpy")){
                    if(!commandSpy.contains(sender))
                    {
                        commandSpy.add(sender);
                        sender.sendMessage("§b[§aoverseer§b]§aStarted observing player commands");
                        return true;
                    }else{
                        commandSpy.remove(sender);
                        sender.sendMessage("§b[§aoverseer§b]Stopped observing player commands");
                        return true;
                    }
                }
                else
                {
                    sender.sendMessage("§cError:§4 You don't have permision for that!");
                    return true;
                }
            }
            //executes the socialSpy command if include chat is disabled
            if(!includeChat){
                if(args[0].equalsIgnoreCase("socialSpy")||args[0].equalsIgnoreCase("sspy")){
                    if(sender.hasPermission("ApOverseer.socialSpy")){
                        if(!socialSpy.contains(sender))
                        {
                            socialSpy.add(sender);
                            sender.sendMessage("§b[§aoverseer§b]§aStarted observing social commands");
                            return true;
                        }else{
                            socialSpy.remove(sender);
                            sender.sendMessage("§b[§aoverseer§b]Stopped observing social commands");
                            return true;
                        }
                    }
                    else
                    {
                        sender.sendMessage("§cError:§4 You don't have permision for that!");
                        return true;
                    }
                }
            }
            //sets the output color of the spy commands
            if(args[0].equalsIgnoreCase("color")){
                if(!sender.hasPermission("ApOverseer.setColor")){
                    sender.sendMessage("§cError:§4 You don't have permission for that!");
                    return true;
                }
                if(args.length!=2)
                {
                    sender.sendMessage("§cError:§4 correct format is /overseer color <color>");
                    return true;
                }
                if(args[1].length()!=1)
                {
                    sender.sendMessage("§cError:§4 \"" + args[1] + "\" is too long to be a color code");
                    return true;
                }
                if(!(args[1].toLowerCase().charAt(0)>='a' && args[1].toLowerCase().charAt(0)<='f') && !(args[1].charAt(0)>='0' && args[1].charAt(0)<='9'))
                {
                    sender.sendMessage("§cError:§4 \"" + args[1] + "\" is not a color code");
                    return true;
                }
                nameColor.put(sender.getName(),args[1]);
                sender.sendMessage("§b[§aoverseer§b]§acolor set to §" + args[1] + "&" +args[1]);
                return true;
            }
        }
        return false;
    }

    @EventHandler
    public void onPlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event){
        boolean testState=true;
        boolean chatCommand=false;
        if(!includeChat)
        {
            for(String testString : chatCommands)
            {
                if(event.getMessage().toLowerCase().startsWith(testString+" ") || event.getMessage().equalsIgnoreCase(testString)){
                    chatCommand=true;
                    testState=false;
                }
            }
        }
        for(String testString : excludedCommands)
        {
            if(event.getMessage().toLowerCase().startsWith(testString+" ") || event.getMessage().equalsIgnoreCase(testString))
                testState=false;
        }
        if(testState)
            for(CommandSender observer: commandSpy){	
                if(!(observer instanceof Player) && !(observer==event.getPlayer()))
                    observer.sendMessage("§b[§aoverseer§b]" + event.getPlayer().getDisplayName() + ": §" + nameToColor(observer.getName())+ event.getMessage());	
            }
        if(chatCommand)
            for(CommandSender observer: socialSpy){
                if(!(observer instanceof Player) && !(observer==event.getPlayer()))
                    observer.sendMessage("§b[§aoverseer§b]" + event.getPlayer().getDisplayName() + ": §" + nameToColor(observer.getName()) + event.getMessage());	
            }
    }

    @EventHandler
    public void onServerCommandEvent(ServerCommandEvent event)
    {
        boolean testState=true;
        boolean chatCommand=false;
        if(!includeChat)
        {
            for(String testString : chatCommands)
            {
                if(("/"+event.getCommand().toLowerCase()).startsWith(testString+" ") || ("/"+event.getCommand()).equalsIgnoreCase(testString))
                {
                    chatCommand=true;
                    testState=false;
                }
            }
        }
        for(String testString : excludedCommands)
        {
            if(("/"+event.getCommand().toLowerCase()).startsWith(testString+" ") || ("/"+event.getCommand()).equalsIgnoreCase(testString))
                testState=false;
        }
        if(testState)
            for(CommandSender observer: commandSpy){
                if((observer instanceof Player))
                    observer.sendMessage("§b[§aoverseer§b] §4console: §" + nameToColor(observer.getName()) + event.getCommand());	
            }
        if(chatCommand)
            for(CommandSender observer: socialSpy){
                if((observer instanceof Player))
                    observer.sendMessage("§b[§aoverseer§b] §4console: §" + nameToColor(observer.getName()) + event.getCommand());	
            }
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args){
        if(command.getName().equalsIgnoreCase("overseer")){
            List<String> tempList = new ArrayList<String>();
            if (args.length==1){
                tempList.add("color");
                tempList.add("commandSpy");
                tempList.add("help");
                if(!includeChat)
                    tempList.add("socialSpy");
                if( args[0].length() > 1 ){
                    for(String tempString : tempList){
                        if(!tempString.startsWith(args[0]))
                            tempList.remove(tempString);
                    }
                }
            }
            if(tempList.size()>0)
                return tempList;
        }
        return null;
    }

    public String nameToColor(String name)
    {
        if(nameColor.containsKey(name))
            return nameColor.get(name);
        return "f";
    }

    public void sendConsoleOutput(String s)
    {
        Bukkit.broadcastMessage(s);
        //         for(CommandSender observer: commandSpy){
        //             if((observer instanceof Player))
        //                 observer.sendMessage(s);	
        //         }
    }

}