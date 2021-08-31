package de.polocloud.api.placeholder;

import java.util.List;

public interface IPlaceHolderManager {


    /**
     * All registered {@link IPlaceHolder}s
     */
    List<IPlaceHolder<?>> getPlaceHolders();

    /**
     * Registers a {@link IPlaceHolder} in cache
     *
     * @param IPlaceholder the placeholder
     */
    void registerPlaceHolder(IPlaceHolder<?> IPlaceholder);

    /**
     * Applies all {@link IPlaceHolder}s to a given input
     *
     * @param input the input
     * @return replaced string
     */
    String applyToString(String input, Object... objects);
}
