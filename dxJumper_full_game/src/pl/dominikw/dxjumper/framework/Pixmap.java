package pl.dominikw.dxjumper.framework;

import pl.dominikw.dxjumper.framework.Graphics.PixmapFormat;

public interface Pixmap {
    public int getWidth();

    public int getHeight();

    public PixmapFormat getFormat();

    public void dispose();
}
