import org.la4j.Matrix;
import org.la4j.decomposition.EigenDecompositor;
import org.la4j.matrix.dense.Basic2DMatrix;

import java.io.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Scanner;

public class main{
    static Scanner read = new Scanner(System.in);
    public static void main(String[] args) throws IOException, InterruptedException {
        String fileName = read.next();
        String[] speciesName = speciesName(fileName);
        int size = sizeMatrix(fileName);
        double [] sizeSpecies = new double[size];
        double[][] leslieMatrix = new double[size][size];
        readFile(fileName, sizeSpecies, leslieMatrix);
        //print(sizeSpecies);
        //print1(leslieMatrix);
        populationDistribution(sizeSpecies, leslieMatrix);
        assintoticAnalysis(leslieMatrix);
        callGnuplot();

    }
    public static String[] speciesName (String path){
        String[] specie = path.split(".txt");
        return specie;
    }
    public static void readFile (String path, double[] size, double[][] leslie) throws FileNotFoundException {
        String vector;
        String [] auxVector;
        int cont=0, i;
        File archive = new File(path);
        Scanner readFile = new Scanner(archive);

        /*
        String [] auxVector2;
        String [] auxVector3;
        String vetor1 = readFile.nextLine();
        auxVector= transformVector(vetor1);
        String vetor2 = readFile.nextLine();
        auxVector2 = transformVector(vetor2);
        String vetor3 = readFile.nextLine();
        auxVector3 = transformVector(vetor3);
        for (int m=1; m<auxVector.length; m++){
            for (int k=0; k<auxVector.length; k++){
                if (m == k+1){
                    leslie[m][k] = Double.parseDouble(auxVector2[k]);
                }
            }
        }
        for (int j=0; j<auxVector.length; j++){
            leslie[0][j] = Double.parseDouble(auxVector3[j]);
        }
        */

        do{
            vector = readFile.nextLine();
            auxVector = transformVector(vector);
            switch (cont){
                case 0:
                    for (i=0; i<auxVector.length; i++){
                        size[i] = Integer.parseInt(auxVector[i]);
                    }
                    break;
                case 1:
                    for (int m=1; m<leslie.length; m++) {
                        for (int k = 0; k < leslie.length; k++) {
                            if (m == k + 1) {
                                leslie[m][k] = Double.parseDouble(auxVector[k]);
                            }
                        }
                    }
                    break;
                default:
                    for (i=0; i<auxVector.length; i++){
                        leslie[0][i] = Double.parseDouble(auxVector[i]);
                    }
                    break;
            }
            cont++;
        }while (readFile.hasNextLine());

        readFile.close();
    }



    public static String[] transformVector (String vector){
        String[] auxVector = vector.split(", ");
        for (int i=0; i<auxVector.length; i++){
            auxVector[i] = auxVector[i].substring(auxVector[i].indexOf("=")+1);
        }
        return auxVector;
    }

    public static int sizeMatrix (String path) throws FileNotFoundException {
        File archive = new File(path);
        Scanner readFile = new Scanner(archive);
        String vector = readFile.nextLine();
        int size = transformVector(vector).length;
        readFile.close();
        return size;
    }

    public static void print (int[] size){
        for (int i=0; i< size.length; i++){
            System.out.printf("%d ", size[i]);
            System.out.println();
        }
    }

    public static void print1 (double[][] size){
        for (int i=0; i< size.length; i++){
            for (int k=0; k< size.length; k++){
                System.out.printf("%f ", size[i][k]);
            }
            System.out.println();
        }
    }

    public static void populationDistribution (double [] initialPopVec, double[][] leslieMatrix) throws IOException {
        Scanner read = new Scanner(System.in);

        //double initialPopVec[] = {1000, 300, 330, 100};
        //double[][] leslieMatrix = {{0.50,2.40,1,0},{0.5,0,0,0},{0,0.8,0,0},{0,0,0.5,0}};
        //double[][] leslieMatrix = {{0, 3, 3.17, 0.39}, {0.11, 0, 0, 0}, {0, 0.29, 0, 0}, {0, 0, 0.33, 0}};

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
                time++;
                //int a=time-1;

                //NORMALIZATION

                System.out.println("Normalized Population Distribution:");
                fillNormalizedPopVec(normalizedPopVec,initialPopVec, time-1);
                printPopDistribution(normalizedPopVec);

                //DIMENSION

                double initialDim=getTotalPopulation(initialPopVec);
                System.out.println("Population Dimension: " + getTotalPopulation(initialPopVec));
                fillArray(initialDim, time-1, popDim);

            } else {
                fillPopulationDistribution(initialPopVec,popVec,leslieMatrix,time);
                printPopDistribution(popVec);
                time++;

                //NORMALIZATION

                System.out.println("Normalized Population Distribution:");
                fillNormalizedPopVec(normalizedPopVec,popVec, time-1);
                printPopDistribution(normalizedPopVec);

                //DIMENSION

                dim=getTotalPopulation(popVec);
                System.out.println("Population Dimension: " + dim);
                System.out.println();
                fillArray(dim, time-1, popDim);

            }

        }
        //APAGAR ANTES DE ENTREGAR
        //System.out.println(Arrays.toString(popDim));
        time=0;
        rateVariation[0] = 0;
        while(time+1<generationNum) {
            rate = getRateOfChangeOverTheYears(time, popDim);
            fillArray(rate, time+1, rateVariation);
            time++;
        }
        dimensionDataFormat(popDim, rateVariation);
        //APAGAR ANTES DE ENTREGAR
        //System.out.println(Arrays.toString(rateVariation));
    }

    public static void generationsDataFormat (double [] popVec, double [] normalizedPopVec, int gen) throws IOException {
        DecimalFormat df = new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.US));
        int filesNum = popVec.length;
        String data = "", fn= "";
        for(int i=0;i<filesNum;i++){
            data =  gen + " " + df.format(popVec[i]) + " " + df.format(normalizedPopVec[i]);
            fn = "class"+(i+1)+".dat";
            dataToFile(fn, data);
        }
    }
    public static void dimensionDataFormat (double [] popDim, double [] rateVariation) throws IOException {
        DecimalFormat df = new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.US));
        String data = "";
        for(int k=0; k < popDim.length; k++){
            data = k + " " + df.format(popDim[k]) + " " + df.format(rateVariation[k]);
            dataToFile("populationTotal.dat", data);
        }
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
    public static void fillNormalizedPopVec(double[] normalizedPopVec, double[] popVec, int gen) throws IOException {
        double totalPopulation = getTotalPopulation(popVec);

        for (int i = 0; i < popVec.length; i++) {
            normalizedPopVec[i] = popVec[i] / totalPopulation;
        }

        generationsDataFormat(popVec, normalizedPopVec, gen);
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
    public static double printPopDistribution(double[] array) {
        double sm = 0;
        for (int i = 0; i < array.length; i++) {
            System.out.println("- Class " + i + " : " + array[i]);
            sm += array[i];
        }
        System.out.println();
        return sm;
    }

    /*-----------------------object = FILL POP DIMENSION AND RATE VARIATION OVER THE YEARS----------------------------*/
    public static void fillArray(double object, int time, double[] array) {
        array[time] = object;
    }


    public static double getRateOfChangeOverTheYears(int time, double [] popDim) {
        double nowGeneration = popDim[time];
        double nextGeneration = popDim[time+1];

        double quocient = nextGeneration/nowGeneration;

        return quocient;
    }

    public static void dataToFile(String fileName, String fileData) throws IOException {
        String textToAppend = fileData;
        File file = new File(fileName);
        if(file.exists()){
            //Set true for append mode
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
            writer.newLine();
            writer.write(textToAppend);
            writer.close();
        }else{
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(textToAppend);
            writer.close();
        }
    }
    public static void callGnuplot () throws IOException, InterruptedException {
        Process process1 = Runtime.getRuntime().exec("gnuplot -c ./testeGnuplot.gp 1 4");
        process1.waitFor();
        deleteDatFiles();
    }
    public static void deleteDatFiles(){
        // Lists all files in folder
        File folder = new File("./");
        File fList[] = folder.listFiles();
        // Searchs .lck
        for (int i = 0; i < fList.length; i++) {
            String pes = String.valueOf(fList[i]);
            if (pes.endsWith(".dat")) {
                // and deletes
                boolean success = (new File(String.valueOf(fList[i])).delete());
            }
        }
    }
    public static void assintoticAnalysis(double leslieMatrix[][]){

        //double leslieMatrix[][]={{0.50, 2.40, 1, 0},{0.5,0, 0, 0}, {0, 0.8, 0, 0}, {0, 0, 0.5, 0}};
        //double leslieMatrix[][]={{0.50, 2.40, 1, 0},{0.5,0, 0, 0}, {0, 0.8, 0, 0}, {0, 0, 0.5, 0}};

        //creation and filling of the eigen Vector Matrix and the eigen Value Matrix through Eigen Decomposition
        double eigenVecM [][] = new double[leslieMatrix.length][leslieMatrix.length];
        double eigenValM [][] = new double[leslieMatrix.length][leslieMatrix.length];
        eigenValAndVecMatrix(leslieMatrix,eigenVecM,eigenValM);


        //creation of the maximum Eigen Vector Array
        double maxVecM [] = new double [leslieMatrix.length];
        //creation of the normalized maximum Eigen Vector Array
        double normalizedMaxVecM [] = new double [maxVecM.length];

        //saving the max Eigen Value through the method maxEigenValue and filling the maxVecM through the fillMaxVecM
        double maxEigenValue = findMaxEigenValue(leslieMatrix,eigenValM,eigenVecM,maxVecM);


        //TESTES:
        /*
        print2D(eigenVecM);
        System.out.println();
        print2D(eigenValM);
        System.out.println();
        System.out.println("The eigen value which has the maximum module is: " + maxEigenValue);
        System.out.println();
        System.out.println("The maximum eigen vector is: ");
        print1D(maxVecM);
         */


        double percChangePop = (maxEigenValue-1) * 100;

        System.out.println("ASYMPTOTIC BEHAVIOUR OF THE POPULATION ASSOCIATED TO THE MAXIMUM EIGEN VALUE");
        System.out.println();
        System.out.println("At Steady State there is a specific constant number associated with a specific population vector.");
        System.out.println("The constant number is the eigen value which has the maximum module of the Leslie Matrix representative of the current population.");
        System.out.println("The vector is its respective eigenvector.");
        System.out.println();
        System.out.print("The eigen value which has the maximum module is, approximately: ");
        System.out.printf("%.2f%n", maxEigenValue);
        System.out.println("This eigen value represents the growth rate.");

        if(maxEigenValue > 1) {
            System.out.print("As the eigen value is greater than 1, this means the population is growing and will be, approximately, ");
            System.out.printf("%.0f", percChangePop);
            System.out.println("% larger in size.");
        } else if(maxEigenValue < 1) {
            System.out.print("As the eigen value is lesser than 1, this means the population is decreasing and will be, approximately, ");
            System.out.printf("%.0f", Math.abs(percChangePop));
            System.out.println("% smaller in size.");
        } else {
            System.out.println("As the eigen value is equal to 1, the population will remain constant in size over time.");
        }

        System.out.println();
        System.out.println("The eigenvector associated to the maximum eigenvalue represents the constant population proportions.");
        System.out.println();
        System.out.println("Constant population proportions: (2 decimal places)");
        printPopDistribution(maxVecM);
        System.out.println();
        System.out.println("Normalized constant population proportions: (2 decimal places)");
        fillNormalizedPopVec(normalizedMaxVecM,maxVecM);
        printPopDistribution(normalizedMaxVecM);
    }
    public static void eigenValAndVecMatrix(double[][] leslieMatrix, double[][] eigenVecM, double[][] eigenValM) {
        Matrix leslie = new Basic2DMatrix(leslieMatrix);

        EigenDecompositor eigenD = new EigenDecompositor(leslie);
        Matrix [] decompLeslie = eigenD.decompose();

        double vecM [][] = decompLeslie[0].toDenseMatrix().toArray();
        double valM [][] = decompLeslie[1].toDenseMatrix().toArray();

        //APAGAR FOR ANTES DE ENTREGAR
        for (int i = 0; i < 2; i++) {
            System.out.println(decompLeslie[i]);
        }
        //TERMINA AQUI

        fillEmptyMatrix(eigenVecM,vecM);
        fillEmptyMatrix(eigenValM,valM);

    }

    public static double findMaxEigenValue(double[][] leslieMatrix, double[][] eigenValM, double[][] eigenVecM, double[] maxVecM) {

        double maxEigenVal = Math.abs(eigenValM[0][0]);
        int columnMaxEigenVal = 0;

        for (int line = 0; line < eigenValM.length; line++) {
            for (int column = 0; column < eigenValM.length; column++) {
                if (Math.abs(eigenValM[line][column]) > maxEigenVal) {
                    maxEigenVal = eigenValM[line][column];
                    columnMaxEigenVal = column;
                }
            }
        }
        fillMaxVecM(eigenVecM,columnMaxEigenVal,maxVecM);

        return maxEigenVal;

    }
    public static void fillMaxVecM(double[][] eigenVecM, int columnMaxEigenVal, double[] maxVecM) {

        for (int line = 0; line < eigenVecM.length; line++) {
            for (int column = 0; column < eigenVecM[line].length; column++) {
                if(column == columnMaxEigenVal) {
                    maxVecM[line] = eigenVecM[line][column];
                }
            }
        }
    }
    public static void fillEmptyMatrix(double[][] emptyMatrix, double[][] matrix) {
        for (int line = 0; line < matrix.length; line++) {
            for (int column = 0; column < matrix[line].length; column++) {
                emptyMatrix[line][column] = matrix[line][column];
            }
        }
    }
    //MÉTODO QUE SE REPETE NA POPULATION_DISTRIBUTION
    public static void fillNormalizedPopVec(double[] normalizedPopVec, double[] popVec) {
        double totalPopulation = getTotalPopulation(popVec);

        for (int i = 0; i < popVec.length; i++) {
            normalizedPopVec[i] = popVec[i] / totalPopulation;
        }
    }
    //APAGAR ANTES DE ENTREGAR
    public static void print2D(double[][] array) {
        for (int line = 0; line < array.length; line++) {
            for (int column = 0; column < array[line].length; column++) {
                System.out.print(array[line][column] + " ");
            }
            System.out.println();
        }
    }
    //APAGAR ANTES DE ENTREGAR
    public static void print1D(double[] array) {
        for (int i = 0; i < array.length; i++) {
            System.out.println(array[i]);
        }
    }

}