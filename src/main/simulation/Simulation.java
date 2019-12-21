package simulation;

import map.WorldMap;
import parameters.WorldParameters;
import visualizer.FXVisualizer;

public class Simulation implements Runnable {
    private WorldMap map;
    private final int daysAmount;
    private final int refreshing;

    public Simulation(int daysAmount, int refreshing, FXVisualizer visualizer){
        this.refreshing=refreshing;
        this.daysAmount=daysAmount;
        map = new WorldMap( WorldParameters.getInstance().getWidth(),WorldParameters.getInstance().getHeight(),WorldParameters.getInstance().getMoveEnergy(),
                WorldParameters.getInstance().getPlantEnergy(),WorldParameters.getInstance().getJungleRatio(), WorldParameters.getInstance().getStartEnergy(),
                WorldParameters.getInstance().getAnimalsAmount());
        map.addObserver(visualizer);

        map.addFirstAnimals();
    }
    @Override
    public void run() {
        for(int i = 0; i < daysAmount; i++){
                map.nextDay();

            try {
                Thread.sleep(refreshing,0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public WorldMap getMap(){
        return this.map;

    }
}
