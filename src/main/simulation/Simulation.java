package simulation;

import map.WorldMap;
import parameters.WorldParameters;
import visualizer.FXVisualizer;

public class Simulation implements Runnable {
    private WorldMap map;
    private final int daysAmount;
    private final int refreshing;

    public Simulation(int daysAmount, int animalAmount, int refreshing, FXVisualizer visualizer){
        this.refreshing=refreshing;
        this.daysAmount=daysAmount;
        int x,y;
        map = new WorldMap( WorldParameters.getInstance().getHeight(),WorldParameters.getInstance().getWidth(),WorldParameters.getInstance().getPlantEnergy(),
                WorldParameters.getInstance().getStartEnergy(),WorldParameters.getInstance().getMoveEnergy(), WorldParameters.getInstance().getJungleRatio());
        map.addObserver(visualizer);

        map.addFirstAnimals(animalAmount);


    }

    @Override
    public void run() {
        for(int i = 0; i < daysAmount; i++){
            map.everyDay();
            //System.out.println(map);
            try {
                Thread.sleep(refreshing,0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
