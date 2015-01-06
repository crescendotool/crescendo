-- shared design parameters
sdp real wheel_radius;
sdp real encoder_resolution;
sdp real line_follow_x; 
sdp real line_follow_y;
sdp array initial_position[3];
sdp real fast_wheel_speed;
sdp real slow_wheel_ratio;

-- encoders
monitored real encoder_left;
monitored real encoder_right;

-- line-following sensors
monitored array lf[2];

-- line-following sensor fail flags
monitored array lf_fail[2];

-- servos
controlled real servo_left;
controlled real servo_right;

monitored real energy_used;