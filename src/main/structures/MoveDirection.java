package structures;

import java.util.Random;
public enum MoveDirection {
    NORTH,
    NORTHEAST,
    EAST,
    SOUTHEAST,
    SOUTH,
    SOUTHWEST,
    WEST,
    NORTHWEST;


    public String toString(){
        switch(this){
            case NORTH :
                return "N";
            case SOUTH :
                return "S";
            case WEST :
                return "W";
            case EAST :
                return "E";
            case NORTHEAST:
                return "NE";
            case NORTHWEST:
                return "NW";
            case SOUTHEAST:
                return "SE";
            case SOUTHWEST:
                return "SW";

        }
        return null;

    }

    public Vector2d toUnitVector (){
        switch(this){
            case NORTH :
                return new Vector2d(0,1);
            case SOUTH :
                return new Vector2d(0,-1);
            case WEST :
                return new Vector2d(-1,0);
            case EAST :
                return new Vector2d(1,0);
            case NORTHEAST:
                return new Vector2d(1,1);
            case NORTHWEST:
                return new Vector2d(-1,1);
            case SOUTHEAST:
                return new Vector2d(1,-1);
            case SOUTHWEST:
                return new Vector2d(-1,-1);

        }
        return null;
    }

    public static MoveDirection randDirection(){
        Random generator = new Random();
        int idx=generator.nextInt(8);
        return MoveDirection.values()[idx % MoveDirection.values().length ];
    }

    public MoveDirection newDirection(int idx){
        return MoveDirection.values()[(this.ordinal() + idx ) % MoveDirection.values().length];
    }

}
