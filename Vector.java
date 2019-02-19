package EAM;

/**
 * Can represent a position as well as a velocity.
 */
class Vector {

    private double[] vector;
    private double limit = Double.MAX_VALUE;


    Vector (int n) {

        vector = new double[n];
    }

    Vector (double[] vector) {
        this.vector = vector.clone();
    }

    Vector (int n, double value) {
        vector = new double[n];
        for (int i = 0; i < n; i++) {
            vector[i] = value;
        }
    }
    public int size()
    {
        return vector.length;
    }

    public double[] getAsDoubleArray()
    {
        return vector;
    }

    void set (int index, double value) {
        vector[index] = value;
    }


    void add (Vector v) {
        for (int i = 0; i < vector.length; i++) {
            vector[i] = vector[i] + v.get(i);
        }
        limit();
    }

    void add(double value)
    {
        for (int i = 0; i < vector.length; i++) {
            vector[i] = vector[i] + value;
        }
        limit();
    }

    double get(int index)
    {
        return vector[index];
    }

    void sub (Vector v) {
        for (int i = 0; i < vector.length; i++) {
            vector[i] = vector[i] - v.get(i);
        }
        limit();
    }

    void mul (double s) {
        for (int i = 0; i < vector.length; i++) {
            vector[i] = vector[i] * s;
        }
        limit();
    }

    void div (double s) {
        for (int i = 0; i < vector.length; i++) {
            vector[i] = vector[i] / s;
        }
        limit();
    }

    void normalize () {
        double m = mag();
        for (int i = 0; i < vector.length; i++) {
            if( m > 0)
            vector[i] = vector[i]/ m;
        }

    }

    private double mag () {
        double sm = 0;
        for (int i = 0; i < vector.length; i++) {
            sm = vector[i] * vector[i];
        }
        return Math.sqrt(sm);
    }

    void limit (double l) {
        limit = l;
        limit();
    }

    private void limit () {
//        double m = mag();
//        if (m > limit) {
//            double ratio = m / limit;
//            for (int i = 0; i < vector.length; i++) {
//                vector[i] = vector[i] / ratio;
//            }
//        }
        for (int i = 0; i < vector.length; i++) {
            if(vector[i] > 100)
                vector[i] = 100;
            if (vector[i] < -100)
                vector[i] = -100;
        }

    }

    public Vector clone () {
        return new Vector(this.vector);
    }

    public String toString () {
        StringBuilder s = new StringBuilder();
        s.append("[ ");
        for (int i = 0; i < vector.length; i++) {
            s.append(vector[i] + " ");

        }
        s.append(" ]");
        return s.toString();
    }

}
