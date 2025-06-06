# Intrusion Detection System (IDS)

A Java program that detects unusual user behavior by comparing activity patterns against normal baselines.

## What it does

1. Reads your event definitions and normal behavior statistics
2. Simulates user activities for a specified number of days
3. Calculates how different the simulated data is from normal behavior
4. Alerts you when anomalies are detected

## How to run

### Step 1: Compile
```bash
javac *.java
```

### Step 2: Run
```bash
java IDS Events.txt Stats.txt 5
```

Replace `5` with the number of days you want to simulate.

## Input files

### Events.txt
First line is the number of events, then define each event:
```
5
Logins:D:0::3:
Time online:C:0:1440:2:
Emails sent:D:0::1:
Emails opened:D:0::1:
Emails deleted:D:0::2:
```

Format: `event_name:type:min:max:weight:`
- Type: `D` for whole numbers, `C` for decimal numbers
- Min/Max: Range of values (leave empty for automatic)
- Weight: How important this event is (higher = more important)

### Stats.txt
First line is the number of events, then define normal behavior:
```
5
Logins:4:1.5:
Time online:150.5:25.00:
Emails sent:10:3:
Emails opened:12:4.5:
Emails deleted:7:2.25:
```

Format: `event_name:average:standard_deviation:`

## Output files

- **ActivityLog.txt** - The simulated user activities
- **AnomalyCounter.txt** - Anomaly scores for each day
- **StatsAlert.txt** - Which days had anomalies detected

## Example

If you run `java IDS Events.txt Stats.txt 3`, the program will:
1. Read your event definitions and normal statistics
2. Simulate 3 days of user activity
3. Check if any day looks suspicious
4. Tell you which days (if any) had anomalies

## Requirements

- Java 8 or newer
- Two properly formatted input files (Events.txt and Stats.txt)
