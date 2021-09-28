package de.polocloud.api.util.system.resources.impl;

import de.polocloud.api.util.system.resources.IResourceConverter;

public class SimpleResourceConverter implements IResourceConverter {

    @Override
    public String convertLongToSize(long value) {
        final long[] dividers = new long[]{T, G, M, K, 1};
        final String[] units = new String[]{"TB", "GB", "MB", "KB", "B"};

        if (value < 1) {
            value = 0;
        }
        String result = null;
        for (int i = 0; i < dividers.length; i++) {
            final long divider = dividers[i];
            if (value >= divider) {
                result = formatSize(value, divider, units[i]);
                break;
            }
        }
        return result;
    }

    @Override
    public String formatSize(long value, long divider, String unit) {
        final double result =
            divider > 1 ? (double) value / (double) divider : (double) value;
        return decimalFormat.format(result) + " " + unit;
    }

    @Override
    public String roundDouble(double value) {
        return roundFormat.format(value);
    }
}
