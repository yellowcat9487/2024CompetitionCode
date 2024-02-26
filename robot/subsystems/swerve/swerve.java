package frc.robot.subsystems.swerve;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;

import frc.robot.subsystems.sensors.*;
import frc.robot.utils.tools;

public class swerve{
  //swerve motor modules

	public static swerveModule lf = new swerveModule(1, 5);
  public static swerveModule lr = new swerveModule(4, 8);
  public static swerveModule rf = new swerveModule(2, 6);
  public static swerveModule rr = new swerveModule(3, 7);

  //variables

  private static double driverHeading;
  private static volatile double robotHeading;
  
  private static double x, y;

  //constants

  private static final double length = 1, width = 1;
  private static final double r = Math.sqrt(length*length + width*width);
  private static final double kMove = 0.45;
  private static final double kTurn = 0.45;

  //functions

  //init 
  public static void init() {
    lf.turningMotor.configSelectedFeedbackSensor(FeedbackDevice.Analog);
    lr.turningMotor.configSelectedFeedbackSensor(FeedbackDevice.Analog);
    rf.turningMotor.configSelectedFeedbackSensor(FeedbackDevice.Analog);
    rr.turningMotor.configSelectedFeedbackSensor(FeedbackDevice.Analog);

    lf.name = "lf";
    lr.name = "lr";
    rf.name = "rf";
    rr.name = "rr";

    lf.setPID(1, 0, 0);
    lr.setPID(1, 0, 0);
    rf.setPID(1, 0, 0);
    rr.setPID(1, 0, 0);
  }

  //move robot 
  public static void move(double _x, double _y, double turn, boolean usingGyro) {
    x = _x; y = _y;

    if(-0.05 < x && x < 0.05) x = 0;
    if(-0.05 < y && y < 0.05) y = 0;
    if(-0.05 < turn && turn < 0.05) turn = 0;

    x *= kMove / Math.sqrt(x*x + y*y);
    y *= kMove / Math.sqrt(x*x + y*y);
    turn *= kTurn;

    //convert the vector of driver's heading to the vector of robot's heading

    if(usingGyro) convertHeading();

    //calculate vector (x, y)

    final double lf_x = x + turn*(length / r), lf_y = y + turn*(width / r);
    final double lr_x = x - turn*(length / r), lr_y = y + turn*(width / r);
    final double rf_x = x + turn*(length / r), rf_y = y - turn*(width / r);
    final double rr_x = x - turn*(length / r), rr_y = y - turn*(width / r);

    //calculate turn degrees

    double lf_degrees = tools.toDegrees(lf_x, lf_y);
    double lr_degrees = tools.toDegrees(lr_x, lr_y);
    double rf_degrees = tools.toDegrees(rf_x, rf_y);
    double rr_degrees = tools.toDegrees(rr_x, rr_y);

    //calculate motor speed

    double lf_speed = Math.sqrt(lf_x*lf_x + lf_y*lf_y);
    double lr_speed = Math.sqrt(lr_x*lr_x + lr_y*lr_y);
    double rf_speed = Math.sqrt(rf_x*rf_x + rf_y*rf_y);
    double rr_speed = Math.sqrt(rr_x*rr_x + rr_y*rr_y);

    //bounding

    double max = 1;
    if(lf_speed > max) max = lf_speed;
    if(lr_speed > max) max = lr_speed;
    if(rf_speed > max) max = rf_speed;
    if(rr_speed > max) max = rr_speed;

    lf_speed /= max;
    lr_speed /= max;
    rf_speed /= max;
    rr_speed /= max;

		//setpoint

    lf.setpoint(lf_speed, lf_degrees);
    lr.setpoint(lr_speed, lr_degrees);
    rf.setpoint(rf_speed, rf_degrees);
    rr.setpoint(rr_speed, rr_degrees);
		
    return;
  }

  //get turningmotor encoder value 
  public static void getEncValue() {
    lf.getEncValue();
    lr.getEncValue();
    rf.getEncValue();
    rr.getEncValue();
  }

  //get robot heading 
  public static void getRobotHeading() {
    if(sensors.gyro.isInitialized()){
      robotHeading = sensors.gyro.getVector()[0];
    }
    else{
      robotHeading = driverHeading;
    }
  }

  //change the vector of driver's heading to the vector of robot's heading 
  private static void convertHeading() {
    if (x == 0 && y == 0) return;

    double yaw = robotHeading - driverHeading;

    if(yaw < 0) yaw += 360;
    if(yaw > 360) yaw -= 360;

    double angle = tools.toDegrees(x, y) - yaw;

    if(angle < 0) angle += 360;
    if(angle > 360) angle -= 360;

    final double[] new_vector = tools.toVector(Math.sqrt(x*x + y*y), angle);

    double max = 1;
    if(new_vector[0] < -max || new_vector[0] > max) max = Math.abs(new_vector[0]);
    if(new_vector[1] < -max || new_vector[1] > max) max = Math.abs(new_vector[1]);

    x = new_vector[0] / max;
    y = new_vector[1] / max;
    
    return;
  }

}
