package components;

import javafx.application.Platform;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import mapElements.IMapElement;
import structures.FieldImage;
import structures.Vector2d;

public class MyNode extends ImageView {


    private final Vector2d position;

    private final Tooltip tooltip;

    public MyNode(IMapElement element, double width, double height) {
        super(element.getImage().getImg());

        setFitWidth(width);
        setFitHeight(height);

        this.position = element.getPosition();
        this.tooltip = new Tooltip(element.toString());
        tooltip.setShowDelay(Duration.ZERO);
        tooltip.setShowDuration(Duration.INDEFINITE);

        Tooltip.install(this, tooltip);
    }

    public void update(IMapElement element, FieldImage image) {
        if(Platform.isFxApplicationThread()){
            tooltip.setText(element.toString());
            setImage(image.getImg());
        }else{
            Platform.runLater(()->{
                tooltip.setText(element.toString());
                setImage(image.getImg());
            });
        }
    }
}
