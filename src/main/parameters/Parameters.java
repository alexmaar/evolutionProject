package parameters;

public class Parameters {
    public final int width;
    public final int height;
    public final int moveEnergy;
    public final int plantEnergy;
    public final double jungleRatio;
    public final int startEnergy;
    public final int animalsAmount;



    public Parameters(int width, int height, int moveEnergy, int plantEnergy, double jungleRatio, int startEnergy, int animalAmount){
        this.width=width;
        this.height=height;
        this.moveEnergy=moveEnergy;
        this.plantEnergy=plantEnergy;
        this.jungleRatio=jungleRatio;
        this.startEnergy=startEnergy;
        this.animalsAmount=animalAmount;

    }

}
