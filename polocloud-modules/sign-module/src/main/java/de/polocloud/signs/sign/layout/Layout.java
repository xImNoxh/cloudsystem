package de.polocloud.signs.sign.layout;

public class Layout {

    private final String[] lines;
    private final BlockLayout blockLayout;

    public Layout(BlockLayout blockLayout, String... lines) {
        this.lines = lines;
        this.blockLayout = blockLayout;
    }

    public String[] getLines() {
        return lines;
    }

    public BlockLayout getBlockLayout() {
        return blockLayout;
    }

}
