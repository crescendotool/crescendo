-- Shared Design Parameters 
sdp real pidGain;
sdp real sdpSlip;

-- Monitored variables
monitored real poleAngle;
monitored real rmsVelocityError;
--monitored real velHigh;

-- Controlled variables
controlled real pwmSettingL;
controlled real pwmSettingR;
controlled real safetySwitchL;
controlled real safetySwitchR;

-- display variables
controlled real controlModeL;
controlled real monitorModeL; 
controlled real controlModeR;
controlled real monitorModeR;
controlled real safetyKey;
controlled real gyroError;

-- Events
--event velGT;
