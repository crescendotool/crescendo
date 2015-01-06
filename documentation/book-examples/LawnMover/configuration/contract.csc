-- shared design parameters
sdp real V; --Vehicle speed
sdp real dist_b;
sdp real mu;
sdp real Look_ahead_dist;
sdp real Control_Parameters;
sdp array initial_Position[3]; --start Position

--sdp real look_ahead_dist; --look-ahead-distance
-- Monitored variables (seen from the DE controller)
monitored array GNSS_Position[4];
-- Controlled variables (seen from the DE controller)
controlled real steering_wheel_angle;
controlled real sc;
