-- monitored variables
monitored real enc_motor;
monitored real enc_load;

-- controlled variables
controlled real pwm_amp;

-- shared design parameters
sdp real ENC_COUNTS;
sdp real USE_SCRIPT;
sdp real DE_ONLY;