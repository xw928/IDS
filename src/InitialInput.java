import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;

public class InitialInput {

    public static HashMap<String, Object[]> eventsData = new HashMap<>();
    public static HashMap<String, Object[]> statsData = new HashMap<>();
    public static HashMap<String, Object[]> baseline = new HashMap<>();

    public static void main(String[] args) throws IOException {
        String eventsFile = "Events.txt";
        String statsFile = "Stats.txt";
        eventsData = readEventsFile(eventsFile);
        statsData = readStatsFile(statsFile);
        checkAndCombineData();
        getBaselineKey();
        Map<String, String> eventTypeMap = getEventType();
        // Print the event types for each event name
        System.out.println("Event Types:");
        for (Map.Entry<String, String> entry : eventTypeMap.entrySet()) {
            System.out.println("Event Name: " + entry.getKey() + ", Event Type: " + entry.getValue());
        }
    }

    public static void getBaselineKey(){
        // Retrieve all keys of baseline
        Set<String> keys = baseline.keySet();
        System.out.println(keys);
    }


    public static Map<String, String> getEventType(){
        Map<String, String> eventTypeMap = new HashMap<>();
        for (Map.Entry<String, Object[]> entry : baseline.entrySet()) {
            String eventName = entry.getKey();
            Object[] eventData = entry.getValue();

            if (eventData.length > 0 && eventData[0] instanceof String) {
                String eventType = (String) eventData[0];
                eventTypeMap.put(eventName, eventType);
            }
        }

        return eventTypeMap;
    }





    public static HashMap<String, Object[]>checkAndCombineData() {
        Set<String> eventNames = eventsData.keySet();
        Set<String> statEventNames = statsData.keySet();

        // Events in Events.txt but not in Stats.txt
        Set<String> missingInStats = new HashSet<>(eventNames);
        missingInStats.removeAll(statEventNames);

        // Events in Stats.txt but not in Events.txt
        Set<String> missingInEvents = new HashSet<>(statEventNames);
        missingInEvents.removeAll(eventNames);

        if (eventNames.size() != statEventNames.size()) {
            System.err.println("Inconsistency: Number of events in Events.txt and Stats.txt doesn't match.");
        }

        if (!missingInStats.isEmpty()) {
            System.err.println("Inconsistency: Missing statistics for events: " + missingInStats);
        }

        if (!missingInEvents.isEmpty()) {
            System.err.println("Inconsistency: Missing events with statistics: " + missingInEvents);
        }


        if (missingInStats.isEmpty() && missingInEvents.isEmpty()){
            for (String eventName : eventNames) {
                if (statEventNames.contains(eventName)) {
                    // Combine data
                    Object[] eventData = eventsData.get(eventName);
                    Object[] statsEventData = statsData.get(eventName);
                    Object[] combinedEventData = new Object[eventData.length + statsEventData.length];
                    System.arraycopy(eventData, 0, combinedEventData, 0, eventData.length);
                    System.arraycopy(statsEventData, 0, combinedEventData, eventData.length, statsEventData.length);

                    // Add to combinedData
                    baseline.put(eventName, combinedEventData);
                }
            }
        }

        return baseline;
    }

    public static HashMap<String, Object[]> readEventsFile(String filePath) throws IOException {
        try(BufferedReader br = new BufferedReader(new FileReader(filePath))){
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.trim().split(":");
                String eventName = parts[0];

                DecimalFormat decimalFormat = new DecimalFormat("#.##");
                // Check and parse values based on the event type
                if (parts.length >= 5) { // Check if there are enough parts in the line
                    if (parts[1].equals("D")) {  // discrete event (integer)
                        Integer minValue = null;
                        Integer maxValue = null;
                        try {
                            minValue = parts[2].isEmpty() ? 0 : Integer.parseInt(parts[2]);
                            maxValue = parts[3].isEmpty() ? null : Integer.parseInt(parts[3]);

                        } catch (NumberFormatException e) {
                            System.err.println("Error: Invalid numeric value for discrete event: [" + eventName + "]");
                            System.exit(0);
                        }

                        int weight = 0;
                        try {
                            weight = Integer.parseInt(parts[4]);
                        } catch (NumberFormatException e) {
                            System.err.println("Error: Invalid weight value (not an integer) for event: [" + eventName + "]");
                            System.exit(0);
                        }
                        eventsData.put(eventName, new Object[]{parts[1], minValue, maxValue, weight});

                    } else if (parts[1].equals("C")) {  // continuous event (two decimal places)
                        Double minValue = null;
                        Double maxValue = null;
                        try {
                            minValue = parts[2].isEmpty() ? 0 : Double.parseDouble(decimalFormat.format(Double.parseDouble(parts[2])));
                            maxValue = parts[3].isEmpty() ? null : Double.parseDouble(decimalFormat.format(Double.parseDouble(parts[3])));
                            int weight = Integer.parseInt(parts[4]);
                            eventsData.put(eventName, new Object[]{parts[1], minValue, maxValue, weight});
                        } catch (NumberFormatException e) {
                            System.err.println("Error: Invalid numeric value for continuous event: [" + eventName + "]");
                            System.exit(0);
                        }

                        int weight = 0;
                        try {
                            weight = Integer.parseInt(parts[4]);
                        } catch (NumberFormatException e) {
                            System.err.println("Error: Invalid weight value (not an integer) for event: [" + eventName + "]");
                            System.exit(0);
                        }
                        eventsData.put(eventName, new Object[]{parts[1], minValue, maxValue, weight});

                    } else {
                        System.err.println("Error: Invalid event type for event: [" + eventName + "]");
                        System.exit(0);
                    }
                }
            }

        }catch(IOException e){
            throw e;
        }

        return eventsData;
    }


    public static HashMap<String, Object[]> readStatsFile(String filePath) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.trim().split(":");
                String statsName = parts[0];

                // Check if there are enough parts in the line
                if (parts.length == 3) {
                    try {
                        // Parse mean and standard deviation with two decimal places
                        DecimalFormat decimalFormat = new DecimalFormat("#.##");
                        double mean = Double.parseDouble(decimalFormat.format(Double.parseDouble(parts[1])));
                        double stdDeviation = Double.parseDouble(decimalFormat.format(Double.parseDouble(parts[2])));

                        statsData.put(statsName, new Object[]{mean, stdDeviation});
                    }catch (NumberFormatException e){
                        System.err.println("Error: Invalid numeric value for statistic: [" + statsName + "]");
                        System.exit(0);
                    }
                }
            }
        } catch (IOException e) {
            throw e;
        }
        return statsData;
    }

    public static HashMap<String, double[]> readNewStatsFile(String filePath) throws IOException{
        HashMap<String, double[]> newStatsFile = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            // Check if the file exists
            if (!br.ready()) {
                System.err.println("Error: The file " + filePath + " does not exist or is empty.");
                System.exit(1);
            }
            String line;
            boolean isFirstLine = true;

            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    // Skip the first line
                    isFirstLine = false;
                    continue;
                }

                String[] parts = line.trim().split(":");
                String statsName = parts[0];

                // Check if there are enough parts in the line
                if (eventsData.containsKey(statsName)) {
                    try {
                        // Parse mean and standard deviation with two decimal places
                        DecimalFormat decimalFormat = new DecimalFormat("#.##");
                        double mean = Double.parseDouble(decimalFormat.format(Double.parseDouble(parts[1])));
                        double stdDeviation = Double.parseDouble(decimalFormat.format(Double.parseDouble(parts[2])));

                        double[] newStatsArray = {mean, stdDeviation};
                        newStatsFile.put(statsName, newStatsArray);
                    }catch(NumberFormatException e){
                        System.err.println("Error: Invalid numeric value for statistic: [" + statsName + "]");
                    }

                }else{
                    System.err.println("Inconsistency: No event found for statistics: " + statsName);
                    System.exit(0);
                }
            }
        } catch (IOException e) {
            throw e;
        }
        return newStatsFile;
    }



}