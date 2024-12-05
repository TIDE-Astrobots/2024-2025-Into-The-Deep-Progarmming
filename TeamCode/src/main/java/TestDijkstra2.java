import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
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
@Config
@Autonomous(name="TestDijkstra2")
public class TestDijkstra2 extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        Field field = new Field();
        waitForStart();
        while(opModeIsActive()) {
            telemetry.addData("Distance between 2-2 and 6-5: ", field.getPathDistance("2-2", "6-5"));
            telemetry.addData("Path from 1-1 and 5-5: ", field.getPathNodes("2-2", "6-5"));
            telemetry.addData("Path from 1-1 and 5-5 (readable): ", field.getPathNodeNames("2-2", "6-5"));
            telemetry.addData("Directions: ", field.getInstructionsString("2-2", "6-5"));
            telemetry.addData("Directions in robot form: ", field.getInstructionsList("2-2", "6-5"));
            telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
            telemetry.update();
        }
    }
}
