package net.wvv.aimoveprd.player;

import jakarta.xml.bind.JAXBException;
import net.minecraft.util.math.Vec3d;
import net.wvv.aimoveprd.logging.PlayerLog;
import org.jpmml.evaluator.Evaluator;
import org.jpmml.evaluator.LoadingModelEvaluatorBuilder;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class PerceptronPlayerMovementRegressor implements IPlayerMovementRegressor {
    private static final int WINDOW_SIZE = 20;
    private volatile Evaluator evaluator;
    private final ReentrantLock lock = new ReentrantLock();

    @Override
    public List<Vec3d> predict(List<PlayerLog> actual, int ticks) {
        if (evaluator == null) {
            initialize();
            return Collections.emptyList();
        }

        if (actual.size() < WINDOW_SIZE) {
            return Collections.emptyList();
        }

        List<Vec3d> path = actual.stream().map(PlayerLog::getMovementXYZ).toList();

        List<Double> x = new ArrayList<>(), y = new ArrayList<>(), z = new ArrayList<>();
        for (Vec3d p : path) {
            x.add(p.x);
            y.add(p.y);
            z.add(p.z);
        }

        List<Double> xPredicted = predictMovements(x);
        List<Double> yPredicted = predictMovements(y);
        List<Double> zPredicted = predictMovements(z);

        Vec3d last = actual.getLast().getXYZ();
        return getAbsolutePredictions(last.x, last.y, last.z, xPredicted, yPredicted, zPredicted);
    }

    private void initialize() {
        if (evaluator != null) {
            return;
        }

        lock.lock();
        try {
            if (evaluator == null) {
                InputStream model = getClass().getClassLoader().getResourceAsStream("assets/ai-move-prd/model.pmml");
                if (model == null) {
                    throw new RuntimeException("Model file not found");
                }
                evaluator = new LoadingModelEvaluatorBuilder().load(model).build();
                evaluator.verify();
            }
        } catch (ParserConfigurationException | SAXException | JAXBException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    private List<Double> predictMovements(List<Double> actualPath) {
        if (actualPath.size() < WINDOW_SIZE) {
            return Collections.emptyList();
        }

        List<Double> predictedPath = new ArrayList<>(actualPath);

        for (int i = 0; i < WINDOW_SIZE; i++) {
            List<Double> window = new ArrayList<>(predictedPath.subList(predictedPath.size() - WINDOW_SIZE, predictedPath.size()));

            Map<String, ?> result = evaluator.evaluate(getArguments(window));
            Double prediction = (Double) result.get("y");

            if (prediction != null) {
                predictedPath.add(prediction);
            }
        }

        return predictedPath.subList(actualPath.size(), predictedPath.size());
    }


    private Map<String, Object> getArguments(List<Double> path) {
        Map<String, Object> arguments = new LinkedHashMap<>();
        for (int i = 1; i <= path.size(); i++) {
            arguments.put("c" + i, path.get(i - 1));
        }
        return arguments;
    }

    private List<Vec3d> getAbsolutePredictions(double xStart, double yStart, double zStart, List<Double> x, List<Double> y, List<Double> z) {
        List<Vec3d> predictions = new ArrayList<>();
        double xCurrent = xStart, yCurrent = yStart, zCurrent = zStart;

        for (int i = 0; i < x.size(); i++) {
            xCurrent += (x.get(i) != null ? x.get(i) : 0);
            yCurrent += (y.get(i) != null ? y.get(i) : 0);
            zCurrent += (z.get(i) != null ? z.get(i) : 0);

            predictions.add(new Vec3d(xCurrent, yCurrent, zCurrent));
        }

        return predictions;
    }


    public void setWindowSize(int size) {
        // Do nothing
    }
}
