import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import HelpfulFunctions.MotorFunctions;
import HelpfulFunctions.Dijkstra.*;

import java.util.ArrayList;
import java.util.List;

@Config
@Autonomous(name = "Right Side Auton")
public class RightSide extends LinearOpMode {
    //region: Creating
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
    private Field gameField;
    private int taskNumber;
    private boolean runOnce;
    private DcMotor armPivot;
    private Servo clawServo;

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
        gameField = new Field("3-1");
        taskNumber = 0;
        runOnce = true;
        clawServo = hardwareMap.get(Servo.class, "clawServo");


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
        armPivot = hardwareMap.dcMotor.get("armPivot");
        //endregion

        //Wait for the user to press start
        waitForStart();
        //called continuously while OpMode is active

        while(opModeIsActive()) {
            /*
            Measurements:
            Circumference = 3.780"
            float ticksPerRevolution = ((((1+(46/17))) * (1+(46/11))) * 28);
            ticksPerRevolution = 537.7
             */

            List<List<String>> steps = gameField.getInstructionsList("5-1", "6-2");
            telemetry.addData("STEPS", steps);
            telemetry.update();
            while(true) {
                if(runOnce) {
                    runOnce = false;
                    List<String> taskList;
                    try {
                        taskList = steps.get(taskNumber);
                    }
                    catch(Exception e) {
                        break;
                    }
                    moveDirectionInInches(taskList.get(0), 24);
                    target = WheelMotorLeftFront.getTargetPosition();
                }

                if(WheelMotorLeftFront.getCurrentPosition() == target) {
                    runOnce = true;
                    taskNumber += 1;
                    target = Integer.MAX_VALUE;
                }
            }

            //ROTATE 45 degrees counter-clockwise
            resetTask();
            while(true) {
                if(runOnce) {
                    runOnce = false;
                    rotateInDegrees(-45);
                }

                if(WheelMotorLeftFront.getCurrentPosition() == WheelMotorLeftFront.getTargetPosition()) {
                    break;
                }
            }
            //RAISE ARM
            resetTask();
            while(true) {
                if(runOnce) {
                    runOnce = false;
                    extendoLeft.setTargetPosition(-100);
                    extendoRight.setTargetPosition(-100);
                    extendoLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    extendoRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    extendoLeft.setPower(1);
                    extendoRight.setPower(1);
                }
                telemetry.addData("currpos:", extendoLeft.getCurrentPosition());
                telemetry.update();
                if(extendoLeft.getTargetPosition() > extendoLeft.getCurrentPosition()) {
                    extendoLeft.setPower(0);
                    extendoRight.setPower(0);
                    break;
                }
            }
            //PIVOT ARM
            resetTask();
            while(true) {
                if(runOnce) {
                    runOnce = false;
                    armPivot.setTargetPosition(120);
                    armPivot.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    armPivot.setPower(0.75);
                }

                if(armPivot.getCurrentPosition() == armPivot.getTargetPosition()) {
                    break;
                }
            }
            //RELEASE THINGY
            resetTask();
            if(runOnce) {
                runOnce = false;
                clawServo.setPosition(0.5);
                Thread.currentThread().wait(1000);
            }

            //TIGHTEN THINGY?
            //LOWER ARM

            //ROTATE 45 DEGREES CLOCKWISE
            //GO DOWN TO 5-3, PICK UP PIECE DIRECTLY IN FRONT
            //GO BACK TO 6-2, ROTATE 45 DEGREES COUNTER CLOCKWISE
            //RAISE ARM
            //RELEASE THINGY
            //TIGHTEN THINGY
            //LOWER ARM

            resetTask();
            gameField = new Field("5-2");
            steps = gameField.getInstructionsList("5-2", "6-2");

            telemetry.addData("STEPS", steps);
            telemetry.update();
            while(true) {
                if(runOnce) {
                    runOnce = false;
                    List<String> taskList;
                    try {
                        taskList = steps.get(taskNumber);
                    }
                    catch(Exception e) {
                        break;
                    }
                    moveDirectionInInches(taskList.get(0), 24);
                    target = WheelMotorLeftFront.getTargetPosition();
                }

                if(WheelMotorLeftFront.getCurrentPosition() == target) {
                    runOnce = true;
                    taskNumber += 1;
                    target = Integer.MAX_VALUE;
                }
            }


            resetTask();
            gameField = new Field("6-2");
            steps = gameField.getInstructionsList("6-2", "2-2");
            telemetry.addData("STEPS", steps);
            telemetry.update();
            while(true) {
                if(runOnce) {
                    runOnce = false;
                    List<String> taskList;
                    try {
                        taskList = steps.get(taskNumber);
                    }
                    catch(Exception e) {
                        break;
                    }
                    moveDirectionInInches(taskList.get(0), 24);
                    target = WheelMotorLeftFront.getTargetPosition();
                }

                if(WheelMotorLeftFront.getCurrentPosition() == target) {
                    runOnce = true;
                    taskNumber += 1;
                    target = Integer.MAX_VALUE;
                }
            }

            resetTask();
            gameField = new Field("2-2");
            steps = gameField.getInstructionsList("2-2", "6-2");
            telemetry.addData("STEPS", steps);
            telemetry.update();
            while(true) {
                if(runOnce) {
                    runOnce = false;
                    List<String> taskList;
                    try {
                        taskList = steps.get(taskNumber);
                    }
                    catch(Exception e) {
                        break;
                    }
                    moveDirectionInInches(taskList.get(0), 24);
                    target = WheelMotorLeftFront.getTargetPosition();
                }

                if(WheelMotorLeftFront.getCurrentPosition() == target) {
                    runOnce = true;
                    taskNumber += 1;
                    target = Integer.MAX_VALUE;
                }
            }


            resetTask();



            telemetry.update();
            stop();
            break;
            /*
            PSUEDOSTEPS
             */

//
//
//            if(task == 0 && runOnce) {
//                runOnce = false;
//                moveDirectionInInches("right", 24);
//            }
//            if(task == 1 && runOnce) {
//                runOnce = false;
//                moveDirectionInInches("left", 24);
//            }
//
//
//            if(WheelMotorLeftFront.getCurrentPosition() == target) {
//                runOnce = true;
//                task += 1;
//                target = 0;
//            }

        }
    }

    public void resetTask() {
        taskNumber = 0;
        target = Integer.MAX_VALUE;
        runOnce = true;
    }
    public void moveDirectionInInches(String direction, float distance) {
        if(direction == "right") {
            moveDistanceInInchesRight(distance);
        }
        if(direction == "left") {
            moveDistanceInInchesRight(-distance);
        }
        if(direction == "forward") {
            moveDistanceInInches(distance);
        }
        if(direction == "back") {
            moveDistanceInInches(-distance);
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
    public void moveDistanceInInches(float distance) {
        for(DcMotor motor : WheelMotors) {
            motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            motor.setTargetPosition(-Math.round((distance/wheelCircumference) * ticksPerRevolution));
            motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        }
        target = -Math.round((distance/wheelCircumference) * ticksPerRevolution);
        for(DcMotor motor: WheelMotors) {
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