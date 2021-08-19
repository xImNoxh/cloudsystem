package de.polocloud.api.command.executor;

import de.polocloud.api.player.ICloudPlayer;

public enum ExecutorType {

    PLAYER {
        @Override
        public boolean isTypeOf(CommandExecutor o) {
            return o instanceof ICloudPlayer;
        }
    }, CONSOLE {
        //TODO: RIGHT CONSOLE EXECUTOR
        @Override
        public boolean isTypeOf(CommandExecutor o) {
            return o instanceof ConsoleExecutor;
        }
    }, ALL {
        @Override
        public boolean isTypeOf(CommandExecutor o) {
            return true;
        }
    };

    /**
     * Checks to see whether the given source is a type of
     * the command source.
     *
     * @param o the source to check
     * @return {@code true} if it is a type of the command
     * source
     */
    public abstract boolean isTypeOf(CommandExecutor o);
}
