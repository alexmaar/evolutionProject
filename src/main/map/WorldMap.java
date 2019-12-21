package map;

import mapElements.Animal;
import mapElements.Grass;
import mapElements.Ground;
import mapElements.IMapElement;
import structures.MoveDirection;
import structures.Vector2d;
import visualizer.IMapObserver;
import visualizer.MapVisualizer;

import java.util.*;


public class WorldMap implements IWorldMap, IPositionChangeObserver, ObservableMap{
    private final int startEnergy;
    private final int plantEnergy;
    public final int moveEnergy;
    private final int animalsAmount;
    public final int height;
    public final int width;
    private int day;

    private final Vector2d jungleUpperRight;
    private final Vector2d jungleLowerLeft;

    private List<Animal> animals = new ArrayList<>();
    private Map<Vector2d, List<IMapElement>> elementsMap = new HashMap<>();
    private HashSet<Vector2d> freeFields= new HashSet<>();
    private MapVisualizer mapVisualizer = new MapVisualizer(this);
    private List<Animal> deadAnimals = new  ArrayList<>();
    private List<IMapObserver> observers = new LinkedList<>();
    public Random generator = new Random();


    public WorldMap( int width,int height, int moveEnergy,int plantEnergy, double jungleRatio, int startEnergy,
                     int animalsAmount){
        this.height=height;
        this.width=width;
        this.plantEnergy=plantEnergy;
        this.startEnergy=startEnergy;
        this.moveEnergy=moveEnergy;
        this.animalsAmount=animalsAmount;
        this.day=0;

        int jungleWidth=  (int) ((double)width * jungleRatio);
        int jungleHeight = (int)  ((double) height * jungleRatio);


        this.jungleLowerLeft= new Vector2d( (this.width/2 -  jungleWidth/2), (this.height/2 - jungleHeight/2));
        this.jungleUpperRight= new Vector2d ((this.width/2 + jungleWidth/2), (this.height/2 + jungleHeight/2));


        for (int i =0; i<width; i++){
            for(int j=0; j<height; j++){
                elementsMap.put(new Vector2d(i,j) , new ArrayList<>());
                freeFields.add(new Vector2d(i,j));

            }
        }
    }

    private Vector2d randomFreePosition(){
        if (this.freeFields.isEmpty()) return null;
        int  idx= generator.nextInt(freeFields.size());
        int i=0;
        for(Vector2d vec : freeFields){
            if (i==idx) return vec;
            i+=1;
        }
        return null;
    }

    public void addElement(Grass grass){
        this.elementsMap.get(grass.getPosition()).add(grass);
        this.freeFields.remove(grass.getPosition());
        updateField(grass.getPosition());

    }
    public void addElement(Animal animal){
        this.elementsMap.get(animal.getPosition()).add(animal);
        this.freeFields.remove(animal.getPosition());
        this.animals.add(animal);
        animal.addObserver(this);
        updateField(animal.getPosition());
    }

     public void addFirstAnimals(){
        if (animalsAmount!=0){
            for (int i=0; i<animalsAmount; i++){
                Vector2d pos=randomFreePosition();
                if (pos !=null) {
                    Animal animal = new Animal(pos, this.startEnergy, MoveDirection.randDirection(), this);
                    addElement(animal);
                }
            }
        }
    }

    public void addGrass() {
        Vector2d pos;
        do pos = randomFreePosition();
        while ((this.jungleUpperRight.follows(pos) && this.jungleLowerLeft.precedes(pos)));
        if (pos != null)
            addElement(new Grass(pos));
        }


    public void addJungleGrass() {
        Vector2d pos;
        do pos = randomFreePosition();
        while (!(this.jungleUpperRight.follows(pos) && this.jungleLowerLeft.precedes(pos)));
            if (pos != null)
                addElement(new Grass(pos));
        }


    public void eatGrass(){
        for (Animal anim : animals){
            List <IMapElement> field = this.elementsMap.get(anim.getPosition());
            Grass toRemove=null;
            if (field.size() !=0) {
                if (field.get(0) instanceof Grass) toRemove =  (Grass) field.get(0);

                if (toRemove !=null) {
                    this.elementsMap.get(toRemove.getPosition()).remove(toRemove);
                    updateField(toRemove.getPosition());
                    if (field.size() > 2) {
                        List<Animal> strongestAnimals=getTheStrongestAnimals(field);
                        if (strongestAnimals.size() > 1 &&
                                strongestAnimals.get(0).getEnergy() == strongestAnimals.get(1).getEnergy())
                            for (Animal eater : strongestAnimals)
                                eater.increaseEnergy(this.plantEnergy / strongestAnimals.size());
                    }
                    else
                        ((Animal) field.get(0)).increaseEnergy(this.plantEnergy);
                }
            }
        }
    }

    public List<Animal> getTheStrongestAnimals(List<IMapElement> parents){
        List<Animal> strongestAnimals=new ArrayList<>();
        Animal anim1=(Animal) parents.get(0);
        Animal anim2=(Animal) parents.get(0);
        for (IMapElement anim : parents)
            if (anim instanceof Animal)
                if ( ((Animal)anim).getEnergy() > anim1.getEnergy()) {
                    anim2 = anim1;
                    anim1 = (Animal)anim;
                }

        for (IMapElement anim : parents)
            if (((Animal)anim).getEnergy()==anim2.getEnergy())
                strongestAnimals.add((Animal)(anim));

        strongestAnimals.add(anim2);
        strongestAnimals.add(anim1);
        return strongestAnimals;
    }

    public void reproduce() {
        List<Animal> strongestAnimals;
        Animal anim1,anim2;
        for (int x=0; x<this.width; x++){
            for(int y=0; y<this.height; y++) {
                List<IMapElement> list = elementsMap.get(new Vector2d(x,y));
                if (list != null && list.size() > 1) {
                    strongestAnimals = getTheStrongestAnimals(list);
                    anim1 = strongestAnimals.get(0);
                    int idx = generator.nextInt(strongestAnimals.size() - 1) + 1;
                    anim2 = strongestAnimals.get(idx);

                    if (anim1.canReproduce() && anim2.canReproduce()) {
                        Animal child = new Animal(anim1,anim2,this);
                        addElement(child);

                        anim1.actualEnergy *= 0.75;
                        anim2.actualEnergy *= 0.75;
                    }

                }
            }
        }
    }

    public boolean checkPosition(Vector2d position){
        return position.getX() >= 0 && position.getX()<= width-1 && position.getY() >=0 && position.getY() <= height-1;
    }

    public void cleanMap(){
            List<Animal> toRemove = new ArrayList<>();
            for (Animal anim : animals){
                if (anim.isDead()) {
                    toRemove.add(anim);
                    anim.setDeathDate(this.day);

                }
            }
            for (Animal animal : toRemove){
                this.elementsMap.get(animal.getPosition()).remove(animal);
                this.animals.remove(animal);
                deadAnimals.add(animal);

                updateField(animal.getPosition());
            }

        }

    public void nextDay(){
        this.day+=1;
        this.cleanMap();
        for (Animal anim : this.animals){
            anim.move();
            anim.addYear();
            updateField(anim.getPosition());
        }
        this.eatGrass();
        this.reproduce();
        this.addGrass();
        this.addJungleGrass();
    }

    @Override
    public boolean isOccupied(Vector2d position) {
            return !(this.elementsMap.get(position).isEmpty());
    }

    @Override
    public IMapElement objectAt(Vector2d position){
        return this.elementsMap.get(position).get(0);
    }


    public void positionChanged(Vector2d oldPosition, Vector2d newPosition){
        List<IMapElement> list = this.elementsMap.get(oldPosition);
        Animal toRemove = null;
        for (IMapElement  anim : list){
            if (anim.getPosition().equals(newPosition)){
                toRemove=(Animal) anim;
            }
        }
        if (toRemove !=null){
            this.elementsMap.get(oldPosition).remove(toRemove);
            this.elementsMap.get(newPosition).add(toRemove);
        }
        updateField(oldPosition);
        updateField(newPosition);
    }

    public int whichDay(){
        return this.day;
    }

    public double getMeanEnergy(){
        int energy=0;
        for (Animal anim : animals)  energy+=anim.getEnergy();
        if (animals.size()==0) return 0;
        else return (double)energy/(double)animals.size();
    }

    public double getMeanLifeTime(){
        int lifetime=0;
        for (Animal anim : deadAnimals)lifetime+=anim.getAge();
        if (deadAnimals.size()==0) return 0;
        else return (double) lifetime / (double) deadAnimals.size();
    }

    public int getAnimalsAmount(){
        return this.animals.size();
    }

    public int getGrassesAmount(){
        int counter=0;
        for(int i=0; i<this.width; i++)
            for (int j=0;j<this.height; j++)
                for (IMapElement elem : this.elementsMap.get(new Vector2d(i,j)))
                    if (elem instanceof Grass) counter+=1;

        return counter;
    }

    @Override
    public String toString(){
        return mapVisualizer.draw(new Vector2d(0,0), new Vector2d(this.width-1, this.height-1));
    }

    private void updateField(Vector2d position){
        if(isOccupied(position)){
            notifyOnFieldChanged(objectAt(position));
        }else{
            notifyOnFieldChanged(new Ground(position));
        }
    }

    @Override
    public void notifyOnFieldChanged(IMapElement element){
        for(IMapObserver observer : observers){
            observer.onFiledChanged(element, element.getImage());
        }
    }

    @Override
    public void addObserver(IMapObserver observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(IMapObserver observer) {
        observers.remove(observer);
    }


}
