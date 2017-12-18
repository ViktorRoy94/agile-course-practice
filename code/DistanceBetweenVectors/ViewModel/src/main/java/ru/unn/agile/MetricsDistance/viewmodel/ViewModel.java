package ru.unn.agile.MetricsDistance.viewmodel;

import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.beans.binding.BooleanBinding;
//import ru.unn.agile.MetricsDistance.Model.MetricsDistance;
import ru.unn.agile.MetricsDistance.Model.MetricsDistance;
import ru.unn.agile.MetricsDistance.Model.MetricsDistance.Metric;

import java.util.ArrayList;
import java.util.List;

public class ViewModel {
    private final StringProperty x1 = new SimpleStringProperty();
    private final StringProperty y1 = new SimpleStringProperty();
    private final StringProperty x2 = new SimpleStringProperty();
    private final StringProperty y2 = new SimpleStringProperty();
    private final StringProperty result = new SimpleStringProperty();
    private final StringProperty status = new SimpleStringProperty();
    private final List<ValueChangeListener> valueChangedListeners = new ArrayList<>();
    private final ObjectProperty<ObservableList<Metric>> metrics =
            new SimpleObjectProperty<>(FXCollections.observableArrayList(Metric.values()));
    private final ObjectProperty<Metric> metric = new SimpleObjectProperty<>();
    private final BooleanProperty calculationDisabled = new SimpleBooleanProperty();



    // FXML needs default c-tor for binding
    public ViewModel() {
        x1.set("");
        y1.set("");
        x2.set("");
        y2.set("");
        metric.set(Metric.Chebyshev);
        result.set("");
        status.set(Status.WAITING.toString());

        BooleanBinding couldCalculate = new BooleanBinding() {
            {
                super.bind(x1, y1, x2, y2);
            }
            @Override
            protected boolean computeValue() {
                return getInputStatus() == Status.READY;
            }
        };
        calculationDisabled.bind(couldCalculate.not());

        // Add listeners to the input text fields
        final List<StringProperty> fields = new ArrayList<StringProperty>() { {
            add(x1);
            add(y1);
            add(x2);
            add(y2);
        } };

        for (StringProperty field : fields) {
            final ValueChangeListener listener = new ValueChangeListener();
            field.addListener(listener);
            valueChangedListeners.add(listener);
        }
    }

    public void calculate() {
        if (calculationDisabled.get()) {
            return;
        }

        final float[] vec1 = {Float.parseFloat(x1.get()), Float.parseFloat(y1.get())};
        final float[] vec2 = {Float.parseFloat(x2.get()), Float.parseFloat(y2.get())};
        final int dimension = 2;
        if (metric.get().name().equals("Chebyshev")) {
            result.set(Float.toString(MetricsDistance.calculateDistanceChebyshev(vec1, vec2)));
        }
        if (metric.get().name().equals("Minkowski")) {
            result.set(Float.toString(
                    MetricsDistance.calculateDistanceMinkowski(vec1, vec2, dimension)));
        }
        System.out.print(result.toString());
        status.set(Status.SUCCESS.toString());
    }

    public StringProperty x1Property() {
        return x1;
    }
    public StringProperty y1Property() {
        return y1;
    }
    public StringProperty x2Property() {
        return x2;
    }
    public StringProperty y2Property() {
        return y2;
    }
    public ObjectProperty<Metric> metricProperty() {
        return metric;
    }
    public final ObservableList<Metric> getMetircs() {
        return metrics.get();
    }
    public final String getResult() {
        return result.get();
    }
    public StringProperty resultProperty() {
        return result;
    }
    public BooleanProperty calculationDisabledProperty() {
        return calculationDisabled;
    }
    public final boolean isCalculationDisabled() {
        return calculationDisabled.get();
    }
    public StringProperty statusProperty() {
        return status;
    }
    public final String getStatus() {
        return status.get();
    }

    private Status getInputStatus() {
        Status inputStatus = Status.READY;
        if (x1.get().isEmpty() || y1.get().isEmpty()
                || x2.get().isEmpty() || x2.get().isEmpty()) {
            inputStatus = Status.WAITING;
        }
        try {
            if (!x1.get().isEmpty()) {
                Double.parseDouble(x1.get());
            }
            if (!y1.get().isEmpty()) {
                Double.parseDouble(y1.get());
            }
            if (!x2.get().isEmpty()) {
                Double.parseDouble(x2.get());
            }
            if (!y2.get().isEmpty()) {
                Double.parseDouble(y2.get());
            }
        } catch (NumberFormatException nfe) {
            inputStatus = Status.BAD_FORMAT;
        }

        return inputStatus;
    }

    private class ValueChangeListener implements ChangeListener<String> {
        @Override
        public void changed(final ObservableValue<? extends String> observable,
                            final String oldValue, final String newValue) {
            status.set(getInputStatus().toString());
        }
    }
}

enum Status {
    WAITING("Please provide input data"),
    READY("Press 'Calculate' or Enter"),
    BAD_FORMAT("Bad format"),
    SUCCESS("Success");

    private final String name;
    Status(final String name) {
        this.name = name;
    }
    public String toString() {
        return name;
    }
}
