package de.polocloud.signs.sign.layout;

public class Layout {

    private String[] lines;
    private BlockLayout blockLayout;

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
