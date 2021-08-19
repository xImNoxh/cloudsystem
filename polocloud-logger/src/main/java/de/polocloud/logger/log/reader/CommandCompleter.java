package de.polocloud.logger.log.reader;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.command.identifier.CommandListener;
import de.polocloud.api.command.identifier.TabCompletable;
import de.polocloud.api.command.runner.ICommandRunner;
import jline.console.completer.Completer;

import java.util.*;

public class CommandCompleter implements Completer {


    @Override
    public int complete(String buffer, int cursor, List<CharSequence> candidates) {
        String[] input = buffer.split(" ");

        List<String> responses = new ArrayList<>();
        List<String> commands = new LinkedList<>();
        for (ICommandRunner command : PoloCloudAPI.getInstance().getCommandManager().getCommands()) {
            commands.add(command.getCommand().name());
        }
        if (buffer.isEmpty() || buffer.indexOf(' ') == -1) {

            String[] args = buffer.split(" ");
            String testString = args[args.length - 1];
            List<String> retu = new LinkedList<>();
            for (String s : commands) {
                if (s != null && (testString.trim().isEmpty() || s.toLowerCase().contains(testString.toLowerCase()))) {
                    retu.add(s);
                }
            }

            if (retu.isEmpty() && !commands.isEmpty()) {
                retu.addAll(commands);
            }

            responses.addAll(retu);
        } else {
            ICommandRunner runner = PoloCloudAPI.getInstance().getCommandManager().getCommands().stream().filter(iCommandRunner -> iCommandRunner.getCommand().name().equalsIgnoreCase(input[0]) || Arrays.asList(iCommandRunner.getCommand().aliases()).contains(input[0])).findFirst().orElse(null);
            if (runner == null) {
                return -1;
            }
            CommandListener listener = runner.getListener();
            if (listener instanceof TabCompletable) {
                TabCompletable tabComplete = (TabCompletable)listener;

                String[] args = buffer.split(" ");
                String testString = args[args.length - 1];

                List<String> strings = new LinkedList<>(Arrays.asList(args));
                strings.remove(0); //Removing command name
                args = strings.toArray(new String[0]);

                List<String> list = tabComplete.onTabComplete(PoloCloudAPI.getInstance().getCommandExecutor(), args);

                List<String> retu = new LinkedList<>();
                for (String s : list) {
                    if (s != null && (testString.trim().isEmpty() || s.toLowerCase().contains(testString.toLowerCase()))) {
                        retu.add(s);
                    }
                }

                if (retu.isEmpty() && !list.isEmpty()) {
                    retu.addAll(list);
                }

                responses.addAll(retu);
            }

        }

        Collections.sort(responses);

        candidates.addAll(responses);
        int lastSpace = buffer.lastIndexOf(' ');

        return (lastSpace == -1) ? cursor - buffer.length() : cursor - (buffer.length() - lastSpace - 1);
    }
}
