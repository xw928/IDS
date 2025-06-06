//calculation.java
import java.util.Random;
public class Calculation {

    public static int getDiscreteX(int min, int max){
        Random random = new Random();
        int x = random.nextInt((max - min) + 1) + min;
        return x;
    }

    public static double getContinuousX(double min, double max){
        Random random = new Random();
        double x = min + (max - min) * random.nextDouble();
        return roundToTwoDecimalPlaces(x);
    }

    public static double calculateDiscreteMean(int[] numbers){
        double sum = 0;
        for (double num : numbers){
            sum += num;
        }
        double mean = sum / numbers.length;
        return roundToTwoDecimalPlaces(mean);
    }

    public static double calculateContinuousMean(double[] numbers){
        double sum = 0;
        for (double num : numbers){
            sum += num;
        }
        double mean = sum / numbers.length;
        return roundToTwoDecimalPlaces(mean);
    }

    public static double calculateDiscreteSD(int[] numbers){
        double mean = calculateDiscreteMean(numbers);
        double sumOfSD = 0;
        for(double number : numbers){
            sumOfSD += Math.pow(number - mean, 2);
        }

        double variance = sumOfSD / numbers.length;
        double standardDeviation = Math.sqrt(variance);

        return roundToTwoDecimalPlaces(standardDeviation);
    }

    public static double calculateContinuousSD(double[] numbers){
        double mean = calculateContinuousMean(numbers);
        double sumOfSD = 0;
        for(double number : numbers){
            sumOfSD += Math.pow(number - mean, 2);
        }

        double variance = sumOfSD / numbers.length;
        double standardDeviation = Math.sqrt(variance);
        return roundToTwoDecimalPlaces(standardDeviation);
    }

    public static double roundToTwoDecimalPlaces(double value) {
        return Math.round(value * 100.0) / 100.0;
    }


    public static double calculateDiscreteZScore(int x, double mean, double sd){
        double zScore = (x - mean) / sd;
        return zScore;
    }

    public static double calculateContinuousZScore(double x, double mean, double sd){
        double zScore = (x - mean) / sd;
        return zScore;
    }

    public static int getDiscreteTargetX(double zScore, double originalMean, double originalSD){
        int targetX = (int) ((zScore * originalSD) + originalMean);
        return targetX;
    }

    public static double getContinuousTargetX(double zScore, double originalMean, double originalSD){
        double targetX = ((zScore * originalSD) + originalMean);
        return roundToTwoDecimalPlaces(targetX);
    }



    public static double[] convertToDoubleArray(Object[] objectArray) {
        double[] doubleArray = new double[objectArray.length];

        for (int i = 0; i < objectArray.length; i++) {
            if (objectArray[i] instanceof Double) {
                doubleArray[i] = (Double) objectArray[i];
            } else if (objectArray[i] instanceof Integer) {
                doubleArray[i] = (Integer) objectArray[i];
            } else {
                // Handle other types or throw an exception if needed
                throw new IllegalArgumentException("Unsupported type in the array");
            }
        }

        return doubleArray;
    }


    public static void main(String[] args) {
        System.out.println(getContinuousX(4,10));
        System.out.println(getDiscreteX(5,8));

        double[] numbers = {0,4,0,1,1};
//        double standardDeviation = calculateStandardDeviation(numbers);
//        System.out.println("Standard Deviation: " + standardDeviation);
    }



}
