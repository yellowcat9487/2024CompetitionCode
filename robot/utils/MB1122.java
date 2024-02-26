package frc.robot.utils;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.RobotController;

public class MB1122 {
  private AnalogInput anaInput;
  
  public MB1122(int anaPin) {
    anaInput = new AnalogInput(anaPin);
  }

  public double getDistance() {
    return 0.625 * anaInput.getValue() / RobotController.getVoltage5V();
    //anaInput.getValue() * (5 / RobotController.getVoltage5V()) * 0.125
  }
}
