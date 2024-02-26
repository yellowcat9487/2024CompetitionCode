package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;

import frc.robot.subsystems.swerve.swerve;
import frc.robot.subsystems.noteSystem.noteSystem;
import frc.robot.subsystems.smartControl.*;
import frc.robot.subsystems.sensors.sensors;

import edu.wpi.first.wpilibj.Joystick;

public class Robot extends TimedRobot {
  private static Joystick player1 = new Joystick(0);
  private static Joystick player2 = new Joystick(1);

  //variables

  private volatile int shooterSpeed;
  private volatile int waitTimes;

  @Override
  public void robotInit() {
    sensors.initGyro();
    swerve.init();
    noteSystem.init();
    detectNote.init();

    shooterSpeed = 0;
    waitTimes = 50;
  }

  @Override
  public void robotPeriodic() {
    swerve.getEncValue();
    swerve.getRobotHeading();
    detectNote.update();
  }

  @Override
  public void autonomousInit() {
    Auto.autoStart();
  }

  @Override
  public void autonomousPeriodic() {
    Auto.update();
  }

  @Override
  public void teleopInit() {
    swerve.move(0, 0, 0,false);
  }

  @Override
  public void teleopPeriodic() {
    twoPlayer();
  }

  @Override
  public void disabledInit() {}

  @Override
  public void disabledPeriodic() {}

  @Override
  public void testInit() {}

  @Override
  public void testPeriodic() {}

  @Override
  public void simulationInit() {}

  @Override
  public void simulationPeriodic() {}


  /************************Functions************************/
  

  public void twoPlayer() {

    /******Basic control******/

    //Swerve 
    double x, y, turn;

    //Intake 
    boolean intake_F, intake_R;

    //Transport 
    boolean transport_F, transport_R;

    //Shooter 
    double shooter_F, shooter_R;

    //Compressor 
    // boolean compress, rise;

    //Go straight (robot heading) 
    boolean moveForward;

    /******Smart control******/

    //Intake & shoot combination 
    boolean runIntakeEvent, runShootEvent;

    //Detect note (limelight) 
    boolean runDetectNote;

    /********Calculate********/

    //Player1 

    x               =  player1.getRawAxis(0);
    y               = -player1.getRawAxis(1);
    turn            =  player1.getRawAxis(4);

    runIntakeEvent  =  player1.getRawButton(1);
    runShootEvent   =  player1.getRawButton(2);

    runDetectNote   =  player1.getRawButton(3);

    moveForward     =  player1.getRawButton(4);

    if(runIntakeEvent){
      intake_F = true;
      intake_R = false;

      transport_F = true;
      transport_R = false;
    }
    else{
      intake_F = false;
      intake_R = false;

      transport_F = false;
      transport_R = false;
    }

    if(runShootEvent){
      if(shooterSpeed < 100){//shooter speed up
        shooterSpeed += 4;//20ms * (100/4) = 0.5s
        if(shooterSpeed > 100) shooterSpeed = 100;
      }
      else if(waitTimes > 0)//wait (0.5s)
        waitTimes--;
      else{//run transport
        transport_F = true;
        transport_R = false;
      }
    }
    else{
      if(!runIntakeEvent){
        transport_F = false;
        transport_R = false;
      }
      
      if(shooterSpeed > 0) shooterSpeed -= 10;
      if(shooterSpeed < 0) shooterSpeed = 0;

      waitTimes = 25;//20ms * 25 = 0.5s
    }

    shooter_F = shooterSpeed / 100.0;
    shooter_R = 0;

    //Player2 

    intake_F        =  intake_F | player2.getRawButton(6);
    intake_R        =  intake_R | player2.getRawButton(5);
 
    transport_F     =  transport_F | player2.getRawButton(8);
    transport_R     =  transport_R | player2.getRawButton(7);

    if(shooter_F < player2.getRawAxis(3)) shooter_F = player2.getRawAxis(3);
    if(shooter_R < player2.getRawAxis(2)) shooter_F = player2.getRawAxis(2);

    runDetectNote   =  runDetectNote | player2.getRawButton(3);

    moveForward     =  moveForward | player2.getRawButton(4);

    if(moveForward){
      x = 0; y = 0.25; turn = 0;
    }

    if(runDetectNote){
      turn = detectNote.getTurnForce();
    }

    /*****Check deadband*****/

    if(-0.05 < x && x < 0.05) x = 0;
    if(-0.05 < y && y < 0.05) y = 0;
    if(-0.05 < turn && turn < 0.05) turn = 0;

    if(shooter_F < 0.05) shooter_F = 0;
    if(shooter_R < 0.05) shooter_R = 0;

    /*******Set point*******/

    swerve.move(x, y, turn, true);
    
    noteSystem.setIntake(intake_F, intake_R);
    noteSystem.setTransport(transport_F, transport_R);
    noteSystem.setShooter(shooter_F, shooter_R);

  }
}

/*









 
`                   * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
`                   * O O O O O O O O O O O O O O O O O O O O O O O O O O O O O O O O O O O O *
`                   * O                                                                     O *
`                   * O                                  _,.=*.                             O *
`                   * O                          __,.=*"'      ".                           O *
`                   * O                         \      _  .=**"'`                           O *
`                   * O                          "===*"H  H                                 O *
`                   * O                                H  H                                 O *
`                   * O                               _H  H___...===***"""***q.             O *
`                   * O           __......====****"""''    ___...===****==.___.b            O *
`                   * O           \      ___...===***""H  H                                 O *
`                   * O            `**""'        qxp   H  H   qxp                           O *
`                   * O                          l l   H  H   l l                           O *
`                   * O                          l l   H  H   l l                           O *
`                   * O                    ___...a l   H  H   l `***""'````*q.              O *
`                   * O             .q"""' ___..., l   H  H   l ..=====..__   \             O *
`                   * O              `a*"''      l l   H  H   l l          ``"'             O *
`                   * O                          l l   H  H   l l             .             O *
`                   * O                          l l   H  H   l l            yH             O *
`                   * O                         _i b   H  H   l l           y H             O *
`                   * O                     _-*' _-*   H  H   b  b         y  j             O *
`                   * O                 _-*' _-*'      H  H    b  '*=====*'  y              O *
`                   * O            .d""' _-*'          H  H     b-_________.d               O *
`                   * O             `a*"'              H  H                                 O *
`                   * O                                q  p                                 O *
`                   * O                                'qp'                                 O *
`                   * O                                                                     O *
`                   * O                                                                     O *
`                   * O O O O O O O O O O O O O O O O O O O O O O O O O O O O O O O O O O O O *
`                   * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 









*/
