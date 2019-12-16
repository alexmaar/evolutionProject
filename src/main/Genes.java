import java.util.Arrays;
import java.util.Random;


public class Genes  {
    public int [] genes ;
    final private int amount = 32;
    final private int range =8;
    public Random generator = new Random();

    public Genes(){
        genes=new int[this.amount];
    }

//    public int[] getGenes(){
//        return this.genes;
//    }

//    public int getGeneFromIndex(int idx){
//        return this.genes[idx];
//    }

    public void firstGenes(){
        for (int i=0; i<this.range; i++){
            this.genes[i]=i;
        }
        for(int i=this.range ; i< this.amount; i++){
            this.genes[i]=generator.nextInt(8);
        }

        Arrays.sort(this.genes);

    }

    public void checkGenes(){
        Integer [] counter= new Integer[8];
        Arrays.fill(counter, 0);
        for (int i=0; i<this.amount; i++){
            counter[this.genes[i]]+=1;
        }

        for (int i=0; i<8; i++){
            if(counter[i]==0) {
                System.out.println("brak " + i);
                int idx = generator.nextInt(8);
                while (counter[idx] <=1) idx = generator.nextInt(8);

                for (int j=0; j< 32 ; j++){
                     if (this.genes[j]==idx) {
                         this.genes[j]=i;
                         break;
                     }
                }
            }
        }
        Arrays.sort(this.genes);

    }

    public  int [] inheritGenes(Genes par1, Genes par2) {
        int[] childGenes = new int[32];

        int a = generator.nextInt(32);
        int b = generator.nextInt(32);
        while ((a==b) || a ==0 || b==0 || a==32 || b==32) b = generator.nextInt(32);

        if (a >b) {
            a = a+b;
            b= a-b;
            a = a-b;
        }

        System.arraycopy(par1.genes, 0, childGenes, 0, a-1 );
        System.arraycopy(par1.genes,a-1, childGenes, a-1, b-a );
        System.arraycopy(par2.genes, b-a, childGenes, b-a, 32 - b+a);

        Arrays.sort(childGenes);
        return childGenes;

    }

    @Override
    public String toString(){
        String genes = "";
        for (int i=0; i< amount; i++)
            genes = genes + " " + this.genes[i];

        return genes;
    }





}
