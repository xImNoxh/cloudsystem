package de.polocloud.api.logger.def;

import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.logger.PoloLog;
import de.polocloud.api.logger.PoloLogger;
import de.polocloud.api.logger.helper.LogHandler;
import de.polocloud.api.logger.helper.LogLevel;
import de.polocloud.api.logger.helper.MinecraftColor;
import de.polocloud.api.util.Cancellable;
import de.polocloud.api.util.PoloHelper;
import de.polocloud.api.util.Snowflake;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class SimplePoloLog implements PoloLog {

    private final String name;
    private final long snowflake;
    private boolean archived;

    private final long startTime;
    private final File file;

    private final Queue<Pair<String, LogLevel>> loggedLines;
    private final List<String> header;
    private final PoloLogger parent;

    private final List<LogHandler> printHandlers;

    /**
     * If next log should be saved
     */
    private boolean saveNext;

    /**
     * If the next log should not be displayed
     */
    private boolean noDisplay;

    public SimplePoloLog(String name, PoloLogger parent) {
        this.name = name;
        this.archived = false;
        this.file = new File(parent.getDirectory(), name + ".log");
        this.parent = parent;

        this.loggedLines = new LinkedList<>();
        this.header = new ArrayList<>();
        this.printHandlers = new ArrayList<>();

        this.saveNext = false;
        this.noDisplay = false;

        this.snowflake = Snowflake.getInstance().nextId();
        this.startTime = System.currentTimeMillis();

        try {
            this.initLog();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addPrintHandler(LogHandler printHandler) {
        this.printHandlers.add(printHandler);
    }

    private void initLog() throws Exception{
        if (!this.file.exists()) {
            this.file.createNewFile();


            this.header.add("#################################################################");
            this.header.add("");
            this.header.add("Log for Logger '" + parent.getName() + "#" + parent.getSnowflake() + "' from " + PoloHelper.SIMPLE_DATE_FORMAT.format(new Date(this.startTime)));
            this.header.add("Current Log : '" + this.name + "#" + this.snowflake + "'");
            this.header.add("===================");
            this.header.add(
                "Polo Version: " + PoloCloudAPI.getInstance().getVersion().version() + "\n" +
                    "Operating System: " + System.getProperty("os.name") + " (" + System.getProperty("os.version") + ")\n" +
                    "System Architecture: " + System.getProperty("os.arch") + "\n" +
                    "Java Version: " + System.getProperty("java.version") + " (" + System.getProperty("java.vendor") + ")"
            );
            this.header.add("");
            this.header.add("#################################################################");

            PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(this.file), StandardCharsets.UTF_8), true);

            for (String s : this.header) {
                printWriter.println(s);
            }

            printWriter.flush();
            printWriter.close();
        } else {
            BufferedReader reader = new BufferedReader(new FileReader(this.file));

            this.archived = true;
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    String s = line.split("] -> ")[1];
                    String s1 = line.split("] -> ")[0]; // [Logger/WARNING
                    String type = s1.split("/")[1];

                    LogLevel level = LogLevel.valueOf(type);
                    this.loggedLines.add(new Pair<>(s, level));
                } catch (Exception e) {
                    //The first lines (header) do not contain any logging messages
                    //Ignoring and reading on but adding line as header
                    header.add(line);
                }
            }


            reader.close();
        }
    }



    @Override
    public Map<String, LogLevel> getLoggedLines() {
        Map<String, LogLevel> logLevelMap = new HashMap<>();
        for (Pair<String, LogLevel> loggedLine : this.loggedLines) {
            logLevelMap.put(loggedLine.getKey(), loggedLine.getValue());
        }
        return logLevelMap;
    }

    @Override
    public void log(LogLevel level, String line) {
        line = MinecraftColor.translateColorCodes('§', line);

        this.archived = false;
        this.loggedLines.add(new Pair<>(line, level));

        if (this.saveNext) {
            this.saveNext = false;

            try {
                Writer output = new BufferedWriter(new FileWriter(this.file, true));
                output.write(line);
                output.write("\n");
                output.flush();
                output.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (noDisplay) {
            noDisplay = false;
            return;
        }

        Cancellable cancellable = new Cancellable();

        for (LogHandler printHandler : printHandlers) {
            printHandler.handleLoggedMessage(cancellable, this, line);
        }

        if (!cancellable.isCancelled()) {
            if (parent.getLevel() == LogLevel.ALL || parent.getLevel().equals(level) && parent.getLevel() != LogLevel.OFF) {
                System.out.println(MinecraftColor.translateColorCodes('§', line));
            }
        }
    }

    @Override
    public PoloLog noDisplay() {
        this.noDisplay = true;
        return this;
    }

    @Override
    public PoloLog saveNextLog() {
        this.saveNext = true;
        return this;
    }

    @Override
    public void delete() throws FileNotFoundException {
        if (!this.file.exists()) {
            throw new FileNotFoundException();
        }
        this.file.delete();
        parent.removeLog(this.name);
    }

    @Override
    public void clear() {
        this.loggedLines.clear();
    }

    @Override
    public void save() {
        try (PrintWriter w = new PrintWriter(new OutputStreamWriter(new FileOutputStream(this.file), StandardCharsets.UTF_8), true)) {

            for (String s : this.header) {
                w.println(MinecraftColor.replaceColorCodes(s));
            }

            for (Pair<String, LogLevel> loggedLine : new ArrayList<>(loggedLines)) {
                String s = loggedLine.getKey();
                LogLevel level = loggedLine.getValue();
                w.println(MinecraftColor.replaceColorCodes(level.format(this.parent, s)));
            }

            w.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public long getStartTime() {
        return startTime;
    }

    @Override
    public File getFile() {
        return file;
    }

    @Override
    public boolean isArchived() {
        return archived;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public long getSnowflake() {
        return snowflake;
    }
}
