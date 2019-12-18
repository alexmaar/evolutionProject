package components;

import javafx.scene.layout.GridPane;
import mapElements.Ground;
import mapElements.IMapElement;
import structures.FieldImage;
import structures.Vector2d;

public class MyPane extends GridPane {
    private MyNode[][] nodes;

    public MyPane(double width, double height, int rowCount, int columnCount) {
        //setHgap(1);
        //setVgap(1);
        nodes = new MyNode[rowCount][columnCount];
        for(int i = 0; i < rowCount; i++){
            for(int j = 0; j < columnCount; j++){
                nodes[i][j] = new MyNode(new Ground(new Vector2d(i,j)), width/rowCount, height/columnCount);
                add(nodes[i][j], i, j,1,1);
            }
        }
    }

    public void updateNode(IMapElement element, FieldImage newImg) {
        nodes[element.getPosition().x][element.getPosition().y].update(element,newImg);
    }
}
