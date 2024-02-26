package frc.robot.subsystems.swerve;

import com.revrobotics.*;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.utils.PID;
import frc.robot.utils.tools;

import com.ctre.phoenix.motorcontrol.TalonSRXControlMode;
import com.ctre.phoenix.motorcontrol.can.*;

public class swerveModule {
	public TalonSRX turningMotor;
	public CANSparkMax driveMotor;

	public String name = "";

	private PID pid;

	//motor power

	private volatile double turningMotorPower;
	private volatile double driveMotorPower;

	//turning degrees of turningmotor

	private volatile double turningValue;

	/**********functions**********/

	//init

	public swerveModule(int turningMotorID, int driveMotorID){
		turningMotor = new TalonSRX(turningMotorID);
		driveMotor = new CANSparkMax(driveMotorID, CANSparkLowLevel.MotorType.kBrushed);
	}

	//set motor power

	public void setpoint(double speed, double angle){
		calculate(angle, turningValue);

		driveMotorPower = speed;

		turningMotor.set(TalonSRXControlMode.PercentOutput, -turningMotorPower);
		driveMotor.set(-driveMotorPower);
	}

	//get encoder value

	public void getEncValue(){
		turningValue = -Math.round(((int)turningMotor.getSelectedSensorPosition() % 1024) / 1024.0 * 360.0);

		if(turningValue < 0) turningValue += 360;
		if(turningValue >= 360) turningValue -= 360;

		SmartDashboard.putNumber(name, turningValue);
	}

	//set pid value(kp, ki, kd)

	public void setPID(double kp, double ki, double kd){
		pid = new PID(kp, ki, kd);
	}

	//calculate turningmotor output

	public void calculate(double SP, double PV){
		double error = SP - PV;

		if(error > 180) error -= 360;
		if(error < -180) error += 360;

		error /= 45.0;

		double output = pid.calculate(tools.bounding(error, -1, 1));

		turningMotorPower = tools.bounding(output, -1, 1);
	}
}
