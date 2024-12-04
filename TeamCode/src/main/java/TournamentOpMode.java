import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import java.io.File;
import java.io.IOException;

@TeleOp(name = "TournamentOpMode V1.0 [Updated 11/14/24]")
public class TournamentOpMode extends LinearOpMode

{
    //region: Creating Variables
    private DcMotor WheelMotorLeftFront;
    private DcMotor WheelMotorLeftBack;
    private DcMotor WheelMotorRightBack;
    private DcMotor WheelMotorRightFront;
    private DcMotor extendoLeft;
    private DcMotor extendoRight;
    private DcMotorEx armPivot;
    private Servo clawServo;
    private boolean hangingMode;
    private Integer armPivotTarget;
    //endregion

    //region: PID Test
    double integralSum = 0;
    //Gradually increase Kp until it is somewhat stable
    double Kp = 0.1;
    //Change after Kp until we get some good shit
    double Ki = 0;
    //Change after Kd until we get some even better shit
    double Kd = 0;
    double Kf = 0;
    ElapsedTime timer = new ElapsedTime();
    double lastError = 0;
    //endregion

    @Override
    public void runOpMode() throws InterruptedException {
        try {
            File myObj = new File("\"C:\\Users\\tidea_\\Downloads\\PIDTests\"");
            if (myObj.createNewFile()) {
                telemetry.addData("File creation status      ", "File created: " + myObj.getName());
            } else {
                telemetry.addData("File creation status      ", ("File already exists."));
            }
        } catch (IOException e) {
            telemetry.addData("File creation status      ", ("An error occurred."));
        }


        //region: Initialize Variables
        //These variables do NOT correspond to a physical object; they are entirely digital and for coding purposes.
        hangingMode = false;

        //This section maps the variables to their corresponding motors/servos.
        WheelMotorLeftFront = HelpfulFunctions.MotorFunctions.initializeMotor("WheelMotorLeftFront", hardwareMap);
        WheelMotorLeftBack = HelpfulFunctions.MotorFunctions.initializeMotor("WheelMotorLeftBack", hardwareMap);
        WheelMotorRightFront = HelpfulFunctions.MotorFunctions.initializeMotor("WheelMotorRightFront", hardwareMap);
        WheelMotorRightBack = HelpfulFunctions.MotorFunctions.initializeMotor("WheelMotorRightBack", hardwareMap);

        //This section sets the directions of motors which should move in reverse so the robot works.
        WheelMotorRightFront.setDirection(DcMotorSimple.Direction.REVERSE);
        WheelMotorRightBack.setDirection(DcMotorSimple.Direction.REVERSE);

        //This section initializes the motors that control the extension arms and sets their settings
        extendoLeft = hardwareMap.dcMotor.get("extendoLeft");
        extendoRight = hardwareMap.dcMotor.get("extendoRight");
        extendoRight.setDirection(DcMotorSimple.Direction.REVERSE);
        //Use BRAKE zero power behavior so that the motors do not allow the arms to move when no power is applied
        extendoLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        extendoRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

//        armPivot = HelpfulFunctions.MotorFunctions.initializeMotor("armPivot", hardwareMap);
        armPivot = hardwareMap.get(DcMotorEx.class,"armPivot");
        armPivot.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        armPivot.setDirection(DcMotorSimple.Direction.REVERSE);
        armPivot.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        armPivot.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
//        armPivot.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        armPivotTarget = 0;
        armPivot.setTargetPositionTolerance(8);

        //This section initializes the claw servo
        clawServo = hardwareMap.servo.get("clawServo");
        //endregion

        //region: Initialize the IMU for navigation
        // Retrieve the IMU from the hardware map
        IMU imu = hardwareMap.get(IMU.class, "imu");
        // Adjust the orientation parameters to match your robot
        IMU.Parameters parameters = new IMU.Parameters(new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.UP,
                RevHubOrientationOnRobot.UsbFacingDirection.FORWARD));
        // Without this, the REV Hub's orientation is assumed to be logo up / USB forward
        imu.initialize(parameters);
        //endregion

        //Wait for the user to hit start
        waitForStart();
        //Called continuously while OpMode is active
        while(opModeIsActive()) {

            //region: PID code
//            armPivot.
//            double power = PIDControl(1000, armPivot.getVelocity()) / 10;
            //endregion

            // This button choice was made so that it is hard to hit on accident.
            // This will reset the robot's navigation
            if (gamepad1.options) {
                imu.resetYaw();
            }

            //region: Move the wheels when the user moves the joystick
            double x = -gamepad1.left_stick_x; // Remember, Y stick value is reversed
            double y = gamepad1.left_stick_y;
            double rx = gamepad1.right_stick_x;

            double botHeading = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);

            // Rotate the movement direction counter to the bot's rotation
            double rotX = x * Math.cos(-botHeading) - y * Math.sin(-botHeading);
            double rotY = x * Math.sin(-botHeading) + y * Math.cos(-botHeading);

            rotX = rotX * 1.1;  // Counteract imperfect strafing

            // Denominator is the largest motor power (absolute value) or 1
            // This ensures all the powers maintain the same ratio,
            // but only if at least one is out of the range [-1, 1]
            double denominator = Math.max(Math.abs(rotY) + Math.abs(rotX) + Math.abs(rx), 1);
            double WheelMotorFrontLeftPower = (rotY + rotX + rx) / denominator;
            double WheelMotorBackLeftPower = (rotY - rotX + rx) / denominator;
            double WheelMotorFrontRightPower = (rotY - rotX - rx) / denominator;
            double WheelMotorBackRightPower = (rotY + rotX - rx) / denominator;

            //Allow the users to move at half speed if they are holding A
            if(gamepad1.a) {
                denominator = Math.max(Math.abs(rotY) + Math.abs(rotX) + Math.abs(rx), 1) * 2;
                WheelMotorFrontLeftPower = (rotY + rotX + rx) / denominator;
                WheelMotorBackLeftPower = (rotY - rotX + rx) / denominator;
                WheelMotorFrontRightPower = (rotY - rotX - rx) / denominator;
                WheelMotorBackRightPower = (rotY + rotX - rx) / denominator;
            }

            //Apply the power
            WheelMotorLeftFront.setPower(WheelMotorFrontLeftPower);
            WheelMotorLeftBack.setPower(WheelMotorBackLeftPower);
            WheelMotorRightFront.setPower(WheelMotorFrontRightPower);
            WheelMotorRightBack.setPower(WheelMotorBackRightPower);
            //endregion
            telemetry.addData("monkey nunts", "oo oo");

            //region: ArmPivot Controls

            if(gamepad2.b) {
                armPivotTarget = 135;
            }
            else if(gamepad2.y) {
                armPivotTarget = 70;
            }
            else if(gamepad2.x) {
                armPivotTarget = 20;
            }
            telemetry.addData("Target: ", armPivotTarget);
            if(armPivot.getCurrentPosition() > armPivotTarget + 6) {
                armPivot.setPower(-0.5);
            }
//            else if (armPivot.getCurrentPosition() > armPivotTarget - 6 && armPivot.getCurrentPosition() < armPivotTarget + 6) {
//                armPivot.setPower(0);
//            }
            else if(armPivot.getCurrentPosition() < armPivotTarget - 6) {
                armPivot.setPower(0.5);
            }
            else {
                armPivot.setPower(0);
            }
//            if(gamepad2.b) {
//                if(armPivot.getCurrentPosition() < 120) {
//                    armPivot.setPower(-1);
//                }
//                else {
//                    armPivot.setPower(1);
//                }
//                armPivot.setTargetPosition(120);
//                armPivot.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//            }
//            else if(gamepad2.y) {
//                if(armPivot.getCurrentPosition() > 80) {
//                    armPivot.setPower(-1);
//                }
//                else {
//                    armPivot.setPower(1);
//                }
//                armPivot.setTargetPosition(80);
//                armPivot.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//            }
//            else if(gamepad2.x) {
//                if(armPivot.getCurrentPosition() > 0) {
//                    armPivot.setPower(-1);
//                }
//                else {
//                    armPivot.setPower(1);
//                }
//                armPivot.setTargetPosition(20);
//                armPivot.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//            }
            telemetry.addData("ArmPivot Pos: ", armPivot.getCurrentPosition());
//            if(gamepad2.dpad_up) {
//                armPivot.setPower(1);
//            }
//            else if (gamepad2.dpad_down) {
//                armPivot.setPower(-1);
//            }
//            else if(gamepad2.a){
//                armPivot.setTargetPosition(armPivot.getCurrentPosition());
//                armPivot.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//                armPivot.setPower(1);
//            }
//            else {
//                armPivot.setPower(0);
//            }
            //endregion

            //region: Extendo arm controls
            if(gamepad2.dpad_right) {
                extendoRight.setPower(1);
                extendoLeft.setPower(1);
            }
            else if(gamepad2.dpad_left) {
                extendoLeft.setPower(-1);
                extendoRight.setPower(-1);
            }
            else if(gamepad2.a) {
                //Make the robot start or stop hanging by setting hangingMode to true or false
                if(hangingMode) {
                    hangingMode = false;
                }
                else {
                    hangingMode = true;
                }
            }
            else if(hangingMode) {
                //If the robot has entered hanging mode, keep it hanging!
                extendoLeft.setPower(1);
                extendoRight.setPower(1);
            }
            else {
                //If the robot is not hanging and nothing is being pressed, apply no power to the arm
                extendoLeft.setPower(0);
                extendoRight.setPower(0);
            }
            //endregion

            //region: Claw controls
            if(gamepad2.right_bumper) {
                clawServo.setPosition(1);
            }
            else if(gamepad2.left_bumper) {
                clawServo.setPosition(0);
            }
            //endregion

            telemetry.update();
        }
    }

    public double PIDControl(double reference, double state) {
        double error = reference - state;
        integralSum += error * timer.seconds();
        double derivative = (error - lastError) / timer.seconds();
        lastError = error;

        telemetry.addData("error: ", error);
        telemetry.addData("integralSum: ", integralSum);
        telemetry.addData("derivative", derivative);

        double output = (error * Kp) + (derivative * Kd) + (integralSum * Ki) + (reference * Kf);
        telemetry.addData("output", output);
        return output;

    }
//    public double maintainArmPivotPosition() {
//        float vertex = 100;
//        float difference = vertex - armPivot.getCurrentPosition();
//        telemetry.addData("diff:", difference);
//        if (difference==0) {
//            telemetry.addData("aaaaaaaaaa", "aaaa");
//            return 0;
//        }
//        double diffSquared = Math.pow(difference, 2);
//        double factor = Math.abs(diffSquared/100);
//        telemetry.addData("factor: ", factor);
//        return factor;
//    }
}