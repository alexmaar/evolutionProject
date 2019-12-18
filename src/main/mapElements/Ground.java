package mapElements;

import structures.FieldImage;
import structures.Vector2d;

public class Ground implements IMapElement{

    private Vector2d position;

    public Ground(Vector2d position){
        this.position = position;
    }

    @Override
    public Vector2d getPosition() {
        return this.position;
    }

    @Override
    public FieldImage getImage() {
        return FieldImage.GROUND;
    }

    @Override
    public String toString() {
        return "Ground "+this.position.toString();
    }
}
