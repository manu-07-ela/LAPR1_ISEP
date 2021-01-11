import java.util.Scanner;
import java.util.Arrays;

public class population_distribution2 {
    public static void main(String[] args) {
        Scanner read = new Scanner(System.in);

        double initialPopVec[] = {1000, 300, 330, 100};
        //double[][] leslieMatrix = {{0.50,2.40,1,0},{0.5,0,0,0},{0,0.8,0,0},{0,0,0.5,0}};
        double[][] leslieMatrix = {{0, 3, 3.17, 0.39}, {0.11, 0, 0, 0}, {0, 0.29, 0, 0}, {0, 0, 0.33, 0}};

        printPopDistribution(initialPopVec);
        printMatrix(leslieMatrix);

        int generationNum, time = 0;


        System.out.println("Insert the generations' number to be estimated: ");

        do {
            generationNum = read.nextInt();
        } while (generationNum <= 0);

        double[] popVec = new double[leslieMatrix.length];
        double[] normalizedPopVec = new double[popVec.length];
        double[] popDim = new double[generationNum];
        double[] rateVariation = new double[generationNum];

        double dim, rate;

        while (time < generationNum) {
            System.out.println("YEAR " + time);
            System.out.println("Population Distribution:");

            if(time == 0) {
                printPopDistribution(initialPopVec);

                //NORMALIZATION

                System.out.println("Normalized Population Distribution:");
                fillNormalizedPopVec(normalizedPopVec,initialPopVec);
                printPopDistribution(normalizedPopVec);

                //DIMENSION

                double initialDim=getTotalPopulation(initialPopVec);
                System.out.println("Population Dimension: " + getTotalPopulation(initialPopVec));

                fillArray(initialDim, time, popDim);
                System.out.println();

                time++;


                fillArray(initialDim, time-1, popDim);


            } else {
                fillPopulationDistribution(initialPopVec,popVec,leslieMatrix,time);
                printPopDistribution(popVec);

                //NORMALIZATION

                System.out.println("Normalized Population Distribution:");
                fillNormalizedPopVec(normalizedPopVec,popVec);
                printPopDistribution(normalizedPopVec);

                //DIMENSION

                dim=getTotalPopulation(popVec);
                System.out.println("Population Dimension: " + dim);
                fillArray(dim, time, popDim);

                //RATE

                rate = getRateOfChangeOverTheYears(time, popDim);
                System.out.println("Rate variation between generation " + (time-1) + " and generation " + (time) + ": " + rate);
                System.out.println();
                fillArray(rate, time-1, rateVariation);

                time++;

            }

        }
        //APAGAR ANTES DE ENTREGAR
        System.out.println("Population Dimension Array:");
        System.out.println(Arrays.toString(popDim));

        /*
        time=0;
        while(time+1<generationNum) {
            rate = getRateOfChangeOverTheYears(time, popDim);
            fillArray(rate, time, rateVariation);
            time++;
        }
         */

        //APAGAR ANTES DE ENTREGAR
        System.out.println("Rate Variation Array:");
        System.out.println(Arrays.toString(rateVariation));


    }


    public static void fillPopulationDistribution(double initialPopVec[], double[] popVec, double[][] leslieMatrix, int time) {
        double mult = 0;
        double[] previousPopVec = new double[popVec.length];

        if (time == 1) {
            for (int line = 0; line < leslieMatrix.length; line++) {
                for (int column = 0; column < leslieMatrix[line].length; column++) {
                    mult = mult + leslieMatrix[line][column] * initialPopVec[column];
                }
                popVec[line] = mult;
                mult = 0;
            }
        } else {
            fillPreviousPopVec(previousPopVec,popVec);
            for (int line = 0; line < leslieMatrix.length; line++) {
                for (int column = 0; column < leslieMatrix[line].length; column++) {
                    mult = mult + leslieMatrix[line][column] * previousPopVec[column];
                }
                popVec[line] = mult;
                mult = 0;
            }
        }
    }
    public static void fillPreviousPopVec(double[] previousPopVec, double[] popVec) {
        for (int i = 0; i < popVec.length; i++) {
            previousPopVec[i] = popVec[i];
        }
    }
    public static double getTotalPopulation(double[] popVec) {
        double sum = 0;

        for (int i = 0; i < popVec.length; i++) {
            sum += popVec[i];
        }
        return sum;
    }
    public static void fillNormalizedPopVec(double[] normalizedPopVec, double[] popVec) {
        double totalPopulation = getTotalPopulation(popVec);

        for (int i = 0; i < popVec.length; i++) {
            normalizedPopVec[i] = popVec[i] / totalPopulation;
        }
    }
    /* -------------------------------- APAGAR ANTES DE ENTREGAR ------------------------------------------------------*/
    public static void printMatrix(double[][] array) {
        for (int line = 0; line < array.length; line++) {
            for (int column = 0; column < array[line].length; column++) {
                System.out.print(array[line][column] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }
    public static void printPopDistribution(double[] array) {
        for (int i = 0; i < array.length; i++) {
            System.out.print("- Class " + i + ": ");
            System.out.printf("%.3f%n", array[i]);
        }
        System.out.println();
    }

    /*-----------------------object = FILL POP DIMENSION AND RATE VARIATION OVER THE YEARS----------------------------*/
    public static void fillArray(double object, int time, double[] array) {
        array[time] = object;
    }


    public static double getRateOfChangeOverTheYears(int time, double [] popDim) {
        double nowGeneration = popDim[time-1];
        double nextGeneration = popDim[time];

        double quocient = nextGeneration/nowGeneration;

        return quocient;
    }

}

