package frc.robot.Subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.PneumaticsModuleType;

import static edu.wpi.first.wpilibj.DoubleSolenoid.Value.kForward;
import static edu.wpi.first.wpilibj.DoubleSolenoid.Value.kReverse;

import edu.wpi.first.wpilibj2.command.PIDSubsystem;
import edu.wpi.first.math.controller.PIDController;

import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import frc.robot.Constants.*;

/**
 * Implements an Intake PIDSubsystem for the robot
 */
public class Intake extends PIDSubsystem {

  private final CANSparkMax m_intakeMotor = new CANSparkMax(IntakeConstants.kMotorPort, MotorType.kBrushless);
  private final RelativeEncoder m_intakeEncoder = m_intakeMotor.getEncoder();

  private final DoubleSolenoid m_intakeValve = new DoubleSolenoid(PneumaticsModuleType.CTREPCM,IntakeConstants.kSolenoidPorts[0],
      IntakeConstants.kSolenoidPorts[1]);

  // Creates a SimpleMotorFeedForward with the specified feedforward gain
  private final SimpleMotorFeedforward m_intakeFeedforward = new SimpleMotorFeedforward(0.0,
      IntakeConstants.kIntakeFF);

  public Intake() {
    // Super implementation that defines the PIDController for the PIDSubsystem with
    // the PID values specified
    super(new PIDController(IntakeConstants.kPID[0],IntakeConstants.kPID[1],IntakeConstants.kPID[2]));
    getController().setTolerance(IntakeConstants.kTolerance);
    m_intakeMotor.setSmartCurrentLimit(IntakeConstants.kCurrentLimit);
    m_intakeMotor.enableVoltageCompensation(GlobalConstants.kVoltCompensation);
    m_intakeMotor.setInverted(true);
    m_intakeMotor.burnFlash();
  }

  @Override
  public void useOutput(double output, double setpoint) {
    m_intakeMotor.set(output + m_intakeFeedforward.calculate(setpoint));
  }

  /**
   * 
   * @return the shooter motor velocity in RPMs
   */
  @Override
  public double getMeasurement() {
    return m_intakeEncoder.getVelocity();
  }

  public void feedIn(){
    setSetpoint(2000.0);
  }
  public void feedOut(){
    setSetpoint(-2000.0);
  }

  public void floorIntake(){
    setSetpoint(11000.0);
    m_intakeValve.set(kForward);
  }

  public double getCurrent(){
    return m_intakeMotor.getOutputCurrent();
  }

  public void end(){
    setSetpoint(0.0);
    m_intakeValve.set(kReverse);
    disable();
  }

}