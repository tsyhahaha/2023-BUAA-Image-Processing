public class Function {
    public static double f(double input) {
        if (input < 86) {
            return input / 2;
        } else if (input < 170) {
            return 2 * input - 129;
        } else {
            return input / 2 + 126;
        }
    }
}
