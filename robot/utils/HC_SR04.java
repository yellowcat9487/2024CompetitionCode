package frc.robot.utils;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.RobotController;
import java.util.TimerTask;

public class HC_SR04 {
  private DigitalOutput trig;
  private DigitalInput echo;
  private java.util.Timer waveTimer;
  private java.util.Timer distanceCalculaterTimer;
  private volatile double distance;

  public HC_SR04(int trigPin, int echoPin) {
    trig = new DigitalOutput(trigPin);
    echo = new DigitalInput(echoPin);

    waveTimer = new java.util.Timer();
    waveTimer.schedule(new waveTimerTask(), 0);

    distanceCalculaterTimer = new java.util.Timer();
    distanceCalculaterTimer.schedule(new distanceCalculaterTask(), 0);
  }

  public double getDistance() {
    return distance;
  }

  private void sendWave() {
    final double startTime = RobotController.getFPGATime();
    trig.set(false);
    while(RobotController.getFPGATime() < startTime + 2) {};
    trig.set(true);
    while(RobotController.getFPGATime() < startTime + 10) {};
    // trig.set(false);
  }

  private void distance_calculater() {
    while(!echo.get()) {}
    final double startTime = RobotController.getFPGATime();
    while(echo.get()) {}
    final double duration = RobotController.getFPGATime() - startTime;
    distance = duration * 0.01715;// duration * 0.0343 / 2.0 
  }

  private class waveTimerTask extends TimerTask {
    public void run() {
      while(true) sendWave();
    }
  }

  private class distanceCalculaterTask extends TimerTask {
    public void run() {
      while(true) distance_calculater();
    }
  }
}

