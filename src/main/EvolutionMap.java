import java.util.*;


public class EvolutionMap implements IWorldMap, IPositionChangeObserver{
    private static int startEnergy;
    private static int plantEnergy;
    public static int moveEnergy;
    public final int height;
    public final int width;

    private final Vector2d jungleUpperRight;
    private final Vector2d jungleLowerLeft;

    private List<Animal> animals = new ArrayList<>();
    private Map<Vector2d, List<IMapElement>> elementsMap = new HashMap<>();

    private MapVisualizer mapVisualizer = new MapVisualizer(this);

    public Random generator = new Random();


    public EvolutionMap(int height,int width, int plantEnergy, int startEnergy, int moveEnergy, double jungleRatio){
        this.height=height;
        this.width=width;
        this.plantEnergy=plantEnergy;
        this.startEnergy=startEnergy;
        this.moveEnergy=moveEnergy;

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


     void addFirstAnimals(int amount){
        if (amount!=0){
            for (int i=0; i<amount; i++){
                Vector2d pos = new Vector2d(generator.nextInt(this.width - 1), generator.nextInt(this.height - 1));
                while (isOccupied(pos))
                    pos = new Vector2d(generator.nextInt(this.width - 1), generator.nextInt(this.height - 1));
                Animal animal = new Animal(pos, this.startEnergy, MoveDirection.randDirection(), this);

            this.animals.add(animal);
            this.elementsMap.get(pos).add(animal);
            animal.addObserver(this);

            }
        }
    }

    public void addGrass(){
        int n=1;
        int x = generator.nextInt(this.width);
        int y = generator.nextInt(this.height) ;
        while ((isOccupied(new Vector2d (x,y)) || (this.jungleLowerLeft.precedes(new Vector2d(x,y)) && (this.jungleUpperRight.follows(new Vector2d(x,y)))))  && n<this.width*this.height ){
            x = generator.nextInt(this.width);
            y = generator.nextInt(this.height) ;
            n+=1;

        }
        Vector2d grassPos=new Vector2d(x,y);
            this.elementsMap.get(grassPos).add(new Grass(new Vector2d(x,y)));
    }

//    public void addGrass(){
//        int n=0;
//        int x = generator.nextInt(this.width-1);
//        int y = generator.nextInt(this.height-1) ;
//        while (isOccupied(new Vector2d (x,y)) || (x >= this.jungleLowerLeft.getX()  && x <=this.jungleUpperRight.getX() && y <= this.jungleUpperRight.getY() && y>= this.jungleLowerLeft.getY() && n<this.width*this.height-((this.jungleUpperRight.getX())  )) ){
//            x = generator.nextInt(this.width-1);
//            y = generator.nextInt(this.height-1) ;
//            n+=1;
//
//        }
//        Vector2d grassPos=new Vector2d(x,y);
//            this.elementsMap.get(grassPos).add(new Grass(new Vector2d(x,y)));
//    }


    public void addJungleGrass() {
        int n =1;

            int x = generator.nextInt((this.jungleUpperRight.getX() - this.jungleLowerLeft.getX()) + 1) + this.jungleLowerLeft.getX();
            int y = generator.nextInt(this.jungleUpperRight.getY() - this.jungleLowerLeft.getY() + 1) + this.jungleLowerLeft.getY();
            while (isOccupied(new Vector2d(x, y)) && n <(this.jungleUpperRight.getX()- this.jungleLowerLeft.getX() ) * (this.jungleUpperRight.getY()-this.jungleLowerLeft.getY() )) {
                x = generator.nextInt(this.jungleUpperRight.getX() - this.jungleLowerLeft.getX() + 1) + this.jungleLowerLeft.getX();
                y = generator.nextInt(this.jungleUpperRight.getY() - this.jungleLowerLeft.getY() + 1) + this.jungleLowerLeft.getY();
                n+=1;

            }
            Vector2d grassPos = new Vector2d(x, y);
            this.elementsMap.get(grassPos).add(new Grass(new Vector2d(x, y)));
        }


    public void eatGrass(){
        for (Animal anim : animals){
            List <IMapElement> tmp = this.elementsMap.get(anim.getPosition());
            Grass toRemove=null;
            if (tmp.size() !=0) {
                if (tmp.get(0) instanceof Grass) toRemove =  (Grass) tmp.get(0);

                if (toRemove !=null) {
                    this.elementsMap.get(toRemove.getPosition()).remove(toRemove);
                    anim.increaseEnergy(this.plantEnergy);
                }
            }
        }
    }

//        public void eatGrass(){
//        for (Animal anim : animals){
//            List <IMapElement> tmp = this.elementsMap.get(anim.getPosition());
//            Grass toRemove=null;
//            if (tmp.size() >2) {
//                if (tmp.get(0) instanceof Grass) toRemove =  (Grass) tmp.get(0);
//
//                if (toRemove !=null) {
//                    this.elementsMap.get(toRemove.getPosition()).remove(toRemove);
//                    Animal maxAnim  = this.elementsMap.get(toRemove.getPosition()).stream().max(Comparator.comparing(i->i.getEnergy());
//                    maxAnim.increaseEnergy(this.plantEnergy);
//                }
//            }
//            else if (tmp.size()==2){
//                if (tmp.get(0) instanceof Grass) toRemove =  (Grass) tmp.get(0);
//
//                if (toRemove !=null) {
//                    this.elementsMap.get(toRemove.getPosition()).remove(toRemove);
//                    anim.increaseEnergy(this.plantEnergy);
//                }
//
//            }
//        }
//    }

    public void reproduce(){
        for (int i=0; i<width; i++){
            for (int j=0; j<height; j++){
                List<IMapElement> list = elementsMap.get(new Vector2d (i,j));
                if ( list !=null && list.size() >1  ){
                    if(list.get(0) instanceof Animal && list.get(1) instanceof Animal) {
                        Animal anim1 = (Animal) list.get(0);
                        Animal anim2 = (Animal) list.get(1);
                        if (anim1.canReproduce() && anim2.canReproduce()) {
                            Vector2d childPosition = anim1.getPosition();

                            MoveDirection childDirection = MoveDirection.randDirection();
                            int childEnergy =  anim1.getEnergy() / 4 +  anim2.getEnergy() / 4;
                            Animal child = new Animal(childPosition, childEnergy, childDirection, this);
                            child.move();
                            child.genes.inheritGenes(anim1.genes, anim2.genes);
                            anim1.actualEnergy *= 0.75;
                            anim2.actualEnergy *= 0.75;
                            this.animals.add(child);
                            this.elementsMap.get(childPosition).add(child);
                        }
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
                if (anim.isDead())
                    toRemove.add(anim);
            }

            for (Animal animal : toRemove){

                this.elementsMap.get(animal.getPosition()).remove(animal);
                this.animals.remove(animal);
            }

        }

    public void everyDay(){
        this.cleanMap();

        System.out.println("liczba zwierzat 1 : " + this.animals.size());

        for (Animal anim : this.animals){
            anim.move();

        }
        this.eatGrass();
        this.reproduce();
        this.addGrass();
        this.addJungleGrass();
        System.out.println("liczba zwierzat 2 : " + this.animals.size());

    }

    @Override
    public void run(MoveDirection[] direction){ }

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
    }

    @Override
    public String toString(){
        return mapVisualizer.draw(new Vector2d(0,0), new Vector2d(this.width-1, this.height-1));
    }

}
