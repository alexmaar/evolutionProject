import components.MyBox;
import components.MyPane;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import parameters.WorldParameters;
import simulation.Simulation;
import visualizer.FXVisualizer;

public class WorldFX extends Application {

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Animal Land");
        //First world
        MyPane pane = new MyPane(WorldParameters.getInstance().getWidth()*25,WorldParameters.getInstance().getHeight()*25, WorldParameters.getInstance().getWidth(), WorldParameters.getInstance().getHeight());
        FXVisualizer visualizer = new FXVisualizer(pane);
        Simulation simulation = new Simulation(1000, 20, 1000, visualizer);
        Thread firstMap = new Thread(simulation);
        firstMap.start();
        MyBox box = new MyBox(pane, firstMap);

        //second world
        MyPane pane2 = new MyPane(WorldParameters.getInstance().getWidth()*25,WorldParameters.getInstance().getHeight()*25, WorldParameters.getInstance().getWidth(), WorldParameters.getInstance().getHeight());
        FXVisualizer visualizer2 = new FXVisualizer(pane2);
        Simulation simulation2 = new Simulation(1000, 20, 500, visualizer2);
        Thread secondMap = new Thread(simulation2);
        secondMap.start();
        MyBox box2 = new MyBox(pane2, secondMap);


        HBox mainBox = new HBox(box, box2);
        mainBox.setSpacing(10);

        Scene scene = new Scene(mainBox);
        stage.setScene(scene);
        stage.show();
    }
}
