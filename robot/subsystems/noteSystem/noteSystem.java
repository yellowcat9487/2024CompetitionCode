package frc.robot.subsystems.noteSystem;

import com.revrobotics.*;

public class noteSystem {
  public static CANSparkMax intake;
  public static CANSparkMax transport;
  public static CANSparkMax shooter1;
  public static CANSparkMax shooter2;

  public static void init() {
    intake    = new CANSparkMax(9, CANSparkLowLevel.MotorType.kBrushless);
    transport = new CANSparkMax(10, CANSparkLowLevel.MotorType.kBrushless);
    shooter1  = new CANSparkMax(11, CANSparkLowLevel.MotorType.kBrushless);
    shooter2  = new CANSparkMax(12, CANSparkLowLevel.MotorType.kBrushless);
  }

  public static void setIntake(boolean intake_F, boolean intake_R) {
    if(intake_F && !intake_R){
      intake.set(1);
    }
    else if(!intake_F && intake_R){
      intake.set(-1);
    }
    else intake.set(0);
  }

  public static void setTransport(boolean transport_F, boolean transport_R) {
    if(transport_F && !transport_R){
      transport.set(1);
    }
    else if(!transport_F && transport_R){
      transport.set(-1);
    }
    else transport.set(0);
  }

  public static void setShooter(double shooter_F, double shooter_R) {
    if(shooter_F > 0.05 && shooter_R < 0.05){
      shooter1.set(shooter_F);
			shooter2.set(-shooter_F);
    }
    else if(shooter_F < 0.05 && shooter_R > 0.05){
			shooter1.set(-shooter_R);
			shooter2.set(shooter_R);
    }
    else{
			shooter1.set(0);
			shooter2.set(0);
    }
  }
}
