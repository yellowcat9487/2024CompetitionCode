package frc.robot.subsystems.hanger;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Solenoid;

public class hanger {
  private static Solenoid leftHanger;
  private static Solenoid rightHanger;
  private static Compressor compressor = new Compressor(0 ,PneumaticsModuleType.CTREPCM);

  public hanger(int leftChennal , int rightChannel) {
    leftHanger = new Solenoid(0, PneumaticsModuleType.CTREPCM, leftChennal);
    rightHanger = new Solenoid(0, PneumaticsModuleType.CTREPCM, rightChannel);
  }

  public void set(boolean compress, boolean rise) {
    if(compress) cheerUp();
    else stopCheeringUp();
    
    if(rise) riseHanger();
    else hang();
  }

  private void cheerUp() {
    compressor.enableDigital();
  }

  private void stopCheeringUp() {
    compressor.disable();
  }

  private void riseHanger() {
    leftHanger.set(true);
    rightHanger.set(true);
  }

  private void hang() {
    leftHanger.set(false);
    rightHanger.set(false);
  }
}
