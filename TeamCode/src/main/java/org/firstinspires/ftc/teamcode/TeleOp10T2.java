/* Copyright (c) 2017 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode;

//import com.qualcomm.ftccommon.configuration.ScannedDevices;
//import com.qualcomm.robotcore.eventloop.opmode.Disabled;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.actuators.DcMotorControl;
import org.firstinspires.ftc.teamcode.actuators.DriveTrain;
import org.firstinspires.ftc.teamcode.actuators.ServoControl;
import org.firstinspires.ftc.teamcode.databases.GamepadSpace;
import org.firstinspires.ftc.teamcode.databases.Statics;

//import com.qualcomm.robotcore.hardware.DcMotor;
//import com.qualcomm.robotcore.util.Range;

/**
 * This file contains an minimal example of a Linear "OpMode". An OpMode is a 'program' that runs in either
 * the autonomous or the teleop period of an FTC match. The names of OpModes appear on the menu
 * of the FTC Driver Station. When an selection is made from the menu, the corresponding OpMode
 * class is instantiated on the Robot Controller and executed.
 *
 * This particular OpMode just executes a basic Tank Drive Teleop for a two wheeled robot
 * It includes all the skeletal structure that all linear OpModes contain.
 *
 * Use Android Studios to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list
 */


@TeleOp(name="SOPH_tankDrive_monoStick", group="Sophomore")
final public class TeleOp10T2 extends LinearOpMode {

    //Initialize objects
    private     DriveTrain      mWheel;

    //private     ServoControl    jArm;
    //private     Servo           jArmObj;
    private     ServoControl    GGrabberL;
    private     ServoControl    GGrabberR;
    private     Servo           GGrabberLObj;
    private     Servo           GGrabberRObj;
    private DcMotorControl      GLift;
    private     DcMotor         GLiftObj;

    final private GamepadSpace previous = new GamepadSpace();

    // Declare OpMode members.
    final private ElapsedTime runtime = new ElapsedTime();


    private void collectGPStat() {
        //previous.stat.Triangle   = gamepad1.y != previous.Triangle;
        previous.stat.LB         = gamepad1.left_bumper != previous.LB;
        previous.stat.JRightX    = gamepad1.right_stick_x != previous.JRightX;
        previous.stat.JRightY    = gamepad1.right_stick_y != previous.JRightY;
        previous.stat.LT         = gamepad1.left_trigger != 0;
        previous.stat.RT         = gamepad1.right_trigger!= 0;
        previous.stat.Circle     = gamepad1.b != previous.Circle;
        previous.stat.DPadUp     = gamepad1.dpad_up != previous.DPadUp;
        previous.stat.DPadDown   = gamepad1.dpad_down != previous.DPadDown;
    }

    private void saveGPData() {

       // previous.Triangle  = gamepad1.y;
        previous.LB        = gamepad1.left_bumper;
        previous.JRightY   = gamepad1.right_stick_y;
        previous.JRightX   = gamepad1.right_stick_x;
        previous.LT        = gamepad1.left_trigger;
        previous.RT        = gamepad1.right_trigger;
        previous.Circle    = gamepad1.b;
        previous.DPadUp    = gamepad1.dpad_up;
        previous.DPadDown  = gamepad1.dpad_down;

    }

    private void initialize(){
        DcMotor BL = hardwareMap.get(DcMotor.class, Statics.SOPH_RL_WHEEL);
        DcMotor BR = hardwareMap.get(DcMotor.class, Statics.SOPH_RR_WHEEL);
        mWheel = new DriveTrain(BL,BR);

        //jArmObj = hardwareMap.get(Servo.class, Statics.Sophomore.Servos.jewel);
        //jArm = new ServoControl(jArmObj, true, 0.13, 0.7);

        //Glyph Grabbers
        GGrabberLObj = hardwareMap.get(Servo.class, Statics.SOPH_LEFT_GLYPH_GRABBER);
        GGrabberRObj = hardwareMap.get(Servo.class, Statics.SOPH_RIGHT_GLYPH_GRABBER);

        GGrabberL = new ServoControl(GGrabberLObj, false, -1, 1);
        GGrabberR = new ServoControl(GGrabberRObj,true,-1,1);
        GLiftObj = hardwareMap.get(DcMotor.class, Statics.GLYPH_LIFT);
        GLift = new DcMotorControl(GLiftObj,false);
    }

    @Override
    public void runOpMode() {

        boolean toShowSecondPage = false;
        boolean toCloseGrabbers = true;
        double  globalSpeed;

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        initialize();
        waitForStart(); // Wait for the game to start (driver presses PLAY)
        runtime.reset();


        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {

            collectGPStat();

            //Toggle Snipping Mode
            if (previous.stat.LB) {
                if (gamepad1.left_bumper) globalSpeed = 0.6;
                else                      globalSpeed = 1.0;

                //Apply Speed
                mWheel.updateSpeedLimit(globalSpeed);
                GGrabberL.updateSpeedLimit(globalSpeed);
                GGrabberR.updateSpeedLimit(globalSpeed);
                GLift.updateSpeedLimit(globalSpeed);

            }


            //Right joystick for driving
            if (previous.stat.JRightY || previous.stat.JRightX) {
                mWheel.tankDrive(-gamepad1.right_stick_y, gamepad1.right_stick_x);
            }


            //Jewel Arm (Currently Disabled)
            //if (previous.stat.Triangle) jArm.moveJewelArm(jArmObj);

            //Glyph Grabber Inward
            if (previous.stat.LT) {
                GGrabberL.moveGlyphGrabber(GGrabberLObj, true);
                GGrabberR.moveGlyphGrabber(GGrabberRObj, true);
            }
            //Glyph Grabber Outward
            else if (previous.stat.RT) {
                GGrabberL.moveGlyphGrabber(GGrabberLObj, false);
                GGrabberR.moveGlyphGrabber(GGrabberRObj, false);
            }

            if (previous.stat.Circle && gamepad1.b) { //Toggle Grabbers
                toCloseGrabbers = !toCloseGrabbers;
                if(!toCloseGrabbers) {GGrabberLObj.setPosition(0.6);GGrabberRObj.setPosition(0.6);}
                else {GGrabberLObj.setPosition(0.35);GGrabberRObj.setPosition(0.35);}
            }

            if (previous.stat.DPadUp || previous.stat.DPadDown) {
                GLift.moveLift(GLiftObj, gamepad1.dpad_up, gamepad1.dpad_down);
            }
            //Save Data for next loop
            saveGPData();

            //Start putting information on the Driver Station
            telemetry.addData("Status           ", "Run Time: " + runtime.toString());// Show the elapsed game time and wheel power.

            if (Statics.SOPH_VISUALIZING) {
                    telemetry.addData("RL encoder: ", "");
                    telemetry.addData("RR encoder: ", "");
                    //telemetry.addData("Jewel Arm:  ", jArm.servoPos);
                    telemetry.addData("RL Wheel:        ", mWheel.getSpeed(1));
                    telemetry.addData("RR Wheel:        ", mWheel.getSpeed(0));
                    telemetry.addData("GGrabbers:       ", GGrabberL.getPos());
                    telemetry.addData("Lift Encoder:    ", GLiftObj.getCurrentPosition());
            }
            telemetry.update();
        }
    }
}