package frc.robot.subsystems.sensors;

import edu.wpi.first.wpilibj.BuiltInAccelerometer;
import edu.wpi.first.wpilibj.I2C;
import frc.robot.utils.BNO055;
import frc.robot.utils.HC_SR04;
import frc.robot.utils.MB1122;

public class sensors {
  public static BNO055 gyro;
  public static HC_SR04 intakeSensor;
  public static MB1122 RSensor;
  public static MB1122 LSensor;
  public static BuiltInAccelerometer accel;

  public static void initAllSensors() {
    gyro = BNO055.getInstance(
      BNO055.opmode_t.OPERATION_MODE_NDOF_FMC_OFF, BNO055.vector_type_t.VECTOR_EULER,
      I2C.Port.kMXP, BNO055.BNO055_ADDRESS_A
    );
    intakeSensor = new HC_SR04(0, 1);
    RSensor = new MB1122(0);
    LSensor = new MB1122(1);
    accel = new BuiltInAccelerometer();
  }

  public static void initGyro() {
    gyro = BNO055.getInstance(
      BNO055.opmode_t.OPERATION_MODE_NDOF_FMC_OFF, BNO055.vector_type_t.VECTOR_EULER,
      I2C.Port.kMXP, BNO055.BNO055_ADDRESS_A
    );
  }

  public static void initAccelerometer() {
    accel = new BuiltInAccelerometer();
  }
}
