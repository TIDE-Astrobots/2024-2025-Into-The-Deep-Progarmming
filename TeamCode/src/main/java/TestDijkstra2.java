//import com.acmerobotics.dashboard.FtcDashboard;
//import com.acmerobotics.dashboard.config.Config;
//import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import HelpfulFunctions.Dijkstra.*;

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
@Autonomous(name="TestDijkstra2")
public class TestDijkstra2 extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        Field field = new Field("3-1");
        waitForStart();
        while(opModeIsActive()) {
            telemetry.addData("Distance between 6-2 and 2-2: ", field.getPathDistance("3-1", "3-2"));
            telemetry.addData("Path from 6-2 and 2-2: ", field.getPathNodes("3-1", "3-2"));
            telemetry.addData("Path from 6-2 and 2-2 (readable): ", field.getPathNodeNames("3-1", "3-2"));
            telemetry.addData("Directions: ", field.getInstructionsString("3-1", "3-2"));
            telemetry.addData("Directions in robot form: ", field.getInstructionsList("3-1", "3-2"));

            telemetry.addData("Distance between 6-2 and 2-2: ", field.getPathDistance("3-2", "6-2"));
            telemetry.addData("Path from 6-2 and 2-2: ", field.getPathNodes("3-2", "6-2"));
            telemetry.addData("Path from 6-2 and 2-2 (readable): ", field.getPathNodeNames("3-2", "6-2"));
            telemetry.addData("Directions: ", field.getInstructionsString("3-2", "6-2"));
            telemetry.addData("Directions in robot form: ", field.getInstructionsList("3-2", "6-2"));

            telemetry.addData("Distance between 6-2 and 2-2: ", field.getPathDistance("6-2", "2-2"));
            telemetry.addData("Path from 6-2 and 2-2: ", field.getPathNodes("6-2", "2-2"));
            telemetry.addData("Path from 6-2 and 2-2 (readable): ", field.getPathNodeNames("6-2", "2-2"));
            telemetry.addData("Directions: ", field.getInstructionsString("6-2", "2-2"));
            telemetry.addData("Directions in robot form: ", field.getInstructionsList("6-2", "2-2"));


//            telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
            telemetry.update();
        }
    }
}
