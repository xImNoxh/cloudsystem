package de.polocloud.api.util.system.ressources;

import java.text.DecimalFormat;

public interface IResourceConverter {

    /**
     * Default values for converting
     */
    long K = 1024;
    long M = K * K;
    long G = M * K;
    long T = G * K;

    DecimalFormat decimalFormat = new DecimalFormat("#,##0.#");
    DecimalFormat roundFormat = new DecimalFormat("#.##");

    /**
     * Converts a long value to a String with better showing
     *
     * @param value, the input of the resources (kb/bytes)
     * @return 's a converted String with better showing (in GB, MB, ...)
     */
    String convertLongToSize(final long value);

    String formatSize(final long value, final long divider, final String unit);

    String roundDouble(double value);

}
