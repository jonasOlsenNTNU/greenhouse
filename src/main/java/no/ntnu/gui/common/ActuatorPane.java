package no.ntnu.gui.common;

import java.util.HashMap;
import java.util.Map;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import no.ntnu.greenhouse.Actuator;
import no.ntnu.greenhouse.ActuatorCollection;
import no.ntnu.tools.Logger;

/**
 * A section of the GUI representing a list of actuators. Can be used both on the sensor/actuator
 * node, and on a control panel node.
 */
public class ActuatorPane extends TitledPane {
  private final Map<Actuator, SimpleStringProperty> actuatorValue = new HashMap<>();
  private final Map<Actuator, SimpleBooleanProperty> actuatorActive = new HashMap<>();

  /**
   * Create an actuator pane.
   *
   * @param actuators A list of actuators to display in the pane.
   */
  public ActuatorPane(ActuatorCollection actuators) {
    super();
    setText("Actuators");
    VBox vbox = new VBox();
    vbox.setSpacing(10);
    setContent(vbox);
    addActuatorControls(actuators, vbox);
    GuiTools.stretchVertically(this);
  }

  /**
   * Adds GUI controls for actuators to a parent pane.
   *
   * @param actuators A collection of actuators to generate GUI controls for
   * @param parent The parent pane where the GUI controls will be added
   */
  private void addActuatorControls(ActuatorCollection actuators, Pane parent) {
    actuators.forEach(actuator ->
        parent.getChildren().add(createActuatorGui(actuator))
    );
  }

  /**
   * Creates a GUI representation of an Actuator, containing a label and a checkbox for toggling its state.
   *
   * @param actuator The Actuator object for which the GUI representation is being created
   * @return A JavaFX Node representing the graphical user interface for the Actuator
   */
  private Node createActuatorGui(Actuator actuator) {
    HBox actuatorGui = new HBox(createActuatorLabel(actuator), createActuatorCheckbox(actuator));
    actuatorGui.setSpacing(5);
    return actuatorGui;
  }

  /**
   * Creates a checkbox UI component for an Actuator with bidirectional binding to the actuator's state.
   *
   * @param actuator The Actuator object for which the checkbox is created
   * @return The JavaFX CheckBox representing the actuator's state
   */
  private CheckBox createActuatorCheckbox(Actuator actuator) {
    CheckBox checkbox = new CheckBox();
    SimpleBooleanProperty isSelected = new SimpleBooleanProperty(actuator.isOn());
    actuatorActive.put(actuator, isSelected);
    checkbox.selectedProperty().bindBidirectional(isSelected);
    checkbox.selectedProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue != null && newValue) {
        actuator.turnOn();
      } else {
        actuator.turnOff();
      }
      Logger.info("Actuator state: " + actuator.isOn());
    });
    return checkbox;
  }

  /**
   * Create a Label GUI component for an Actuator with text bound to the actuator's properties.
   *
   * @param actuator The Actuator object for which the Label GUI component is being created
   * @return A JavaFX Label representing the graphical user interface for the Actuator
   */
  private Label createActuatorLabel(Actuator actuator) {
    SimpleStringProperty props = new SimpleStringProperty(generateActuatorText(actuator));
    actuatorValue.put(actuator, props);
    Label label = new Label();
    label.textProperty().bind(props);
    return label;
  }

  private String generateActuatorText(Actuator actuator) {
    String onOff = actuator.isOn() ? "ON" : "off";
    return actuator.getType() + ": " + onOff;
  }

  /**
   * An actuator has been updated, update the corresponding GUI parts.
   *
   * @param actuator The actuator which has been updated
   */
  public void update(Actuator actuator) {
    SimpleStringProperty actuatorText = actuatorValue.get(actuator);
    SimpleBooleanProperty actuatorSelected = actuatorActive.get(actuator);
    if (actuatorText == null || actuatorSelected == null) {
      throw new IllegalStateException("Can't update GUI for an unknown actuator: " + actuator);
    }

    Platform.runLater(() -> {
      actuatorText.set(generateActuatorText(actuator));
      actuatorSelected.set(actuator.isOn());
    });
  }
}
