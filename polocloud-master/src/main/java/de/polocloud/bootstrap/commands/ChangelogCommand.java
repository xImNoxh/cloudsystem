package de.polocloud.bootstrap.commands;

import de.polocloud.api.command.annotation.Arguments;
import de.polocloud.api.command.annotation.Command;
import de.polocloud.api.command.executor.CommandExecutor;
import de.polocloud.api.command.identifier.CommandListener;
import de.polocloud.api.command.identifier.TabCompletable;
import de.polocloud.bootstrap.Master;
import de.polocloud.api.logger.PoloLogger;
import de.polocloud.api.logger.helper.LogLevel;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ChangelogCommand implements CommandListener, TabCompletable {

    public ChangelogCommand() {
    }


    @Command(name = "changelog", description = "Show the latest changes", aliases = "")
    public void execute(CommandExecutor sender, String[] fullArgs, @Arguments(onlyFirstArgs = {"bootstrap", "api"}, min = 0, max = 1, message = {"----[Changelog]----", "Use §3changelog §7show the changelog", "Use §3changelog bootstrap/api §7to show the changelog of the specific type", "----[/Changelog]----"}) String... params) {
       if(params.length == 0){
           PoloLogger.print(LogLevel.INFO, "Loading §3changelog§7...");
           String bootstrapChangelog = Master.getInstance().getClient().getChangelogRequestService().getChangelog("bootstrap", Master.getInstance().getCurrentVersion());
           PoloLogger.print(LogLevel.INFO, "----[Changelog (§3" + Master.getInstance().getCurrentVersion() + "§7)]----\n[Bootstrap\n" + bootstrapChangelog + "\n----[/Changelog]----");
       }else if(params.length == 1){
           if(params[0].equalsIgnoreCase("bootstrap")){
               PoloLogger.print(LogLevel.INFO, "Loading §3changelog§7...");
               String bootstrapChangelog = Master.getInstance().getClient().getChangelogRequestService().getChangelog("bootstrap", Master.getInstance().getCurrentVersion());
               PoloLogger.print(LogLevel.INFO, "----[Changelog (§3" + Master.getInstance().getCurrentVersion() + "§7)]----\n[Bootstrap\n" + bootstrapChangelog + "\n]\n----[/Changelog]----");
           } else{
               sendHelp();
           }
       }else{
           sendHelp();
       }
    }

    private void sendHelp(){
        String[] helpArray = new String[] {"----[Changelog]----", "Use §3changelog §7show the changelog", "Use §3changelog bootstrap §7to show the changelog of the bootstrap", "----[/Changelog]----"};
        for (String s : helpArray) {
            PoloLogger.print(LogLevel.INFO, s);
        }
    }

    @Override
    public List<String> onTabComplete(CommandExecutor executor, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("bootstrap", "api");
        }
        return new LinkedList<>();
    }
}
