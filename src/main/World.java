public class World {

    public static void main(String [] arg){
        EvolutionMap map = new EvolutionMap(15,15,1,30, 3, 0.35);


        map.addFirstAnimals(50);

        System.out.println(map);

        for (int i=0; i< 110; i++){
            map.everyDay();
            System.out.println(map);
        }
    }
}
