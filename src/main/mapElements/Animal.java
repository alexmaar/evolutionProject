package mapElements;
import map.*;
import structures.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
public class Animal implements IMapElement {

    private Vector2d position;
    private MoveDirection orientation;
    private WorldMap map;
    private  int startEnergy;
    public int actualEnergy;
    private Genes genes ;
    public Random generator = new Random();
    private List<IPositionChangeObserver> observers = new LinkedList<>();
    private int age=0;
    private int era;
    private int deathDay;


    public Animal(Vector2d position, int startEnergy, MoveDirection orientation , WorldMap map){
        this.position=position;
        this.startEnergy=startEnergy;
        this.orientation=orientation;
        this.actualEnergy=startEnergy;
        this.genes=new Genes();
        this.map=map;

    }

    public Animal (Animal anim1, Animal anim2, WorldMap map){
        this.map=map;
        this.position=this.childPosition(anim1);
        this.startEnergy=anim1.getEnergy() / 4 +  anim2.getEnergy() / 4;
        this.orientation=MoveDirection.randDirection();
        this.actualEnergy=this.startEnergy;
        this.genes = new Genes(anim1.genes,anim2.genes);


    }

    public Vector2d childPosition(Animal parent1){
        Vector2d childPosition = parent1.getPosition();
        MoveDirection childDirection;
        int n=0;
        do{
            childDirection=MoveDirection.randDirection();
            n+=1;
        }
        while ((!this.map.checkPosition(childPosition.add(Objects.requireNonNull(childDirection.toUnitVector()))) ||
                this.map.isOccupied(childPosition.add(Objects.requireNonNull(childDirection.toUnitVector())))) && n<8);

        childPosition=childPosition.add(Objects.requireNonNull(childDirection.toUnitVector()));
        return childPosition;
    }

    public int getEnergy(){
        return this.actualEnergy;
    }

    public Vector2d getPosition(){
        return this.position;
    }

    @Override
    public FieldImage getImage() { return FieldImage.ANIMAL; }

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

    public void addYear(){
        this.age+=1;
    }

    public int getAge(){
        return this.age;
    }


    public void move(){
        int idx = generator.nextInt(32);
        idx=this.genes.getGeneFromIndex(idx);
        this.orientation= this.orientation.newDirection(idx);
        Vector2d newPos = this.position.add(this.orientation.toUnitVector());
        Vector2d oldPosition=this.getPosition();

            if (newPos.getX() < 0) newPos = new Vector2d(this.map.width-1, newPos.getY());
            if (newPos.getX() >= this.map.width) newPos = new Vector2d(0, newPos.getY());
            if (newPos.getY() < 0) newPos = new Vector2d(newPos.getX(), this.map.height-1);
            if (newPos.getY() >= this.map.height) newPos = new Vector2d(newPos.getX(), 0);
            this.position=newPos;

        this.positionChanged(oldPosition,this.getPosition());
        this.changeEnergyLevel(this.map.moveEnergy);

    }

    @Override
    public String toString(){
             return "Animal "+this.position.toString()+" "+this.actualEnergy;
    }

    public void addObserver(IPositionChangeObserver observer){
        this.observers.add(observer);
    }

    public void removeObserver(IPositionChangeObserver observer){
        this.observers.remove(observer);
    }

    public void positionChanged(Vector2d oldPosition, Vector2d newPosition) {
        for (IPositionChangeObserver obs : observers) {
            obs.positionChanged(oldPosition, newPosition);
        }
    }

     public void setDeathDate(int day){
            this.deathDay=day;
        }



}
