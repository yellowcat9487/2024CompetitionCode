package frc.robot.subsystems.smartControl;

import frc.robot.utils.PID;
import frc.robot.utils.LimelightHelpers;

public class detectNote {
  private static PID pid;

  //variables 

  private static volatile double turn_force;

  //functions 

  public static void init() {
    turn_force = 0;
    pid = new PID(1.5, (1e-6), 2.1);//ki = 10^-6 
  }

  public static double getTurnForce() { 
    return turn_force;
  }

  public static void update() {
    double tx = LimelightHelpers.getTX("");
    double ta = LimelightHelpers.getTA("");

    boolean isDetectedNote = (ta >= 0.1) ? true : false;

    if(!isDetectedNote) tx = 0;

    turn_force = pid.calculate(tx / 32.0);
  }
}
