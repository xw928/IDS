import java.io.File;
import java.io.IOException;
import java.util.*;

public class IDS {

    private static HashMap<String, Object[]> baseline = new HashMap<>();
    private static HashMap<String, Object[]> activityLog = new HashMap<>();
    private static HashMap<String, double[]> anomalyCounters = new HashMap<>();
    private static double[] dailyCounter;

    public static void main(String[] args) throws IOException {
        String eventsFile = args[0];
        String statsFile = args[1];
        String Day = args[2];
        int day;

        // Check if the eventsFile is a valid path
        if (!isValidFilePath(eventsFile)) {
            System.err.println("Invalid events file path. Please check and try again.");
            return;
        }

        // Check if the statsFile is a valid path
        if (!isValidFilePath(statsFile)) {
            System.err.println("Invalid stats file path. Please check and try again.");
            return;
        }

        // Parse the day from the command line arguments
        try {
            day = Integer.parseInt(Day);
        } catch (NumberFormatException e) {
            System.err.println("Invalid input for the number of days. Please enter a valid integer.");
            return;
        }

        System.out.println("========== Initial Input ==========\n");
        System.out.println("Reading the Events.txt file......\n");
        System.out.println("Reading the Stats.txt file......\n");
        initialInput(eventsFile, statsFile);


        System.out.println("\n========== Activity Simulation and Log Engine ==========");
        activitySimulationEngine(day);
        WriteObjectToFile.writeActivityLogFile(activityLog);

        System.out.println("========== Analysis Engine ==========");
        HashMap<String, double[]> trainingStats = AnalysisEngine.generateTrainingStats(activityLog);
        System.out.println("\nBaseline Statistic (Training)");
        analysisEngine(day, trainingStats, activityLog);
        WriteObjectToFile.writeAnomalyCounterFile(anomalyCounters, dailyCounter);

        System.out.println("========== Alert Engine ==========");
        alertEngine(statsFile);

        System.out.println("========== Start Monitor ==========\n");
        while(true){
            Scanner input = new Scanner(System.in);
            System.out.print("Enter 'yes' to continue with another new stats.txt ('no' to quit): ");
            String ans = input.nextLine();

            if (Objects.equals(ans, "yes")) {
                startMonitor(eventsFile,statsFile);
            } else if(Objects.equals(ans, "no")) {
                System.out.println("Program terminated by user.");
                System.exit(0);
            }else{
                System.out.println("Incorrect input, Try again ~");
            }
        }

    }

    private static void startMonitor(String eventsFile, String statsFile) throws IOException {
        System.out.println("\nAlert Engine is ready......");

        Scanner scannerStats = new Scanner(System.in);
        HashMap<String, double[]> newStatsFile;
        String newStats;
        while (true) {
            System.out.print("\nPlease enter the new stats name: ");
            newStats = scannerStats.nextLine();

            // Check if the statsFile is a valid path
            if (!isValidFilePath(newStats)) {
                System.out.println("Invalid new stats file path. Please check and try again.");
            } else {
                newStatsFile = InitialInput.readNewStatsFile(newStats);
                break; // Break out of the loop if a valid file name is provided
            }
        }

        Scanner scannerDay = new Scanner(System.in);
        String prompt = "\nPlease enter the number of days to monitor: ";
        int newDay = checkDayInput(scannerDay, prompt);
        System.out.println();

        initialInput(eventsFile, statsFile);
        activitySimulationEngine(newDay);
        System.out.println("\nCompare to NEW stats file");
        analysisEngine(newDay,newStatsFile,activityLog);
        alertEngine(newStats);


    }

    private static void alertEngine(String statsFileName){
        Set<String> keys = baseline.keySet();
        int sumWeight = 0;

        for(String event: keys){
            int weight = (int) baseline.get(event)[3];
            sumWeight += weight;
        }

        int threshold = 2 * sumWeight;
        System.out.println("\nAnomaly Threshold: " + threshold);

        System.out.println("\nAnomaly counters based on threshold of " + threshold);

        String[] resultArray = new String[dailyCounter.length];
        for (int i = 0; i < dailyCounter.length; i++) {
            String result;
            double dailyCounterValue = dailyCounter[i];

            if(dailyCounterValue >= threshold){
                result = "Anomaly is detected. Daily anomaly counter of " + (dailyCounter[i]+1) + " exceed the threshold " + threshold;
            }else {
                result = "No anomaly is detected.";
            }
            resultArray[i] = "Day " + (i + 1) + ": " + result;
        }

        // Output the results or use the array as needed
        for (String result : resultArray) {
            System.out.println(result);
        }

        System.out.println();
        WriteObjectToFile.writeAlertFile(resultArray, statsFileName);
    }

    private static void analysisEngine(int day, HashMap<String, double[]> statsFile, HashMap<String, Object[]> activityLog){

        for (Map.Entry<String, double[]> entry : statsFile.entrySet()) {
            String event = entry.getKey();
            double[] stats = entry.getValue();
            System.out.println(event + ": " + Arrays.toString(stats));
        }

        System.out.println();
        anomalyCounters = AnalysisEngine.generateAnomalyCounter(baseline, statsFile, activityLog);

        System.out.println("Anomaly Counter");
        for (Map.Entry<String, double[]> entry : anomalyCounters.entrySet()) {
            String event = entry.getKey();
            double[] value = entry.getValue();
            System.out.println(event + ": " + Arrays.toString(value));
        }

        dailyCounter = AnalysisEngine.calculateDailyCounter(anomalyCounters, day);
        System.out.println("\nTotal Anomaly Counter: " + Arrays.toString(dailyCounter));

    }



    private static void activitySimulationEngine(int day){
        Set<String> keys = baseline.keySet();

        for(String eventName: keys){
            Object eventType = baseline.get(eventName)[0];
            Object min = baseline.get(eventName)[1];
            Object maximum = baseline.get(eventName)[2];
            double originalMean = (double) baseline.get(eventName)[4];
            double originalSD = (double) baseline.get(eventName)[5];

            Object[] targetX;

            if (Objects.equals(eventType, "D")){
                targetX = ActivitySimulation.discreteStatistic(day, min, maximum, originalMean, originalSD);
            }else{    // continuous event
                targetX = ActivitySimulation.continuousStatistic(day, min, maximum, originalMean, originalSD);
            }
            activityLog.put(eventName, targetX);
        }

        System.out.println("\nActivity Log");
        for (String eventName : activityLog.keySet()) {
            Object[] consistentValue = activityLog.get(eventName);
            System.out.println(eventName + ": " + Arrays.toString(consistentValue));
        }
    }



    private static void initialInput(String eventsFile, String statsFile)throws IOException{

        // Call the readEventsFile method to read the file and populate the events HashMap
        HashMap<String, Object[]> eventsData = InitialInput.readEventsFile(eventsFile);
        System.out.println("Contents of Events.txt:");
        for (String eventName : eventsData.keySet()) {
            Object[] values = eventsData.get(eventName);
            System.out.println(eventName + ": " + Arrays.toString(values));
        }

        System.out.println();

        HashMap<String, Object[]> statsData = InitialInput.readStatsFile(statsFile);
        System.out.println("Contents of Stats.txt:");
        for (String statName : statsData.keySet()) {
            Object[] values = statsData.get(statName);
            System.out.println(statName + ": " + Arrays.toString(values));
        }

        System.out.println();

        baseline = InitialInput.checkAndCombineData();

        // Check for inconsistencies or errors
        boolean hasError = baseline.isEmpty();

        if (hasError) {
            System.err.println("Error in initial input. Please check the content of the files.");
            System.exit(0);
        } else {
            System.out.println("Contents of Events.txt and Stats.txt:");
            for (String eventName : baseline.keySet()) {
                System.out.println(eventName + ": " + Arrays.toString(baseline.get(eventName)));
            }
        }
    }


    private static int checkDayInput(Scanner scanner, String prompt) {
        int day = 0;
        boolean isValidInput = false;

        while (!isValidInput) {
            System.out.print(prompt);

            if (scanner.hasNextInt()) {
                day = scanner.nextInt();
                isValidInput = true;
            } else {
                System.out.println("Invalid input. Please enter a valid integer.");
                scanner.next(); // Consume the invalid input to prevent an infinite loop
            }
        }

        return day;
    }

    public static boolean isValidFilePath(String filePath) {
        File file = new File(filePath);
        return file.exists() && file.isFile();
    }


}