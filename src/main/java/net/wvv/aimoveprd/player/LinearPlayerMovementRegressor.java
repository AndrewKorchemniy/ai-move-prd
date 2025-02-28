package net.wvv.aimoveprd.player;

import net.minecraft.util.math.Vec3d;
import net.wvv.aimoveprd.logging.PlayerLog;

import java.util.ArrayList;
import java.util.List;

public class LinearPlayerMovementRegressor implements IPlayerMovementRegressor {
    private int windowSize = 20;

    public void setWindowSize(int size) {
        windowSize = size;
    }

    public List<Vec3d> predict(List<PlayerLog> actual, int ticks) {
        var actualPath = actual.stream().map(PlayerLog::getXYZ).toList();
        if (actualPath.size() < windowSize) {
            return new ArrayList<>();
        }
        actualPath = actualPath.subList(actualPath.size() - windowSize, actualPath.size());

        // Split apart the actual list into x, y, and z components
        var x_train = new double[actualPath.size()];
        var y_train = new double[actualPath.size()];
        var z_train = new double[actualPath.size()];

        for (int i = 0; i < actualPath.size(); i++) {
            x_train[i] = actualPath.get(i).x;
            y_train[i] = actualPath.get(i).y;
            z_train[i] = actualPath.get(i).z;
        }

        var x_predict = linearRegression(x_train, ticks);
        var y_predict = linearRegression(y_train, ticks);
        var z_predict = linearRegression(z_train, ticks);

        var predicted = new ArrayList<Vec3d>();
        for (int i = 0; i < ticks; i++) {
            predicted.add(new Vec3d(x_predict[i], y_predict[i], z_predict[i]));
        }

        return predicted;
    }

    private double[] linearRegression(double[] train, int ticks) {
        int n = train.length;
        if (n == 0) return new double[ticks];

        double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0;

        // Calculate the sums required for the linear regression
        for (int i = 0; i < n; i++) {
            double yi = train[i];

            sumX += i;
            sumY += yi;
            sumXY += (double) i * yi;
            sumX2 += (double) i * (double) i;
        }

        // Calculate the slope (m) and intercept (b) for the linear equation y = mx + b
        double m = (n * sumXY - sumX * sumY) / (n * sumX2 - sumX * sumX);
        double b = (sumY - m * sumX) / n;

        // Calculate predictions for the given number of ticks
        double[] predictions = new double[ticks];
        for (int i = 0; i < ticks; i++) {
            double xi = n + i; // Predict the position beyond the available data
            predictions[i] = m * xi + b;
        }

        return predictions;
    }
}
