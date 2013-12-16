-- Shared Design Parameters 

sdp real encoder_resolution;
sdp real linefollow_lateral_offset;
sdp real linefollow_longitudinal_offset;
sdp array initial_Position [2];
sdp real initial_Angle;

-- Monitored variables
monitored real rightLSValue;
monitored real leftLSValue;
monitored real leftEncoderValue; -- Number of revolutions!!!
monitored real rightEncoderValue;

-- Controlled variables
controlled real leftMotorDC;
controlled real rightMotorDC;

-- Events
-- event HIGH;

