//import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Servo;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

/*
TODO: Figure out how to know if the next position is in front, behind, to the left, or to the right
TODO: Figure out how to translate the amount of nodes that it should go in one direction to a distance
TODO: Have it set to a sequence so that it does all this without me having to worry about it
 */
//@Config
@Disabled
@Autonomous(name="Matias' Claw")
public class MatiasClaw extends LinearOpMode {
    private Servo rightServo;
    private Servo leftServo;
    @Override
    public void runOpMode() throws InterruptedException {
        leftServo = hardwareMap.servo.get("rightServo");
        rightServo = hardwareMap.servo.get("leftServo");
        waitForStart();
        while(opModeIsActive()) {
            if(gamepad1.dpad_right) {
                leftServo.setPosition(leftServo.getPosition() + 0.1);
                rightServo.setPosition(rightServo.getPosition());
            }
            if(gamepad1.dpad_left) {
                leftServo.setPosition(leftServo.getPosition() - 0.1);
                rightServo.setPosition(rightServo.getPosition() -0.1);
            }
        }
    }
}
