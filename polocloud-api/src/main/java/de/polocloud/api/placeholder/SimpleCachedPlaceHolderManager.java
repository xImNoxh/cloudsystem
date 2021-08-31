package de.polocloud.api.placeholder;

import java.util.LinkedList;
import java.util.List;


public class SimpleCachedPlaceHolderManager implements IPlaceHolderManager {

    private final List<IPlaceHolder<?>> placeHolders;


    public SimpleCachedPlaceHolderManager() {
        this.placeHolders = new LinkedList<>();
    }


    @Override
    public void registerPlaceHolder(IPlaceHolder<?> iPlaceHolder) {
        this.placeHolders.add(iPlaceHolder);
    }


    @Override
    public String applyToString(String input, Object... objects) {
        for (IPlaceHolder placeHolder : placeHolders) {
            for (Object object : objects) {
                for (Class<?> acceptedClass : placeHolder.getAcceptedClasses()) {
                    if (acceptedClass.equals(object.getClass())) {
                        input = placeHolder.apply(object, input);
                    }
                }
            }
        }
        return input;
    }

    @Override
    public List<IPlaceHolder<?>> getPlaceHolders() {
        return placeHolders;
    }
}
