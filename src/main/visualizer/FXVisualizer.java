package visualizer;

import components.MyPane;
import mapElements.IMapElement;
import structures.FieldImage;

public class FXVisualizer implements IMapObserver {

    private MyPane pane;

    public FXVisualizer(MyPane pane) {
        this.pane = pane;
    }

    @Override
    public void onFiledChanged(IMapElement element, FieldImage newImage){
        this.pane.updateNode(element, newImage);
    }
}
