public class ActivitySimulation {

    public static Object[] continuousStatistic(int day, Object min, Object maximum, double oriMean, double oriSD){
        double max;

        if(maximum == null){
            max = oriSD * 4;
        }else{
            max = (double) maximum;
        }

        // create an array to store x value per day
        double[] xArray = new double[day];
        for (int i = 0; i < day; i++) {
            xArray[i] = Calculation.getContinuousX((Double) min, max);
        }

        double meanX = Calculation.calculateContinuousMean(xArray);
        double sdX = Calculation.calculateContinuousSD(xArray);

        // create an array to store zScore per day
        double[] zScoreArray = new double[day];
        for (int i = 0; i < day; i++) {
            double x = xArray[i];
            zScoreArray[i] = Calculation.calculateContinuousZScore(x, meanX, sdX);
        }

        Object[] targetX = new Object[day];
        for (int i = 0; i < day; i++){
            double zScore = zScoreArray[i];
            targetX[i] = Calculation.getContinuousTargetX(zScore, oriMean, oriSD);
        }

        return targetX;

    }


    public static Object[] discreteStatistic(int day, Object min, Object maximum, double oriMean, double oriSD){
        int max;

        if(maximum == null){
            max = (int) (oriSD * 4);
        }else{
            max = (int) maximum;
        }

        // create an array to store x value per day
        int[] xArray = new int[day];
        for (int i = 0; i < day; i++) {
            xArray[i] = Calculation.getDiscreteX((Integer) min,max);
        }

        double meanX = Calculation.calculateDiscreteMean(xArray);
        double sdX = Calculation.calculateDiscreteSD(xArray);

        // create an array to store zScore per day
        double[] zScoreArray = new double[day];
        for (int i = 0; i < day; i++) {
            int x = xArray[i];
            zScoreArray[i] = Calculation.calculateDiscreteZScore(x, meanX, sdX);
        }

        Object[] targetX = new Object[day];
        for (int i = 0; i < day; i++){
            double zScore = zScoreArray[i];
            targetX[i] = Calculation.getDiscreteTargetX(zScore, oriMean, oriSD);
        }

        return targetX;

    }

}
