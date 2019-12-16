import java.util.LinkedList;
import java.util.List;
import java.util.Random;
public class Animal implements IMapElement  {

    Vector2d position;
    private MoveDirection orientation;
    public EvolutionMap map;
    public  int startEnergy;
    public int actualEnergy;
    Genes genes = new Genes();
    public Random generator = new Random();

    private List<IPositionChangeObserver> observers = new LinkedList<>();

    public Animal(Vector2d position, int startEnergy, MoveDirection orientation , EvolutionMap map){
        this.position=position;
        this.startEnergy=startEnergy;
        this.orientation=orientation;
        this.actualEnergy=startEnergy;
        this.map=map;
        genes.firstGenes();
    }

    public int getEnergy(){
        return this.actualEnergy;
    }

    public Vector2d getPosition(){
        return this.position;
    }

    public boolean isDead(){
         return this.getEnergy() <= 0;
    }


    public MoveDirection getOrientation(){
        return this.orientation;
    }

    public void changeEnergyLevel( int unit){
        this.actualEnergy=this.actualEnergy-unit;
    }

    public void increaseEnergy(int grassEnergy){
        this.actualEnergy=this.actualEnergy+grassEnergy;
    }


    public boolean canReproduce(){
        return this.actualEnergy  >= this.startEnergy/2;
    }

    public void move(){
        int idx = generator.nextInt(32);
        idx=this.genes.genes[idx];
        MoveDirection tmp = this.orientation.newDirection(idx);
        this.orientation=tmp;
        Vector2d newPos = this.position.add(this.orientation.toUnitVector());

        Vector2d oldPosition=this.getPosition();

        if (this.map.checkPosition(newPos))
            this.position=newPos;
        else {
            this.position=new Vector2d ((newPos.getX()+1) % this.map.width, (newPos.getY()+1 ) % this.map.height);
        }
        this.positionChanged(oldPosition,this.getPosition());
        this.changeEnergyLevel(this.map.moveEnergy);


    }

//
//    @Override
//    public String toString(){
//       // return this.orientation.toString();
//        if (this.isDead()) return "X";
//        else return "@";
//    }

    @Override
    public String toString() {
      //  if (this.isDead()) return "X";
      //  else {
            switch (this.orientation) {
                case NORTH:
                    return " N  ";
                case NORTHEAST:
                    return " NE ";
                case EAST:
                    return " E  ";
                case SOUTHEAST:
                    return " SE ";
                case SOUTH:
                    return " S  ";
                case SOUTHWEST:
                    return " SW ";
                case WEST:
                    return " W  ";
                case NORTHWEST:
                    return " NW ";
                default:
                    return " -  ";
            }
        }
   // }

    public void addObserver(IPositionChangeObserver observer){
        this.observers.add(observer);
    }

    public void removeObserver(IPositionChangeObserver observer){
        this.observers.remove(observer);
    }

    public void positionChanged(Vector2d oldPosition, Vector2d newPosition){
        for (IPositionChangeObserver obs : observers){
            obs.positionChanged(oldPosition, newPosition);
        }

    }



}
