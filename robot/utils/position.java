package frc.robot.utils;

import frc.robot.subsystems.sensors.sensors;

public class position {
  private volatile static double[] vector = {0, 0};
  private volatile static double[] vectorSpeed = {0, 0};

  private static volatile double robotHeading;
  private static double driverHeading = 0;

  private static volatile boolean working = false;

  public static double[] getVector() {
    return vector;
  }

  public static boolean isWorking() {
    return working;
  }

  public static void update() {
    if(sensors.gyro.isInitialized()){
      robotHeading = sensors.gyro.getVector()[0];
      working = true;
    }
    else{
      robotHeading = driverHeading;
      working = false;
    }

    double[] rawForce = {9.8 * sensors.accel.getZ(), 9.8 * sensors.accel.getX()};

    double[] force = tools.toVector(
      Math.sqrt(rawForce[0]*rawForce[0] + rawForce[1]*rawForce[1]),
      tools.toDegrees(rawForce[0], rawForce[1]) - (driverHeading - robotHeading));

    vector[0] += (2 * vectorSpeed[0] + force[0]) * 0.01;//... * 0.01 = ... * 0.02s / 2;
    vector[1] += (2 * vectorSpeed[1] + force[1]) * 0.01;

    vectorSpeed[0] += force[0];
    vectorSpeed[1] += force[1];
  }
}
