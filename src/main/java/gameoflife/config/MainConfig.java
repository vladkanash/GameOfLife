package gameoflife.config;

import java.util.ListResourceBundle;

/**
 * Created by Vlad Kanash on 9.7.16.
 */

public class MainConfig extends ListResourceBundle {
    @Override
    protected Object[][] getContents() {
        return new Object[][] {
                {"gameTitle", "Game of Life"},
                {"CancelKey", "Cancel"},
                {"initialWidth", 800},
                {"initialHeight", 600},

                {"speedLabel", "Speed: "},
                {"generationLabel", "Generation: "},

                {"zoomHuge", 30},
                {"zoomNormal", 10},
                {"zoomSmall", 4},
                {"zoomTiny", 1}
        };
    }
}
