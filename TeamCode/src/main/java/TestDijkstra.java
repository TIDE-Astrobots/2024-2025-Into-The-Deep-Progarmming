import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import HelpfulFunctions.Dijkstra.*;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//
import java.io.PrintStream;
import java.util.Iterator;

@Config
@Autonomous(name="TestDijkstra")
public class TestDijkstra extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        Node OneA = new Node("1-1");
        Node TwoA = new Node("2-1");
        Node ThreeA = new Node("3-1");
        Node FourA = new Node("4-1");
        Node FiveA = new Node("5-1");
        Node SixA = new Node("6-1");
        Node OneB = new Node("1-2");
        Node TwoB = new Node("2-2");
        Node ThreeB = new Node("3-2");
        Node FourB = new Node("4-2");
        Node FiveB = new Node("5-2");
        Node SixB = new Node("6-2");
        Node OneC = new Node("1-3");
        Node TwoC = new Node("2-3");
        Node FiveC = new Node("5-3");
        Node SixC = new Node("6-3");
        Node OneD = new Node("1-4");
        Node TwoD = new Node("2-4");
        Node FiveD = new Node("5-4");
        Node SixD = new Node("6-4");
        Node OneE = new Node("1-5");
        Node TwoE = new Node("2-5");
        Node FiveE = new Node("5-5");
        Node SixE = new Node("6-5");
        Node OneF = new Node("1-6");
        Node TwoF = new Node("2-6");
        Node ThreeF = new Node("3-6");
        Node FourF = new Node("4-6");
        Node FiveF = new Node("5-6");
        Node SixF = new Node("6-6");
        OneA.addDestination(OneB, 24);
        OneA.addDestination(TwoA, 24);
        OneB.addDestination(OneA, 24);
        OneB.addDestination(OneC, 24);
        OneB.addDestination(TwoB, 24);
        OneC.addDestination(OneC, 24);
        OneC.addDestination(OneD, 24);
        OneD.addDestination(OneC, 24);
        OneD.addDestination(OneE, 24);
        OneE.addDestination(OneD, 24);
        OneE.addDestination(OneF, 24);
        OneF.addDestination(OneE, 24);
        OneF.addDestination(TwoF, 24);
        TwoA.addDestination(OneA, 24);
        TwoA.addDestination(TwoB, 24);
        TwoA.addDestination(ThreeA, 24);
        TwoB.addDestination(OneB, 24);
        TwoB.addDestination(TwoC, 24);
        TwoB.addDestination(ThreeB, 24);
        TwoB.addDestination(TwoA, 24);
        TwoC.addDestination(TwoB, 24);
        TwoC.addDestination(TwoD, 24);
        TwoC.addDestination(OneC, 24);
        TwoD.addDestination(TwoE, 24);
        TwoD.addDestination(TwoC, 24);
        TwoD.addDestination(OneD, 24);
        TwoE.addDestination(TwoF, 24);
        TwoF.addDestination(ThreeF, 24);
        TwoF.addDestination(OneF, 24);
        ThreeA.addDestination(ThreeB, 24);
        ThreeA.addDestination(FourA, 24);
        ThreeA.addDestination(TwoA, 24);
        ThreeB.addDestination(FourB, 24);
        ThreeB.addDestination(TwoB, 24);
        ThreeF.addDestination(FourF, 24);
        ThreeF.addDestination(TwoF, 24);
        FourA.addDestination(FourB, 24);
        FourA.addDestination(FiveA, 24);
        FourA.addDestination(ThreeA, 24);
        FourB.addDestination(FiveB, 24);
        FourB.addDestination(ThreeB, 24);
        FourB.addDestination(FourA, 24);
        FourF.addDestination(FiveF, 24);
        FourF.addDestination(ThreeF, 24);
        FiveA.addDestination(FiveB, 24);
        FiveA.addDestination(SixA, 24);
        FiveA.addDestination(FourA, 24);
        FiveB.addDestination(FiveC, 24);
        FiveB.addDestination(SixB, 24);
        FiveB.addDestination(FourB, 24);
        FiveB.addDestination(FiveA, 24);
        FiveC.addDestination(FiveD, 24);
        FiveC.addDestination(SixC, 24);
        FiveC.addDestination(FiveB, 24);
        FiveD.addDestination(FiveE, 24);
        FiveD.addDestination(SixD, 24);
        FiveD.addDestination(FiveC, 24);
        FiveE.addDestination(FiveD, 24);
        FiveE.addDestination(FiveF, 24);
        FiveE.addDestination(SixE, 24);
        FiveF.addDestination(SixF, 24);
        FiveF.addDestination(FourF, 24);
        FiveF.addDestination(FiveE, 24);
        SixA.addDestination(FiveA, 24);
        SixA.addDestination(SixB, 24);
        SixB.addDestination(FiveB, 24);
        SixB.addDestination(SixA, 24);
        SixB.addDestination(SixC, 24);
        SixC.addDestination(FiveC, 24);
        SixC.addDestination(SixB, 24);
        SixC.addDestination(SixD, 24);
        SixD.addDestination(FiveD, 24);
        SixD.addDestination(SixC, 24);
        SixD.addDestination(SixE, 24);
        SixE.addDestination(FiveE, 24);
        SixE.addDestination(SixD, 24);
        SixE.addDestination(SixF, 24);
        SixF.addDestination(FiveF, 24);
        SixF.addDestination(SixE, 24);
        Graph graph = new Graph();
        graph.addNode(OneA);
        graph.addNode(TwoA);
        graph.addNode(ThreeA);
        graph.addNode(FourA);
        graph.addNode(FiveA);
        graph.addNode(SixA);
        graph.addNode(OneB);
        graph.addNode(TwoB);
        graph.addNode(ThreeB);
        graph.addNode(FourB);
        graph.addNode(FiveB);
        graph.addNode(SixB);
        graph.addNode(OneC);
        graph.addNode(TwoC);
        graph.addNode(FiveC);
        graph.addNode(SixC);
        graph.addNode(OneD);
        graph.addNode(TwoD);
        graph.addNode(FiveD);
        graph.addNode(SixD);
        graph.addNode(OneE);
        graph.addNode(TwoE);
        graph.addNode(FiveE);
        graph.addNode(SixE);
        graph.addNode(OneF);
        graph.addNode(TwoF);
        graph.addNode(ThreeF);
        graph.addNode(FourF);
        graph.addNode(FiveF);
        graph.addNode(SixF);

        Graph fromA = Dijkstra.calculateShortestPathFromSource(graph, OneA);
        waitForStart();
        while(opModeIsActive()) {
            for(Node node : fromA.getNodes()) {
                String str = " Path to " + node.getName() + " weighs " + node.getDistance() + "\nPath is: ";
                for(Node innerNode : node.getShortestPath()) {
                    str = str + innerNode.getName() + " ";
                }
                telemetry.addData(OneA.getName(), str);

            }

            telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
            telemetry.update();
        }
    }
}
