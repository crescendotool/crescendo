-- inputs
monitored real sensorLeft;
monitored real sensorRight;
monitored real sensorFront;

monitored real wheelCountLeft;
monitored real wheelCountRight;

-- outputs
controlled array motorVoltages[2];

--parameters:
sdp	real encoder_resolution;
sdp	real linefollow_lateral_offset; --//1cm offset in local_x direction
sdp	real linefollow_longitudinal_offset; --//6.65cm offset from c.o.m. in local_y direction
sdp real r_wheel;

	--// Robot initialisation

--sdp array initial_Position[2]; --// position, x,y in metres relative to the centre of the map
--sdp	real initial_Angle;	--// angle in radians, 0 = north (y+ve) and +ve is clocwise when looking down
sdp	real routeIndex;