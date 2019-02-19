package EAM;

import com.sun.jdi.DoubleValue;

import java.security.spec.ECPoint;
import java.util.Random;


/**
 * Represents a swarm of particles from the Particle EAM Optimization algorithm.
 */
public class EAM {

    private int numOfParticles, epochs;
    private int dimension;
    private int function; // The function to search.
    private Particle[] oldPopulation;
    private Particle[] newPopulation;
    private static int bestParticle;
    private static double f_average;
    private static int worstParticle;
    private static int debug = 1;
    private static double old_best = Double.MAX_VALUE;
    /**
     * When Particles are created they are given a random position.
     * The random position is selected from a specified range.
     * If the begin range is 0 and the end range is 10 then the
     * value will be between 0 (inclusive) and 10 (exclusive).
     */
    private int beginRange, endRange;
    private static final int DEFAULT_BEGIN_RANGE = -100;
    private static final int DEFAULT_END_RANGE = 100;


    /**
     * Construct the EAM with custom values.
     * @param particles     the number of particles to create
     * @param epochs        the number of generations
     */
    public EAM(int function, int dimension, int particles, int epochs) {
        this.numOfParticles = particles;
        this.epochs = epochs;
        this.function = function;
        this.dimension = dimension;
        double infinity = Double.POSITIVE_INFINITY;
        beginRange = DEFAULT_BEGIN_RANGE;
        endRange = DEFAULT_END_RANGE;

    }

    /**
     * Execute the algorithm.
     */
    public void run () {

        oldPopulation = initialize();
        newPopulation = new Particle[numOfParticles];

        for (int k = 0; k < epochs; k++) {

            int best = 0, worst = 0;
            double sum = 0;
            for (int i = 0; i < oldPopulation.length; i++) {
                double functionValue = oldPopulation[i].getBestEval();

                if (oldPopulation[best].getBestEval() > functionValue) {
                    best = i;
                } else if (oldPopulation[worst].getBestEval() < functionValue) {
                    worst = i;
                }
                sum += functionValue;
            }
            bestParticle = best;
            worstParticle = worst;
            double average = sum / numOfParticles;
            f_average = average;
            ///

            generateNewPopulation();
            selection();

            double cbest = getGlobalBest();
            if(cbest < old_best && debug == 1) {
                old_best = cbest;
                System.out.println(String.format("Current Best %4d : %f", k+1, cbest));
            }
        }
    }



    private void generateNewPopulation()
    {
        Random r = new Random();
        for (int i = 0; i < numOfParticles; i++) {
            if(i == bestParticle)
                continue;
            newPopulation[i] = oldPopulation[i].clone();
//            newPopulation[i].add(r.nextDouble() *
//                    (oldPopulation[bestParticle].getBestEval() - oldPopulation[worstParticle].getBestEval())
//            );
            newPopulation[i].adjustNonBest(r.nextDouble(), oldPopulation[bestParticle], oldPopulation[worstParticle]);
        }
        //for best particle
        double c = oldPopulation[bestParticle].getBestEval() / f_average;
        newPopulation[bestParticle] = oldPopulation[bestParticle].clone();
        newPopulation[bestParticle].multiply(c);
        newPopulation[bestParticle].add(r.nextDouble());
    }


    /**
     * Create a set of particles, each with random starting positions.
     * @return  an array of particles
     */
    private Particle[] initialize () {
        Particle[] particles = new Particle[numOfParticles];
        for (int i = 0; i < numOfParticles; i++) {
            Particle particle = new Particle(function, beginRange, endRange, dimension);
            particles[i] = particle;
        }
        return particles;
    }


    private void selection()
    {
        Particle[] combined = new Particle[2*numOfParticles];
        for (int i = 0; i < numOfParticles; i++) {
            combined[i] = oldPopulation[i];
            combined[i+numOfParticles] = newPopulation[i];
        }

        for (int i = 0; i < numOfParticles +1; i++) {
            int min_index = i;
            for (int j = i+1; j < 2 * numOfParticles; j++) {
                if(combined[j].getBestEval() < combined[min_index].getBestEval())
                {
                    min_index = j;
                }
            }

            //swap
            Particle temp = combined[i];
            combined[i] = combined[min_index];
            combined[min_index] = temp;
        }

        for (int i = 0; i < numOfParticles; i++) {
            oldPopulation[i] = combined[i];
        }

    }

    public double getGlobalBest()
    {
        return oldPopulation[bestParticle].getBestEval();
    }

}
