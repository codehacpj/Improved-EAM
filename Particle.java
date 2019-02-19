package EAM;

import java.util.Random;

/**
 * Represents a particle from the Particle EAM Optimization algorithm.
 */
class Particle {

    private Vector position;        // Current position.
    private double bestEval;        // Personal best value.
    private int function;  // The evaluation function to use.
    private double beginRange;
    private double endRange;

    /**
     * Construct a Particle with a random starting position.
     * @param beginRange    the minimum xyz values of the position (inclusive)
     * @param endRange      the maximum xyz values of the position (exclusive)
     */
    Particle (int function, int beginRange, int endRange, int dimension) {
        if (beginRange >= endRange) {
            throw new IllegalArgumentException("Begin range must be less than end range.");
        }
        this.function = function;
        position = new Vector(dimension);
        this.beginRange = beginRange;
        this.endRange = endRange;
        setRandomPosition(beginRange, endRange);

        bestEval = eval();
    }

    Particle( int function, int dimension, double eval)
    {
        this.function = function;
        position = new Vector(dimension);
        this.beginRange = beginRange;
        this.endRange = endRange;

        bestEval = eval;
    }


    /**
     * The evaluation of the current position.
     * @return      the evaluation
     */
    private double eval () {
        testfunc tf = new testfunc();
//        tf.test_func(x, f, dimension,population_size,func_num);
        double[] result = new double[1];
        try
        {
            tf.test_func( position.getAsDoubleArray(),result, position.size(), 1, function );
        }
        catch (Exception e)
        {
            System.out.println("Evaluation Exception : " + e.getMessage());
        }
        return  result[0];
    }

    private void setRandomPosition (int beginRange, int endRange) {
        for (int i = 0; i < position.size(); i++) {
            position.set(i, rand(beginRange, endRange));
        }
    }

    /**
     * Generate a random number between a certain range.
     * @param beginRange    the minimum value (inclusive)
     * @param endRange      the maximum value (exclusive)
     * @return              the randomly generated value
     */
    private static int rand (int beginRange, int endRange) {
        Random r = new java.util.Random();
        return r.nextInt(endRange - beginRange) + beginRange;
    }

    /**
     * Update the personal best if the current evaluation is better.
     */
    void updatePersonalBest () {
        bestEval = eval();
//        if (eval < bestEval) {
//            bestPosition = position.clone();
//            bestEval = eval;
//        }
    }

    /**
     * Get a copy of the position of the particle.
     * @return  the x position
     */
    Vector getPosition () {
        return position;
    }


    /**
     * Get the value of the personal best solution.
     * @return  the evaluation
     */
    double getBestEval () {
        return bestEval;
    }

    public Particle clone() {
        Particle p = new Particle(this.function, this.position.size(), this.eval());
        p.position.add(this.position);
        return p;
    }

    void add(double value)
    {
        position.add(value);
        updatePersonalBest();
    }


    void adjustNonBest(double rand, Particle best, Particle worst)
    {
        double [] bestV = best.getPosition().getAsDoubleArray();
        double [] worstV = worst.getPosition().getAsDoubleArray();

        for (int i = 0; i < position.size(); i++) {
            position.set(i, position.get(i) + (rand * (bestV[i] - worstV[i])));
        }
        updatePersonalBest();
    }
    void multiply(double value)
    {
        position.mul(value);
        updatePersonalBest();
    }


}
