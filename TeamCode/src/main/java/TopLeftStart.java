//
//import com.acmerobotics.dashboard.FtcDashboard;
//import com.acmerobotics.dashboard.config.Config;
//import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import HelpfulFunctions.Dijkstra.*;

import java.util.ArrayList;
import java.util.List;

//@Config
@Disabled
@Autonomous(name = "TopLeftStart V2.0")
public class TopLeftStart extends LinearOpMode {
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
    private int target;
    private Field gameField;
    private int taskNumber;
    private boolean runOnce;
    //endregion

    private DcMotorEx armPivot;

    @Override
    public void runOpMode() throws InterruptedException {
        //region: Initializing Variables
        //These variables do NOT correspond to a physical object; they are entirely digital and for coding purposes.
//        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        float speedMultipler = 0.4f;
        ticksPerRevolution = 537.7f;
        wheelCircumference = 11.8737f;
        wheelSideLength = 1.486f;
        dist = -36f;
        rot = 180f;
        target = Integer.MAX_VALUE;
        gameField = new Field("3-1");
        taskNumber = 0;
        runOnce = true;
        armPivot = hardwareMap.get(DcMotorEx.class, "armPivot");


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

        while(opModeIsActive()) {
            /*
            Measurements:
            Circumference = 3.780"
            float ticksPerRevolution = ((((1+(46/17))) * (1+(46/11))) * 28);
            ticksPerRevolution = 537.7
            Left 6.5 inches, Down 10 inches
             */

            // Move toward high basket
            List<List<String>> steps = new ArrayList<>();//gameField.getInstructionsList("5-1", "6-2");
            List<String> step1 = new ArrayList<>();
            step1.add("forward");
            step1.add("24");
            steps.add(step1);
            List<String> step2;
            telemetry.addData("STEPS", steps);
            telemetry.update();
            while(true) {
                if (runOnce) {
                    runOnce = false;
                    List<String> task = new ArrayList<>();
                    try {
                        task = steps.get(taskNumber);
                    } catch (Exception e) {
                        break;
                    }
                    moveDirectionInInches(task.get(0), Integer.parseInt(task.get(1)));
                    target = WheelMotorLeftFront.getTargetPosition();
                }

                if (WheelMotorLeftFront.getCurrentPosition() == target) {
                    runOnce = true;
                    taskNumber += 1;
                    target = Integer.MAX_VALUE;
                }
            }
            resetTask();

            // Rotate toward high basket
            while (true) {
                if(runOnce) {
                    runOnce = false;
                    rotateInDegrees(45);
                    target = WheelMotorLeftFront.getTargetPosition();
                }

                if(WheelMotorLeftFront.getCurrentPosition() == target) {
                    break;
                }
            }

            resetTask();
            // put sample in high basket, may need to move backward slightly

            while(true) {
                if(runOnce) {
                    runOnce = false;
                    extendoLeft.setTargetPosition(3000);
                    extendoRight.setTargetPosition(3000);
                    extendoLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    extendoRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    extendoLeft.setPower(1);
                    extendoRight.setPower(1);
                    target = 3000;
                }

                if(extendoLeft.getCurrentPosition() == target) {
                    break;
                }
            }
            resetTask();

            while(true) {
                if(runOnce) {
                    runOnce = false;
                    armPivot.setTargetPosition(120);
                    armPivot.setPower(0.75);
                    target = 120;
                }

                if(armPivot.getCurrentPosition() == target) {
                    break;
                }
            }

            resetTask();

            while(true) {
                if(runOnce) {
                    runOnce = false;
                    armPivot.setTargetPosition(10);
                    extendoLeft.setTargetPosition(0);
                    extendoRight.setTargetPosition(0);
                }

                if(extendoLeft.getTargetPosition() == extendoLeft.getCurrentPosition() && armPivot.getCurrentPosition() == armPivot.getTargetPosition()) {
                    break;
                }
            }


            // turn toward samples
            while (true) {
                if(runOnce) {
                    runOnce = false;
                    rotateInDegrees(-135);
                    target = WheelMotorLeftFront.getTargetPosition();
                }

                if(WheelMotorLeftFront.getCurrentPosition() == target) {
                    break;
                }
            }
            resetTask();

            // align to pick up a sample
            steps = new ArrayList<>();//gameField.getInstructionsList("5-1", "6-2");
            step1 = new ArrayList<>();
            step1.add("back");
            step1.add("6.5");
            steps.add(step1);
            step2 = new ArrayList<>();
            step2.add("right");
            step2.add("13");
            steps.add(step2);
            telemetry.addData("STEPS", steps);
            telemetry.update();
            while(true) {
                if (runOnce) {
                    runOnce = false;
                    List<String> task = new ArrayList<>();
                    try {
                        task = steps.get(taskNumber);
                    } catch (Exception e) {
                        break;
                    }
                    moveDirectionInInches(task.get(0), Float.parseFloat(task.get(1)));
                    target = WheelMotorLeftFront.getTargetPosition();
                }

                if (WheelMotorLeftFront.getCurrentPosition() == target) {
                    runOnce = true;
                    taskNumber += 1;
                    target = Integer.MAX_VALUE;
                }
            }
            resetTask();

            // Pick up sample

            steps = new ArrayList<>();//gameField.getInstructionsList("5-1", "6-2");
            step1 = new ArrayList<>();
            step1.add("forward");
            step1.add("6.5");
            steps.add(step1);
            step2 = new ArrayList<>();
            step2.add("left");
            step2.add("13");
            steps.add(step2);
            telemetry.addData("STEPS", steps);
            telemetry.update();
            while(true) {
                if (runOnce) {
                    runOnce = false;
                    List<String> task = new ArrayList<>();
                    try {
                        task = steps.get(taskNumber);
                    } catch (Exception e) {
                        break;
                    }
                    moveDirectionInInches(task.get(0), Float.parseFloat(task.get(1)));
                    target = WheelMotorLeftFront.getTargetPosition();
                }

                if (WheelMotorLeftFront.getCurrentPosition() == target) {
                    runOnce = true;
                    taskNumber += 1;
                    target = Integer.MAX_VALUE;
                }
            }
            resetTask();

            while (true) {
                if(runOnce) {
                    runOnce = false;
                    rotateInDegrees(135);
                    target = WheelMotorLeftFront.getTargetPosition();
                }

                if(WheelMotorLeftFront.getCurrentPosition() == target) {
                    break;
                }
            }
            // put sample in basket, may need to move forward slightly
            resetTask();

            while (true) {
                if(runOnce) {
                    runOnce = false;
                    rotateInDegrees(-135);
                    target = WheelMotorLeftFront.getTargetPosition();
                }

                if(WheelMotorLeftFront.getCurrentPosition() == target) {
                    break;
                }
            }

            resetTask();
            steps = new ArrayList<>();//gameField.getInstructionsList("5-1", "6-2");
            step1 = new ArrayList<>();
            step1.add("back");
            step1.add("6.5");
            steps.add(step1);
            step2 = new ArrayList<>();
            step2.add("right");
            step2.add("1");
            steps.add(step2);
            telemetry.addData("STEPS", steps);
            telemetry.update();
            while(true) {
                if (runOnce) {
                    runOnce = false;
                    List<String> task = new ArrayList<>();
                    try {
                        task = steps.get(taskNumber);
                    } catch (Exception e) {
                        break;
                    }
                    moveDirectionInInches(task.get(0), Float.parseFloat(task.get(1)));
                    target = WheelMotorLeftFront.getTargetPosition();
                }

                if (WheelMotorLeftFront.getCurrentPosition() == target) {
                    runOnce = true;
                    taskNumber += 1;
                    target = Integer.MAX_VALUE;
                }
            }
            resetTask();

            // Pick up sample

            steps = new ArrayList<>();//gameField.getInstructionsList("5-1", "6-2");
            step1 = new ArrayList<>();
            step1.add("forward");
            step1.add("6.5");
            steps.add(step1);
            step2 = new ArrayList<>();
            step2.add("left");
            step2.add("1");
            steps.add(step2);
            telemetry.addData("STEPS", steps);
            telemetry.update();
            while(true) {
                if (runOnce) {
                    runOnce = false;
                    List<String> task = new ArrayList<>();
                    try {
                        task = steps.get(taskNumber);
                    } catch (Exception e) {
                        break;
                    }
                    moveDirectionInInches(task.get(0), Float.parseFloat(task.get(1)));
                    target = WheelMotorLeftFront.getTargetPosition();
                }

                if (WheelMotorLeftFront.getCurrentPosition() == target) {
                    runOnce = true;
                    taskNumber += 1;
                    target = Integer.MAX_VALUE;
                }
            }
            resetTask();

            while (true) {
                if(runOnce) {
                    runOnce = false;
                    rotateInDegrees(135);
                    target = WheelMotorLeftFront.getTargetPosition();
                }

                if(WheelMotorLeftFront.getCurrentPosition() == target) {
                    break;
                }
            }
            resetTask();
            // put sample in basket, may need to move forward slightly
            while (true) {
                if(runOnce) {
                    runOnce = false;
                    rotateInDegrees(-135);
                    target = WheelMotorLeftFront.getTargetPosition();
                }

                if(WheelMotorLeftFront.getCurrentPosition() == target) {
                    break;
                }
            }
            resetTask();
            steps = new ArrayList<>();//gameField.getInstructionsList("5-1", "6-2");
            step1 = new ArrayList<>();
            step1.add("forward");
            step1.add("36");
            steps.add(step1);
            step2 = new ArrayList<>();
            step2.add("right");
            step2.add("5.5");
            steps.add(step2);
            telemetry.addData("STEPS", steps);
            telemetry.update();
            while(true) {
                if (runOnce) {
                    runOnce = false;
                    List<String> task = new ArrayList<>();
                    try {
                        task = steps.get(taskNumber);
                    } catch (Exception e) {
                        break;
                    }
                    moveDirectionInInches(task.get(0), Float.parseFloat(task.get(1)));
                    target = WheelMotorLeftFront.getTargetPosition();
                }

                if (WheelMotorLeftFront.getCurrentPosition() == target) {
                    runOnce = true;
                    taskNumber += 1;
                    target = Integer.MAX_VALUE;
                }
            }
            resetTask();

            while (true) {
                if(runOnce) {
                    runOnce = false;
                    rotateInDegrees(90);
                    target = WheelMotorLeftFront.getTargetPosition();
                }

                if(WheelMotorLeftFront.getCurrentPosition() == target) {
                    break;
                }
            }
            resetTask();
            //Pick up sample, may not be in position after rotating
            while (true) {
                if(runOnce) {
                    runOnce = false;
                    rotateInDegrees(-90);
                    target = WheelMotorLeftFront.getTargetPosition();
                }

                if(WheelMotorLeftFront.getCurrentPosition() == target) {
                    break;
                }
            }
            resetTask();
            steps = new ArrayList<>();//gameField.getInstructionsList("5-1", "6-2");
            step1 = new ArrayList<>();
            step1.add("back");
            step1.add("36");
            steps.add(step1);
            step2 = new ArrayList<>();
            step2.add("left");
            step2.add("5.5");
            steps.add(step2);
            telemetry.addData("STEPS", steps);
            telemetry.update();
            while(true) {
                if (runOnce) {
                    runOnce = false;
                    List<String> task = new ArrayList<>();
                    try {
                        task = steps.get(taskNumber);
                    } catch (Exception e) {
                        break;
                    }
                    moveDirectionInInches(task.get(0), Float.parseFloat(task.get(1)));
                    target = WheelMotorLeftFront.getTargetPosition();
                }

                if (WheelMotorLeftFront.getCurrentPosition() == target) {
                    runOnce = true;
                    taskNumber += 1;
                    target = Integer.MAX_VALUE;
                }
            }
            resetTask();

            while (true) {
                if(runOnce) {
                    runOnce = false;
                    rotateInDegrees(135);
                    target = WheelMotorLeftFront.getTargetPosition();
                }

                if(WheelMotorLeftFront.getCurrentPosition() == target) {
                    break;
                }
            }
            resetTask();

            // put sample in basket, may need to move forward slightly

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
    public void rotateInDegrees(float degrees) {
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