package gameoflife.config;

import java.util.ListResourceBundle;

/**
 * Created by root on 9.7.16.
 */
public final class FilePathConfig extends ListResourceBundle {
    @Override
    protected Object[][] getContents() {
        return new Object[][] {
                {"playImage", "Play.png"},
                {"pauseImage", "Pause.png"},
                {"stepImage", "Step.png"}
        };
    }
}
