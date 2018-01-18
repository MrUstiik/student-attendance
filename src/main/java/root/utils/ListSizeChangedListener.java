package root.utils;

import javafx.collections.ListChangeListener;
import javafx.scene.control.ScrollBar;
import root.models.Student;

public class ListSizeChangedListener implements ListChangeListener<Student> {
    private static int listSize = 0;
    private static ScrollBar scrollBar;

    public ListSizeChangedListener(ScrollBar bar) {
        this.scrollBar = bar;
    }

    @Override
    public void onChanged(Change<? extends Student> c) {
        int newSize = c.getList().size();
        if(newSize != listSize){
            listSize = newSize;
            TableUtils.initScrollBar(scrollBar, newSize);
        }
    }
}
