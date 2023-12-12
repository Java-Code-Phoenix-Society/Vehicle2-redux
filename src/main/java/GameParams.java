import java.util.HashMap;

public class GameParams {
    public HashMap<String, String> paramMap;
    GameParams() {
        paramMap = new HashMap<>();
        paramMap.put("Bild_w", "1600");
        paramMap.put("Bild_h", "1200");
        paramMap.put("Bild", "Levels/LevelLong_f.gif");
        paramMap.put("Bild_c", "Levels/LevelLong_c.jpg");

        paramMap.put("StartX", "80");
        paramMap.put("StartY", "1155");

        paramMap.put("scaleSize", "70");  // percent
        paramMap.put("scaleF", "10");  // percent
        paramMap.put("scaleM", "100"); // percent

        paramMap.put("mHook", "100"); // hundredths
        paramMap.put("mRope", "50");  // hundredths
        paramMap.put("mWheels", "100"); // hundredths
        paramMap.put("mAxis", "200"); // hundredths
        paramMap.put("mCorpus", "200"); // hundredths

        paramMap.put("l0Rope", "2");
        paramMap.put("v0Rope", "1500");

        paramMap.put("FRopeMin", "100");
        paramMap.put("FRopeMax", "4000");
        paramMap.put("FWheels", "5000");
        paramMap.put("FCorpus", "10000");
        paramMap.put("FEngine", "60");

        paramMap.put("Gravity", "70");
        paramMap.put("dt", "25");
        paramMap.put("delay", "10"); // Milliseconds.
        paramMap.put("Reibung", "990"); // Friction in thousandths
        paramMap.put("ReibungW", "965"); // Friction in thousandths
        paramMap.put("width", "800"); // pixels
        paramMap.put("height", "600"); // pixels
    }
}
