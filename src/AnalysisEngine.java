import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class AnalysisEngine {



    public static HashMap<String, double[]> generateTrainingStats(HashMap<String, Object[]> activityLog){
        HashMap<String, double[]> trainingData = new HashMap<>();
        HashMap<String, double[]> trainingStats = new HashMap<>();
        // convert object[] to double[]
        for (Map.Entry<String, Object[]> event : activityLog.entrySet()) {
            Object[] objectArray = event.getValue();
            double[] doubleArray = Calculation.convertToDoubleArray(objectArray);
            trainingData.put(event.getKey(), doubleArray);
        }

        for (Map.Entry<String, double[]> entry : trainingData.entrySet()) {
            String event = entry.getKey();
            double[] values = entry.getValue();
            double mean = Calculation.calculateContinuousMean(values);
            double stdDeviation = Calculation.calculateContinuousSD(values);
            // Store mean and stdDeviation in the trainingStats map
            double[] trainStatArray = {mean, stdDeviation};
            trainingStats.put(event, trainStatArray);
        }
        return trainingStats;
    }

    public static HashMap<String, double[]> generateAnomalyCounter(HashMap<String, Object[]> baseline, HashMap<String, double[]> statsFile, HashMap<String, Object[]> activityLog){
        double anomalyCount;
        HashMap<String, double[]> anomalyCounters = new HashMap<>();

        for (Map.Entry<String, Object[]> event : activityLog.entrySet()) {
            String eventName = event.getKey();

            Object[] objectData = event.getValue();
            double[] dataArray = Calculation.convertToDoubleArray(objectData);
            double[] statsArray = statsFile.get(eventName);
            double[] anomalyArray = new double[dataArray.length];

            for (int i = 0; i < dataArray.length; i++) {
                int weight = (Integer) baseline.get(eventName)[3];
                anomalyCount = ((statsArray[0] - dataArray[i]) / statsArray[1]);
                anomalyCount = Math.abs(anomalyCount);
                anomalyCount = anomalyCount * weight;
                anomalyCount = Double.parseDouble(String.format("%.4f", anomalyCount));
                anomalyArray[i] = anomalyCount;
            }
            anomalyCounters.put(eventName, anomalyArray);
        }
        return anomalyCounters;
    }

    public static double[] calculateDailyCounter(HashMap<String, double[]> anomalyCounters, int days) {

        double[] dailyCounter = new double[days];

        for (double[] anomalyArray : anomalyCounters.values()) {
            for (int i = 0; i < days; i++) {
                dailyCounter[i] += anomalyArray[i];
            }
        }

        // Round each value in dailyCounter to 4 decimal places
        for (int i = 0; i < days; i++) {
            dailyCounter[i] = roundToFourDecimalPlaces(dailyCounter[i]);
        }
        return dailyCounter;
    }

    private static double roundToFourDecimalPlaces(double value) {
        return Math.round(value * 10000.0) / 10000.0;
    }
}
