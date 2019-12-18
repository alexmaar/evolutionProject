package mapElements;
import map.*;
import structures.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
public class Animal implements IMapElement {

    Vector2d position;
    private MoveDirection orientation;
    public WorldMap map;
    public  int startEnergy;
    public int actualEnergy;
    public Genes genes = new Genes();

    public Random generator = new Random();

    private List<IPositionChangeObserver> observers = new LinkedList<>();
    private List<Animal> children = new LinkedList<>();
    private int age;
    private int era;
    private int deathDay;


    public Animal(Vector2d position, int startEnergy, MoveDirection orientation , WorldMap map){
        this.position=position;
        this.startEnergy=startEnergy;
        this.orientation=orientation;
        this.actualEnergy=startEnergy;
        this.map=map;
        this.age=0;
        genes.firstGenes();
    }

    public int getEnergy(){
        return this.actualEnergy;
    }

    public Vector2d getPosition(){
        return this.position;
    }

    @Override
    public FieldImage getImage() {
        return FieldImage.ANIMAL;
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

    public void addYear(){
        this.age+=1;
    }

    public int getAge(){
        return this.age;
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
            if (newPos.getX() < 0) newPos = new Vector2d(this.map.width-1, newPos.getY());
            if (newPos.getX() >= this.map.width) newPos = new Vector2d(0, newPos.getY());
            if (newPos.getY() < 0) newPos = new Vector2d(newPos.getX(), this.map.height-1);
            if (newPos.getY() >= this.map.height) newPos = new Vector2d(newPos.getX(), 0);
            this.position=newPos;
        }

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

    public void addChildren(Animal child){
        this.children.add(child);
    }

    public List<Animal> getChildren(){
        return this.children;
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
