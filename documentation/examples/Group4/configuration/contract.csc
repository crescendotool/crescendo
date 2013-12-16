-- shared design parameters 
--sdp real AToDResolutionBits;
--sdp real AToDNoiseBits;

sdp real encoder_resolution;
sdp real linefollow_lateral_offset;
sdp real linefollow_longitudinal_offset;

sdp array initial_Position[2];
sdp real initial_Angle;
 
-- inputs
monitored real lineSensorLeftShared;
monitored real lineSensorCenterShared;
monitored real lineSensorRightShared;

monitored real positionSensorLeftShared;
monitored real positionSensorRightShared;

-- outputs
controlled real motorControlSignalLeftShared;
controlled real motorControlSignalRightShared;