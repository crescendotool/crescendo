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
monitored real lf_left;
monitored real lf_right;

-- line-following sensor fail flags
monitored bool lf_left_fail_flag;
monitored bool lf_right_fail_flag;

-- servos
controlled real servo_left;
controlled real servo_right;