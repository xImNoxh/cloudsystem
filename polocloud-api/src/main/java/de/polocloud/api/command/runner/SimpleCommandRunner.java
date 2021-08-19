
package de.polocloud.api.command.runner;

import com.esotericsoftware.reflectasm.MethodAccess;
import de.polocloud.api.PoloCloudAPI;
import de.polocloud.api.command.executor.ExecutorType;
import de.polocloud.api.command.annotation.*;
import de.polocloud.api.command.executor.CommandExecutor;
import de.polocloud.api.command.identifier.CommandListener;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class SimpleCommandRunner implements ICommandRunner {

    /**
     * The method access
     */
    private final MethodAccess access;

    /**
     * The listener
     */
    private final CommandListener listener;

    /**
     * The method index
     */
    private final int methodIndex;

    /**
     * The command of this instance
     */
    private final Command command;

    /**
     * The parameters
     */
    private final Parameter[] parameters;

    private final int varargsIndex;

    private final int minimumParameters;
    private final int maximumParameters;

    private final boolean showUsage;
    private final String[] message;
    private final String[] onlyArgs;

    private final int minimumVariableArgs;
    private final int maximumVariableArgs;

    private final String[] permissionRequired;
    private final ExecutorType[] allowedSourceTypes;

    @Override
    public CommandListener getListener() {
        return listener;
    }

    public SimpleCommandRunner(MethodAccess access, CommandListener listener, Method method, Command command) {
        this.access = access;
        this.listener = listener;
        this.methodIndex = access.getIndex(method.getName(), method.getParameterTypes());
        this.command = command;
        this.parameters = Arrays.copyOfRange(method.getParameters(), 2, method.getParameterCount());
        int vIndex = -1, min = parameters.length, max = parameters.length;
        int minVar = 0, maxVar = Integer.MAX_VALUE;
        String[] message = {"Enter arguments in range between %min% - %max%!"};
        String[] onlyArgs = new String[0];
        boolean showUsage = false;

        for (int i = 0; i < this.parameters.length; i++) {
            Parameter param = this.parameters[i];
            if (param.getType().isArray() || List.class.isAssignableFrom(param.getType())) {
                if (vIndex != -1) {
                    throw new RuntimeException("CommandDispatcher can't have more than one variable length parameter");
                }
                vIndex = i;

                Arguments arguments = param.getAnnotation(Arguments.class);
                if (arguments != null) {
                    if ((minVar = arguments.min()) < 0 && arguments.min() != -1) {
                        throw new IllegalArgumentException("CommandDispatcher minimum cannot be less than 0");
                    }
                    showUsage = arguments.showUsage();
                    message = arguments.message();
                    if (arguments.onlyFirstArgs().length != 0) {
                        onlyArgs = arguments.onlyFirstArgs();
                    }

                    if (arguments.max() == -1) {
                        max = Integer.MAX_VALUE;
                    } else {
                        max += maxVar - 1;
                    }

                    if (arguments.min() == -1) {
                        min = -1;
                    } else {
                        min += minVar - 1;
                    }

                } else {
                    min -= 1;
                    max = Integer.MAX_VALUE;
                }

                if (min > max) {
                    throw new RuntimeException("CommandDispatcher minimum cannot be more than the maximum");
                }
            }
        }
        this.varargsIndex = vIndex;
        this.minimumParameters = min;
        this.maximumParameters = max;
        this.minimumVariableArgs = minVar;
        this.maximumVariableArgs = maxVar;
        this.message = message;
        this.showUsage = showUsage;
        this.onlyArgs = onlyArgs;

        CommandPermission permissionRequired = method.getAnnotation(CommandPermission.class);
        if (permissionRequired != null && permissionRequired.value().length > 0) {
            this.permissionRequired = permissionRequired.value();
        } else {
            this.permissionRequired = null;
        }

        CommandExecutors sources = method.getAnnotation(CommandExecutors.class);
        if (sources != null && sources.value().length > 0) {
            this.allowedSourceTypes = sources.value();
        } else {
            this.allowedSourceTypes = new ExecutorType[]{ ExecutorType.ALL };
        }
    }

    @Override
    public String getUsage() {
        StringBuilder sb = new StringBuilder("/" + command.name());
        for (Parameter param : parameters) {
            sb.append(" <").append(param.getName());
            Class<?> type = param.getType();
            if (param.getType().isArray()) {
                sb.append("...");
                type = type.getComponentType();
            }
            if (type != String.class && type != Object.class) {
                sb.append(" : ").append(type.getSimpleName());
            }
            sb.append('>');
        }
        return sb.toString();
    }

    @Override
    public void runCommand(String cmd, CommandExecutor source, String[] args) {
        if (this.permissionRequired != null) {
            boolean hasPermission = false;
            for (String permission : this.permissionRequired) {
                if (source.hasPermission(permission)) {
                    hasPermission = true;
                }
            }
            if (!hasPermission) {
                source.sendMessage("You do not have the appropriate permissions to execute this command. Contact the server administrators if you believe that this is in error.");
                return;
            }
        }

        boolean allowedType = false;
        for (ExecutorType cst : allowedSourceTypes) {
            if (cst.isTypeOf(source)) {
                allowedType = true;
                break;
            }
        }
        if (!allowedType) {
            source.sendMessage("The current CommandExecutor (" + source.getType().name() + ") is not allowed to execute this command!");
            return;
        }

        boolean tooFewArguments = minimumParameters == -1 ? false : args.length < this.minimumParameters;
        boolean tooManyArguments = maximumParameters == -1 ? false : args.length > this.maximumParameters;
        if (tooFewArguments || tooManyArguments) {
            for (String s : message) {
                source.sendMessage(s.replace("%min%", this.minimumParameters + "").replace("%max%", this.maximumParameters + ""));
            }
            if (!showUsage) {
                return;
            }
            source.sendMessage("Usage: " + this.getUsage());
            return;
        }

        int variableArgsCount = args.length - parameters.length + 1;

        String[] fullArgs = new String[args.length + 1];
        fullArgs[0] = cmd;
        System.arraycopy(args, 0, fullArgs, 1, args.length);

        Object[] invokeArguments = new Object[2 + this.parameters.length];
        invokeArguments[0] = source;
        invokeArguments[1] = fullArgs;

        List<Object> variableArgs = new ArrayList<>();

        for (int i = 0; i < args.length; i++) {
            int paramIndex = i;
            if (varargsIndex >= 0 && i >= varargsIndex) {
                paramIndex = i >= varargsIndex + variableArgsCount ? i - variableArgsCount + 1 : varargsIndex;
            }
            Parameter param = this.parameters[paramIndex];
            Class<?> paramType = param.getType().isArray() ? param.getType().getComponentType() : param.getType();
            Object result;
            try {
                result = PoloCloudAPI.getInstance().getCommandManager().transform(args[i], param, paramType);
            } catch (UnsupportedOperationException ex) {
                source.sendMessage("Failed to parse \"" + args[i] + "\" as a " + paramType.getSimpleName() + "!");
                source.sendMessage("Maybe no Transformer was registered for " + paramType.getSimpleName() + "=");
                return;
            } catch (Exception ex) {
                source.sendMessage("\"" + args[i] + "\" is not a valid input!");
                source.sendMessage("Please enter a value of type " + paramType.getSimpleName() + "!");
                return;
            }
            if (paramIndex == varargsIndex) {
                if (result != null) {
                    variableArgs.add(result);
                }
            } else {
                invokeArguments[2 + paramIndex] = result;
            }
        }

        if (varargsIndex >= 0) {
            tooFewArguments = minimumVariableArgs == -1 ? false : variableArgs.size() < this.minimumVariableArgs;
            tooManyArguments = maximumVariableArgs == -1 ? false : variableArgs.size() > this.maximumVariableArgs;
            if (tooFewArguments || tooManyArguments) {
                for (String s : message) {
                    source.sendMessage(s.replace("%min%", this.minimumParameters + "").replace("%max%", this.maximumParameters + ""));
                }
                if (!showUsage) {
                    return;
                }
                source.sendMessage("Usage: " + this.getUsage());
                return;
            }
            Object arr = Array.newInstance(parameters[varargsIndex].getType().getComponentType(), variableArgs.size());
            for (int i = 0, j = variableArgs.size(); i < j; i++) {
                Array.set(arr, i, variableArgs.get(i));
            }
            invokeArguments[2 + varargsIndex] = arr;
        }
        if (fullArgs.length >= 2 && (onlyArgs.length > 0 && !Arrays.asList(onlyArgs).contains(fullArgs[1]))) {
            for (String s : message) {
                source.sendMessage(s.replace("%min%", this.minimumParameters + "").replace("%max%", this.maximumParameters + ""));
            }
            if (!showUsage) {
                return;
            }
            source.sendMessage("Usage: " + this.getUsage());
            return;
        }

        this.access.invoke(this.listener, this.methodIndex, invokeArguments);
    }

    @Override
    public Command getCommand() {
        return command;
    }

    @Override
    public boolean isContainedBy(Class<? extends CommandListener> aClass) {
        return this.listener.getClass().equals(aClass);
    }

}
