package org.firstinspires.ftc.teamcode.mechanisms;

import static com.qualcomm.robotcore.hardware.DcMotorSimple.Direction.FORWARD;
import static com.qualcomm.robotcore.hardware.DcMotorSimple.Direction.REVERSE;
import static com.qualcomm.robotcore.hardware.DcMotor.RunMode.RUN_USING_ENCODER;
import static com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior.BRAKE;
import static com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior.FLOAT;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

/**
 * This is the Launcher fly wheel class.
 * Configure launch motor and feeder directions i.e. FORWARD/REVERSE based on hardware setting.
 */
public class Launcher {
    private DcMotorEx launcher;
    private String launcherName = "launcher";
    private DcMotorSimple.Direction launcherDirection = FORWARD;
    private DcMotor.ZeroPowerBehavior laucherZeroPowerBehavior = BRAKE;

    private double targetVelocity = 1300;
    private double minVelocity    = 1280;

    private CRServo feeder;
    private String feederName = "feeder";
    private DcMotorSimple.Direction feederDirection = REVERSE;

    private enum LaunchState { IDLE, SPIN_UP, LAUNCH}
    private LaunchState launchState = LaunchState.IDLE;

    public void build(HardwareMap hardwareMap) {
        launcher = hardwareMap.get(DcMotorEx.class, launcherName);
        launcher.setMode(RUN_USING_ENCODER);
        launcher.setDirection(launcherDirection);
        launcher.setZeroPowerBehavior(BRAKE);
        launcher.setPIDFCoefficients(RUN_USING_ENCODER, new PIDFCoefficients(40, 0, 0, 12.5));
        launcher.setVelocity(0.0);
        feeder = hardwareMap.get(CRServo.class, feederName);
        feeder.setDirection(feederDirection);
        feeder.setPower(0.0);
    }

    public Launcher launcherName(String launcherName) {
        this.launcherName = launcherName;
        return this;
    }

    public Launcher launcherDirection(DcMotorSimple.Direction motorDirection) {
        launcherDirection = motorDirection;
        return this;
    }

    public Launcher launcherUseBrakeMode(boolean useBrakeMode) {
        if (useBrakeMode)
            laucherZeroPowerBehavior = BRAKE;
        else
            laucherZeroPowerBehavior = FLOAT;
        return this;
    }

    public Launcher feederName(String feederName) {
        this.feederName = feederName;
        return this;
    }

    public Launcher feederDirection(DcMotorSimple.Direction motorDirection) {
        feederDirection = motorDirection;
        return this;
    }

    public void setMotorDirection(DcMotorSimple.Direction motorDirection) {
        launcher.setDirection(motorDirection);
    }

    public DcMotorSimple.Direction getMotorDirection() {
        return launcherDirection;
    }

    public void setFeederDirection(DcMotorSimple.Direction motorDirection) {
        feederDirection = motorDirection;
        feeder.setDirection(feederDirection);
    }

    public DcMotorSimple.Direction getFeederDirection() {
        return feederDirection;
    }

    public void setLauncherOff() {
        launcher.setVelocity(0.0);
        feeder.setPower(0.0);
        launchState = LaunchState.IDLE;
    }

    public void launch(boolean launch) {
        switch (launchState) {
            case IDLE:
                if (launch) {
                    launcher.setVelocity(targetVelocity);
                    launchState = LaunchState.SPIN_UP;
                }
                break;

            case SPIN_UP:
                if (!launch) {
                    launcher.setVelocity(0.0);
                    launchState = LaunchState.IDLE;
                }
                else if (launcher.getVelocity() >= minVelocity) {
                    feeder.setPower(1.0);
                    launchState = LaunchState.LAUNCH;
                }
                break;

            case LAUNCH:
                if (!launch) {
                    launcher.setVelocity(0.0);
                    feeder.setPower(0.0);
                    launchState = LaunchState.IDLE;
                }
                break;
        }
    }

    public String getState() {
        return launchState.toString();
    }

    public double getLauncherVelocity() {
        return launcher.getVelocity();
    }

    public void setLauncherVelocity(double target, double min) {
        targetVelocity = target;
        minVelocity = min;
    }
}