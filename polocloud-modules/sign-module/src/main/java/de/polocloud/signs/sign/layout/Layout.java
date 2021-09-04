package de.polocloud.signs.sign.layout;

public class Layout {

    private String[] lines;
    private de.polocloud.signs.sign.layout.BlockLayout blockLayout;

    public Layout(de.polocloud.signs.sign.layout.BlockLayout blockLayout, String... lines) {
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
