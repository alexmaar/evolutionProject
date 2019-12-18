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
    private static int startEnergy;
    private static int plantEnergy;
    public static int moveEnergy;
    public final int height;
    public final int width;
    public int ordinalDay;

    private final Vector2d jungleUpperRight;
    private final Vector2d jungleLowerLeft;

    private List<Animal> animals = new ArrayList<>();
    private Map<Vector2d, List<IMapElement>> elementsMap = new HashMap<>();
    private HashSet<Vector2d> occupiedFields =  new HashSet<>();

    private MapVisualizer mapVisualizer = new MapVisualizer(this);

    private List<Animal> deadAnimals = new  ArrayList<>();

    private List<IMapObserver> observers = new LinkedList<>();

    public Random generator = new Random();


    public WorldMap(int height, int width, int plantEnergy, int startEnergy, int moveEnergy, double jungleRatio){
        this.height=height;
        this.width=width;
        this.plantEnergy=plantEnergy;
        this.startEnergy=startEnergy;
        this.moveEnergy=moveEnergy;
        this.ordinalDay=0;

        int jungleWidth=  (int) ((double)width * jungleRatio);
        int jungleHeight = (int)  ((double) height * jungleRatio);


        this.jungleLowerLeft= new Vector2d( (this.width/2 -  jungleWidth/2), (this.height/2 - jungleHeight/2));
        this.jungleUpperRight= new Vector2d ((this.width/2 + jungleWidth/2), (this.height/2 + jungleHeight/2));


        for (int i =0; i<width; i++){
            for(int j=0; j<height; j++){
                elementsMap.put(new Vector2d(i,j) , new ArrayList<>());
            }
        }
    }


     public void addFirstAnimals(int amount){
        if (amount!=0){
            for (int i=0; i<amount; i++){
                Vector2d pos = new Vector2d(generator.nextInt(this.width), generator.nextInt(this.height));
                while (isOccupied(pos))
                    pos = new Vector2d(generator.nextInt(this.width ), generator.nextInt(this.height ));
                Animal animal = new Animal(pos, this.startEnergy, MoveDirection.randDirection(), this);

            this.animals.add(animal);
            this.elementsMap.get(pos).add(animal);
            this.occupiedFields.add(pos);
            animal.addObserver(this);
            updateField(pos);

            }
        }
    }

    public List<List<IMapElement>> getOccupiedFields(){
        List<List<IMapElement>> fields = new LinkedList<>();
        for (Vector2d v : occupiedFields){
            fields.add(new LinkedList<>(this.elementsMap.get(v)));
        }
        return fields;
    }

    public void addGrass(){
        int n=1;
        int amount=this.width*this.height;
        int x = generator.nextInt(this.width);
        int y = generator.nextInt(this.height) ;
        while ((isOccupied(new Vector2d (x,y)) || (this.jungleLowerLeft.precedes(new Vector2d(x,y)) && (this.jungleUpperRight.follows(new Vector2d(x,y)))))  && n<amount ){
            x = generator.nextInt(this.width);
            y = generator.nextInt(this.height) ;
            n+=1;
        }
        Vector2d grassPos=new Vector2d(x,y);
        if (n<amount-2) {
            this.elementsMap.get(grassPos).add(new Grass(new Vector2d(x, y)));
            this.occupiedFields.add(new Vector2d (x,y));
            updateField(grassPos);
        }
    }


    public void addJungleGrass() {
        int n =1;
        int amount=(this.jungleUpperRight.getX()- this.jungleLowerLeft.getX() ) * (this.jungleUpperRight.getY()-this.jungleLowerLeft.getY());
        int x = generator.nextInt((this.jungleUpperRight.getX() - this.jungleLowerLeft.getX()) + 1) + this.jungleLowerLeft.getX();
        int y = generator.nextInt(this.jungleUpperRight.getY() - this.jungleLowerLeft.getY() + 1) + this.jungleLowerLeft.getY();
        while (isOccupied(new Vector2d(x, y)) && n <amount) {
            x = generator.nextInt(this.jungleUpperRight.getX() - this.jungleLowerLeft.getX() + 1) + this.jungleLowerLeft.getX();
            y = generator.nextInt(this.jungleUpperRight.getY() - this.jungleLowerLeft.getY() + 1) + this.jungleLowerLeft.getY();
            n+=1;
        }
        Vector2d grassPos = new Vector2d(x, y);
        if (n<amount-2) {
            this.elementsMap.get(grassPos).add(new Grass(new Vector2d(x, y)));
            this.occupiedFields.add(new Vector2d(x,y));
            updateField(grassPos);
        }
        }


    public void eatGrass(){

        for (Animal anim : animals){
            List <IMapElement> tmp = this.elementsMap.get(anim.getPosition());

            Grass toRemove=null;
            if (tmp.size() !=0) {
                if (tmp.get(0) instanceof Grass) toRemove =  (Grass) tmp.get(0);
                if (toRemove !=null) {

                    this.elementsMap.get(toRemove.getPosition()).remove(toRemove);
                    updateField(toRemove.getPosition());
                    if (tmp.size() > 2) {
                        List<Animal> strongestAnimals=getTheStrongestAnimals(tmp);
                        if (strongestAnimals.size() > 1 && strongestAnimals.get(0).getEnergy() == strongestAnimals.get(1).getEnergy())
                            for (Animal eater : strongestAnimals)
                                eater.increaseEnergy(this.plantEnergy / strongestAnimals.size());
                    }
                    else {
                        ((Animal) tmp.get(0)).increaseEnergy(this.plantEnergy);
                    }
                }
            }
        }
    }

    public List<Animal> getTheStrongestAnimals(List<IMapElement> parents){
        List<Animal> strongestAnimals=new ArrayList<>();
        Animal anim1=(Animal) parents.get(0);
        Animal anim2=(Animal) parents.get(0);

        for (IMapElement anim : parents){
            if (anim instanceof Animal) {
                if ( ((Animal)anim).getEnergy() > anim1.getEnergy()) {
                    anim2 = anim1;
                    anim1 = (Animal)anim;
                }
            }
        }

        for (IMapElement anim : parents){
            if (((Animal)anim).getEnergy()==anim2.getEnergy())
                strongestAnimals.add((Animal)(anim));
        }
        strongestAnimals.add(anim2);
        strongestAnimals.add(anim1);
        return strongestAnimals;
    }

    public void reproduce() {
        List<Animal> strongestAnimals;
        Animal anim1;
        Animal anim2;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                List<IMapElement> list = elementsMap.get(new Vector2d(i, j));
                if (list != null && list.size() > 1) {
                    strongestAnimals = getTheStrongestAnimals(list);
                    anim1 = strongestAnimals.get(0);
                    int idx = generator.nextInt(strongestAnimals.size() - 1) + 1;
                    anim2 = strongestAnimals.get(idx);

                    if (anim1.canReproduce() && anim2.canReproduce()) {
                        getNewChild(anim1, anim2);
                    }
                }
            }

        }
    }

    public void getNewChild(Animal anim1, Animal anim2){
        Vector2d childPosition = anim1.getPosition();
        MoveDirection childDirection;
        int n=0;
        do{
            childDirection=MoveDirection.randDirection();
            n+=1;
        }
        while ((!checkPosition(childPosition.add(childDirection.toUnitVector())) || isOccupied(childPosition.add(childDirection.toUnitVector()))) && n<8);

        childPosition=childPosition.add(childDirection.toUnitVector());

        int childEnergy =  anim1.getEnergy() / 4 +  anim2.getEnergy() / 4;
        Animal child = new Animal(childPosition, childEnergy, childDirection, this);
        child.genes.inheritGenes(anim1.genes, anim2.genes);
        child.genes.checkGenes();
        anim1.actualEnergy *= 0.75;
        anim2.actualEnergy *= 0.75;
        anim1.addChildren(child);
        anim2.addChildren(child);
        this.animals.add(child);
        child.addObserver(this);
        this.elementsMap.get(childPosition).add(child);
        updateField(childPosition);
    }


    public boolean checkPosition(Vector2d position){
        return position.getX() >= 0 && position.getX()<= width-1 && position.getY() >=0 && position.getY() <= height-1;
    }

    public void cleanMap(){
            List<Animal> toRemove = new ArrayList<>();
            for (Animal anim : animals){
                if (anim.isDead()) {
                    toRemove.add(anim);
                    anim.setDeathDate(this.ordinalDay);
                }
            }
            for (Animal animal : toRemove){
                this.elementsMap.get(animal.getPosition()).remove(animal);
                this.animals.remove(animal);
                updateField(animal.getPosition());
            }

        }

    public void everyDay(){
        this.ordinalDay+=1;
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

    public double getMeanEnergy(){
        int energy=0;
        for (Animal anim : animals){
            energy+=anim.getEnergy();
        }
        if (animals.size()==0) return 0;
        else return (double)energy/(double)animals.size();
    }

    public double getMeanChildren(){
        int children=0;
        for (Animal anim : animals){
            children+=anim.getChildren().size();
        }
        if(animals.size()==0) return 0;
        else return (double) children / (double)animals.size();
    }

    public double getMeanLifeTime(){
        int lifetime=0;
        for (Animal anim : deadAnimals){
            lifetime+=anim.getAge();
        }
        if (deadAnimals.size()==0) return 0;
        else return (double) lifetime / (double) deadAnimals.size();
    }
//
//    public int getGrasessAmount(){
//        int counter=0;
//        for (IMapElement elem : elementsMap) if (elem instanceof Grass) counter += 1;
//        return counter;
//    }

    public int getAnimalsAmount(){
        return this.animals.size();
    }

    public int getDominantGene(){
        Integer [] counter= new Integer[8];
        Arrays.fill(counter, 0);
        for (Animal anim : animals){
            for(int i=0; i<32; i++){
                counter[anim.genes.genes[i]]+=1;
            }
        }
        int max=0;
        for (int i=1;i<8; i++){
            if (counter[i]>counter[max]) max=i;
        }
        return max;

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
