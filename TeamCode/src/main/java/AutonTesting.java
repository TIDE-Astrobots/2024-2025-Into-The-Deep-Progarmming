import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import HelpfulFunctions.MotorFunctions;

import java.util.ArrayList;
import java.util.List;

@Config
@Autonomous(name = "AutonTesting V1.1")
public class AutonTesting extends LinearOpMode {
    //region: Creating Variables
    //these variables correspond to servos and motors. They are displayed in order of distance to Control Hub.
    private DcMotor WheelMotorLeftFront;
    private DcMotor WheelMotorLeftBack;
    private DcMotor WheelMotorRightBack;
    private DcMotor WheelMotorRightFront;
    private DcMotor chainLeft;
    private DcMotor chainRight;
    private DcMotor extendoLeft;
    private DcMotor extendoRight;
    private DcMotor[] WheelMotors;
    private float ticksPerRevolution;
    private float wheelCircumference;
    private float wheelSideLength;
    private float dist;
    private float rot;
    private int task;
    private int target;
    //endregion

    @Override
    public void runOpMode() throws InterruptedException {
        //region: Initializing Variables
        //These variables do NOT correspond to a physical object; they are entirely digital and for coding purposes.
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        float speedMultipler = 0.4f;
        ticksPerRevolution = 537.7f;
        wheelCircumference = 11.8737f;
        wheelSideLength = 1.486f;
        dist = -36f;
        rot = 180f;
        task = 0;
        target = Integer.MAX_VALUE;

        //This section maps the variables to their corresponding motors/servos
        WheelMotorLeftFront = HelpfulFunctions.MotorFunctions.initializeMotor("WheelMotorLeftFront", hardwareMap);
        WheelMotorRightFront = HelpfulFunctions.MotorFunctions.initializeMotor("WheelMotorRightFront", hardwareMap);
        WheelMotorLeftBack = HelpfulFunctions.MotorFunctions.initializeMotor("WheelMotorLeftBack", hardwareMap);
        WheelMotorRightBack = HelpfulFunctions.MotorFunctions.initializeMotor("WheelMotorRightBack", hardwareMap);
        WheelMotorRightFront.setDirection(DcMotorSimple.Direction.REVERSE);
        WheelMotorRightBack.setDirection(DcMotorSimple.Direction.REVERSE);
        //This section creates an array of motors that will make some later code easier
        WheelMotors = new DcMotor[4];
        WheelMotors[0] = WheelMotorLeftFront;
        WheelMotors[1] = WheelMotorRightFront;
        WheelMotors[2] = WheelMotorLeftBack;
        WheelMotors[3] = WheelMotorRightBack;
        WheelMotors[0].setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        WheelMotors[1].setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        WheelMotors[2].setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        WheelMotors[3].setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        WheelMotors[0].setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        WheelMotors[1].setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        WheelMotors[2].setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        WheelMotors[3].setMode(DcMotor.RunMode.RUN_USING_ENCODER);


        //This section initializes the motors that control the extension arms and sets their settings
        extendoLeft = hardwareMap.dcMotor.get("extendoLeft");
        extendoRight = hardwareMap.dcMotor.get("extendoRight");
        extendoRight.setDirection(DcMotorSimple.Direction.REVERSE);
        extendoLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        extendoRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        //endregion

        //Wait for the user to press start
        waitForStart();
        //called continuously while OpMode is active
        boolean runOnce = true;
        while(opModeIsActive()) {
            /*
            Measurements:
            Circumference = 3.780"
            float ticksPerRevolution = ((((1+(46/17))) * (1+(46/11))) * 28);
            ticksPerRevolution = 537.7
             */
            //moveRobotInDirection("right", 0.25f);
            if(task == 0 && runOnce) {
                runOnce = false;
                moveDistanceInInches(WheelMotors, 24);
            }
            if(task == 1 && runOnce) {
                runOnce = false;
                rotateInDegrees(180);
            }
            if(task == 2 && runOnce) {
                runOnce = false;
                moveDistanceInInchesRight(24);
            }

            if(WheelMotorLeftFront.getCurrentPosition() == target) {
                runOnce = true;
                task += 1;
                target = 0;
            }
            telemetry.addData("Task #", task);
            telemetry.addData("Wheel Position (Ticks) ", WheelMotorLeftFront.getCurrentPosition());
            telemetry.addData("Intended Position (Ticks) ", target);
//            telemetry.addData("Wheel Position (Inches) ",(WheelMotorLeftFront.getCurrentPosition()) / ticksPerRevolution * wheelSideLength);
//            telemetry.addData("Intended position (Inches) ", dist);

//            telemetry.addData("Wheel Position (Ticks) ", WheelMotorLeftFront.getCurrentPosition());
//            telemetry.addData("Intended Position (Ticks) ", dist/wheelSideLength * ticksPerRevolution);
//            telemetry.addData("Wheel Position (Inches) ",(WheelMotorLeftFront.getCurrentPosition()) / ticksPerRevolution * wheelSideLength);
//            telemetry.addData("Intended position (Inches) ", dist);
//            telemetry.addData("Wheel Position (Ticks) ", -WheelMotorLeftFront.getCurrentPosition());
//            telemetry.addData("Intended Position (Ticks) ", 36/wheelCircumference * ticksPerRevolution);
//            telemetry.addData("Wheel Position (Inches) ",(WheelMotorLeftFront.getCurrentPosition()) / ticksPerRevolution * wheelCircumference);
//            telemetry.addData("Intended position (Inches) ", 18);
            telemetry.update();
        }
    }
public void rotateInDegrees(float degrees)
{
    int magnitude = (int) Math.round(4600 * Math.sin((Math.PI/180) * degrees/6.325));
    WheelMotorLeftFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    WheelMotorLeftFront.setTargetPosition(magnitude);
    WheelMotorLeftFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    target = magnitude;

    WheelMotorLeftBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    WheelMotorLeftBack.setTargetPosition(magnitude);
    WheelMotorLeftBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);

    WheelMotorRightFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    WheelMotorRightFront.setTargetPosition(-magnitude);
    WheelMotorRightFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);

    WheelMotorRightBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    WheelMotorRightBack.setTargetPosition(-magnitude);
    WheelMotorRightBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    for(DcMotor motor : WheelMotors) {
        motor.setPower(0.25);
    }
}
public void moveDistanceInInchesRight(float distance) {
        int magnitude = Math.round((distance/wheelSideLength) * (ticksPerRevolution * (wheelSideLength/wheelCircumference)));
        WheelMotorLeftFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        WheelMotorLeftFront.setTargetPosition(magnitude);
        WheelMotorLeftFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        target = magnitude;

        WheelMotorLeftBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        WheelMotorLeftBack.setTargetPosition(-magnitude);
        WheelMotorLeftBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        WheelMotorRightFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        WheelMotorRightFront.setTargetPosition(-magnitude);
        WheelMotorRightFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        WheelMotorRightBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        WheelMotorRightBack.setTargetPosition(magnitude);
        WheelMotorRightBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        for(DcMotor motor : WheelMotors) {
            motor.setPower(0.25);
        }
}
    public void moveDistanceInInches(DcMotor[] motors, float distance) {
        telemetry.addData("in the function: ", "confirmed");
        for(DcMotor motor : motors) {
            motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            motor.setTargetPosition(-Math.round((distance/wheelCircumference) * ticksPerRevolution));
            motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        }
        telemetry.addData("Motors set target position: ", "confirmed");
        target = -Math.round((distance/wheelCircumference) * ticksPerRevolution);
        telemetry.addData("Target position set: ", target);
        for(DcMotor motor: motors) {
            motor.setPower(0.5);
        }
    }

    public void moveRobotInDirection(String direction, float speedMultipler) {
        if (direction == "forward") {
            WheelMotorLeftFront.setPower(-1 * speedMultipler);
            WheelMotorRightFront.setPower(1 * speedMultipler);
            WheelMotorLeftBack.setPower(-1 * speedMultipler);
            WheelMotorRightBack.setPower(1 * speedMultipler);
        } else if (direction == "backward") {
            WheelMotorLeftFront.setPower(1 * speedMultipler);
            WheelMotorRightFront.setPower(-1 * speedMultipler);
            WheelMotorLeftBack.setPower(1 * speedMultipler);
            WheelMotorRightBack.setPower(-1 * speedMultipler);
        } else if (direction == "left") {
            WheelMotorLeftFront.setPower(1 * speedMultipler);
            WheelMotorRightFront.setPower(1 * speedMultipler);
            WheelMotorLeftBack.setPower(1 * speedMultipler);
            WheelMotorRightBack.setPower(1 * speedMultipler);
        } else if (direction == "right") {
            WheelMotorLeftFront.setPower(-1 * speedMultipler);
            WheelMotorRightFront.setPower(-1 * speedMultipler);
            WheelMotorLeftBack.setPower(-1 * speedMultipler);
            WheelMotorRightBack.setPower(-1 * speedMultipler);
        }
    }

    public void stopRobot() {
        WheelMotorLeftFront.setPower(0);
        WheelMotorRightFront.setPower(0);
        WheelMotorLeftBack.setPower(0);
        WheelMotorRightBack.setPower(0);
    }
}