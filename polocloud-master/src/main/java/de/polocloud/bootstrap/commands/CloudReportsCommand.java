package de.polocloud.bootstrap.commands;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.command.annotation.Arguments;
import de.polocloud.api.command.annotation.Command;
import de.polocloud.api.command.executor.CommandExecutor;
import de.polocloud.api.command.identifier.CommandListener;
import de.polocloud.api.command.identifier.TabCompletable;
import de.polocloud.api.logger.PoloLogger;
import de.polocloud.api.logger.helper.LogLevel;
import de.polocloud.client.PoloCloudClient;
import de.polocloud.client.enumeration.ReportType;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CloudReportsCommand implements CommandListener, TabCompletable {
    public CloudReportsCommand() {
    }

    @Command(name = "cloudreports", description = "Manage you privacy to our servers", aliases = "")
    public void execute(CommandExecutor sender, String[] fullArgs, @Arguments(onlyFirstArgs = {"disable", "info", "setmode"}, min = 0, max = 2, message = {"----[CloudReports]----", "Use §3cloudreports disable §7to disable any reports", "Use §3cloudreports info §7to show your current mode and if the mode is not NONE your reported exceptions", "", "Available Modes:", "§3NONE§7: No information will be sent to us", "§3MINIMAL§7: Minimal information about a exception will be sent (stacktrace, version, master or wrapper)", "§3OPTIONAL§7: More information will be sent (Java-Version, stacktrace, version, master or wrapper)", "----[/CloudReports]----"}) String... params) {

        if(params.length == 1){
            if(params[0].equalsIgnoreCase("disable")){
                if(PoloCloudClient.getInstance().getClientConfig().getReportType().equals(ReportType.NONE)){
                    PoloLogger.print(LogLevel.INFO, "§7The §bReport-Mode §7is already disabled!");
                }else{
                    PoloLogger.print(LogLevel.INFO, "§7Disabling...");
                    PoloCloudClient.getInstance().getClientConfig().setReportType(ReportType.NONE);
                    PoloCloudAPI.getInstance().getConfigSaver().save(PoloCloudClient.getInstance().getClientConfig(), new File("client.json"));
                    PoloLogger.print(LogLevel.INFO, "You have §asuccessfully §cdisabled §7the reporting of information to the polocloud servers!");
                }
            }else if(params[0].equals("info")){
                PoloLogger.print(LogLevel.INFO, "§7----[§bReport-Info§7]----");
                PoloLogger.print(LogLevel.INFO, "§7Your current Report-Mode is » §b" + PoloCloudClient.getInstance().getClientConfig().getReportType());
                if(!PoloCloudClient.getInstance().getClientConfig().getReportType().equals(ReportType.NONE)){
                    PoloLogger.print(LogLevel.INFO, "§7According to the config, your system has reported » §b" + PoloCloudClient.getInstance().getClientConfig().getReportedExceptions() + " §7exceptions");
                    PoloLogger.print(LogLevel.INFO, "§bThank you §7for sharing your exceptions and information with us! §c<3");
                }
                PoloLogger.print(LogLevel.INFO, "§7----[§b/Report-Info§7]----");
            }
        }else if(params.length == 2){
            if(params[0].equalsIgnoreCase("setMode")){
                try{
                    PoloLogger.print(LogLevel.INFO, "§7Setting...");
                    ReportType type = ReportType.valueOf(params[1].toUpperCase());
                    PoloCloudClient.getInstance().getClientConfig().setReportType(type);
                    PoloLogger.print(LogLevel.INFO, "§7The §bReport-Mode §7was §asuccessfully §7set to » §b" + type + "§7!");
                }catch(IllegalArgumentException exception){
                    PoloLogger.print(LogLevel.WARNING, "§7This mode §7doesn't exists.");
                }
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandExecutor executor, String[] args) {
        if(args.length == 0){
            return Arrays.asList("info", "disable", "setmode");
        }else if(args.length == 1){
            if(args[0].equals("setmode")){
                List<String> options = new ArrayList<>();
                for (ReportType value : ReportType.values()) {
                    options.add(String.valueOf(value));
                }
                return options;
            }
        }
        return new ArrayList<>();
    }
}
