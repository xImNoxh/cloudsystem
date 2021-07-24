package de.polocloud.bootstrap.commands;

import de.polocloud.api.commands.CloudCommand;
import de.polocloud.api.commands.CommandType;
import de.polocloud.api.commands.ICommandExecutor;
import de.polocloud.api.util.Hastebin;

import java.io.*;

@CloudCommand.Info(name = "logMe", aliases = "", description = "Log the Master to hastebin", commandType = CommandType.CONSOLE)
public class LogMeCommand extends CloudCommand {

    private final Hastebin hastebin = new Hastebin();
    private final File logfile = new File("log/latest.log");


    @Override
    public void execute(ICommandExecutor commandSender, String[] args) {

        StringBuilder logString = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(logfile))) {
            String line;
            while ((line = br.readLine()) != null) {
                // process the line.
                logString.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            String url = hastebin.post(logString.toString(), true);
            System.out.println("link: " + url);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
