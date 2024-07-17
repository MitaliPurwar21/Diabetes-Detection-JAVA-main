import java.io.*;
import java.util.*;

public class Compare {
    public static void main(String[] args) throws NumberFormatException, IOException {
        System.out.println("Welcome, This program is implementation of KNN algorthim for Women Diabetes Dataset");
        Scanner sc = new Scanner(System.in);
        KNN_Implementation trn_ds = new KNN_Implementation();
        String trainfilename = "./diabetes.csv";
        String testfilename = "./diabetes_test.csv";
        trn_ds.getKValueandDistMetrics();
        trn_ds.loadtestData(testfilename);
        trn_ds.loadtrainLabelData(trainfilename);
        trn_ds.distanceCalcualte();
        sc.close();
    }
}

class KNN_Implementation {
    /*
     * sc object for getting user input
     */
    Scanner sc = new Scanner(System.in);
    int knn_value = 1;
    int DistanceMetricsSelction = 0;
    int totalNumberOfLabel = 0;
    // created lists for storing training and testing datasets label and features.
    private List<double[]> trainfeatures = new ArrayList<>();
    private List<String> trainlabel = new ArrayList<>();
    private List<double[]> testfeatures = new ArrayList<>();
    private List<String> testlabel = new ArrayList<>();
    /*
     * loading testing data and extracting features and label for training dataset
     *
     */

    void loadtestData(String testfilename) throws NumberFormatException, IOException {
        File testfile = new File(testfilename);
        try {
            BufferedReader testreadFile = new BufferedReader(new FileReader(testfile));
            PrintWriter pw = new PrintWriter("RealTestLabel.txt");
            //String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
            String testline = testreadFile.readLine();
            while ((testline = testreadFile.readLine()) != null) {

                String[] split = testline.split(",");
//                if(alphabet.indexOf(split[0].charAt(0)) > -1)
//                    continue;
                double[] feature = new double[split.length - 1];
                for (int i = 0; i < split.length - 1; i++)
                {
                    if(split[i].indexOf('.') > -1)
                        feature[i] = Double.parseDouble(split[i]);
                    else
                        feature[i] = Integer.parseInt(split[i]);
                }
                if(feature[2] == 0 || feature [3] == 0 || feature[5] == 0 )
                    continue;
                testfeatures.add(feature);
                testlabel.add(split[feature.length]);
// writing original label for test data to file and counting number of label.
                pw.println(split[feature.length]);
                totalNumberOfLabel++;
            }
            pw.close();
            testreadFile.close();
        } catch (FileNotFoundException e) {
// TODO Auto catch block e.printStackTrace();
        }
    }

    void loadtrainLabelData(String trainfilename) throws NumberFormatException, IOException {
        File trainfile = new File(trainfilename);
        try {
            BufferedReader trainreadFile = new BufferedReader(new FileReader(trainfile));
            PrintWriter pw = new PrintWriter("RealTrainLabel.txt");
            //String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
            String trainline = trainreadFile.readLine();
            while ((trainline = trainreadFile.readLine()) != null) {
                String[] split = trainline.split(",");
//                if(alphabet.indexOf(split[0].charAt(0)) > -1)
//                    continue;

                double[] feature = new double[split.length - 1];
                for (int i = 0; i < split.length - 1; i++)
                {
                    if(split[i].indexOf('.') > -1)
                        feature[i] = Double.parseDouble(split[i]);
                    else
                        feature[i] = Integer.parseInt(split[i]);
                }

                if(feature[2] == 0 || feature [3] == 0 || feature[5] == 0 )
                    continue;
                trainfeatures.add(feature);

                trainlabel.add(split[feature.length]);
// writing original label for test data to file and counting number of label.
                pw.println(split[feature.length]);
                totalNumberOfLabel++;
            }
            pw.close();
            trainreadFile.close();
        } catch (FileNotFoundException e) {
// TODO Auto catch block e.printStackTrace();
        }
    }

    /*
     * Based on user input, calling algorithm to calculate distance
     */
    void distanceCalcualte() throws IOException {
        if (DistanceMetricsSelction == 1) {
            euclideanTrainDistance();
            euclideanTestDistance();
// calling accuracy method to show accuracy of model.
            accuracy();
        } else if (DistanceMetricsSelction == 2) {
            manhattanTrainDistance();
            manhattanTestDistance();
            accuracy();
        } else {
// if user selecting invalid options then they must select correct option.
            System.out.println("Invalid Selection");
            getKValueandDistMetrics();
            distanceCalcualte();
        }
    }

    @SuppressWarnings("unchecked")
    void euclideanTrainDistance() throws FileNotFoundException {
        KNN_Distance euclidean = new KNN_Distance();

        Iterator<double[]> trainOverfitITR = trainfeatures.iterator();

        PrintWriter pw = new PrintWriter("EuclideanTrainResult.txt");
        while (trainOverfitITR.hasNext()) {
            double trainOverfitF[] = trainOverfitITR.next();
            Iterator<double[]> trainITR = trainfeatures.iterator();
            int noOfobject = 0;
            ArrayList<DistanceAndFeatures> ts = new ArrayList<>();
            while (trainITR.hasNext()) {
                double trainF[] = trainITR.next();
                double dist = 0;
                dist = euclidean.getEuclideanDistance(trainF, trainOverfitF);
                String trainFeat = trainlabel.get(noOfobject);
                DistanceAndFeatures DfObject = new DistanceAndFeatures(dist, trainFeat);
                ts.add(DfObject);
                Collections.sort(ts);
                noOfobject++;
            }
            /*
             * counting top predicted label based on k value
             */
            int flag = 0, positive = 0, negative = 0;
            while (flag < knn_value) {
                DistanceAndFeatures s = ts.get(flag);
                String s1 = s.getLabel();
                if (s1.equals("1"))
                    positive++;
                else
                    negative++;
                flag++;
            }
            /*counting label and selecting highest label count as prediction label and writing to output file.*/
            if (positive > negative) {
                pw.println("1");
            } else
                pw.println("0");
        }
        pw.close();
    }

    @SuppressWarnings("unchecked")
    void euclideanTestDistance() throws FileNotFoundException {
        KNN_Distance euclidean = new KNN_Distance();
        Iterator<double[]> testITR = testfeatures.iterator();
        PrintWriter pw = new PrintWriter("EuclideanTestResult.txt");

        while (testITR.hasNext()) {
            double testF[] = testITR.next();
            Iterator<double[]> trainITR = trainfeatures.iterator();
            int noOfobject = 0;
            ArrayList<DistanceAndFeatures> ts = new ArrayList<>();
            while (trainITR.hasNext())
            {
                double trainF[] = trainITR.next();
                double dist = 0;
                dist = euclidean.getEuclideanDistance(trainF, testF);
                String trainFeat = trainlabel.get(noOfobject);
                DistanceAndFeatures DfObject = new DistanceAndFeatures(dist, trainFeat);
                ts.add(DfObject);
                Collections.sort(ts);
                noOfobject++;
            }
            /*
             * counting top predicted label based on k value
             */
            int flag = 0, positive = 0, negative = 0;
            while (flag < knn_value) {
                DistanceAndFeatures s = ts.get(flag);
                String s1 = s.getLabel();
                if (s1.equals("1"))
                    positive++;
                else
                    negative++;
                flag++;
            }

            /*
             * counting label and selecting highest label count as prediction label and
             * writing to output file.
             */

            if (positive > negative) {
                pw.println("1");
            } else
                pw.println("0");
        }

        pw.close();
    }

    /*
     * Manhattan Distance
     *
     * Calling Manhattan method to calculate distance and writing output to file.
     *
     */
    @SuppressWarnings("unchecked")
    void manhattanTrainDistance() throws FileNotFoundException {
        KNN_Distance euclidean = new KNN_Distance();
        Iterator<double[]> trainOverfitITR = trainfeatures.iterator();

        PrintWriter pw = new PrintWriter("ManhattanTrainResult.txt");
        while (trainOverfitITR.hasNext()) {
            double trainOverfitF[] = trainOverfitITR.next();
            Iterator<double[]> trainITR = trainfeatures.iterator();
            int noOfobject = 0;
            ArrayList<DistanceAndFeatures> ts = new ArrayList<>();
            while (trainITR.hasNext()) {
                double trainF[] = trainITR.next();
                double dist ;
                dist = euclidean.getManhattanDistance(trainF, trainOverfitF);

                String trainFeat = trainlabel.get(noOfobject);
                DistanceAndFeatures DfObject = new DistanceAndFeatures(dist, trainFeat);
                ts.add(DfObject);
                Collections.sort(ts);
                noOfobject++;
            }
            /*
             * counting top predicted label based on k value
             */
            int flag = 0, positive = 0, negative = 0;
            while (flag < knn_value) {
                DistanceAndFeatures s = ts.get(flag);
                String s1 = s.getLabel();
                if (s1.equals("1"))
                    positive++;
                else
                    negative++;
                flag++;

            }

            /*
             * counting label and selecting highest label count as prediction label and
             * writing to output file.
             */
            if (positive > negative) {
                pw.println("1");
            } else
                pw.println("0");
        }

        pw.close();
    }

    @SuppressWarnings("unchecked")
    void manhattanTestDistance() throws FileNotFoundException {
        KNN_Distance euclidean = new KNN_Distance();
        Iterator<double[]> testITR = testfeatures.iterator();
        PrintWriter pw = new PrintWriter("ManhattanTestResult.txt");

        while (testITR.hasNext()) {
            double testF[] = testITR.next();
            Iterator<double[]> trainITR = trainfeatures.iterator();
            int noOfobject = 0;
            ArrayList<DistanceAndFeatures> ts = new ArrayList<>();
            while (trainITR.hasNext()) {
                double trainF[] = trainITR.next();
                double dist = 0;
                dist = euclidean.getManhattanDistance(trainF, testF);

                String trainFeat = trainlabel.get(noOfobject);
                DistanceAndFeatures DfObject = new DistanceAndFeatures(dist, trainFeat);
                ts.add(DfObject);
                Collections.sort(ts);

                noOfobject++;
            }
            /*
             * counting top predicted label based on k value
             */
            int flag = 0, positive = 0, negative = 0;
            while (flag < knn_value) {
                DistanceAndFeatures s = ts.get(flag);
                String s1 = s.getLabel();
                if (s1.equals("1"))
                    positive++;
                else
                    negative++;
                flag++;
            }

            /*
             * counting label and selecting highest label count as prediction label and
             * writing to output file.
             */
            if (positive > negative) {
                pw.println("1");
            } else
                pw.println("0");
        }

        pw.close();
    }

    /*
     * method to get K value and Distance metrics.
     */
    void getKValueandDistMetrics() {
        System.out.println("Enter the K value of KNN ");
        knn_value = sc.nextInt();
// Restricted k value less 50.

        if (knn_value > 150) {
            System.out.println("K Value is out of range.");
            getKValueandDistMetrics();
        } else {

            System.out.println("Select below distance metric(1 or 2)\n1 Eucildean Distance Metrics\n2 Manhanttan Distance Metrics");
            DistanceMetricsSelction = sc.nextInt();
        }
    }

    /*
     * Calculating accuracy for model based Euclidean and Manhattan distance.
     */
    void accuracy() throws IOException {
        int count = 0;
        File fileTest = null;
        File fileTrain = null;
        if (DistanceMetricsSelction == 1) {
            fileTest = new File("EuclideanTestResult.txt");
            fileTrain = new File("EuclideanTrainResult.txt");
        } else if (DistanceMetricsSelction == 2) {
            fileTest = new File("ManhattanTestResult.txt");
            fileTrain = new File("ManhattanTrainResult.txt");
        }

        BufferedReader rf = new BufferedReader(new FileReader(fileTest));
        BufferedReader label = new BufferedReader(new FileReader(new File("RealTestLabel.txt")));
        String s = rf.readLine();
        while (s != null) {
            String lab = label.readLine();
            if (s.equals(lab)) {

            } else {
                count++;
            }
            s = rf.readLine();
        }
        System.out.println("Test Accuracy is: " + ((float) 100 - (((float) count / totalNumberOfLabel) * 100)) + "%");
        rf.close();
        label.close();
//


        BufferedReader rf2 = new BufferedReader(new FileReader(fileTrain));
        BufferedReader label2 = new BufferedReader(new FileReader(new File("RealTrainLabel.txt")));
        String s2 = rf2.readLine();
        while (s2 != null) {
            String lab = label2.readLine();
            if (s2.equals(lab)) {
            } else {
                count++;
            }

            s2 = rf2.readLine();
        }
        System.out.println("Train Accuracy is: " + ((float) 100 - (((float) count / totalNumberOfLabel) * 100)) + "%");
        rf2.close();
        label2.close();
    }
}

class KNN_Distance {
    public double getEuclideanDistance(double[] features1, double[] features2) {
        double sum = 0;
        for (int i = 0; i < features2.length; i++) { //System.out.println(features1[i]+" "+features2[i]);
//applied Euclidean distance formula
            sum += Math.pow(features1[i] - features2[i], 2);
        }
        return Math.sqrt(sum);
    }

    public double getManhattanDistance(final double[] features1, final double[] features2) {
        double sum = 0;
        for (int i = 0; i < features2.length ; i++)
//Applied Manhattan distance formula
            sum += Math.abs(features1[i] - features2[i]);
        return sum;
    }
}

@SuppressWarnings("rawtypes")
class DistanceAndFeatures implements Comparable {
    double dist;
    String label;

    public DistanceAndFeatures(double dist, String label) {
        this.dist = dist;
        this.label = label;
    }

    public double getDist() {
        return dist;
    }

    public String getLabel() {
        return label;
    }

    //Overriding to string method to show distance and label together separated by ;
    public String toString() {
        return dist + ";" + label;
    }

    //implementing compareTO method for customized sorting based on distance.
    public int compareTo(Object obj) {
// TODO Auto-generated method stub
        DistanceAndFeatures df = (DistanceAndFeatures) obj;
        double distance1 = this.dist;
        double distance2 = df.dist;
        if (distance1 < distance2)
            return -1;
        else if (distance1 > distance2) return 1;
        else
            return -1;
    }
}