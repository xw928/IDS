import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

public class WriteObjectToFile {

    public static void writeActivityLogFile(HashMap<String, Object[]> activityLog){
        System.out.println("\nWriting Activity Log into ActivityLog.txt file......");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("ActivityLog.txt"))) {
            for (String eventName : activityLog.keySet()) {
                Object[] consistentValue = activityLog.get(eventName);
                writer.write(eventName + ": " + Arrays.toString(consistentValue));
                writer.newLine();
            }
            System.out.println("The data has been successfully written!\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeAnomalyCounterFile(HashMap<String, double[]> anomalyCounter, double[] dailyCounter){
        System.out.println("\nWriting Anomaly Counter into AnomalyCounter.txt file......");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("AnomalyCounter.txt"))) {
            for (String eventName : anomalyCounter.keySet()) {
                double[] anomalyValue = anomalyCounter.get(eventName);
                writer.write(eventName + ": " + Arrays.toString(anomalyValue));
                writer.newLine();
            }

            writer.newLine(); // Add a newline between anomaly counter and daily counter

            writer.write("Total Anomaly Counter: " + Arrays.toString(dailyCounter));

            System.out.println("The data has been successfully written!\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeAlertFile(String[] resultArray, String statsFileName){
        // Generate the alert file name based on the stats file name
        String alertFileName = generateAlertFileName(statsFileName);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(alertFileName))) {
            for (String s : resultArray) {
                writer.write(s);
                writer.newLine();
            }
            System.out.println("Alerts written to: " + alertFileName);
            System.out.println();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String generateAlertFileName(String statsFileName) {
        // Extract file name without extension
        String fileNameWithoutExtension = statsFileName.substring(0, statsFileName.lastIndexOf('.'));

        // Generate alert file name by appending "Alert" to the original file name
        String alertFileName = fileNameWithoutExtension + "Alert.txt";

        return alertFileName;
    }

}
