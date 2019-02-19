package EAM;

import java.io.*;
import java.util.Hashtable;
import java.util.Scanner;

public class Benchmark {
    public static boolean isCmd = true;
    public static void main(String[] args) throws Exception
    {
        boolean isargs = false;
        int dim = 0;
        int fn=0;
        String path_prefix;
        int iter = 1000;
        int runs = 51;
        String command ;
        int pop = 40;
        Hashtable<String, String> arg = runArgParser(args);
        dim = !arg.containsKey("dim") ? dim : Integer.parseInt(arg.get("dim"));
        fn = !arg.containsKey("fun") ? fn : Integer.parseInt(arg.get("fun"));
        iter = !arg.containsKey("itr") ? iter : Integer.parseInt(arg.get("itr"));
        pop = !arg.containsKey("pop") ? pop : Integer.parseInt(arg.get("pop"));
        runs = !arg.containsKey("run") ? runs : Integer.parseInt(arg.get("run"));
        path_prefix = !arg.containsKey("out") ? "./" : arg.get("out");
        isCmd = !arg.containsKey("cmd") ? true : false;
        command = !arg.containsKey("scr") ? "" : arg.get("scr");
        if (!command.equals("")) {
            System.out.println(command);
        }


        int[] valid_dimensions = new int[]{ 2, 5, 10, 20, 30, 50, 100 };
        FileWriter f;
        try {

            for (int j = 0; j < 1; j++) {
                int dimension = dim;
                for (int i = 0; i < 1; i++) {
                    String FullFile = path_prefix + "EAM_" + fn + "_" + dimension + ".txt";
                    if(isCmd)
                    checkExists(FullFile);
                    f = new FileWriter(FullFile);
                    int particles = pop;
                    int iterations = iter;
                    int function = fn;


                    //statistics
                    double[] results_error = new double[runs];
                    double max_val = -Double.MAX_VALUE, min_val = Double.MAX_VALUE, sum_val = 0.0;
                    double max_time  = -Double.MAX_VALUE, min_time = Double.MAX_VALUE, sum_time = 0.0;
                    double max_error  = -Double.MAX_VALUE, min_error = Double.MAX_VALUE, sum_error = 0.0;
                    double[] mode = new double[1];
                    int k = 0;
                    for (k = 0; k < runs; k++) {
                        System.out.println("--------------------------- RUN " + (k+1) +"-----------------------------");

                        long startTime = System.currentTimeMillis();
                        EAM s = new EAM(function, dimension, particles, iterations);
                        s.run();
                        long endTime = System.currentTimeMillis();
                        double totalTime = (endTime - startTime)/1000.0;
                        double function_value = s.getGlobalBest();
                        double error = Math.abs(getFunctionOptimal(function) - s.getGlobalBest());
                        if(error < 1e-8)
                            error = 0.0;
                        sum_val += function_value;
                        sum_time += totalTime;
                        sum_error += error;
                        results_error[k] = error;

                        max_error = Math.max(max_error, error);
                        max_time = Math.max(max_time, totalTime);
                        max_val = Math.max(max_val, function_value);

                        min_error = Math.min(min_error, error);
                        min_time = Math.min(min_time, totalTime);
                        min_val = Math.min(min_val, function_value);


                        f.write((k + 1) + " " + function_value + " " + error + " " + totalTime + "\n");
                        System.out.println("Global Best : " + s.getGlobalBest());
                        System.out.println("Error: " + Math.abs(getFunctionOptimal(function) - s.getGlobalBest()));
                        System.out.println("Execution Time : " + (totalTime) + "s");
                        System.out.println("--------------------------- COMPLETE -------------------------");
                        f.flush();
                    }

                    f.write("Max" + " " + (max_val) + " " + (max_error) + " " + (max_time)+ "\n");
                    f.write("Min" + " " + (min_val) + " " + (min_error) + " " + (min_time) + "\n");
                    f.write("Avg" + " " + (sum_val / k) + " " + (sum_error / k) + " " + (sum_time / k) + "\n");
                    f.write("Standard Deviation " + getsd(results_error, sum_error / runs) + " (" + 0 + ") ");
                    if(k > 1)
                        f.write("Median " + results_error[(k/2)+1] );
                    f.close();
                }
            }

            if(!command.equals(""))
                Runtime.getRuntime().exec("/bin/sh -c " + command);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void checkExists(String fullFile) {
        File x = new File(fullFile);
        if(x.exists())
        {
            String output;
            do{
                System.out.print("File already exists.Do you wish to continue? (Y/N) : ");
                Scanner s = new Scanner(System.in);
                output = s.next();
                if(output.equalsIgnoreCase("N"))
                {
                    System.exit(0);
                }

            }while (!output.equalsIgnoreCase("Y"));

        }
    }

    private static Hashtable<String, String> runArgParser(String[] args)
    {
        Hashtable<String, String> maps = new Hashtable<>();

        if(args.length > 0 && args[0].equals("-a"))
        {
            maps.put("dim", args[1]);
            maps.put("fun", args[2]);
            if (!args[3].endsWith("/")) {
                args[3] = args[3] + File.pathSeparator;
            }
            maps.put("out", args[3]);

            if(args.length > 4 && (args.length -4 ) %2 == 0)
            {
                int t;
                for ( t = 4; t < args.length; t+=2) {
                    switch (args[t])
                    {
                        case "-r":
                            maps.put("run",args[t+1]);
                            break;
                        case "-p":
                            maps.put("pop",args[t+1]);
                            break;
                        case "-i":
                            maps.put("itr",args[t+1]);
                            break;
                        case "-exec":
                            maps.put("scr",args[t+1]);
                            break;
                        case "-cmd":
                            maps.put("cmd",args[t+1]);
                            break;
                    }
                }
            }
            else
            {
                System.out.println("Non-regular args ignored / not exists");
            }
        }
        else if(args.length > 0 && args[0].equals("-h"))
        {
            printHelp();
            System.exit(0);
        }
        else
        {
            printHelp();
            System.exit(0);
        }
        return maps;
    }
    private static void printHelp()
    {
        System.out.println("java EAM.Benchmark -a dim func output_dir [ -r run -i iter -p pop]]");
        System.out.println("    >> java EAM.Benchmark -a 2 1 ./Output [ -r 21 -i 1000 -p 40 ]");
        System.out.println("Command execute after program ends: ");
        System.out.println("    >> java EAM.Benchmark -a 2 1 ./Output [ -exec shutdown ]");
    }
    private static double getFunctionOptimal(int function)
    {
        if(function < 15)
            return (function - 15) * 100;
        else
            return (function - 14) * 100;
    }

    private static double getsd(double[] error, double mean)
    {
        double ss = 0;
        for (int i = 0; i < error.length; i++) {
            ss += Math.pow(error[i]- mean, 2);
        }
        return  Math.sqrt(ss / (error.length - 1));
    }


}
