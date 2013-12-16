-- shared design parameters 
sdp real encoder_resolution;
sdp real linefollow_lateral_offset;
sdp real linefollow_longitudinal_offset;
sdp array initial_Position[2];
sdp real initial_Angle;

-- Monitored variables (seen from the DE controller)
monitored real sensorVisionLeft;
monitored real sensorVisionRight;

monitored real sensorRotationSpeedLeft;
monitored real sensorRotationSpeedRight;

-- Controlled variables (seen from the DE controller)
controlled real actuatorWheelLeft;
controlled real actuatorWheelRight; 
