-- shared design parameters
sdp real P_k;
sdp real Voltage_Power;

--sdp real voltage_power;

-- Monitored variables
monitored real wheelRotations;
monitored real ImuOrientation;
monitored real steerRotations;

-- Controlled variables
controlled real drivingControl;
controlled real steeringControl;
