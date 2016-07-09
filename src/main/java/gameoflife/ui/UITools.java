package gameoflife.ui;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import java.util.ResourceBundle;
import java.util.function.Consumer;

/**
 * Created by Vlad Kanash on 9.7.16.
 */

class UITools {

    private final static String BUNDLE_PATH = "gameoflife.config.FilePathConfig";
    private static final ResourceBundle pathConfig = ResourceBundle.getBundle(BUNDLE_PATH);

    static MenuItem initMenuItem(final Menu parent, final String text,
                                        final int accelerator, final int type, final Consumer<SelectionEvent> action) {
        final MenuItem item = new MenuItem(parent, type);
        item.setText(text);
        if (accelerator != 0) {
            item.setAccelerator(accelerator);
        }
        item.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                action.accept(e);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        return item;
    }

    static MenuItem initMenuItem(final Menu parent, final String text,
                                 final int accelerator, final int type, final Runnable action) {
        return initMenuItem(parent, text, accelerator, type, e -> action.run());
    }

    static ToolItem initImageButton(final ToolBar mainToolBar,
                                            final String pathKey, final int type, final Consumer<SelectionEvent> action) {
        ToolItem item = new ToolItem(mainToolBar, type);
        ImageData img = new ImageData(pathConfig.getString(pathKey));
        item.setImage(new Image(mainToolBar.getDisplay(), img));
        item.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                action.accept(e);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        return item;
    }

    static ToolItem initImageButton(final ToolBar mainToolBar, final String pathKey, final int type, final Runnable action) {
        return initImageButton(mainToolBar, pathKey, type, e -> action.run());
    }
}
