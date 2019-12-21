package structures;

import java.util.Arrays;
import java.util.Random;


public class Genes  {
    private int [] genes ;
    final private int amount = 32;
    final private int range =8;
    public Random generator = new Random();

    public Genes(){
        genes=new int[32];
        for (int i=0; i<this.range; i++){
            this.genes[i]=i;
        }
        for(int i=this.range ; i< this.amount; i++){
            this.genes[i]=generator.nextInt(8);
        }
        Arrays.sort(this.genes);

    }

    public Genes(Genes g1,Genes g2){
        genes = new int[32];

        int a = generator.nextInt(30)+1;
        int b = generator.nextInt(31-a)+a+1;

        System.arraycopy(g1.genes, 0, genes, 0, a-1 );
        System.arraycopy(g1.genes,a-1, genes, a-1, b-a );
        System.arraycopy(g2.genes, b-a, genes, b-a, 32 - b+a);

        Integer [] counter= new Integer[8];
        Arrays.fill(counter, 0);
        for (int i=0; i<this.amount; i++){
            counter[genes[i]]+=1;
        }

        for (int i=0; i<8; i++){
            if(counter[i]==0) {
                int idx = generator.nextInt(32);
                while (counter[genes[idx]]==0) idx = generator.nextInt(32);
                genes[idx]=i;
            }
        }

        Arrays.sort(genes);
    }

    public int[] getGenes(){ return this.genes; }

    public int getGeneFromIndex(int idx){
        return this.genes[idx];
    }

    @Override
    public String toString(){
        String genes = "";
        for (int i=0; i< amount; i++)
            genes = genes + " " + this.genes[i];

        return genes;
    }





}
