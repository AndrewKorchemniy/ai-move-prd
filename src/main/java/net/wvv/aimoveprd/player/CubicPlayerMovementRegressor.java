package net.wvv.aimoveprd.player;

import net.minecraft.util.math.Vec3d;
import net.wvv.aimoveprd.logging.PlayerLog;

import java.util.ArrayList;
import java.util.List;

public class CubicPlayerMovementRegressor implements IPlayerMovementRegressor {
    private int windowSize = 20;

    public void setWindowSize(int size) {
        windowSize = size;
    }

    @Override
    public List<Vec3d> predict(List<PlayerLog> actual, int ticks) {
        var actualPath = actual.stream().map(PlayerLog::getXYZ).toList();
        if (actualPath.size() < windowSize) {
            return new ArrayList<>();
        }
        actualPath = actualPath.subList(actualPath.size() - windowSize, actualPath.size());

        // split apart the actual list into x, y, and z components
        // then use the best fit line algorithm to predict the next position
        // return the predicted position as a Vec3d list
        var x_train = new double[actualPath.size()];
        var y_train = new double[actualPath.size()];
        var z_train = new double[actualPath.size()];

        for (int i = 0; i < actualPath.size(); i++) {
            x_train[i] = actualPath.get(i).x;
            y_train[i] = actualPath.get(i).y;
            z_train[i] = actualPath.get(i).z;
        }

        var x_predict = leastSquares(x_train, ticks);
        var y_predict = leastSquares(y_train, ticks);
        var z_predict = leastSquares(z_train, ticks);

        var predicted = new ArrayList<Vec3d>();
        for (int i = 0; i < ticks; i++) {
            predicted.add(new Vec3d(x_predict[i], y_predict[i], z_predict[i]));
        }

        return predicted;
    }

    private double[] leastSquares(double[] train, int ticks) {
        int n = train.length;
        if (n == 0) return new double[ticks];

        double[] x = new double[n];
        for (int i = 0; i < n; i++) {
            x[i] = i;
        }

        // Calculate cubic regression coefficients
        double sumX = 0, sumX2 = 0, sumX3 = 0, sumX4 = 0, sumX5 = 0, sumX6 = 0;
        double sumY = 0, sumXY = 0, sumX2Y = 0, sumX3Y = 0;

        for (int i = 0; i < n; i++) {
            double xi = x[i];
            double yi = train[i];
            double xi2 = xi * xi;
            double xi3 = xi2 * xi;
            double xi4 = xi3 * xi;
            double xi5 = xi4 * xi;
            double xi6 = xi5 * xi;

            sumX += xi;
            sumX2 += xi2;
            sumX3 += xi3;
            sumX4 += xi4;
            sumX5 += xi5;
            sumX6 += xi6;
            sumY += yi;
            sumXY += xi * yi;
            sumX2Y += xi2 * yi;
            sumX3Y += xi3 * yi;
        }

        // Solve system of equations using matrix inversion or Gaussian elimination
        double[][] A = {
                {n, sumX, sumX2, sumX3},
                {sumX, sumX2, sumX3, sumX4},
                {sumX2, sumX3, sumX4, sumX5},
                {sumX3, sumX4, sumX5, sumX6}
        };

        double[] B = {sumY, sumXY, sumX2Y, sumX3Y};

        double[] coefficients = gaussianElimination(A, B);

        double[] predictions = new double[ticks];
        for (int i = 0; i < ticks; i++) {
            double xi = n + i;
            predictions[i] = coefficients[0] + coefficients[1] * xi + coefficients[2] * xi * xi + coefficients[3] * xi * xi * xi;
        }

        return predictions;
    }

    private double[] gaussianElimination(double[][] A, double[] B) {
        int n = B.length;
        for (int i = 0; i < n; i++) {
            int maxRow = i;
            for (int k = i + 1; k < n; k++) {
                if (Math.abs(A[k][i]) > Math.abs(A[maxRow][i])) {
                    maxRow = k;
                }
            }
            double[] temp = A[i];
            A[i] = A[maxRow];
            A[maxRow] = temp;
            double t = B[i];
            B[i] = B[maxRow];
            B[maxRow] = t;

            for (int k = i + 1; k < n; k++) {
                double factor = A[k][i] / A[i][i];
                B[k] -= factor * B[i];
                for (int j = i; j < n; j++) {
                    A[k][j] -= factor * A[i][j];
                }
            }
        }

        double[] result = new double[n];
        for (int i = n - 1; i >= 0; i--) {
            double sum = 0;
            for (int j = i + 1; j < n; j++) {
                sum += A[i][j] * result[j];
            }
            result[i] = (B[i] - sum) / A[i][i];
        }

        return result;
    }

}
