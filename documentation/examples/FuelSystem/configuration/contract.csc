-- monitored variables
monitored real LFT_lvl; -- := 0.0;
monitored real LMT_lvl; -- := 0.0;
monitored real TT_lvl; -- := 0.0;

-- controlled variables
controlled bool LMT2LFT_valve; -- := false;
controlled bool LMT2TT_valve; -- := false;
controlled bool TT2LFT_valve; -- := false;

-- events
event enter_cruise;
event enter_pre_landing;