//import com.acmerobotics.dashboard.FtcDashboard;
//import com.acmerobotics.dashboard.config.Config;
//import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

//@Config
@Disabled
@TeleOp(name="FtcDashboardTest", group="Linear")
public class FtcDashboardTest extends LinearOpMode {
    Integer peebis;

    @Override
    public void runOpMode() throws InterruptedException {
        peebis = 0;

        waitForStart();
        while (opModeIsActive()) {
            peebis += 1;
//            telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
            telemetry.addData("peebis:", peebis);
            telemetry.update();
        }
    }
}
