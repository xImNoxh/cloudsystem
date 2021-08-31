
package de.polocloud.api.command;

import com.esotericsoftware.reflectasm.MethodAccess;
import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.command.annotation.Command;
import de.polocloud.api.command.executor.CommandExecutor;
import de.polocloud.api.command.identifier.CommandListener;
import de.polocloud.api.command.runner.ICommandRunner;
import de.polocloud.api.command.runner.SimpleCommandRunner;
import de.polocloud.api.player.ICloudPlayer;
import de.polocloud.api.util.Acceptable;
import de.polocloud.api.util.PoloHelper;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

public class SimpleCommandManager implements ICommandManager {

    private final Map<String, ICommandRunner> dispatchers;

    /**
     * The current filter
     */
    private Acceptable<ICommandRunner> filter;

    //Transformers
    private static final Map<Class<?>, BiFunction<String, Parameter, ?>> transformers = new ConcurrentHashMap<>();
    private static final Map<Class<?>, BiFunction<String, Parameter, ?>> inheritedTransformers = new ConcurrentHashMap<>();


    /**
     * Creates a new command handler. Cannot be used by
     * plugins.
     */
    public SimpleCommandManager() {
        this.dispatchers = new ConcurrentHashMap<>();
        this.filter = null;

        this.registerTransformer(byte.class, (s, p) -> {
            try {
                return Byte.valueOf(s);
            } catch (Exception ex) {
                throw new NullPointerException("Invalid input! Enter an integer in -128 to 127!");
            }
        });
        this.registerTransformer(short.class, (s, p) -> {
            try {
                return Short.valueOf(s);
            } catch (Exception ex) {
                throw new NullPointerException("Invalid input! Enter an integer in -65536 to 65535!");
            }
        });
        this.registerTransformer(int.class, (s, p) -> {
            try {
                return Integer.valueOf(s);
            } catch (Exception ex) {
                throw new NullPointerException("Invalid input! Enter an integer!");
            }
        });
        this.registerTransformer(long.class, (s, p) -> {
            try {
                return Long.valueOf(s);
            } catch (Exception ex) {
                throw new NullPointerException("Invalid input! Enter an integer!");
            }
        });
        this.registerTransformer(float.class, (s, p) -> {
            try {
                return Float.valueOf(s);
            } catch (Exception ex) {
                throw new NullPointerException("Invalid input! Enter a number!");
            }
        });
        this.registerTransformer(double.class, (s, p) -> {
            try {
                return Double.valueOf(s);
            } catch (Exception ex) {
                throw new NullPointerException("Invalid input! Enter a number!");
            }
        });
        this.registerTransformer(Number.class, (s, p) -> {
            try {
                return Double.valueOf(s);
            } catch (Exception ex) {
                throw new NullPointerException("Invalid input! Enter a number!");
            }
        });
        this.registerTransformer(boolean.class, (s, p) -> !s.isEmpty() && (s.charAt(0) == 'y' || s.charAt(0) == 't'));
        this.registerTransformer(String.class, (s, p) -> s);
        this.registerTransformer(Object.class, (s, p) -> s);

        this.registerTransformer(String[].class, (s, parameter) -> s.split( " "));
        this.registerTransformer(ICloudPlayer.class, (s, p) -> {
            try {
                return PoloCloudAPI.getInstance().getCloudPlayerManager().getCached(s);
            } catch (Exception e) {
                throw new NullPointerException("The player '" + s + "' seems not to be online!");
            }
        });
    }

    @Override
    public void registerCommand(CommandListener listener) {

        Class<? extends CommandListener> cls = listener.getClass();
        MethodAccess access = MethodAccess.get(cls);
        for (Method method : cls.getDeclaredMethods()) {
            Command cmd = method.getAnnotation(Command.class);
            if (cmd == null) {
                continue;
            }

            Class<?>[] parameterTypes = method.getParameterTypes();
            String methodSignature = PoloHelper.getMethodSignature(method);
            String name = cmd.name().toLowerCase();

            ICommandRunner dispatcher;
            if (parameterTypes.length >= 2 && parameterTypes[0] == CommandExecutor.class && parameterTypes[1] == String[].class) {
                try {
                    dispatcher = new SimpleCommandRunner(access, listener, method, cmd);
                } catch (Throwable ex) {
                    ex.printStackTrace();
                    PoloCloudAPI.getInstance().getCommandExecutor().sendMessage("Error registering command \"" + name + "\" from " + methodSignature + ": " + ex.getMessage());
                    continue;
                }
            } else {
                PoloCloudAPI.getInstance().getCommandExecutor().sendMessage("Error registering command \"" + name + "\" from " + methodSignature + ": does not match any expected method signature");
                continue;
            }

            this.dispatchers.put(name, dispatcher);
            for (String _alias : cmd.aliases()) {
                String alias = _alias.toLowerCase();
                this.dispatchers.put(alias, dispatcher);
            }
        }
    }

    @Override
    public void unregisterCommand(Class<? extends CommandListener> listener) {
       this.dispatchers.entrySet().removeIf(e -> e.getValue().isContainedBy(listener));
    }

    public List<ICommandRunner> getDispatchers() {
        return new LinkedList<>(dispatchers.values());
    }

    @Override
    public boolean runCommand(String cmd, CommandExecutor source) {
        String[] split = cmd.split("\\s+");
        String label = split[0].toLowerCase();
        ICommandRunner dispatcher;

        dispatcher = this.dispatchers.get(label);
        if (dispatcher == null) {
            return false;
        }
        if (filter != null && !filter.isAccepted(dispatcher)) {
            return true;
        }

        String[] args = new String[split.length - 1];
        if (args.length > 0) {
            System.arraycopy(split, 1, args, 0, args.length);
        }

        try {
            dispatcher.runCommand(split[0], source, args);
        } catch (Exception ex) {
            if (source == null) {
                return false;
            }
            source.sendMessage("An error occurred while executing the command.");
            ex.printStackTrace();
        }
        return true;
    }

    @Override
    public List<ICommandRunner> getCommands() {
        LinkedList<ICommandRunner> commands = new LinkedList<>(this.dispatchers.values());
        Set<ICommandRunner> set = new HashSet<>(commands);
        commands.clear();
        commands.addAll(set);
        return commands;
    }

    @Override
    public Map<String, ICommandRunner> getCommandsAsMap() {
        return this.dispatchers;
    }

    @Override
    public void setFilter(Acceptable<ICommandRunner> filter) {
        this.filter = filter;
    }

    @Override
    public <T> void registerTransformer(Class<T> clazz, BiFunction<String, Parameter, T> transformer) {
        if (transformers.putIfAbsent(clazz, transformer) != null)
            return;
        Class<?> cls = clazz.getSuperclass();
        while (cls != null && cls != Object.class) {
            if (inheritedTransformers.putIfAbsent(cls, transformer) != null)
                break;
            cls = cls.getSuperclass();
        }
    }

    @Override
    public <T> T transform(String input, Parameter parameter, Class<T> clazz) throws Exception {
        BiFunction<String, Parameter, ?> transformer = transformers.get(clazz);
        if (transformer == null)
            transformer = inheritedTransformers.get(clazz);
        if (transformer == null)
            throw new UnsupportedOperationException("transformer missing for " + clazz);
        return (T) transformer.apply(input, parameter);
    }


}
