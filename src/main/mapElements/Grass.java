package mapElements;
import structures.FieldImage;
import structures.Vector2d;

public class Grass implements IMapElement {
    Vector2d position;

    public Grass(Vector2d position){

        this.position=position;
    }

    public Vector2d getPosition(){
        return this.position;
    }

    @Override
    public FieldImage getImage() {
        return FieldImage.GRASS;
    }


    @Override
    public String toString(){
        return  "Plant "+position.toString() ;
    }


}
