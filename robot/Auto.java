package frc.robot;

import edu.wpi.first.wpilibj.RobotController;

import frc.robot.utils.LimelightHelpers;
import frc.robot.utils.PID;
import frc.robot.utils.tools;

import frc.robot.subsystems.noteSystem.noteSystem;
import frc.robot.subsystems.sensors.sensors;
import frc.robot.subsystems.swerve.swerve;


public class Auto {
  //variables

  private static volatile double swerveForce[]; //{x, y, turn}

  private static volatile boolean runIntake;
  private static volatile boolean runTransport;
  private static volatile double shooterSpeed;

  private static volatile int waitStartShoot;
  private static volatile int waitStopShoot;

  private static volatile double intakeDistance = 0;
  private static volatile double backDistance = 0;

  private static volatile double tx = 0, ta = 0;
  
  private static volatile boolean usingGyro = false;
  private static volatile boolean goToGetNote = false;

  private static double startTime;

  /**
   * ~ status ~
   * 
   * 0 -> Looking for note
   * 1 -> Back to speaker
   * 2 -> Shooting
   * 3 -> Move forward
   */
  private static volatile int status = 0;
  
  private static PID turnPID = new PID(1.2, (1e-6), 2.1);

  //functions 

  public static void autoStart() {
    startTime = getNowTimeSec();
    status = 2;//shoot

    swerveForce = new double[]{0, 0, 0};
    usingGyro = false;
    swerveSetPoint();

    runIntake = false;
    runTransport = false;
    shooterSpeed = 0;

    waitStartShoot = 10;
    waitStopShoot = 150;
  }

  public static void update() {
    getValue();
    claculate();
    swerveSetPoint();
    noteSystemSetPoint();
  }

  public static void getValue() {
    intakeDistance = 0;
    backDistance = 0;

    tx = LimelightHelpers.getTX("");
    ta = LimelightHelpers.getTY("");
  }

  private static void claculate() {
    double nowTime = getNowTimeSec() - startTime;

    // Is timeout ?
    if(nowTime > 12) status = 3;
    
    switch (status) {
    case 0 :// Looking for note
      detectNote();
      break;

    case 1 :// Back to speaker
      status = 3;// Nothing
      break;

    case 2 :// Shoot
      shoot();
      break;

    case 3 :// Move forward (driver heading)
      swerveForce = new double[]{0, 0.25, 0};
      usingGyro = true;
      break;

    }
  }

  private static void detectNote() {
    double y, turn;//x = 0
    usingGyro = false;

    /***** Looking for note *****/
    
    //find note
    if (ta > 0.05){
      y = aimNoteForwardForce();

      if(-0.5 < tx && tx < 0.5){
        turn = 0;
        turnPID.resetIntergral();
      }
      else{
        turn = tools.bounding(turnPID.calculate(tx / 32.0), -1, 1);
      }
    }

    //Didn't detect note
    else{
      y = 0;

      double angle = sensors.gyro.getVector()[0];

      if(angle > 180) angle -= 360;

      if(-1 < angle && angle < 1){
        turn = 0;
        turnPID.resetIntergral();
      }
      else{
        turn = -tools.bounding(turnPID.calculate(angle), -1, 1);
      }//??
    }

    /***** Controll intake and transport *****/

    if(ta > 0.25) goToGetNote = true;

    if(goToGetNote){
      y = 1;

      runIntake = true;
      runTransport = true;
    }
    else{
      runIntake = false;
      runTransport = false;
    }

    // Is got note ?
    if(!(15 < intakeDistance && intakeDistance < 45) && goToGetNote){//??
      goToGetNote = false;

      runIntake = false;
      runTransport = false;

      status = 1;//back to speaker
    }

    swerveForce = new double[]{0, y, turn};

  }

  private static void shoot() {//total : 2.5s
    if(shooterSpeed < 1){//0.4s
      runTransport = false;
      shooterSpeed += 0.05;
      
      waitStartShoot = 5;
    }
    else if(waitStartShoot > 0){//0.1s
      runTransport = false;
      shooterSpeed = 1;

      waitStartShoot--;
      waitStopShoot = 100;
    }
    else if(waitStopShoot > 0){//2s
      runTransport = true;
      shooterSpeed = 1;

      waitStopShoot--;
    }
    else{
      runTransport = false;
      shooterSpeed = 0;

      status = 0;
    }

    swerveForce = new double[]{0, 0, 0};
  }

  private static double aimNoteForwardForce() {
    if(tx == 0) return 1;
    else return 1/Math.abs(tx);
  }

  private static double getNowTimeSec() {
    return RobotController.getFPGATime()*(1e-6);
  }

  private static void swerveSetPoint() {
    swerve.move(swerveForce[0], swerveForce[1], swerveForce[2], usingGyro);
  }

  private static void noteSystemSetPoint() {
    noteSystem.setIntake(runIntake, false);
    noteSystem.setTransport(runTransport, false);
    noteSystem.setShooter(shooterSpeed, 0);
  }

}
