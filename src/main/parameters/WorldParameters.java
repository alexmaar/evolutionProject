package parameters;


import parser.JsonParser;

public class WorldParameters {
    private static WorldParameters worldParameters=null;

    Parameters parameters;

    private WorldParameters(){
        parameters = JsonParser.parse();
    }

    public int getHeight(){
        return parameters.height;
    }

    public int getWidth(){
        return parameters.width;
    }

    public double getJungleRatio(){
        return parameters.jungleRatio;
    }

    public int getPlantEnergy(){
        return parameters.plantEnergy;
    }

    public int getMoveEnergy(){
        return parameters.moveEnergy;
    }

    public int getStartEnergy(){
        return parameters.startEnergy;
    }

    public static WorldParameters getInstance(){
        if(worldParameters == null)
            worldParameters=new WorldParameters();
        return worldParameters;
    }
}
